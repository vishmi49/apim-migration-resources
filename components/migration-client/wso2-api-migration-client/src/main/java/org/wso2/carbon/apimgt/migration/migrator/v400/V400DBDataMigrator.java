/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.apimgt.migration.migrator.v400;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.VHost;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.dao.APIMgtDAO;
import org.wso2.carbon.apimgt.migration.dto.*;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifactImpl;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;

public class V400DBDataMigrator extends Migrator {

    private RegistryService registryService;
    List<Tenant> tenants;
    TenantManager tenantManager;

    public V400DBDataMigrator() throws UserStoreException {
        tenants = loadTenants();
        registryService = new RegistryServiceImpl();
    }

    private static final Log log = LogFactory.getLog(V400DBDataMigrator.class);
    private static char[] TRUST_STORE_PASSWORD = System.getProperty("javax.net.ssl.trustStorePassword").toCharArray();
    private static String TRUST_STORE = System.getProperty("javax.net.ssl.trustStore");
    public static final String BEGIN_CERTIFICATE_STRING = "-----BEGIN CERTIFICATE-----\n";
    public static final String END_CERTIFICATE_STRING = "-----END CERTIFICATE-----";

    APIMgtDAO apiMgtDAO = APIMgtDAO.getInstance();

    @Override
    public void migrate() throws APIMigrationException {
        log.info("Start migrating Labels to Vhosts ..........");
        migrateLabelsToVhosts();
        log.info("Successfully migrated Labels to Vhosts ..........");

        log.info("Start migrating API Product Mappings  ..........");
        migrateProductMappingTable();
        log.info("Successfully migrated API Product Mappings ..........");

        log.info("Start migrating Endpoint Certificates  ..........");
        migrateEndpointCertificates();
        log.info("Successfully migrated Endpoint Certificates.");

        log.info("Start replacing KM name by UUID  ..........");
        replaceKMNamebyUUID();
        log.info("Successfully replaced KM name by UUID.");

        log.info("Start updating API uuid and status in all api artifacts  ..........");
        moveUUIDToDBFromRegistry();
        log.info("Successfully updated the UUID and Status for all API artifacts.");
    }

    private void migrateLabelsToVhosts() throws APIMigrationException {
        try {
            // retrieve labels
            List<LabelDTO> labelDTOS = apiMgtDAO.getLabels();
            List<GatewayEnvironmentDTO> environments = new ArrayList<>(labelDTOS.size());

            // converts to dynamic environments
            for (LabelDTO labelDTO : labelDTOS) {
                GatewayEnvironmentDTO environment = new GatewayEnvironmentDTO();
                environment.setUuid(labelDTO.getLabelId());
                // skip checking an environment exists with the same name in deployment toml
                // eg: label 'Default' and environment 'Default' in toml.
                environment.setName(labelDTO.getName());
                environment.setDisplayName(labelDTO.getName());
                environment.setDescription(labelDTO.getDescription());
                environment.setTenantDomain(labelDTO.getTenantDomain());

                List<VHost> vhosts = new ArrayList<>(labelDTO.getAccessUrls().size());
                for (String accessUrl : labelDTO.getAccessUrls()) {
                    if (!StringUtils.contains(accessUrl, VHost.PROTOCOL_SEPARATOR)) {
                        accessUrl = VHost.HTTPS_PROTOCOL + VHost.PROTOCOL_SEPARATOR + accessUrl;
                    }
                    VHost vhost = VHost.fromEndpointUrls(new String[]{accessUrl});
                    vhosts.add(vhost);
                }
                environment.setVhosts(vhosts);
                environments.add(environment);
            }
            // insert dynamic environments
            apiMgtDAO.addDynamicGatewayEnvironments(environments);
            apiMgtDAO.dropLabelTable();
        } catch (APIMigrationException e) {
            throw new APIMigrationException("Error while Reading Labels", e);
        } catch (APIManagementException e) {
            throw new APIMigrationException("Error while Converting Endpoint URLs to VHost", e);
        }
    }

    private void migrateProductMappingTable() throws APIMigrationException {
        apiMgtDAO.updateProductMappings();
    }

    public void migrateEndpointCertificates() throws APIMigrationException {

        File trustStoreFile = new File(TRUST_STORE);

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream localTrustStoreStream = new FileInputStream(trustStoreFile)) {
                trustStore.load(localTrustStoreStream, TRUST_STORE_PASSWORD);
            }
            Set<String> aliases = APIMgtDAO.getInstance().retrieveListOfEndpointCertificateAliases();
            Map<String, String> certificateMap = new HashMap<>();
            if (aliases != null) {
                for (String alias : aliases) {
                    Certificate certificate = trustStore.getCertificate(alias);
                    if (certificate != null) {
                        byte[] encoded = Base64.encodeBase64(certificate.getEncoded());
                        String base64EncodedString = BEGIN_CERTIFICATE_STRING.concat(new String(encoded)).concat("\n")
                                .concat(END_CERTIFICATE_STRING);
                        base64EncodedString = Base64.encodeBase64URLSafeString(base64EncodedString.getBytes());
                        certificateMap.put(alias, base64EncodedString);
                    }
                }
            }
            APIMgtDAO.getInstance().updateEndpointCertificates(certificateMap);
        } catch (NoSuchAlgorithmException | IOException | CertificateException
                | KeyStoreException | APIMigrationException e) {
            throw new APIMigrationException("Error while Migrating Endpoint Certificates", e);
        }
    }

    public void replaceKMNamebyUUID() throws APIMigrationException {
        APIMgtDAO apiMgtDAO = APIMgtDAO.getInstance();

        for (Tenant tenant : tenants) {
            //Add tenant specific resident key manager with uuids to the AM_KEY_MANAGER table
            log.info("Adding default key manager and updating key mappings for tenant: "
                    + tenant.getId() + "(" + tenant.getDomain() + ")");
            addDefaultKM(apiMgtDAO, tenant.getDomain());
            apiMgtDAO.replaceKeyMappingKMNamebyUUID(tenant);
            apiMgtDAO.replaceRegistrationKMNamebyUUID(tenant);
        }
    }

    private void addDefaultKM(APIMgtDAO apiMgtDAO, String tenantDomain) throws APIMigrationException {
        if (apiMgtDAO.getKeyManagerConfigurationByName(tenantDomain,
                org.wso2.carbon.apimgt.impl.APIConstants.KeyManager.DEFAULT_KEY_MANAGER) == null) {

            KeyManagerConfigurationDTO keyManagerConfigurationDTO = new KeyManagerConfigurationDTO();
            keyManagerConfigurationDTO.setName(org.wso2.carbon.apimgt.impl.APIConstants.KeyManager.DEFAULT_KEY_MANAGER);
            keyManagerConfigurationDTO.setEnabled(true);
            keyManagerConfigurationDTO.setUuid(UUID.randomUUID().toString());
            keyManagerConfigurationDTO.setTenantDomain(tenantDomain);
            keyManagerConfigurationDTO.setDescription(
                    org.wso2.carbon.apimgt.impl.APIConstants.KeyManager.DEFAULT_KEY_MANAGER_DESCRIPTION);
            keyManagerConfigurationDTO
                    .setType(org.wso2.carbon.apimgt.impl.APIConstants.KeyManager.DEFAULT_KEY_MANAGER_TYPE);
            TokenHandlingDTO tokenHandlingDto = new TokenHandlingDTO();
            tokenHandlingDto.setEnable(true);
            tokenHandlingDto.setType(TokenHandlingDTO.TypeEnum.REFERENCE);
            tokenHandlingDto.setValue(org.wso2.carbon.apimgt.impl.APIConstants.KeyManager.UUID_REGEX);
            keyManagerConfigurationDTO
                    .addProperty(org.wso2.carbon.apimgt.impl.APIConstants.KeyManager.TOKEN_FORMAT_STRING,
                            new Gson().toJson(Arrays.asList(tokenHandlingDto)));
            apiMgtDAO.addKeyManagerConfiguration(keyManagerConfigurationDTO);
        }
    }

    /**
     * Get the List of APIs and pass it to DAO method to update the uuid
     * @throws APIMigrationException APIMigrationException
     */
    protected void moveUUIDToDBFromRegistry() throws APIMigrationException {

        List<APIInfoDTO> apiInfoDTOList = new ArrayList<>();
        tenantManager = ServiceHolder.getRealmService().getTenantManager();
        try {
            List<Tenant> tenants = APIUtil.getAllTenantsWithSuperTenant();
            for (Tenant tenant : tenants) {
                try {
                    int apiTenantId = tenantManager.getTenantId(tenant.getDomain());
                    APIUtil.loadTenantRegistry(apiTenantId);
                    Utility.startTenantFlow(tenant.getDomain());
                    Registry registry =
                            ServiceHolder.getRegistryService().getGovernanceSystemRegistry(apiTenantId);
                    GenericArtifactManager tenantArtifactManager = APIUtil.getArtifactManager(registry,
                            APIConstants.API_KEY);
                    if (tenantArtifactManager != null) {
                        GenericArtifact[] tenantArtifacts = tenantArtifactManager.getAllGenericArtifacts();
                        for (GenericArtifact artifact : tenantArtifacts) {
                            String artifactPath = ((GenericArtifactImpl) artifact).getArtifactPath();
                            if (artifactPath.contains("/apimgt/applicationdata/apis/")) {
                                continue;
                            }
                            APIInfoDTO apiInfoDTO = new APIInfoDTO();
                            apiInfoDTO.setUuid(artifact.getId());
                            apiInfoDTO.setApiProvider(
                                    APIUtil.replaceEmailDomainBack(artifact.getAttribute("overview_provider")));
                            apiInfoDTO.setApiName(artifact.getAttribute("overview_name"));
                            apiInfoDTO.setApiVersion(artifact.getAttribute("overview_version"));
                            apiInfoDTO.setStatus(((GenericArtifactImpl) artifact).getLcState());
                            apiInfoDTOList.add(apiInfoDTO);
                        }
                    }
                } finally {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            }
            apiMgtDAO.updateUUIDAndStatus(apiInfoDTOList);

        } catch (RegistryException e) {
            throw new APIMigrationException("Error while initiation the registry", e);
        } catch (UserStoreException e) {
            throw new APIMigrationException("Error while retrieving the tenants", e);
        } catch (APIManagementException e) {
            throw new APIMigrationException("Error while Retrieving API artifact from the registry", e);
        }
    }
}
