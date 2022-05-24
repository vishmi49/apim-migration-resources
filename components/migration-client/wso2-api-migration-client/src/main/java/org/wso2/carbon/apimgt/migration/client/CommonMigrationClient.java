/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.wso2.carbon.apimgt.api.APIDefinitionValidationResponse;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.ErrorHandler;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.SystemConfigurationsDAO;
import org.wso2.carbon.apimgt.impl.definitions.AsyncApiParserUtil;
import org.wso2.carbon.apimgt.impl.definitions.GraphQLSchemaDefinition;
import org.wso2.carbon.apimgt.impl.definitions.OASParserUtil;
import org.wso2.carbon.apimgt.impl.utils.APIMWSDLReader;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.impl.wsdl.model.WSDLValidationResponse;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.dao.APIMgtDAO;
import org.wso2.carbon.apimgt.migration.dto.APIInfoDTO;
import org.wso2.carbon.apimgt.migration.util.Constants;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.utils.multitenancy.MultitenantUtils.getTenantAwareUsername;

public class CommonMigrationClient extends MigrationClientBase {

    protected Registry registry;
    protected TenantManager tenantManager;
    private static final Log log = LogFactory.getLog(ScopeRoleMappingPopulationClient.class);
    APIMgtDAO apiMgtDAO = APIMgtDAO.getInstance();
    SystemConfigurationsDAO systemConfigurationsDAO = SystemConfigurationsDAO.getInstance();
    protected List<String> preValidationServiceList = new ArrayList<>();
    private String migrateFromVersion;
    String V400 = "4.0.0";

    public CommonMigrationClient(String tenantArguments, String blackListTenantArguments, String tenantRange,
                           TenantManager tenantManager, String migrateFromVersion) throws UserStoreException {
        super(tenantArguments, blackListTenantArguments, tenantRange, tenantManager);
        this.tenantManager = tenantManager;
        this.migrateFromVersion = migrateFromVersion;
        preValidationServiceList.add(Constants.preValidationService.API_DEFINITION_VALIDATION);
        preValidationServiceList.add(Constants.preValidationService.API_ENDPOINT_VALIDATION);
    }

    public void commonDataMigration() throws APIMigrationException {

        if (!V400.equals(migrateFromVersion)) {
            moveUUIDToDBFromRegistry();
        }
        apiMgtDAO.updateApiOrganizations();
    }

    /**
     * Get the List of APIs and pass it to DAO method to update the uuid
     * @throws APIMigrationException APIMigrationException
     */
    protected void moveUUIDToDBFromRegistry() throws APIMigrationException {

        List<APIInfoDTO> apiInfoDTOList = new ArrayList<>();
        try {
            List<Tenant> tenants = APIUtil.getAllTenantsWithSuperTenant();
            for (Tenant tenant : tenants) {
                try {
                    int apiTenantId = tenantManager.getTenantId(tenant.getDomain());
                    APIUtil.loadTenantRegistry(apiTenantId);
                    startTenantFlow(tenant.getDomain());
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

    /**
     * This method can be used as pre-validation step
     *
     * @param validateStep validateStep
     * @throws APIMigrationException APIMigrationException due to pre migration validation failure
     */
    public void preMigrationValidation(String validateStep) throws APIMigrationException {

        log.info("Executing pre migration step ..........");

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
                    log.debug(
                            "End pre migration validation of api definition for tenant " + tenant.getId() + '(' + tenant
                                    .getDomain() + ')');
                }
            }
            log.info("Successfully executed the pre validation step.");
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
                        .getSystemConfig(organization, APIConstants.ConfigType.TENANT.toString());
                if (StringUtils.isEmpty(tenantConfig)) {
                    systemConfigurationsDAO
                            .addSystemConfig(organization, APIConstants.ConfigType.TENANT.toString(), formattedTenantConf);
                } else {
                    systemConfigurationsDAO
                            .updateSystemConfig(organization, APIConstants.ConfigType.TENANT.toString(), formattedTenantConf);
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

    protected void startTenantFlow(String tenantDomain) {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
    }
}
