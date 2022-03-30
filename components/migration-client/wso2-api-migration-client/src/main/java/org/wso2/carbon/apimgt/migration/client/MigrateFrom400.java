/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.migration.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIDefinitionValidationResponse;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIProvider;
import org.wso2.carbon.apimgt.api.ErrorHandler;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.ResourceFile;
import org.wso2.carbon.apimgt.impl.APIConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.wso2.carbon.apimgt.impl.APIConstants.ConfigType;
import org.wso2.carbon.apimgt.impl.dao.SystemConfigurationsDAO;
import org.wso2.carbon.apimgt.impl.definitions.AsyncApiParserUtil;
import org.wso2.carbon.apimgt.impl.definitions.GraphQLSchemaDefinition;
import org.wso2.carbon.apimgt.impl.definitions.OASParserUtil;
import org.wso2.carbon.apimgt.impl.utils.APIFileUtil;
import org.wso2.carbon.apimgt.impl.utils.APIMWSDLReader;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.impl.utils.APIVersionComparator;

import org.wso2.carbon.apimgt.impl.wsdl.WSDLProcessor;
import org.wso2.carbon.apimgt.impl.wsdl.model.WSDLValidationResponse;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.client.sp_migration.APIMStatMigrationException;
import org.wso2.carbon.apimgt.migration.dao.APIMgtDAO;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.common.mappings.APIMappingUtil;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.common.mappings.PublisherCommonUtils;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.GraphQLValidationResponseDTO;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifactImpl;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.wso2.carbon.apimgt.impl.utils.APIMWSDLReader.getWSDLProcessor;
import static org.wso2.carbon.utils.multitenancy.MultitenantUtils.getTenantAwareUsername;

public class MigrateFrom400 extends MigrationClientBase implements MigrationClient {

    private static final Log log = LogFactory.getLog(MigrateFrom400.class);
    APIMgtDAO apiMgtDAO = APIMgtDAO.getInstance();
    SystemConfigurationsDAO systemConfigurationsDAO = SystemConfigurationsDAO.getInstance();
    private RegistryService registryService;
    protected List<String> preValidationServiceList = new ArrayList<>();

    public MigrateFrom400(String tenantArguments, String blackListTenantArguments, String tenantRange,
            RegistryService registryService, TenantManager tenantManager)
            throws UserStoreException {
        super(tenantArguments, blackListTenantArguments, tenantRange, tenantManager);
        this.registryService = registryService;
        preValidationServiceList.add(Constants.preValidationService.API_DEFINITION_VALIDATION);
        preValidationServiceList.add(Constants.preValidationService.API_ENDPOINT_VALIDATION);
    }

    @Override
    public void databaseMigration() throws APIMigrationException, SQLException {

        populateApiCategoryOrganizations();
        populateApiOrganizations();
        populateApplicationOrganizations();
        populateGWEnvironmentOrganizations();
    }

    private void populateApiCategoryOrganizations() throws APIMigrationException {

        try {
            Map<Integer, String> tenantIdsAndOrganizations = APIUtil.getAllTenantsWithSuperTenant().stream()
                    .collect(Collectors.toMap(Tenant::getId, Tenant::getDomain));
            apiMgtDAO.updateApiCategoryOrganizations(tenantIdsAndOrganizations);
        } catch (UserStoreException e) {
            throw new APIMigrationException("Failed to retrieve tenants");
        }
    }

    private void populateGWEnvironmentOrganizations() throws APIMigrationException {
        apiMgtDAO.populateGWEnvironmentOrganizations();
    }

    private void populateApiOrganizations() throws APIMigrationException {

        apiMgtDAO.updateApiOrganizations();
    }

    private void populateApplicationOrganizations() throws APIMigrationException {

        Map<Integer, String> subscriberOrganizations = new HashMap<>();
        Map<Integer, Integer> subscriberIdsAndTenantIds = apiMgtDAO.getSubscriberIdsAndTenantIds();
        for (Map.Entry<Integer, Integer> subscriberIdAndTenantId : subscriberIdsAndTenantIds.entrySet()) {
            String organization = APIUtil.getTenantDomainFromTenantId(subscriberIdAndTenantId.getValue());
            subscriberOrganizations.put(subscriberIdAndTenantId.getKey(), organization);
        }
        apiMgtDAO.updateApplicationOrganizations(subscriberOrganizations);
    }

    @Override
    public void migrateTenantConfToDB() throws APIMigrationException {
        for (Tenant tenant : getTenantsArray()) {
            addTenantConfToDB(tenant);
        }
    }

    public void addTenantConfToDB(Tenant tenant) throws APIMigrationException {
        int tenantId = tenant.getId();
        String organization = APIUtil.getTenantDomainFromTenantId(tenantId);
        JSONObject tenantConf = getTenantConfigFromRegistry(tenant.getId());
        ObjectMapper mapper = new ObjectMapper();
        String formattedTenantConf = null;

        try {
            if (tenantConf != null) {
                formattedTenantConf = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tenantConf);
            }
        } catch (JsonProcessingException jse) {
            log.error("Error while JSON Processing tenant conf :" + jse);
            log.info("Hence, skipping tenant conf to db migration for tenant Id :" + tenantId);
        }

        if (formattedTenantConf != null) {
            try {
                String tenantConfig = systemConfigurationsDAO
                        .getSystemConfig(organization, ConfigType.TENANT.toString());
                if (StringUtils.isEmpty(tenantConfig)) {
                    systemConfigurationsDAO
                            .addSystemConfig(organization, ConfigType.TENANT.toString(), formattedTenantConf);
                } else {
                    systemConfigurationsDAO
                            .updateSystemConfig(organization, ConfigType.TENANT.toString(), formattedTenantConf);
                }
            } catch (APIManagementException e) {
                log.info("Error while adding to tenant conf to database for tenant: " + tenantId + "with Error :"
                        + e);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("tenant conf value is empty.");
            }
        }
    }

    @Override
    public void registryResourceMigration() throws APIMigrationException {
        rxtMigration(registryService);
    }

    /**
     * This adds version timestamp to the rxt and db.
     */
    @Override
    public void registryDataPopulation() throws APIMigrationException {

        log.info("Registry data population for API Manager " + Constants.VERSION_4_0_0 + " started.");

        boolean isTenantFlowStarted = false;
        for (Tenant tenant : getTenantsArray()) {
            if (log.isDebugEnabled()) {
                log.debug("Start rxtMigration for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
            }
            try {
                PrivilegedCarbonContext.startTenantFlow();
                isTenantFlowStarted = true;

                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenant.getDomain(), true);
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenant.getId(), true);

                String adminName = getTenantAwareUsername(APIUtil.replaceEmailDomainBack(tenant.getAdminName()));

                if (log.isDebugEnabled()) {
                    log.debug("Tenant admin username : " + adminName);
                }

                ServiceHolder.getTenantRegLoader().loadTenantRegistry(tenant.getId());
                UserRegistry registry = ServiceHolder.getRegistryService().getGovernanceSystemRegistry(tenant.getId());
                GenericArtifactManager artifactManager = APIUtil.getArtifactManager(registry, APIConstants.API_KEY);

                if (artifactManager != null) {
                    GovernanceUtils.loadGovernanceArtifacts(registry);
                    GenericArtifact[] artifacts = artifactManager.getAllGenericArtifacts();
                    Map<String, List<API>> apisMap = new TreeMap<>();
                    Map<API, GenericArtifact> apiToArtifactMapping = new HashMap<>();

                    for (GenericArtifact artifact : artifacts) {
                        try {
                            String artifactPath = ((GenericArtifactImpl) artifact).getArtifactPath();
                            if (artifactPath.contains("/apimgt/applicationdata/apis/")) {
                                continue;
                            }
                            API api = APIUtil.getAPI(artifact, registry);
                            if (StringUtils.isNotEmpty(api.getVersionTimestamp())) {
                                if (log.isDebugEnabled()) {
                                    log.info(
                                            "VersionTimestamp already available in APIName: " + api.getId().getApiName()
                                                    + api.getId().getVersion());
                                }
                            }
                            if (api == null) {
                                log.error("Cannot find corresponding api for registry artifact " + artifact
                                        .getAttribute("overview_name") + '-' + artifact.getAttribute("overview_version")
                                        + '-' + artifact.getAttribute("overview_provider") + " of tenant " + tenant
                                        .getId() + '(' + tenant.getDomain() + ") in AM_DB");
                                continue;
                            }

                            if (log.isDebugEnabled()) {
                                log.debug("Doing the RXT migration for API : " + artifact.getAttribute("overview_name")
                                        + '-' + artifact.getAttribute("overview_version") + '-' + artifact
                                        .getAttribute("overview_provider") + '-' + artifact
                                        .getAttribute("overview_versionComparable") + '-' + " of tenant " + tenant.getId() + '('
                                        + tenant.getDomain() + ")");
                            }
                            if (!apisMap.containsKey(api.getId().getApiName())) {
                                List<API> versionedAPIsList = new ArrayList<>();
                                apisMap.put(api.getId().getApiName(), versionedAPIsList);

                            }
                            apisMap.get(api.getId().getApiName()).add(api);
                            if (!apiToArtifactMapping.containsKey(api)) {
                                apiToArtifactMapping.put(api, artifact);
                            }
                        } catch (Exception e) {
                            // we log the error and continue to the next resource.
                           throw new APIMigrationException("Unable to migrate api metadata definition of API : " + artifact
                                            .getAttribute("overview_name") + '-' + artifact
                                            .getAttribute("overview_version") + '-' + artifact
                                            .getAttribute("overview_provider"), e);
                        }
                    }

                    // set the versionTimestamp for each API
                    for (String apiName : apisMap.keySet()) {
                        List<API> versionedAPIList = apisMap.get(apiName);
                        versionedAPIList.sort(new APIVersionComparator());
                        long versionTimestamp = System.currentTimeMillis();
                        long oneDay = 86400;
                        for (int i = versionedAPIList.size(); i > 0; i--) {
                            API apiN = versionedAPIList.get(i - 1);
                            apiN.setVersionTimestamp(versionTimestamp + "");
                            apiToArtifactMapping.get(apiN)
                                    .setAttribute("overview_versionComparable", String.valueOf(versionTimestamp));
                            log.info("Setting Version Comparable for API " + apiN.getUuid());
                            try {
                                artifactManager.updateGenericArtifact(apiToArtifactMapping.get(apiN));
                            } catch (GovernanceException e) {
                                throw new APIMigrationException(
                                        "Failed to update versionComparable for API: " + apiN.getId().getApiName()
                                                + " version: " + apiN.getId().getVersion() + " versionComparable: "
                                                + apiN.getVersionTimestamp() + " at registry");
                            }
                            versionTimestamp -= oneDay;
                            GenericArtifact artifact;
                            try {
                                artifact = artifactManager.getGenericArtifact(apiN.getUuid());
                            } catch (GovernanceException e) {
                                throw new APIMigrationException(
                                        "Failed to retrieve API: " + apiN.getId().getApiName() + " version: " + apiN
                                                .getId().getVersion() + " from registry.");
                            }
                            // validate registry update
                            API api = APIUtil.getAPI(artifact, registry);
                            if (StringUtils.isEmpty(api.getVersionTimestamp())) {
                                log.error("VersionComparable is empty for API: " + apiN.getId().getApiName()
                                        + " version: " + apiN.getId().getVersion() + " versionComparable: " + api
                                        .getVersionTimestamp() + " at registry.");
                            } else {
                                log.info("VersionTimestamp successfully updated API: " + apiN.getId().getApiName()
                                        + " version: " + apiN.getId().getVersion() + " versionComparable: " + api
                                        .getVersionTimestamp());
                            }
                        }
                        try {
                            apiMgtDAO.populateApiVersionTimestamp(versionedAPIList);
                        } catch (APIMigrationException e) {
                            throw new APIMigrationException("Exception while populating versionComparable for api "
                                    + apiName + " tenant: " + tenant.getDomain() + "at database");
                        }
                    }
                    log.info("Successfully migrated data for api rxts to include versionComparable..........");
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("No api artifacts found in registry for tenant " + tenant.getId() + '(' + tenant
                                .getDomain() + ')');
                    }
                }
            } catch (APIManagementException e) {
                throw new APIMigrationException("Error occurred while reading API from the artifact ", e);
            } catch (RegistryException e) {
                throw new APIMigrationException("Error occurred while accessing the registry ", e);
            } finally {
                if (isTenantFlowStarted) {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("End rxtMigration for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
            }
        }
        log.info("Rxt resource migration done for all the tenants");
    }

    /**
     * This method can be used as pre-validation step
     *
     * @param validateStep validateStep
     * @throws APIMigrationException APIMigrationException due to pre migration validation failure
     */
    @Override
    public void preMigrationValidation(String validateStep) throws APIMigrationException {

        boolean isTenantFlowStarted = false;
        if (preValidationServiceList.contains(validateStep)) {
            for (Tenant tenant : getTenantsArray()) {
                addTenantConfToDB(tenant);
                if (log.isDebugEnabled()) {
                    log.debug("Start api definition validation for tenant " + tenant.getId() + '(' + tenant.getDomain()
                            + ')');
                }
                try {
                    PrivilegedCarbonContext.startTenantFlow();
                    isTenantFlowStarted = true;

                    PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenant.getDomain(), true);
                    PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenant.getId(), true);
                    String adminName = getTenantAwareUsername(APIUtil.replaceEmailDomainBack(tenant.getAdminName()));

                    if (log.isDebugEnabled()) {
                        log.debug("Tenant admin username : " + adminName);
                    }
                    validateRegistryData(tenant, validateStep);
                } finally {
                    if (isTenantFlowStarted) {
                        PrivilegedCarbonContext.endTenantFlow();
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug("End pre migration validation of api definition for tenant " + tenant.getId() + '(' + tenant
                            .getDomain() + ')');
                }
            }
        } else {
            log.warn("Pre migration step" + validateStep + " is not defined.");
        }
    }

    private void validateRegistryData(Tenant tenant, String validateStep) throws APIMigrationException {
        try {
            ServiceHolder.getTenantRegLoader().loadTenantRegistry(tenant.getId());
            UserRegistry registry = ServiceHolder.getRegistryService().getGovernanceSystemRegistry(tenant.getId());
            GenericArtifactManager artifactManager = APIUtil.getArtifactManager(registry, APIConstants.API_KEY);

            if (artifactManager != null) {
                GovernanceUtils.loadGovernanceArtifacts(registry);
                GenericArtifact[] artifacts = artifactManager.getAllGenericArtifacts();
                String artifactPath = "";

                log.info("Starting validate the api definitions of tenant " + tenant.getDomain() + "..........");

                for (GenericArtifact artifact : artifacts) {
                    try {
                        artifactPath = ((GenericArtifactImpl) artifact).getArtifactPath();
                        if (log.isDebugEnabled()) {
                            log.debug("artifact path:  " + artifactPath);
                        }

                        if (artifactPath.contains("/apimgt/applicationdata/apis/")) {
                            continue;
                        }

                        API api = APIUtil.getAPI(artifact, registry);

                        if (Constants.preValidationService.API_ENDPOINT_VALIDATION.equals(validateStep)) {
                            validateEndpoints(api);
                        } else if (Constants.preValidationService.API_DEFINITION_VALIDATION.equals(validateStep)) {
                            validateAPIDefinitions(api, registry);
                        }
                    } catch (Exception e) {
                        throw new APIMigrationException("Error occurred while retrieving API from the registry: "
                                + "artifact path name " + artifactPath, e);
                    }
                }
                log.info("Successfully validated the api definitions of tenant " + tenant.getDomain() + "..........");
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(
                            "No api artifacts found in registry for tenant " + tenant.getId() + '(' + tenant.getDomain()
                                    + ')');
                }
            }
        } catch (APIManagementException e) {
            throw new APIMigrationException("Error occurred while reading API from the artifact ", e);
        } catch (RegistryException e) {
            throw new APIMigrationException("Error occurred while accessing the registry ", e);
        }
    }

    private void validateEndpoints(API api) {
        try {
            APIDTO apiDto = APIMappingUtil.fromAPItoDTO(api);
            if (!PublisherCommonUtils.validateEndpoints(apiDto)) {
                log.error(
                        "Invalid/Malformed endpoint URL(s) detected in " + api.getId().getApiName() + " version: " + api
                                .getId().getVersion());
            }
        } catch (APIManagementException e) {
            log.error("Error while mapping API to API DTO for " + api.getId().getApiName() + " version: " + api.getId()
                    .getVersion() , e);
        }
    }

    private void validateAPIDefinitions(API api, Registry registry) {

        String apiType = api.getType();
        if (!isStreamingAPI(apiType)) {
            if (log.isDebugEnabled()) {
                log.debug("Validating api definitions of api " + api.getId().getApiName() + " version: " + api.getId()
                        .getVersion() + "type: " + apiType);
            }

            // validate swagger content except for streaming APIs
            openAPIValidation(api, registry);

            // validate GraphQL API definition and WSDL API definitions
            if (APIConstants.APITransportType.GRAPHQL.toString().equalsIgnoreCase(apiType)) {
                graphqlAPIDefinitionValidation(api, registry);
            } else if (APIConstants.API_TYPE_SOAP.equalsIgnoreCase(apiType)) {
                wsdlValidation(api, registry);
            }
        } else {
            streamingAPIDefinitionValidation(api, registry);
        }
    }

    private void openAPIValidation(API api, Registry registry) {

        APIDefinitionValidationResponse validationResponse = null;
        if (log.isDebugEnabled()) {
            log.debug("Validating open api definition of  " + api.getId().getApiName() + " version: " + api.getId()
                    .getVersion() + "type: " + api.getType());
        }

        try {
            String swaggerDefinition = OASParserUtil.getAPIDefinition(api.getId(), registry);
            validationResponse = OASParserUtil.validateAPIDefinition(swaggerDefinition, Boolean.TRUE);
        } catch (APIManagementException e) {
            log.error(" Error while validating open api definition for " + api.getId().getApiName() +
                    " version: " + api.getId().getVersion() + " " + e);
        }

        if (validationResponse != null && !validationResponse.isValid()) {
            for (ErrorHandler error : validationResponse.getErrorItems()) {
                log.error("Open API Definition is invalid. ErrorMessage: " + error.getErrorMessage()
                        + " ErrorDescription: " + error.getErrorDescription());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Successfully validated open api definition of " + api.getId().getApiName() + " version: " + api
                                .getId().getVersion() + "type: " + api.getType());
            }
        }
    }

    private void graphqlAPIDefinitionValidation(API api, Registry registry) {

        GraphQLValidationResponseDTO graphQLValidationResponseDTO = null;
        if (log.isDebugEnabled()) {
            log.debug("Validating graphQL schema definition of " + api.getId().getApiName() + " version: "
                    + api.getId().getVersion() + "type: " + api.getType());
        }

        if (api.getGraphQLSchema() == null) {
            GraphQLSchemaDefinition definition = new GraphQLSchemaDefinition();
            try {
                String graphqlSchema = definition.getGraphqlSchemaDefinition(api.getId(), registry);
                graphQLValidationResponseDTO = PublisherCommonUtils
                        .validateGraphQLSchema("schema.graphql", graphqlSchema);
            } catch (APIManagementException e) {
                log.error(" Error while validating graphql api definition for API:" + api.getId().getApiName()
                        + " version: " + api.getId().getVersion() + " " + e);
            }
        }

        if (graphQLValidationResponseDTO != null && !graphQLValidationResponseDTO.isIsValid()) {
            log.error(" Invalid GraphQL definition found. " + "ErrorMessage: " + graphQLValidationResponseDTO
                    .getErrorMessage());
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Successfully validated graphql schema of " + api.getId().getApiName() + " version: " + api
                        .getId().getVersion() + "type: " + api.getType());
            }
        }
    }

    private void wsdlValidation(API api, Registry registry) {

        WSDLValidationResponse wsdlValidationResponse = null;
        String wsdlArchivePath = APIUtil.getWsdlArchivePath(api.getId());
        byte[] wsdl;

        if (log.isDebugEnabled()) {
            log.debug("Validating wsdl of " + api.getId().getApiName() + " version: " + api.getId().getVersion()
                    + "type: " + api.getType());
        }

        try {
            if (registry.resourceExists(wsdlArchivePath)) {
                wsdl = (byte[]) registry.get(wsdlArchivePath).getContent();
                InputStream targetStream = new ByteArrayInputStream(wsdl);
                wsdlValidationResponse = APIMWSDLReader.extractAndValidateWSDLArchive(targetStream);
            } else {
                String apiPath = APIUtil.getAPIPath(api.getId());
                int prependIndex = apiPath.indexOf(api.getId().getVersion()) + api.getId().getVersion().length();
                String apiSourcePath = apiPath.substring(0, prependIndex);
                String wsdlResourcePath =
                        apiSourcePath + RegistryConstants.PATH_SEPARATOR + api.getId().getProviderName() + "--" + api
                                .getId().getApiName() + api.getId().getVersion() + ".wsdl";
                wsdl = (byte[]) registry.get(wsdlResourcePath).getContent();
                wsdlValidationResponse = APIMWSDLReader.validateWSDLFile(wsdl);
            }
        } catch (RegistryException e) {
            log.error(" Error while getting wsdl file. " + e);
        } catch (APIManagementException e) {
            log.error(
                    " Error while validating wsdl file of API:" + api.getId().getApiName() + " version: " + api.getId()
                            .getVersion() + " " + e);
        }


        if (wsdlValidationResponse != null && !wsdlValidationResponse.isValid()) {
            log.error(" Invalid WSDL definition found. " + wsdlValidationResponse.getError());
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Successfully validated wsdl file of " + api.getId().getApiName() + " version: "
                        + api.getId().getVersion() + "type: " + api.getType());
            }
        }
    }

    private void streamingAPIDefinitionValidation(API api, Registry registry) {
        String apiPath = null;
        String asyncAPIDefinition = "";
        APIDefinitionValidationResponse validationResponse = null;

        if (log.isDebugEnabled()) {
            log.debug("Validating streaming api definition of " + api.getId().getApiName() + " version: "
                    + api.getId().getVersion() + "type: " + api.getType());
        }

        try {
            apiPath = GovernanceUtils.getArtifactPath(registry, api.getUuid());
        } catch (GovernanceException e) {
            log.error(" Error while getting AsyncAPI definition. " + e);
        }

        if  (apiPath != null) {
            int prependIndex = apiPath.lastIndexOf("/api");
            String apiSourcePath = apiPath.substring(0, prependIndex);
            String definitionPath = apiSourcePath + "/" + "asyncapi.json";

            try {
                if (registry.resourceExists(definitionPath)) {
                    Resource apiDocResource = registry.get(definitionPath);
                    asyncAPIDefinition = new String((byte[]) ((byte[]) apiDocResource.getContent()),
                            Charset.defaultCharset());
                }
            } catch (RegistryException e) {
                log.error(" Error while getting AsyncAPI definition for API: " + api.getId().getApiName() + " version: "
                        + api.getId().getVersion() + e);
            }

            if (!asyncAPIDefinition.isEmpty()) {
                try {
                    validationResponse = AsyncApiParserUtil.validateAsyncAPISpecification(asyncAPIDefinition,
                            true);
                } catch (APIManagementException e) {
                    log.error(" Error while validating AsyncAPI definition for API:" + api.getId().getApiName() +
                    " version: " + api.getId().getVersion() + " " + e);
                }
                if (validationResponse != null && !validationResponse.isValid()) {
                    log.error(" Invalid AsyncAPI definition found. " + validationResponse.getErrorItems());
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Validating streaming api definition of API:" + api.getId().getApiName() + " version: "
                        + api.getId().getVersion() + "type: " + api.getType());
            }
        } else {
            log.error("apiPath of  " + api.getId().getApiName() + " version: " + api.getId().getVersion() + " "
                    + "is null");
        }
    }

    private boolean isStreamingAPI(String apiType) {
        return (APIConstants.APITransportType.WS.toString().equalsIgnoreCase(apiType)
                || APIConstants.APITransportType.SSE.toString().equalsIgnoreCase(apiType)
                || APIConstants.APITransportType.WEBSUB.toString().equalsIgnoreCase(apiType)
                || APIConstants.APITransportType.ASYNC.toString().equalsIgnoreCase(apiType));
    }

    @Override
    public void fileSystemMigration() throws APIMigrationException {

    }

    @Override
    public void cleanOldResources() throws APIMigrationException {

    }

    @Override
    public void statsMigration() throws APIMigrationException, APIMStatMigrationException {

    }

    @Override
    public void tierMigration(List<String> options) throws APIMigrationException {

    }

    @Override
    public void updateArtifacts() throws APIMigrationException {
    }

    @Override
    public void populateSPAPPs() throws APIMigrationException {

    }

    @Override
    public void populateScopeRoleMapping() throws APIMigrationException {

    }

    @Override
    public void scopeMigration() throws APIMigrationException {

    }

    @Override
    public void spMigration() throws APIMigrationException {

    }

    @Override
    public void updateScopeRoleMappings() throws APIMigrationException {

        for (Tenant tenant : getTenantsArray()) {
            loadAndSyncTenantConf(tenant.getId());
        }
    }

}
