package org.wso2.carbon.apimgt.migration.validator.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;

import java.io.IOException;
import java.nio.charset.Charset;

public abstract class Utils {
    private static final Log log = LogFactory.getLog(Utils.class);
    private final String migrateFromVersion;

    public Utils(String migrateFromVersion) {
        this.migrateFromVersion = migrateFromVersion;
    }

    // TODO: need to check whether there are active revisions for later versions
    public String getAPIDefinition(UserRegistry registry, String apiName, String apiVersion, String provider) {
        String resourcePath = getOpenAPIDefinitionFilePath(apiName, apiVersion, provider);
        JSONParser parser = new JSONParser();
        String apiDocContent = null;
        try {
            if (registry.resourceExists(resourcePath + APIConstants.API_OAS_DEFINITION_RESOURCE_NAME)) {
                Resource apiDocResource = registry.get(resourcePath + APIConstants.API_OAS_DEFINITION_RESOURCE_NAME);
                apiDocContent = new String((byte[]) apiDocResource.getContent(), Charset.defaultCharset());
                parser.parse(apiDocContent);
            } else {
                // resource not found
                log.warn("No resources found");
            }
        } catch (ParseException | RegistryException e) {
            log.error("Exception occurred when getting the definition", e);
        }
        return apiDocContent;
    }

    // TODO: need to check whether there are active revisions for later versions
    public String getGraphqlSchemaDefinition(UserRegistry registry, String apiName, String apiVersion, String provider)
            throws APIManagementException {
        String resourcePath = getGraphqlDefinitionFilePath(apiName, apiVersion, provider);
        String schemaDoc = null;
        String schemaName = provider + APIConstants.GRAPHQL_SCHEMA_PROVIDER_SEPERATOR + apiName + apiVersion
                + APIConstants.GRAPHQL_SCHEMA_FILE_EXTENSION;
        String schemaResourcePath = resourcePath + schemaName;
        try {
            if (registry.resourceExists(schemaResourcePath)) {
                org.wso2.carbon.registry.api.Resource schemaResource = registry.get(schemaResourcePath);
                schemaDoc = IOUtils.toString(schemaResource.getContentStream(),
                        RegistryConstants.DEFAULT_CHARSET_ENCODING);
            }
        } catch (org.wso2.carbon.registry.api.RegistryException e) {
            String errorMsg = "Error while getting schema file from the registry " + schemaResourcePath;
            log.error(errorMsg, e);
            throw new APIManagementException(errorMsg, e);
        } catch (IOException e) {
            String errorMsg = "Error occurred while getting the content of schema: " + schemaName;
            log.error(errorMsg);
            throw new APIManagementException(errorMsg, e);
        }
        return schemaDoc;
    }

    public static String getOpenAPIDefinitionFilePath(String apiName, String apiVersion, String apiProvider) {
        return APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR + apiProvider + RegistryConstants.PATH_SEPARATOR +
                apiName + RegistryConstants.PATH_SEPARATOR + apiVersion + RegistryConstants.PATH_SEPARATOR;
    }

    public static String getGraphqlDefinitionFilePath(String apiName, String apiVersion, String apiProvider) {
        return APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR + apiProvider + RegistryConstants.PATH_SEPARATOR +
                apiName + RegistryConstants.PATH_SEPARATOR + apiVersion + RegistryConstants.PATH_SEPARATOR;
    }

    public String getWSDLArchivePath(String apiName, String apiVersion, String provider) {
        return APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR + replaceEmailDomain(provider)
                + RegistryConstants.PATH_SEPARATOR + apiName + RegistryConstants.PATH_SEPARATOR + apiVersion
                + RegistryConstants.PATH_SEPARATOR + APIConstants.API_WSDL_ARCHIVE_LOCATION + provider
                + APIConstants.WSDL_PROVIDER_SEPERATOR + apiName + apiVersion + APIConstants.ZIP_FILE_EXTENSION;
    }

    public String getWSDLPath(String apiName, String apiVersion, String provider) {
        String apiPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR
                + replaceEmailDomain(provider) + RegistryConstants.PATH_SEPARATOR + apiName
                + RegistryConstants.PATH_SEPARATOR + apiVersion + APIConstants.API_RESOURCE_NAME;
        int prependIndex = apiPath.indexOf(apiVersion) + apiVersion.length();
        String apiSourcePath = apiPath.substring(0, prependIndex);
        return apiSourcePath + RegistryConstants.PATH_SEPARATOR + provider + APIConstants.WSDL_PROVIDER_SEPERATOR
                + apiName + apiVersion + APIConstants.WSDL_FILE_EXTENSION;
    }

    public String getAPIPath(String apiName, String apiVersion, String provider) {
        return APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR + replaceEmailDomain(provider)
                + RegistryConstants.PATH_SEPARATOR + apiName + RegistryConstants.PATH_SEPARATOR + apiVersion
                + APIConstants.API_RESOURCE_NAME;
    }

    public String replaceEmailDomain(String input) {
        if (input != null && input.contains(APIConstants.EMAIL_DOMAIN_SEPARATOR)) {
            input = input.replace(APIConstants.EMAIL_DOMAIN_SEPARATOR, APIConstants.EMAIL_DOMAIN_SEPARATOR_REPLACEMENT);
        }
        return input;
    }

    public boolean isStreamingAPI(String apiType) {
        return (APIConstants.APITransportType.WS.toString().equalsIgnoreCase(apiType)
                || APIConstants.APITransportType.SSE.toString().equalsIgnoreCase(apiType)
                || APIConstants.APITransportType.WEBSUB.toString().equalsIgnoreCase(apiType)
                || APIConstants.APITransportType.ASYNC.toString().equalsIgnoreCase(apiType));
    }

    public String getMigrateFromVersion() {
        return migrateFromVersion;
    }
}
