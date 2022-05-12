package org.wso2.carbon.apimgt.migration.migrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.internal.APIManagerComponent;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.dto.UserRoleFromPermissionDTO;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import static org.wso2.carbon.apimgt.impl.utils.APIUtil.getTenantDomainFromTenantId;

public class Utility {
    private static final Log log = LogFactory.getLog(Utility.class);
    private static List<Tenant> tenantsArray;
    private static final String MIGRATION = "Migration";
    private static final String VERSION_3 = "3.0.0";
    private static final String META = "Meta";
    public static String PRE_MIGRATION_SCRIPT_DIR = CarbonUtils.getCarbonHome() + File.separator
            + "migration-resources" + File.separator + "migration-scripts"+ File.separator
            + "pre-migration-scripts" + File.separator;
    public static String POST_MIGRATION_SCRIPT_DIR = CarbonUtils.getCarbonHome() + File.separator
            + "migration-resources" + File.separator + "migration-scripts"+ File.separator
            + "post-migration-scripts" + File.separator;

    public static void buildTenantList(TenantManager tenantManager, List<Tenant> tenantList, String tenantArguments)
            throws UserStoreException {
        if (tenantArguments.contains(",")) { // Multiple arguments specified
            String[] parts = tenantArguments.split(",");

            for (String part : parts) {
                if (part.length() > 0) {
                    populateTenants(tenantManager, tenantList, part);
                }
            }
        } else { // Only single argument provided
            populateTenants(tenantManager, tenantList, tenantArguments);
        }
    }

    /**
     * Loads tenant-conf.json (tenant config) to registry from the tenant-conf.json available in the file system.
     * If any REST API scopes are added to the local tenant-conf.json, they will be updated in the registry.
     *
     * @param tenantID tenant Id
     * @throws APIManagementException when error occurred while loading the tenant-conf to registry
     */
    public static void loadAndSyncTenantConf(int tenantID) throws APIMigrationException {

        org.wso2.carbon.registry.core.service.RegistryService registryService = ServiceHolder.getRegistryService();
        try {
            UserRegistry registry = registryService.getConfigSystemRegistry(tenantID);
            byte[] data = getTenantConfFromFile();
            if (registry.resourceExists(APIConstants.API_TENANT_CONF_LOCATION)) {
                log.debug("tenant-conf of tenant " + tenantID + " is  already uploaded to the registry");
                Optional<Byte[]> migratedTenantConf = migrateTenantConfScopes(tenantID);
                if (migratedTenantConf.isPresent()) {
                    log.debug("Detected new additions to tenant-conf of tenant " + tenantID);
                    data = ArrayUtils.toPrimitive(migratedTenantConf.get());
                } else {
                    log.debug("No changes required in tenant-conf.json of tenant " + tenantID);
                    return;
                }
            }
            log.debug("Adding/updating tenant-conf.json to the registry of tenant " + tenantID);
            updateTenantConf(registry, data);
            log.debug("Successfully added/updated tenant-conf.json of tenant  " + tenantID);
        } catch (RegistryException e) {
            throw new APIMigrationException("Error while saving tenant conf to the registry of tenant " + tenantID, e);
        } catch (IOException e) {
            throw new APIMigrationException("Error while reading tenant conf file content of tenant " + tenantID, e);
        } catch (APIMigrationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the content of the local tenant-conf.json as a JSON Object
     *
     * @return JSON content of the local tenant-conf.json
     * @throws IOException error while reading local tenant-conf.json
     */
    public static byte[] getTenantConfFromFile() throws IOException {
        JSONObject tenantConfJson = null;
        String tenantConfLocation = CarbonUtils.getCarbonHome() + File.separator +
                APIConstants.RESOURCE_FOLDER_LOCATION + File.separator +
                APIConstants.API_TENANT_CONF;
        File tenantConfFile = new File(tenantConfLocation);
        byte[] data;
        if (tenantConfFile.exists()) { // Load conf from resources directory in pack if it exists
            try (FileInputStream fileInputStream = new FileInputStream(tenantConfFile)) {
                data = IOUtils.toByteArray(fileInputStream);
            }
        } else { // Fallback to loading the conf that is stored at jar level if file does not exist in pack
            try (InputStream inputStream = APIManagerComponent.class
                    .getResourceAsStream("/tenant/" + APIConstants.API_TENANT_CONF)) {
                data = IOUtils.toByteArray(inputStream);
            }
        }
        return data;
    }

    private static JSONObject getRESTAPIScopesFromTenantConfig(JSONObject tenantConf) {

        return (JSONObject) tenantConf.get(APIConstants.REST_API_SCOPES_CONFIG);
    }

    private static JSONObject getRESTAPIScopeRoleMappingsFromTenantConfig(JSONObject tenantConf) {

        return (JSONObject) tenantConf.get(APIConstants.REST_API_ROLE_MAPPINGS_CONFIG);
    }
    /**
     * Returns the REST API scopes JSONObject from the tenant-conf.json in the file system
     *
     * @return REST API scopes JSONObject from the tenant-conf.json in the file system
     * @throws APIManagementException when error occurred while retrieving local REST API scopes.
     */
    private static JSONObject getRESTAPIScopesConfigFromFileSystem() throws APIMigrationException {

        try {
            byte[] tenantConfData = getTenantConfFromFile();
            String tenantConfDataStr = new String(tenantConfData, Charset.defaultCharset());
            JSONParser parser = new JSONParser();
            JSONObject tenantConfJson = (JSONObject) parser.parse(tenantConfDataStr);
            if (tenantConfJson == null) {
                throw new APIMigrationException("tenant-conf.json (in file system) content cannot be null");
            }
            JSONObject restAPIScopes = getRESTAPIScopesFromTenantConfig(tenantConfJson);
            if (restAPIScopes == null) {
                throw new APIMigrationException("tenant-conf.json (in file system) should have RESTAPIScopes config");
            }
            return restAPIScopes;
        } catch (IOException e) {
            throw new APIMigrationException("Error while reading tenant conf file content from file system", e);
        } catch (ParseException e) {
            throw new APIMigrationException("ParseException thrown when parsing tenant config json from string " +
                    "content", e);
        }
    }


    /**
     * Returns the REST API role mappings JSONObject from the tenant-conf.json in the file system
     *
     * @return REST API role mappings JSONObject from the tenant-conf.json in the file system
     * @throws APIManagementException when error occurred while retrieving local REST API role mappings.
     */
    private static JSONObject getRESTAPIRoleMappingsConfigFromFileSystem() throws APIMigrationException {

        try {
            byte[] tenantConfData = getTenantConfFromFile();
            String tenantConfDataStr = new String(tenantConfData, Charset.defaultCharset());
            JSONParser parser = new JSONParser();
            JSONObject tenantConfJson = (JSONObject) parser.parse(tenantConfDataStr);
            if (tenantConfJson == null) {
                throw new APIMigrationException("tenant-conf.json (in file system) content cannot be null");
            }
            JSONObject roleMappings = getRESTAPIScopeRoleMappingsFromTenantConfig(tenantConfJson);
            if (roleMappings == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Scope role mappings are not defined in the tenant-conf.json in file system");
                }
            }
            return roleMappings;
        } catch (IOException e) {
            throw new APIMigrationException("Error while reading tenant conf file content from file system", e);
        } catch (ParseException e) {
            throw new APIMigrationException("ParseException thrown when parsing tenant config json from string " +
                    "content", e);
        }
    }

    /**
     * Migrate the newly added scopes to the tenant-conf which is already in the registry identified with tenantId and
     * its byte content is returned. If there were no changes done, an empty Optional will be returned.
     *
     * @param tenantId Tenant Id
     * @return Optional byte content
     * @throws APIManagementException when error occurred while updating the updating the tenant-conf with scopes.
     */
    public static Optional<Byte[]> migrateTenantConfScopes(int tenantId) throws APIMigrationException {

        JSONObject tenantConf = getTenantConfigFromRegistry(tenantId);
        JSONObject scopesConfigTenant = getRESTAPIScopesFromTenantConfig(tenantConf);
        JSONObject scopeConfigLocal = getRESTAPIScopesConfigFromFileSystem();
        JSONObject roleMappingConfigTenant = getRESTAPIScopeRoleMappingsFromTenantConfig(tenantConf);
        JSONObject roleMappingConfigLocal = getRESTAPIRoleMappingsConfigFromFileSystem();
        Map<String, String> scopesTenant = APIUtil.getRESTAPIScopesFromConfig(scopesConfigTenant,
                roleMappingConfigTenant);
        Map<String, String> scopesLocal = APIUtil.getRESTAPIScopesFromConfig(scopeConfigLocal, roleMappingConfigLocal);
        JSONArray tenantScopesArray = (JSONArray) scopesConfigTenant.get(APIConstants.REST_API_SCOPE);
        boolean isRoleUpdated = false;
        boolean isMigrated = false;
        JSONObject metaJson = (JSONObject) tenantConf.get(MIGRATION);

        if (metaJson != null && metaJson.get(VERSION_3) != null) {
            isMigrated = Boolean.parseBoolean(metaJson.get(VERSION_3).toString());
        }

        if (!isMigrated) {
            try {
                //Get admin role name of the current domain
                String adminRoleName = CarbonContext.getThreadLocalCarbonContext().getUserRealm()
                        .getRealmConfiguration().getAdminRoleName();
                for (int i = 0; i < tenantScopesArray.size(); i++) {
                    JSONObject scope = (JSONObject) tenantScopesArray.get(i);
                    String roles = scope.get(APIConstants.REST_API_SCOPE_ROLE).toString();
                    if (APIConstants.APIM_SUBSCRIBE_SCOPE.equals(scope.get(APIConstants.REST_API_SCOPE_NAME)) &&
                            !roles.contains(adminRoleName)) {
                        tenantScopesArray.remove(i);
                        JSONObject scopeJson = new JSONObject();
                        scopeJson.put(APIConstants.REST_API_SCOPE_NAME, APIConstants.APIM_SUBSCRIBE_SCOPE);
                        scopeJson.put(APIConstants.REST_API_SCOPE_ROLE,
                                roles + APIConstants.MULTI_ATTRIBUTE_SEPARATOR_DEFAULT + adminRoleName);
                        tenantScopesArray.add(scopeJson);
                        isRoleUpdated = true;
                        break;
                    }
                }
                if (isRoleUpdated) {
                    JSONObject metaInfo = new JSONObject();
                    JSONObject migrationInfo = new JSONObject();
                    migrationInfo.put(VERSION_3, true);
                    metaInfo.put(MIGRATION, migrationInfo);
                    tenantConf.put(META, metaInfo);
                }
            } catch (UserStoreException e) {
                String tenantDomain = getTenantDomainFromTenantId(tenantId);
                String errorMessage = "Error while retrieving admin role name of " + tenantDomain;
                log.error(errorMessage, e);
                throw new APIMigrationException(errorMessage, e);
            }
            Set<String> scopes = scopesLocal.keySet();
            //Find any scopes that are not added to tenant conf which is available in local tenant-conf
            scopes.removeAll(scopesTenant.keySet());
            if (!scopes.isEmpty() || isRoleUpdated) {
                for (String scope : scopes) {
                    JSONObject scopeJson = new JSONObject();
                    scopeJson.put(APIConstants.REST_API_SCOPE_NAME, scope);
                    scopeJson.put(APIConstants.REST_API_SCOPE_ROLE, scopesLocal.get(scope));
                    if (log.isDebugEnabled()) {
                        log.debug("Found scope that is not added to tenant-conf.json in tenant " + tenantId +
                                ": " + scopeJson);
                    }
                    tenantScopesArray.add(scopeJson);
                }
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String formattedTenantConf = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tenantConf);
                    if (log.isDebugEnabled()) {
                        log.debug("Finalized tenant-conf.json: " + formattedTenantConf);
                    }
                    return Optional.of(ArrayUtils.toObject(formattedTenantConf.getBytes()));
                } catch (JsonProcessingException e) {
                    throw new APIMigrationException("Error while formatting tenant-conf.json of tenant " + tenantId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.debug("Scopes in tenant-conf.json in tenant " + tenantId + " are already migrated.");
                return Optional.empty();
            }
        } else {
            log.debug("Scopes in tenant-conf.json in tenant " + tenantId + " are already migrated.");
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static void populateTenants(TenantManager tenantManager, List<Tenant> tenantList, String argument)
            throws UserStoreException {

        if (log.isDebugEnabled()) {
            log.debug("Argument provided : " + argument);
        }

        if (argument.contains("@")) { // Username provided as argument
            int tenantID = tenantManager.getTenantId(argument);

            if (tenantID != -1) {
                tenantList.add(tenantManager.getTenant(tenantID));
            } else {
                log.error("Tenant does not exist for username " + argument);
            }
        } else { // Domain name provided as argument
            if (MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equalsIgnoreCase(argument)) {
                Tenant superTenant = new Tenant();
                superTenant.setDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
                superTenant.setId(MultitenantConstants.SUPER_TENANT_ID);
                tenantList.add(superTenant);
            }
            else {
                Tenant[] tenants = tenantManager.getAllTenantsForTenantDomainStr(argument);

                if (tenants.length > 0) {
                    tenantList.addAll(Arrays.asList(tenants));
                } else {
                    log.error("Tenant does not exist for domain " + argument);
                }
            }
        }
    }

    /**
     * This method is used to retrieve a string where the domain name is added in front of the user role name
     */
    private static String addDomainToName(String userRoleName, String domainName) {
        if (StringUtils.equals(domainName.toLowerCase(), Constants.USER_DOMAIN_INTERNAL.toLowerCase())) {
            // This check should be done for domain names with "Internal". Otherwise addDomainToName function will
            // convert this to uppercase (INTERNAL).
            return Constants.USER_DOMAIN_INTERNAL + "/" + userRoleName;
        } else {
            return UserCoreUtil.addDomainToName(userRoleName, domainName);
        }
    }

    /**
     * This method is used to retrieve a string with multiple permissions by escaping slashes
     * Example: If you provide "/permission/mypermission/" as startPermission and "/permission" as endPermission
     * this will produces a string as "'/permission/mypermission/', '/permission/mypermission', '/permission/,
     * '/permission'"
     */
    public static String makePermissionsStringByEscapingSlash(String startPermission, String endPermission) {
        StringBuilder permissions = new StringBuilder();
        permissions.append("'").append(startPermission).append("', ");
        for (int i = startPermission.length() - 1; i >= 0; i--) {
            if (!StringUtils.equals(startPermission.substring(0, i + 1), endPermission)) {
                if (startPermission.charAt(i) == '/') {
                    permissions.append("'").append(startPermission, 0, i + 1).append("', ");
                    permissions.append("'").append(startPermission, 0, i).append("', ");
                }
            } else {
                break;
            }
        }
        return StringUtils.chop(permissions.toString().trim());
    }

    public static JSONObject getTenantConfigFromRegistry(int tenantId) throws APIMigrationException {

        try {
            if (tenantId != org.wso2.carbon.base.MultitenantConstants.SUPER_TENANT_ID) {
                APIUtil.loadTenantRegistry(tenantId);
            }
            org.wso2.carbon.registry.core.service.RegistryService registryService =
                    ServiceHolder.getRegistryService();
            UserRegistry registry = registryService.getConfigSystemRegistry(tenantId);
            Resource resource;
            if (registry.resourceExists(APIConstants.API_TENANT_CONF_LOCATION)) {
                resource = registry.get(APIConstants.API_TENANT_CONF_LOCATION);
                String content = new String((byte[]) resource.getContent(), Charset.defaultCharset());
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(content);
            } else {
                return null;
            }
        } catch (RegistryException | ParseException e) {
            throw new APIMigrationException("Error while getting tenant config from registry for tenant: "
                    + tenantId, e);
        }
    }

    public static void updateTenantConf(String tenantConfString, int tenantId) throws APIMigrationException {

        org.wso2.carbon.registry.core.service.RegistryService registryService = ServiceHolder.getRegistryService();
        try {
            UserRegistry registry = registryService.getConfigSystemRegistry(tenantId);
            updateTenantConf(registry, tenantConfString.getBytes());
        } catch (RegistryException e) {
            throw new APIMigrationException("Error while saving tenant conf to the registry of tenant "
                    + tenantId, e);
        }
    }

    public static void updateTenantConf(UserRegistry registry, byte[] data) throws RegistryException {

        Resource resource = registry.newResource();
        resource.setMediaType(APIConstants.API_TENANT_CONF_MEDIA_TYPE);
        resource.setContent(data);
        registry.put(APIConstants.API_TENANT_CONF_LOCATION, resource);
    }

    /**
     * This method is used to add the fields (Internal/creator, Internal/publisher and Internal/subscriber) and
     * assign the created user roles list as values to the object
     */
    public static void createOrUpdateRoleMappingsField(JSONObject roleMappings,
                                                       List<UserRoleFromPermissionDTO> userRolesListWithCreatePermission,
                                                       List<UserRoleFromPermissionDTO> userRolesListWithPublishPermission,
                                                       List<UserRoleFromPermissionDTO> userRolesListWithSubscribePermission,
                                                       List<UserRoleFromPermissionDTO> userRolesListWithAdminPermission) {
        if (userRolesListWithCreatePermission.size() > 0) {
            if (roleMappings.get(Constants.CREATOR_ROLE) == null) {
                roleMappings.put(Constants.CREATOR_ROLE,
                        getUserRoleArrayAsString(userRolesListWithCreatePermission));
            } else {
                roleMappings.put(
                        Constants.CREATOR_ROLE,
                        getMergedUserRolesAndRoleMappings(userRolesListWithCreatePermission,
                                String.valueOf(roleMappings.get(Constants.CREATOR_ROLE))));
            }
        }

        if (userRolesListWithPublishPermission.size() > 0) {
            if (roleMappings.get(Constants.PUBLISHER_ROLE) == null) {
                roleMappings
                        .put(Constants.PUBLISHER_ROLE, getUserRoleArrayAsString(userRolesListWithPublishPermission));
            } else {
                roleMappings.put(Constants.PUBLISHER_ROLE,
                        getMergedUserRolesAndRoleMappings(userRolesListWithPublishPermission,
                                String.valueOf(roleMappings.get(Constants.PUBLISHER_ROLE))));
            }
        }

        if (userRolesListWithSubscribePermission.size() > 0) {
            if (roleMappings.get(Constants.SUBSCRIBER_ROLE) == null) {
                roleMappings
                        .put(Constants.SUBSCRIBER_ROLE, getUserRoleArrayAsString(userRolesListWithSubscribePermission));
            } else {
                roleMappings.put(Constants.SUBSCRIBER_ROLE,
                        getMergedUserRolesAndRoleMappings(userRolesListWithSubscribePermission,
                                String.valueOf(roleMappings.get(Constants.SUBSCRIBER_ROLE))));
            }
        }

        if (userRolesListWithAdminPermission.size() > 0) {
            if (roleMappings.get(Constants.ADMIN_ROLE) == null) {
                roleMappings.put(Constants.ADMIN_ROLE, getUserRoleArrayAsString(userRolesListWithAdminPermission));
            } else {
                roleMappings.put(Constants.ADMIN_ROLE,
                        getMergedUserRolesAndRoleMappings(userRolesListWithAdminPermission,
                                String.valueOf(roleMappings.get(Constants.ADMIN_ROLE))));
            }
        }
    }

    /**
     * This method is used to retrieve user roles as a comma separated string
     */
    private static String getUserRoleArrayAsString(List<UserRoleFromPermissionDTO> userRoleFromPermissionDTOs) {
        List<String> updatedUserRoles = new ArrayList<>();
        for (UserRoleFromPermissionDTO userRoleFromPermissionDTO : userRoleFromPermissionDTOs) {
            String userRoleName = userRoleFromPermissionDTO.getUserRoleName();
            String domainName = userRoleFromPermissionDTO.getUserRoleDomainName();
            updatedUserRoles.add(addDomainToName(userRoleName, domainName));
        }
        return StringUtils.join(updatedUserRoles, ",");
    }

    /**
     * This method is used to retrieve merged existing role mappings and new user roles
     */
    private static String getMergedUserRolesAndRoleMappings(List<UserRoleFromPermissionDTO> userRoles, String roleMappings) {
        // Splitting
        ArrayList<String> roleMappingsArray = new ArrayList<String>(Arrays.asList(StringUtils.
                split(roleMappings, ",")));
        // Trimming
        for (int i = 0; i < roleMappingsArray.size(); i++)
            roleMappingsArray.set(i, roleMappingsArray.get(i).trim());

        for (UserRoleFromPermissionDTO userRole : userRoles) {
            String domainNameAddedUserRoleName = addDomainToName(userRole.getUserRoleName(), userRole.getUserRoleDomainName());
            if (!roleMappingsArray.contains(domainNameAddedUserRoleName)) {
                roleMappingsArray.add(domainNameAddedUserRoleName);
            }
        }
        return StringUtils.join(roleMappingsArray, ",");
    }

    public static void startTenantFlow(String tenantDomain, int tenantId, String username) {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenantId);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(username);
    }

    public static void startTenantFlow(String tenantDomain) {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
    }

    public static String toString(Document newDoc) throws Exception {
        DOMSource domSource = new DOMSource(newDoc);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        String output = sw.toString();
        return output.substring(output.indexOf("?>") + 2);
    }
}
