package org.wso2.carbon.apimgt.migration.validator.validators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIDefinition;
import org.wso2.carbon.apimgt.api.APIDefinitionValidationResponse;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.ErrorHandler;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIConstants.OASResourceAuthTypes;
import org.wso2.carbon.apimgt.impl.definitions.AsyncApiParserUtil;
import org.wso2.carbon.apimgt.impl.definitions.OASParserUtil;
import org.wso2.carbon.apimgt.impl.utils.APIMWSDLReader;
import org.wso2.carbon.apimgt.impl.wsdl.model.WSDLValidationResponse;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.validator.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.migration.validator.utils.Utils;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.common.mappings.PublisherCommonUtils;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.GraphQLValidationResponseDTO;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Pattern;

public class V410Validator extends Validator {
    private static final Log log = LogFactory.getLog(V410Validator.class);
    private final String saveSwagger = System.getProperty(Constants.preValidationService.SAVE_INVALID_DEFINITION);

    public V410Validator(Utils utils) {
        super(utils);
    }

    @Override
    public void validateEndpoints() {

    }

    @Override
    public void validateAPIDefinition() {
        if (!utils.isStreamingAPI(apiType)) {
            validateOpenAPIDefinition();
            if (APIConstants.APITransportType.GRAPHQL.toString().equalsIgnoreCase(apiType)) {
                validateGraphQLAPIDefinition();
            } else if (APIConstants.API_TYPE_SOAP.equalsIgnoreCase(apiType)
                    || APIConstants.API_TYPE_SOAPTOREST.equalsIgnoreCase(apiType)) {
                validateWSDLDefinition();
            }
        } else {
            validateStreamingAPIDefinition();
        }
    }

    @Override
    public void validateApiAvailability() {
        try {
            log.info("Validating API availability in db for API {name: " + apiName + ", version: " +
                    apiVersion + ", provider: " + provider + "}");
            int id = ApiMgtDAO.getInstance().getAPIID(provider, apiName, apiVersion);
            if (id == -1) {
                log.error("Unable to find the API " + "{name: " + apiName + ", version: " +
                        apiVersion + ", provider: " + provider + "} in the database");
            }
        } catch (SQLException e) {
            log.error("Error while getting the database connection ", e);
        }
    }

    public void validateOpenAPIDefinition() {
        String apiDefinition = null;
        APIDefinitionValidationResponse validationResponse = null;
        log.info("Validating open API definition of API {name: " + apiName + ", version: " +
                apiVersion + ", provider: " + provider + "}");
        try {
            apiDefinition = utils.getAPIDefinition(registry, apiName, apiVersion, provider, apiId);
            if (apiDefinition != null) {
                validationResponse = OASParserUtil.validateAPIDefinition(apiDefinition, Boolean.TRUE);
            }
        } catch (APIManagementException e) {
            log.error("Error while validating open API definition for " + apiName + " version: " + apiVersion
                    + " type: " + apiType, e);
        }
        if (validationResponse != null && !validationResponse.isValid()) {
            if (saveSwagger != null) {
                utils.saveInvalidDefinition(apiId, apiDefinition);
            }
            for (ErrorHandler error : validationResponse.getErrorItems()) {
                log.error("OpenAPI Definition for API {name: " + apiName + ", version: " +
                        apiVersion + ", provider: " + provider + "}" + " is invalid. ErrorMessage: " +
                        error.getErrorMessage() + " ErrorDescription: " + error.getErrorDescription());
            }
        } else if (apiDefinition == null) {
            log.error("Error while validating open API definition for " + apiName + " version: " + apiVersion
                    + " type: " + apiType + ". Swagger definition of the API is missing...");
        } else {
            APIDefinition parser = validationResponse.getParser();
            try {
                if (parser != null) {
                    parser.getURITemplates(apiDefinition);
                }
                log.info("Successfully validated open API definition of " + apiName + " version: " + apiVersion
                        + " type: " + apiType);
            } catch (APIManagementException e) {
                if (saveSwagger != null) {
                    utils.saveInvalidDefinition(apiId, apiDefinition);
                }
                log.error("Error while retrieving URI Templates for " + apiName + " version: " + apiVersion
                        + " type: " + apiType, e);
            }
        }
    }

    private void validateGraphQLAPIDefinition() {
        GraphQLValidationResponseDTO graphQLValidationResponseDTO = null;
        log.info("Validating graphQL schema definition of " + apiName + " version: " + apiVersion + " type: "
                + apiType);
        try {
            String graphqlSchema = utils.getGraphqlSchemaDefinition(registry, apiName, apiVersion, provider, apiId);
            graphQLValidationResponseDTO = PublisherCommonUtils
                    .validateGraphQLSchema("schema.graphql", graphqlSchema);
        } catch (APIManagementException e) {
            log.error(" Error while validating graphql api definition for API:" + apiName
                    + " version: " + apiVersion + " " + e);
        }
        if (graphQLValidationResponseDTO != null && !graphQLValidationResponseDTO.isIsValid()) {
            log.error(" Invalid GraphQL definition found. " + "ErrorMessage: " + graphQLValidationResponseDTO
                    .getErrorMessage());
        } else {
            log.info("Successfully validated graphql schema of " + apiName + " version: " + apiVersion + "type: "
                    + apiType);
        }
    }

    private void validateWSDLDefinition() {
        WSDLValidationResponse wsdlValidationResponse = null;
        String wsdlArchivePath = utils.getWSDLArchivePath(apiName, apiVersion, provider);
        byte[] wsdl;
        log.info("Validating WSDL of " + apiName + " version: " + apiVersion + " type: " + apiType);
        try {
            if (registry.resourceExists(wsdlArchivePath)) {
                wsdl = (byte[]) registry.get(wsdlArchivePath).getContent();
                InputStream targetStream = new ByteArrayInputStream(wsdl);
                wsdlValidationResponse = APIMWSDLReader.extractAndValidateWSDLArchive(targetStream);
            } else {
                String wsdlPath = utils.getWSDLPath(apiName, apiVersion, provider);
                wsdl = (byte[]) registry.get(wsdlPath).getContent();
                wsdlValidationResponse = APIMWSDLReader.validateWSDLFile(wsdl);
            }
        } catch (RegistryException e) {
            log.error("Error while getting wsdl file", e);
        } catch (APIManagementException e) {
            log.error("Error while validating wsdl file of API:" + apiName + " version: " + apiVersion, e);
        }
        if (wsdlValidationResponse != null && !wsdlValidationResponse.isValid()) {
            log.error("Invalid WSDL definition found. " + wsdlValidationResponse.getError());
        } else {
            log.info("Successfully validated wsdl file of " + apiName + " version: " + apiVersion + " type: "
                    + apiType);
        }
    }

    private void validateStreamingAPIDefinition() {
        if ("4.0.0".equals(utils.getMigrateFromVersion())) {
            String apiPath = null;
            String asyncAPIDefinition = "";
            APIDefinitionValidationResponse validationResponse = null;
            log.info("Validating streaming api definition of " + apiName + " version: " + apiVersion + " type: "
                    + apiType);
            try {
                apiPath = GovernanceUtils.getArtifactPath(registry, apiId);
            } catch (GovernanceException e) {
                log.error(" Error while getting AsyncAPI definition. " + e);
            }
            if (apiPath != null) {
                int prependIndex = apiPath.lastIndexOf("/api");
                String apiSourcePath = apiPath.substring(0, prependIndex);
                String definitionPath = apiSourcePath + "/" + "asyncapi.json";
                try {
                    if (registry.resourceExists(definitionPath)) {
                        Resource apiDocResource = registry.get(definitionPath);
                        asyncAPIDefinition = new String((byte[]) apiDocResource.getContent(),
                                Charset.defaultCharset());
                    }
                } catch (RegistryException e) {
                    log.error(" Error while getting AsyncAPI definition for API: " + apiName + " version: " + apiVersion
                            , e);
                }

                if (!asyncAPIDefinition.isEmpty()) {
                    try {
                        validationResponse = AsyncApiParserUtil.validateAsyncAPISpecification(asyncAPIDefinition, true);
                    } catch (APIManagementException e) {
                        log.error(" Error while validating AsyncAPI definition for API:" + apiName + " version: "
                                + apiVersion, e);
                    }
                    if (validationResponse != null && !validationResponse.isValid()) {
                        log.error(" Invalid AsyncAPI definition found. " + validationResponse.getErrorItems());
                    }
                }
                log.info("Validating streaming api definition of API:" + apiName + " version: " + apiVersion
                        + " type: " + apiType);
            } else {
                log.error("apiPath of  " + apiName + " version: " + apiVersion + " is null");
            }
        } else {
            log.info("API " + apiName + " version: " + apiVersion + " type: " + apiType + " was not validated "
                    + "since the AsyncAPI definitions are supported after API Manager 4.0.0");
        }
    }

    @Override
    public void validateApiResourceLevelAuthScheme() {

        Pattern pattern = Pattern.compile("2\\.\\d\\.\\d");

        if (pattern.matcher(utils.getMigrateFromVersion()).matches()) {
            log.info("Validating Resource Level Auth Scheme of API {name: " + apiName + ", version: " + apiVersion
                    + ", provider: " + provider + "}");
            try {
                int id = ApiMgtDAO.getInstance().getAPIID(provider, apiName, apiVersion);
                Set<URITemplate> uriTemplates = ApiMgtDAO.getInstance().getURITemplatesByAPIID(id);
                for (URITemplate uriTemplate : uriTemplates) {
                    if (!(OASResourceAuthTypes.APPLICATION_OR_APPLICATION_USER.equals(uriTemplate.getAuthType())
                            || OASResourceAuthTypes.NONE.equals(uriTemplate.getAuthType())
                            || "Any".equals(uriTemplate.getAuthType()))) {
                        log.warn("Resource level Authentication Schemes 'Application', 'Application User' are not " +
                                "supported, Resource {HTTP verb: " + uriTemplate.getHTTPVerb() + ", URL pattern: " +
                                uriTemplate.getUriTemplate() + ", Auth Scheme: " + uriTemplate.getAuthType() + "}");
                    }
                }
            } catch (SQLException e) {
                log.error("Error on Retrieving URITemplates for apiResourceLevelAuthSchemeValidation", e);
            }
            log.info("Completed Validating Resource Level Auth Scheme of API {name: " + apiName + ", version: "
                    + apiVersion + ", provider: " + provider + "}");
        }


    }

    @Override
    public void validateApiDeployedGatewayType(GenericArtifact apiArtifact) {
        log.info("Validating deployed gateway type for API {name: " + apiName + ", version: " + apiVersion
                + ", provider: " + provider + "}");
        try {
            String environments = apiArtifact.getAttribute(APIConstants.API_OVERVIEW_ENVIRONMENTS);
            if ("none".equals(environments)) {
                log.warn("No gateway environments are configured for API {name: " + apiName + ", version: " + apiVersion
                        + ", provider: " + provider + "}. Hence revision deployment will be skipped at migration");
            }
            log.info("Completed deployed gateway type validation for API {name: " + apiName + ", version: " + apiVersion
                    + ", provider: " + provider + "}");
        } catch (GovernanceException e) {
            log.error("Error on retrieving API Gateway environment from API generic artifact", e);
        }

    }
}
