package org.wso2.carbon.apimgt.migration.migrator.v400.dao;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.dto.ClientCertificateDTO;
import org.wso2.carbon.apimgt.api.model.*;
import org.wso2.carbon.apimgt.api.model.graphql.queryanalysis.CustomComplexityDetails;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.dao.constants.SQLConstants;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.impl.utils.RemoteUserManagerClient;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class ApiMgtDAO {

    private static final Log log = LogFactory.getLog(org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO.class);
    private final Object scopeMutex = new Object();
    private boolean forceCaseInsensitiveComparisons = false;
    private boolean multiGroupAppSharingEnabled = false;
    private static ApiMgtDAO INSTANCE = null;

    private ApiMgtDAO() {

        APIManagerConfiguration configuration = ServiceHolder
                .getAPIManagerConfigurationService().getAPIManagerConfiguration();

        String caseSensitiveComparison = ServiceHolder.
                getAPIManagerConfigurationService().getAPIManagerConfiguration().
                getFirstProperty(APIConstants.API_STORE_FORCE_CI_COMPARISIONS);
        if (caseSensitiveComparison != null) {
            forceCaseInsensitiveComparisons = Boolean.parseBoolean(caseSensitiveComparison);
        }

        multiGroupAppSharingEnabled = APIUtil.isMultiGroupAppSharingEnabled();
    }

    /**
     * Method to get the instance of the ApiMgtDAO.
     *
     * @return {@link org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO} instance
     */
    public static ApiMgtDAO getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new ApiMgtDAO();
        }

        return INSTANCE;
    }

    /**
     * Returns the Environments List for the TenantId.
     *
     * @param tenantDomain The tenant domain.
     * @return List of Environments.
     */
    public List<Environment> getAllEnvironments(String tenantDomain) throws APIManagementException {

        List<Environment> envList = new ArrayList<>();
        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(Constants.GET_ENVIRONMENT_BY_TENANT_SQL)) {
            prepStmt.setString(1, tenantDomain);
            try (ResultSet rs = prepStmt.executeQuery()) {
                while (rs.next()) {
                    Integer id = rs.getInt("ID");
                    String uuid = rs.getString("UUID");
                    String name = rs.getString("NAME");
                    String displayName = rs.getString("DISPLAY_NAME");
                    String description = rs.getString("DESCRIPTION");
                    String provider = rs.getString("PROVIDER");

                    Environment env = new Environment();
                    env.setId(id);
                    env.setUuid(uuid);
                    env.setName(name);
                    env.setDisplayName(displayName);
                    env.setDescription(description);
                    env.setProvider(provider);
                    env.setVhosts(getVhostGatewayEnvironments(connection, id));
                    envList.add(env);
                }
            }
        } catch (SQLException e) {
            handleException("Failed to get Environments in tenant domain: " + tenantDomain, e);
        }
        return envList;
    }


    /**
     * Adds an API Product revision record to the database
     *
     * @param apiRevision content of the revision
     * @throws APIManagementException if an error occurs when adding a new API revision
     */
    public void addAPIProductRevision(APIRevision apiRevision) throws APIManagementException {

        try (Connection connection = APIMgtDBUtil.getConnection()) {
            try {
                connection.setAutoCommit(false);
                // Adding to AM_REVISION table
                PreparedStatement statement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.ADD_API_REVISION);
                statement.setInt(1, apiRevision.getId());
                statement.setString(2, apiRevision.getApiUUID());
                statement.setString(3, apiRevision.getRevisionUUID());
                statement.setString(4, apiRevision.getDescription());
                statement.setString(5, apiRevision.getCreatedBy());
                statement.executeUpdate();

                // Retrieve API Product ID
                APIProductIdentifier apiProductIdentifier =
                        APIUtil.getAPIProductIdentifierFromUUID(apiRevision.getApiUUID());
                int apiId = getAPIID(apiProductIdentifier, connection);
                int tenantId =
                        APIUtil.getTenantId(APIUtil.replaceEmailDomainBack(apiProductIdentifier.getProviderName()));

                // Adding to AM_API_URL_MAPPING table
                PreparedStatement getURLMappingsStatement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.
                                GET_URL_MAPPINGS_WITH_SCOPE_AND_PRODUCT_ID_BY_PRODUCT_ID);
                getURLMappingsStatement.setInt(1, apiId);
                List<URITemplate> urlMappingList = new ArrayList<>();
                try (ResultSet rs = getURLMappingsStatement.executeQuery()) {
                    while (rs.next()) {
                        String script = null;
                        URITemplate uriTemplate = new URITemplate();
                        uriTemplate.setHTTPVerb(rs.getString(1));
                        uriTemplate.setAuthType(rs.getString(2));
                        uriTemplate.setUriTemplate(rs.getString(3));
                        uriTemplate.setThrottlingTier(rs.getString(4));
                        InputStream mediationScriptBlob = rs.getBinaryStream(5);
                        if (mediationScriptBlob != null) {
                            script = APIMgtDBUtil.getStringFromInputStream(mediationScriptBlob);
                        }
                        uriTemplate.setMediationScript(script);
                        if (!StringUtils.isEmpty(rs.getString(6))) {
                            Scope scope = new Scope();
                            scope.setKey(rs.getString(6));
                            uriTemplate.setScope(scope);
                        }
                        if (rs.getInt(7) != 0) {
                            // Adding api id to uri template id just to store value
                            uriTemplate.setId(rs.getInt(7));
                        }
                        urlMappingList.add(uriTemplate);
                    }
                }

                Map<String, URITemplate> uriTemplateMap = new HashMap<>();
                for (URITemplate urlMapping : urlMappingList) {
                    if (urlMapping.getScope() != null) {
                        URITemplate urlMappingNew = urlMapping;
                        URITemplate urlMappingExisting = uriTemplateMap.get(urlMapping.getUriTemplate()
                                + urlMapping.getHTTPVerb());
                        if (urlMappingExisting != null && urlMappingExisting.getScopes() != null) {
                            if (!urlMappingExisting.getScopes().contains(urlMapping.getScope())) {
                                urlMappingExisting.setScopes(urlMapping.getScope());
                                uriTemplateMap.put(urlMappingExisting.getUriTemplate() + urlMappingExisting.getHTTPVerb(),
                                        urlMappingExisting);
                            }
                        } else {
                            urlMappingNew.setScopes(urlMapping.getScope());
                            uriTemplateMap.put(urlMappingNew.getUriTemplate() + urlMappingNew.getHTTPVerb(),
                                    urlMappingNew);
                        }
                    } else if (urlMapping.getId() != 0) {
                        URITemplate urlMappingExisting = uriTemplateMap.get(urlMapping.getUriTemplate()
                                + urlMapping.getHTTPVerb());
                        if (urlMappingExisting == null) {
                            uriTemplateMap.put(urlMapping.getUriTemplate() + urlMapping.getHTTPVerb(), urlMapping);
                        }
                    } else {
                        uriTemplateMap.put(urlMapping.getUriTemplate() + urlMapping.getHTTPVerb(), urlMapping);
                    }
                }

                PreparedStatement insertURLMappingsStatement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.INSERT_URL_MAPPINGS);
                for (URITemplate urlMapping : uriTemplateMap.values()) {
                    insertURLMappingsStatement.setInt(1, urlMapping.getId());
                    insertURLMappingsStatement.setString(2, urlMapping.getHTTPVerb());
                    insertURLMappingsStatement.setString(3, urlMapping.getAuthType());
                    insertURLMappingsStatement.setString(4, urlMapping.getUriTemplate());
                    insertURLMappingsStatement.setString(5, urlMapping.getThrottlingTier());
                    insertURLMappingsStatement.setString(6, apiRevision.getRevisionUUID());
                    insertURLMappingsStatement.addBatch();
                }
                insertURLMappingsStatement.executeBatch();

                // Add to AM_API_RESOURCE_SCOPE_MAPPING table and to AM_API_PRODUCT_MAPPING
                PreparedStatement getRevisionedURLMappingsStatement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.GET_REVISIONED_URL_MAPPINGS_ID);
                PreparedStatement insertScopeResourceMappingStatement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.INSERT_SCOPE_RESOURCE_MAPPING);
                PreparedStatement insertProductResourceMappingStatement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.INSERT_PRODUCT_REVISION_RESOURCE_MAPPING);
                for (URITemplate urlMapping : uriTemplateMap.values()) {
                    getRevisionedURLMappingsStatement.setInt(1, urlMapping.getId());
                    getRevisionedURLMappingsStatement.setString(2, apiRevision.getRevisionUUID());
                    getRevisionedURLMappingsStatement.setString(3, urlMapping.getHTTPVerb());
                    getRevisionedURLMappingsStatement.setString(4, urlMapping.getAuthType());
                    getRevisionedURLMappingsStatement.setString(5, urlMapping.getUriTemplate());
                    getRevisionedURLMappingsStatement.setString(6, urlMapping.getThrottlingTier());
                    if (urlMapping.getScopes() != null) {
                        try (ResultSet rs = getRevisionedURLMappingsStatement.executeQuery()) {
                            while (rs.next()) {
                                for (Scope scope : urlMapping.getScopes()) {
                                    insertScopeResourceMappingStatement.setString(1, scope.getKey());
                                    insertScopeResourceMappingStatement.setInt(2, rs.getInt(1));
                                    insertScopeResourceMappingStatement.setInt(3, tenantId);
                                    insertScopeResourceMappingStatement.addBatch();
                                }
                            }
                        }
                    }
                    try (ResultSet rs = getRevisionedURLMappingsStatement.executeQuery()) {
                        while (rs.next()) {
                            insertProductResourceMappingStatement.setInt(1, apiId);
                            insertProductResourceMappingStatement.setInt(2, rs.getInt(1));
                            insertProductResourceMappingStatement.setString(3, apiRevision.getRevisionUUID());
                            insertProductResourceMappingStatement.addBatch();
                        }
                    }

                }
                insertScopeResourceMappingStatement.executeBatch();
                insertProductResourceMappingStatement.executeBatch();

                // Adding to AM_API_CLIENT_CERTIFICATE
                PreparedStatement getClientCertificatesStatement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.GET_CLIENT_CERTIFICATES);
                getClientCertificatesStatement.setInt(1, apiId);
                List<ClientCertificateDTO> clientCertificateDTOS = new ArrayList<>();
                try (ResultSet rs = getClientCertificatesStatement.executeQuery()) {
                    while (rs.next()) {
                        ClientCertificateDTO clientCertificateDTO = new ClientCertificateDTO();
                        clientCertificateDTO.setAlias(rs.getString(1));
                        clientCertificateDTO.setCertificate(APIMgtDBUtil.getStringFromInputStream(rs.getBinaryStream(2)));
                        clientCertificateDTO.setTierName(rs.getString(3));
                        clientCertificateDTOS.add(clientCertificateDTO);
                    }
                }
                PreparedStatement insertClientCertificateStatement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.INSERT_CLIENT_CERTIFICATES);
                for (ClientCertificateDTO clientCertificateDTO : clientCertificateDTOS) {
                    insertClientCertificateStatement.setInt(1, tenantId);
                    insertClientCertificateStatement.setString(2, clientCertificateDTO.getAlias());
                    insertClientCertificateStatement.setInt(3, apiId);
                    insertClientCertificateStatement.setBinaryStream(4,
                            getInputStream(clientCertificateDTO.getCertificate()));
                    insertClientCertificateStatement.setBoolean(5, false);
                    insertClientCertificateStatement.setString(6, clientCertificateDTO.getTierName());
                    insertClientCertificateStatement.setString(7, apiRevision.getRevisionUUID());
                    insertClientCertificateStatement.addBatch();
                }
                insertClientCertificateStatement.executeBatch();

                // Adding to AM_GRAPHQL_COMPLEXITY table
                PreparedStatement getGraphQLComplexityStatement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.GET_GRAPHQL_COMPLEXITY);
                List<CustomComplexityDetails> customComplexityDetailsList = new ArrayList<>();
                getGraphQLComplexityStatement.setInt(1, apiId);
                try (ResultSet rs1 = getGraphQLComplexityStatement.executeQuery()) {
                    while (rs1.next()) {
                        CustomComplexityDetails customComplexityDetails = new CustomComplexityDetails();
                        customComplexityDetails.setType(rs1.getString("TYPE"));
                        customComplexityDetails.setField(rs1.getString("FIELD"));
                        customComplexityDetails.setComplexityValue(rs1.getInt("COMPLEXITY_VALUE"));
                        customComplexityDetailsList.add(customComplexityDetails);
                    }
                }

                PreparedStatement insertGraphQLComplexityStatement = connection
                        .prepareStatement(SQLConstants.APIRevisionSqlConstants.INSERT_GRAPHQL_COMPLEXITY);
                for (CustomComplexityDetails customComplexityDetails : customComplexityDetailsList) {
                    insertGraphQLComplexityStatement.setString(1, UUID.randomUUID().toString());
                    insertGraphQLComplexityStatement.setInt(2, apiId);
                    insertGraphQLComplexityStatement.setString(3, customComplexityDetails.getType());
                    insertGraphQLComplexityStatement.setString(4, customComplexityDetails.getField());
                    insertGraphQLComplexityStatement.setInt(5, customComplexityDetails.getComplexityValue());
                    insertGraphQLComplexityStatement.setString(6, apiRevision.getRevisionUUID());
                    insertGraphQLComplexityStatement.addBatch();
                }
                insertGraphQLComplexityStatement.executeBatch();
                updateLatestRevisionNumber(connection, apiRevision.getApiUUID(), apiRevision.getId());
                addAPIRevisionMetaData(connection, apiRevision.getApiUUID(), apiRevision.getRevisionUUID());
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                handleException("Failed to add API Revision entry of API Product UUID "
                        + apiRevision.getApiUUID(), e);
            }
        } catch (SQLException e) {
            handleException("Failed to add API Revision entry of API Product UUID "
                    + apiRevision.getApiUUID(), e);
        }
    }

    private void updateLatestRevisionNumber(Connection connection, String apiUUID, int revisionId) throws SQLException {

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(SQLConstants.UPDATE_REVISION_CREATED_BY_API_SQL)) {
            preparedStatement.setInt(1, revisionId);
            preparedStatement.setString(2, apiUUID);
            preparedStatement.executeUpdate();
        }
    }

    private void addAPIRevisionMetaData(Connection connection, String apiUUID, String revisionUUID) throws SQLException {

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(SQLConstants.ADD_API_REVISION_METADATA)) {
            preparedStatement.setString(1, apiUUID);
            preparedStatement.setString(2, revisionUUID);
            preparedStatement.setString(3, apiUUID);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * This method will read the result set and populate OperationPolicy object, which later will be set to the URI template.
     * This object has the information regarding the policy allocation
     *
     * @param rs Result set
     * @return OperationPolicy object
     * @throws APIManagementException
     * @throws SQLException
     */
    private OperationPolicy populateOperationPolicyWithRS(ResultSet rs) throws SQLException, APIManagementException {

        OperationPolicy operationPolicy = new OperationPolicy();
        operationPolicy.setPolicyName(rs.getString("POLICY_NAME"));
        operationPolicy.setPolicyVersion(rs.getString("POLICY_VERSION"));
        operationPolicy.setPolicyId(rs.getString("POLICY_UUID"));
        operationPolicy.setOrder(rs.getInt("POLICY_ORDER"));
        operationPolicy.setDirection(rs.getString("DIRECTION"));
        operationPolicy.setParameters(APIMgtDBUtil.convertJSONStringToMap(rs.getString("PARAMETERS")));
        return operationPolicy;
    }

    public int getAPIID(Identifier apiId, Connection connection) throws APIManagementException, SQLException {

        int id = -1;
        String getAPIQuery = SQLConstants.GET_API_ID_SQL;

        if (apiId instanceof APIProductIdentifier) {
            getAPIQuery = SQLConstants.GET_API_PRODUCT_ID_SQL;
        }

        try (PreparedStatement prepStmt = connection.prepareStatement(getAPIQuery)) {
            prepStmt.setString(1, APIUtil.replaceEmailDomainBack(apiId.getProviderName()));
            prepStmt.setString(2, apiId.getName());
            prepStmt.setString(3, apiId.getVersion());
            try (ResultSet rs = prepStmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("API_ID");
                }
                if (id == -1) {
                    String msg = "Unable to find the API: " + apiId + " in the database";
                    log.error(msg);
                    throw new APIManagementException(msg);
                }
            }
        }
        return id;
    }

    /**
     * To get the input stream from string.
     *
     * @param value : Relevant string that need to be converted to input stream.
     * @return input stream.
     */
    private InputStream getInputStream(String value) {

        byte[] cert = value.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(cert);
    }



    /**
     * Returns a list of vhosts belongs to the gateway environments
     *
     * @param connection DB connection
     * @param envId      Environment id.
     * @return list of vhosts belongs to the gateway environments.
     */
    private List<VHost> getVhostGatewayEnvironments(Connection connection, Integer envId) throws APIManagementException {

        List<VHost> vhosts = new ArrayList<>();
        try (PreparedStatement prepStmt = connection.prepareStatement(SQLConstants.GET_ENVIRONMENT_VHOSTS_BY_ID_SQL)) {
            prepStmt.setInt(1, envId);
            try (ResultSet rs = prepStmt.executeQuery()) {
                while (rs.next()) {
                    String host = rs.getString("HOST");
                    String httpContext = rs.getString("HTTP_CONTEXT");
                    Integer httpPort = rs.getInt("HTTP_PORT");
                    Integer httpsPort = rs.getInt("HTTPS_PORT");
                    Integer wsPort = rs.getInt("WS_PORT");
                    Integer wssPort = rs.getInt("WSS_PORT");

                    VHost vhost = new VHost();
                    vhost.setHost(host);
                    vhost.setHttpContext(httpContext == null ? "" : httpContext);
                    vhost.setHttpPort(httpPort);
                    vhost.setHttpsPort(httpsPort);
                    vhost.setWsPort(wsPort);
                    vhost.setWssPort(wssPort);
                    vhosts.add(vhost);
                }
            }
        } catch (SQLException e) {
            handleException("Failed to get gateway environments list of VHost: ", e);
        }
        return vhosts;
    }

    private void handleException(String msg, Throwable t) throws APIManagementException {

        log.error(msg, t);
        throw new APIManagementException(msg, t);
    }

    /**
     * Get API UUID by the API Identifier.
     *
     * @param identifier API Identifier
     * @return String UUID
     * @throws APIManagementException if an error occurs
     */
    public String getUUIDFromIdentifier(APIIdentifier identifier) throws APIManagementException {

        String uuid = null;
        String sql = Constants.GET_UUID_BY_IDENTIFIER_SQL;
        try (Connection connection = APIMgtDBUtil.getConnection()) {
            PreparedStatement prepStmt = connection.prepareStatement(sql);
            prepStmt.setString(1, APIUtil.replaceEmailDomainBack(identifier.getProviderName()));
            prepStmt.setString(2, identifier.getApiName());
            prepStmt.setString(3, identifier.getVersion());
            try (ResultSet resultSet = prepStmt.executeQuery()) {
                while (resultSet.next()) {
                    uuid = resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            handleException("Failed to get the UUID for API : " + identifier.getApiName() + '-'
                    + identifier.getVersion(), e);
        }
        return uuid;
    }

    /**
     * Get API UUID by passed parameters.
     *
     * @param provider Provider of the API
     * @param apiName  Name of the API
     * @param version  Version of the API
     * @return String UUID
     * @throws APIManagementException if an error occurs
     */
    public String getUUIDFromIdentifier(String provider, String apiName, String version) throws APIManagementException {

        String uuid = null;
        String sql = SQLConstants.GET_UUID_BY_IDENTIFIER_SQL;
        try (Connection connection = APIMgtDBUtil.getConnection()) {
            PreparedStatement prepStmt = connection.prepareStatement(sql);
            prepStmt.setString(1, APIUtil.replaceEmailDomainBack(provider));
            prepStmt.setString(2, apiName);
            prepStmt.setString(3, version);
            try (ResultSet resultSet = prepStmt.executeQuery()) {
                while (resultSet.next()) {
                    uuid = resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            handleException("Failed to get the UUID for API : ", e);
        }
        return uuid;
    }

    public int addApplication(Application application, String userId) throws APIManagementException {

        Connection conn = null;
        int applicationId = 0;
        String loginUserName = getLoginUserName(userId);
        try {
            conn = APIMgtDBUtil.getConnection();
            conn.setAutoCommit(false);
            applicationId = addApplication(application, loginUserName, conn);
            Subscriber subscriber = getSubscriber(userId);
            String tenantDomain = MultitenantUtils.getTenantDomain(subscriber.getName());

            if (multiGroupAppSharingEnabled) {
                updateGroupIDMappings(conn, applicationId, application.getGroupId(), tenantDomain);
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    log.error("Failed to rollback the add Application ", e1);
                }
            }
            handleException("Failed to add Application", e);
        } finally {
            APIMgtDBUtil.closeAllConnections(null, conn, null);
        }
        return applicationId;
    }

    /**
     * Adds a new record in AM_APPLICATION_GROUP_MAPPING for each group
     *
     * @param conn
     * @param applicationId
     * @param groupIdString group id values separated by commas
     * @return
     * @throws APIManagementException
     */
    private boolean updateGroupIDMappings(Connection conn, int applicationId, String groupIdString, String tenant)
            throws APIManagementException {

        boolean updateSuccessful = false;

        PreparedStatement removeMigratedGroupIdsStatement = null;
        PreparedStatement deleteStatement = null;
        PreparedStatement insertStatement = null;
        String deleteQuery = SQLConstants.REMOVE_GROUP_ID_MAPPING_SQL;
        String insertQuery = SQLConstants.ADD_GROUP_ID_MAPPING_SQL;

        try {
            // Remove migrated Group ID information so that it can be replaced by updated Group ID's that are now
            // being saved. This is done to ensure that there is no conflicting migrated Group ID data remaining
            removeMigratedGroupIdsStatement = conn.prepareStatement(SQLConstants.REMOVE_MIGRATED_GROUP_ID_SQL);
            removeMigratedGroupIdsStatement.setInt(1, applicationId);
            removeMigratedGroupIdsStatement.executeUpdate();

            deleteStatement = conn.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, applicationId);
            deleteStatement.executeUpdate();

            if (!StringUtils.isEmpty(groupIdString)) {

                String[] groupIdArray = groupIdString.split(",");

                insertStatement = conn.prepareStatement(insertQuery);
                for (String group : groupIdArray) {
                    insertStatement.setInt(1, applicationId);
                    insertStatement.setString(2, group);
                    insertStatement.setString(3, tenant);
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
            updateSuccessful = true;
        } catch (SQLException e) {
            updateSuccessful = false;
            handleException("Failed to update GroupId mappings ", e);
        } finally {
            APIMgtDBUtil.closeAllConnections(removeMigratedGroupIdsStatement, null, null);
            APIMgtDBUtil.closeAllConnections(deleteStatement, null, null);
            APIMgtDBUtil.closeAllConnections(insertStatement, null, null);
        }
        return updateSuccessful;
    }

    /**
     * This method used tot get Subscriber from subscriberId.
     *
     * @param subscriberName id
     * @return Subscriber
     * @throws APIManagementException if failed to get Subscriber from subscriber id
     */
    public Subscriber getSubscriber(String subscriberName) throws APIManagementException {

        Connection conn = null;
        Subscriber subscriber = null;
        PreparedStatement ps = null;
        ResultSet result = null;

        int tenantId = APIUtil.getTenantId(subscriberName);

        String sqlQuery = SQLConstants.GET_TENANT_SUBSCRIBER_SQL;
        if (forceCaseInsensitiveComparisons) {
            sqlQuery = SQLConstants.GET_TENANT_SUBSCRIBER_CASE_INSENSITIVE_SQL;
        }

        try {
            conn = APIMgtDBUtil.getConnection();

            ps = conn.prepareStatement(sqlQuery);
            ps.setString(1, subscriberName);
            ps.setInt(2, tenantId);
            result = ps.executeQuery();

            if (result.next()) {
                subscriber = new Subscriber(result.getString(APIConstants.SUBSCRIBER_FIELD_EMAIL_ADDRESS));
                subscriber.setEmail(result.getString("EMAIL_ADDRESS"));
                subscriber.setId(result.getInt("SUBSCRIBER_ID"));
                subscriber.setName(subscriberName);
                subscriber.setSubscribedDate(result.getDate(APIConstants.SUBSCRIBER_FIELD_DATE_SUBSCRIBED));
                subscriber.setTenantId(result.getInt("TENANT_ID"));
            }
        } catch (SQLException e) {
            handleException("Failed to get Subscriber for :" + subscriberName, e);
        } finally {
            APIMgtDBUtil.closeAllConnections(ps, conn, result);
        }
        return subscriber;
    }

    /**
     * returns a subscriber record for given username,tenant Id
     *
     * @param username   UserName
     * @param tenantId   Tenant Id
     * @param connection
     * @return Subscriber
     * @throws APIManagementException if failed to get subscriber
     */
    private Subscriber getSubscriber(String username, int tenantId, Connection connection)
            throws APIManagementException {

        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        Subscriber subscriber = null;
        String sqlQuery;

        if (forceCaseInsensitiveComparisons) {
            sqlQuery = SQLConstants.GET_SUBSCRIBER_CASE_INSENSITIVE_SQL;
        } else {
            sqlQuery = SQLConstants.GET_SUBSCRIBER_DETAILS_SQL;
        }

        try {
            prepStmt = connection.prepareStatement(sqlQuery);
            prepStmt.setString(1, username);
            prepStmt.setInt(2, tenantId);
            rs = prepStmt.executeQuery();

            if (rs.next()) {
                subscriber = new Subscriber(rs.getString("USER_ID"));
                subscriber.setEmail(rs.getString("EMAIL_ADDRESS"));
                subscriber.setId(rs.getInt("SUBSCRIBER_ID"));
                subscriber.setSubscribedDate(rs.getDate("DATE_SUBSCRIBED"));
                subscriber.setTenantId(rs.getInt("TENANT_ID"));
                return subscriber;
            }
        } catch (SQLException e) {
            handleException("Error when reading the application information from" + " the persistence store.", e);
        } finally {
            APIMgtDBUtil.closeAllConnections(prepStmt, null, rs);
        }
        return subscriber;
    }

    /**
     * @param application Application
     * @param userId      User Id
     * @throws APIManagementException if failed to add Application
     */
    public int addApplication(Application application, String userId, Connection conn)
            throws APIManagementException, SQLException {

        PreparedStatement ps = null;
        conn.setAutoCommit(false);
        ResultSet rs = null;

        int applicationId = 0;
        try {
            int tenantId = APIUtil.getTenantId(userId);

            //Get subscriber Id
            Subscriber subscriber = getSubscriber(userId, tenantId, conn);
            if (subscriber == null) {
                String msg = "Could not load Subscriber records for: " + userId;
                log.error(msg);
                throw new APIManagementException(msg);
            }
            //This query to update the AM_APPLICATION table
            String sqlQuery = SQLConstants.APP_APPLICATION_SQL;
            // Adding data to the AM_APPLICATION  table
            //ps = conn.prepareStatement(sqlQuery);
            ps = conn.prepareStatement(sqlQuery, new String[]{"APPLICATION_ID"});
            if (conn.getMetaData().getDriverName().contains("PostgreSQL")) {
                ps = conn.prepareStatement(sqlQuery, new String[]{"application_id"});
            }

            ps.setString(1, application.getName());
            ps.setInt(2, subscriber.getId());
            ps.setString(3, application.getTier());
            ps.setString(4, application.getCallbackUrl());
            ps.setString(5, application.getDescription());

            if (APIConstants.DEFAULT_APPLICATION_NAME.equals(application.getName())) {
                ps.setString(6, APIConstants.ApplicationStatus.APPLICATION_APPROVED);
            } else {
                ps.setString(6, APIConstants.ApplicationStatus.APPLICATION_CREATED);
            }

            String groupId = application.getGroupId();
            if (multiGroupAppSharingEnabled) {
                // setting an empty groupId since groupid's should be saved in groupId mapping table
                groupId = "";
            }
            ps.setString(7, groupId);
            ps.setString(8, subscriber.getName());

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(9, timestamp);
            ps.setTimestamp(10, timestamp);
            ps.setString(11, application.getUUID());
            ps.setString(12, String.valueOf(application.getTokenType()));
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            while (rs.next()) {
                applicationId = Integer.parseInt(rs.getString(1));
            }

            //Adding data to AM_APPLICATION_ATTRIBUTES table
            if (application.getApplicationAttributes() != null) {
                addApplicationAttributes(conn, application.getApplicationAttributes(), applicationId, tenantId);
            }
        } catch (SQLException e) {
            handleException("Failed to add Application", e);
        } finally {
            APIMgtDBUtil.closeAllConnections(ps, null, rs);
        }
        return applicationId;
    }

    private void addApplicationAttributes(Connection conn, Map<String, String> attributes, int applicationId,
                                          int tenantId)
            throws APIManagementException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (attributes != null) {
                ps = conn.prepareStatement(SQLConstants.ADD_APPLICATION_ATTRIBUTES_SQL);
                for (Map.Entry<String, String> attribute : attributes.entrySet()) {
                    if (StringUtils.isNotEmpty(attribute.getKey()) && StringUtils.isNotEmpty(attribute.getValue())) {
                        ps.setInt(1, applicationId);
                        ps.setString(2, attribute.getKey());
                        ps.setString(3, attribute.getValue());
                        ps.setInt(4, tenantId);
                        ps.addBatch();
                    }
                }
                int[] update = ps.executeBatch();
            }
        } catch (SQLException e) {
            handleException("Error in adding attributes of application with id: " + applicationId, e);
        } finally {
            APIMgtDBUtil.closeAllConnections(ps, null, rs);
        }
    }

    /**
     * identify the login username is primary or secondary
     *
     * @param userID
     * @return
     * @throws APIManagementException
     */
    private String getLoginUserName(String userID) throws APIManagementException {

        String primaryLogin = userID;
        if (isSecondaryLogin(userID)) {
            primaryLogin = getPrimaryLoginFromSecondary(userID);
        }
        return primaryLogin;
    }

    /**
     * Get the primaryLogin name using secondary login name. Primary secondary
     * Configuration is provided in the identitiy.xml. In the userstore, it is
     * users responsibility TO MAINTAIN THE SECONDARY LOGIN NAME AS UNIQUE for
     * each and every users. If it is not unique, we will pick the very first
     * entry from the userlist.
     *
     * @param login
     * @return
     * @throws APIManagementException
     */
    public String getPrimaryLoginFromSecondary(String login) throws APIManagementException {

        Map<String, Map<String, String>> loginConfiguration = ServiceHolder
                .getAPIManagerConfigurationService().getAPIManagerConfiguration().getLoginConfiguration();
        String claimURI, username = null;
        if (isUserLoggedInEmail(login)) {
            Map<String, String> emailConf = loginConfiguration.get(APIConstants.EMAIL_LOGIN);
            claimURI = emailConf.get(APIConstants.CLAIM_URI);
        } else {
            Map<String, String> userIdConf = loginConfiguration.get(APIConstants.USERID_LOGIN);
            claimURI = userIdConf.get(APIConstants.CLAIM_URI);
        }

        try {
            String[] user = RemoteUserManagerClient.getInstance().getUserList(claimURI,login);
            if (user.length > 0) {
                username = user[0];
            }
        } catch (Exception e) {

            handleException("Error while retrieving the primaryLogin name using secondary loginName : " + login, e);
        }
        return username;
    }

    /**
     * Return the existing versions for the given api name for the provider
     *
     * @param apiName     api name
     * @param apiProvider provider
     * @return set version
     * @throws APIManagementException
     */
    public Set<String> getAPIVersions(String apiName, String apiProvider) throws APIManagementException {

        Set<String> versions = new HashSet<String>();

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLConstants.GET_API_VERSIONS)) {
            statement.setString(1, APIUtil.replaceEmailDomainBack(apiProvider));
            statement.setString(2, apiName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                versions.add(resultSet.getString("API_VERSION"));
            }
        } catch (SQLException e) {
            handleException("Error while retrieving versions for api " + apiName + " for the provider " + apiProvider,
                    e);
        }
        return versions;
    }

    /**
     * Check whether the given scope key is already assigned locally to another API which are different from the given
     * API or its versioned APIs under given tenant.
     *
     * @param apiIdentifier API Identifier
     * @param scopeKey      candidate scope key
     * @param tenantId      tenant id
     * @return true if the scope key is already available
     * @throws APIManagementException if failed to check the context availability
     */
    public boolean isScopeKeyAssignedLocally(APIIdentifier apiIdentifier, String scopeKey, int tenantId)
            throws APIManagementException {

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLConstants.IS_SCOPE_ATTACHED_LOCALLY)) {
            statement.setString(1, scopeKey);
            statement.setInt(2, tenantId);
            statement.setInt(3, tenantId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String provider = rs.getString("API_PROVIDER");
                    String apiName = rs.getString("API_NAME");
                    // Check if the provider name and api name is same.
                    // Return false if we're attaching the scope to another version of the API.
                    return !(provider.equals(APIUtil.replaceEmailDomainBack(apiIdentifier.getProviderName()))
                            && apiName.equals(apiIdentifier.getApiName()));
                }
            }
        } catch (SQLException e) {
            handleException("Failed to check scope key availability for: " + scopeKey, e);
        }
        return false;
    }

    /**
     * This method returns the set of APIs for given subscriber
     *
     * @param subscriber subscriber
     * @return Set<API>
     * @throws org.wso2.carbon.apimgt.api.APIManagementException if failed to get SubscribedAPIs
     */
    public Set<SubscribedAPI> getSubscribedAPIs(Subscriber subscriber, String groupingId)
            throws APIManagementException {

        Set<SubscribedAPI> subscribedAPIs = new LinkedHashSet<>();

        //identify subscribeduser used email/ordinalusername
        String subscribedUserName = getLoginUserName(subscriber.getName());
        subscriber.setName(subscribedUserName);

        String sqlQuery =
                appendSubscriptionQueryWhereClause(groupingId,
                        SQLConstants.GET_SUBSCRIBED_APIS_OF_SUBSCRIBER_SQL);

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sqlQuery);
             ResultSet result = getSubscriptionResultSet(groupingId, subscriber, ps)) {
            while (result.next()) {
                String apiType = result.getString("TYPE");

                if (APIConstants.API_PRODUCT.toString().equals(apiType)) {
                    APIProductIdentifier identifier =
                            new APIProductIdentifier(APIUtil.replaceEmailDomain(result.getString("API_PROVIDER")),
                                    result.getString("API_NAME"), result.getString("API_VERSION"));

                    SubscribedAPI subscribedAPI = new SubscribedAPI(subscriber, identifier);

                    initSubscribedAPIDetailed(connection, subscribedAPI, subscriber, result);
                    subscribedAPIs.add(subscribedAPI);
                } else {
                    APIIdentifier identifier = new APIIdentifier(APIUtil.replaceEmailDomain(result.getString
                            ("API_PROVIDER")), result.getString("API_NAME"),
                            result.getString("API_VERSION"));
                    SubscribedAPI subscribedAPI = new SubscribedAPI(subscriber, identifier);

                    initSubscribedAPIDetailed(connection,subscribedAPI, subscriber, result);
                    subscribedAPIs.add(subscribedAPI);
                }
            }
        } catch (SQLException e) {
            handleException("Failed to get SubscribedAPI of :" + subscriber.getName(), e);
        }

        return subscribedAPIs;
    }

    private void initSubscribedAPIDetailed(Connection connection, SubscribedAPI subscribedAPI, Subscriber subscriber, ResultSet result)
            throws SQLException, APIManagementException {

        subscribedAPI.setSubscriptionId(result.getInt("SUBS_ID"));
        subscribedAPI.setSubStatus(result.getString("SUB_STATUS"));
        subscribedAPI.setSubCreatedStatus(result.getString("SUBS_CREATE_STATE"));
        String tierName = result.getString(APIConstants.SUBSCRIPTION_FIELD_TIER_ID);
        String requestedTierName = result.getString(APIConstants.SUBSCRIPTION_FIELD_TIER_ID_PENDING);
        subscribedAPI.setTier(new Tier(tierName));
        subscribedAPI.setRequestedTier(new Tier(requestedTierName));
        subscribedAPI.setUUID(result.getString("SUB_UUID"));
        //setting NULL for subscriber. If needed, Subscriber object should be constructed &
        // passed in
        int applicationId = result.getInt("APP_ID");

        Application application = new Application(result.getString("APP_NAME"), subscriber);
        application.setId(result.getInt("APP_ID"));
        application.setTokenType(result.getString("APP_TOKEN_TYPE"));
        application.setCallbackUrl(result.getString("CALLBACK_URL"));
        application.setUUID(result.getString("APP_UUID"));

        if (multiGroupAppSharingEnabled) {
            application.setGroupId(getGroupId(connection, application.getId()));
            application.setOwner(result.getString("OWNER"));
        }

        subscribedAPI.setApplication(application);
    }

    /**
     * Fetches all the groups for a given application and creates a single string separated by comma
     *
     * @param applicationId
     * @return comma separated group Id String
     * @throws APIManagementException
     */
    private String getGroupId(Connection connection, int applicationId) throws SQLException {

        ArrayList<String> grpIdList = new ArrayList<String>();
        String sqlQuery = SQLConstants.GET_GROUP_ID_SQL;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, applicationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    grpIdList.add(resultSet.getString("GROUP_ID"));
                }
            }
        }
        return String.join(",", grpIdList);
    }



    private ResultSet getSubscriptionResultSet(String groupingId, Subscriber subscriber,
                                               PreparedStatement statement) throws SQLException {

        int tenantId = APIUtil.getTenantId(subscriber.getName());
        int paramIndex = 0;

        if (groupingId != null && !"null".equals(groupingId) && !groupingId.isEmpty()) {
            if (multiGroupAppSharingEnabled) {
                String tenantDomain = MultitenantUtils.getTenantDomain(subscriber.getName());
                String[] groupIDArray = groupingId.split(",");

                statement.setInt(++paramIndex, tenantId);
                for (String groupId : groupIDArray) {
                    statement.setString(++paramIndex, groupId);
                }
                statement.setString(++paramIndex, tenantDomain);
                statement.setString(++paramIndex, subscriber.getName());

            } else {
                statement.setInt(++paramIndex, tenantId);
                statement.setString(++paramIndex, groupingId);
                statement.setString(++paramIndex, subscriber.getName());
            }
        } else {
            statement.setInt(++paramIndex, tenantId);
            statement.setString(++paramIndex, subscriber.getName());
        }

        return statement.executeQuery();
    }


    private ResultSet getSubscriptionResultSet(String groupingId, Subscriber subscriber, String applicationName,
                                               PreparedStatement statement) throws SQLException {

        int tenantId = APIUtil.getTenantId(subscriber.getName());
        int paramIndex = 0;

        if (groupingId != null && !"null".equals(groupingId) && !groupingId.isEmpty()) {
            if (multiGroupAppSharingEnabled) {
                String tenantDomain = MultitenantUtils.getTenantDomain(subscriber.getName());

                String[] groupIDArray = groupingId.split(",");

                statement.setInt(++paramIndex, tenantId);
                statement.setString(++paramIndex, applicationName);
                for (String groupId : groupIDArray) {
                    statement.setString(++paramIndex, groupId);
                }
                statement.setString(++paramIndex, tenantDomain);
                statement.setString(++paramIndex, subscriber.getName());
            } else {
                statement.setInt(++paramIndex, tenantId);
                statement.setString(++paramIndex, applicationName);
                statement.setString(++paramIndex, groupingId);
                statement.setString(++paramIndex, subscriber.getName());
            }
        } else {
            statement.setInt(++paramIndex, tenantId);
            statement.setString(++paramIndex, applicationName);
            statement.setString(++paramIndex, subscriber.getName());
        }

        return statement.executeQuery();
    }

    private String appendSubscriptionQueryWhereClause(final String groupingId, String sqlQuery) {

        if (groupingId != null && !"null".equals(groupingId) && !groupingId.isEmpty()) {
            if (multiGroupAppSharingEnabled) {
                String[] groupIDArray = groupingId.split(",");
                List<String> questionMarks = new ArrayList<>(Collections.nCopies(groupIDArray.length, "?"));
                final String paramString = String.join(",", questionMarks);

                if (forceCaseInsensitiveComparisons) {
                    sqlQuery += " AND  ( (APP.APPLICATION_ID IN  (SELECT APPLICATION_ID " +
                            " FROM AM_APPLICATION_GROUP_MAPPING  " +
                            " WHERE GROUP_ID IN (" + paramString + ") AND TENANT = ?))" +
                            "  OR  ( LOWER(SUB.USER_ID) = LOWER(?) ))";
                } else {
                    sqlQuery += " AND  ( (APP.APPLICATION_ID IN (SELECT APPLICATION_ID FROM " +
                            "AM_APPLICATION_GROUP_MAPPING WHERE GROUP_ID IN (" + paramString + ") AND TENANT = ?))  " +
                            "OR  ( SUB.USER_ID = ? ))";
                }
            } else {
                if (forceCaseInsensitiveComparisons) {
                    sqlQuery += " AND (APP.GROUP_ID = ? OR ((APP.GROUP_ID='' OR APP.GROUP_ID IS NULL)" +
                            " AND LOWER(SUB.USER_ID) = LOWER(?)))";
                } else {
                    sqlQuery += " AND (APP.GROUP_ID = ? OR ((APP.GROUP_ID='' OR APP.GROUP_ID IS NULL)" +
                            " AND SUB.USER_ID = ?))";
                }
            }
        } else {
            if (forceCaseInsensitiveComparisons) {
                sqlQuery += " AND LOWER(SUB.USER_ID) = LOWER(?)  ";
            } else {
                sqlQuery += " AND  SUB.USER_ID = ? ";
            }
        }

        return sqlQuery;
    }


    /**
     * Return ids of the versions for the given name for the given provider
     *
     * @param apiName     api name
     * @param apiProvider provider
     * @return set ids
     * @throws APIManagementException
     */
    public List<API> getAllAPIVersions(String apiName, String apiProvider) throws APIManagementException {

        List<API> apiVersions = new ArrayList<API>();

        try (Connection connection = APIMgtDBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLConstants.GET_API_VERSIONS_UUID)) {
            statement.setString(1, APIUtil.replaceEmailDomainBack(apiProvider));
            statement.setString(2, apiName);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String version = resultSet.getString("API_VERSION");
                String status = resultSet.getString("STATUS");
                String versionTimestamp = resultSet.getString("VERSION_COMPARABLE");
                String context = resultSet.getString("CONTEXT");
                String contextTemplate = resultSet.getString("CONTEXT_TEMPLATE");

                String uuid = resultSet.getString("API_UUID");
                if (APIConstants.API_PRODUCT.equals(resultSet.getString("API_TYPE"))) {
                    // skip api products
                    continue;
                }
                API api = new API(new APIIdentifier(apiProvider, apiName,
                        version, uuid));
                api.setUuid(uuid);
                api.setStatus(status);
                api.setVersionTimestamp(versionTimestamp);
                api.setContext(context);
                api.setContextTemplate(contextTemplate);
                apiVersions.add(api);
            }
        } catch (SQLException e) {
            handleException("Error while retrieving versions for api " + apiName + " for the provider " + apiProvider,
                    e);
        }
        return apiVersions;
    }

    /**
     * Identify whether the loggedin user used his Primary Login name or Secondary login name
     *
     * @param userId
     * @return
     */
    private boolean isSecondaryLogin(String userId) {

        Map<String, Map<String, String>> loginConfiguration = ServiceHolder
                .getAPIManagerConfigurationService().getAPIManagerConfiguration().getLoginConfiguration();
        if (loginConfiguration.get(APIConstants.EMAIL_LOGIN) != null) {
            Map<String, String> emailConf = loginConfiguration.get(APIConstants.EMAIL_LOGIN);
            if ("true".equalsIgnoreCase(emailConf.get(APIConstants.PRIMARY_LOGIN))) {
                return !isUserLoggedInEmail(userId);
            }
            if ("false".equalsIgnoreCase(emailConf.get(APIConstants.PRIMARY_LOGIN))) {
                return isUserLoggedInEmail(userId);
            }
        }
        if (loginConfiguration.get(APIConstants.USERID_LOGIN) != null) {
            Map<String, String> userIdConf = loginConfiguration.get(APIConstants.USERID_LOGIN);
            if ("true".equalsIgnoreCase(userIdConf.get(APIConstants.PRIMARY_LOGIN))) {
                return isUserLoggedInEmail(userId);
            }
            if ("false".equalsIgnoreCase(userIdConf.get(APIConstants.PRIMARY_LOGIN))) {
                return !isUserLoggedInEmail(userId);
            }
        }
        return false;
    }

    /**
     * Identify whether the loggedin user used his ordinal username or email
     *
     * @param userId
     * @return
     */
    private boolean isUserLoggedInEmail(String userId) {

        return userId.contains("@");
    }

    /**
     * Get API Product UUID by the API Product Identifier.
     *
     * @param identifier API Product Identifier
     * @return String UUID
     * @throws APIManagementException if an error occurs
     */
    public String getUUIDFromIdentifier(APIProductIdentifier identifier) throws APIManagementException {

        String uuid = null;
        String sql = SQLConstants.GET_UUID_BY_IDENTIFIER_SQL;
        try (Connection connection = APIMgtDBUtil.getConnection()) {
            PreparedStatement prepStmt = connection.prepareStatement(sql);
            prepStmt.setString(1, APIUtil.replaceEmailDomainBack(identifier.getProviderName()));
            prepStmt.setString(2, identifier.getName());
            prepStmt.setString(3, identifier.getVersion());
            try (ResultSet resultSet = prepStmt.executeQuery()) {
                while (resultSet.next()) {
                    uuid = resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            handleException("Failed to retrieve the UUID for the API Product : " + identifier.getName() + '-'
                    + identifier.getVersion(), e);
        }
        return uuid;
    }

    /**
     * Returns all the scopes assigned for given apis
     *
     * @param apiIdsString list of api ids separated by commas
     * @return Map<String, Set < String>> set of scope keys for each apiId
     * @throws APIManagementException
     */
    public Map<String, Set<String>> getScopesForAPIS(String apiIdsString) throws APIManagementException {

        Map<String, Set<String>> apiScopeSet = new HashMap();

        try (Connection conn = APIMgtDBUtil.getConnection()) {

            String sqlQuery = Constants.GET_SCOPES_FOR_API_LIST;

            if (conn.getMetaData().getDriverName().contains("Oracle")) {
                sqlQuery = Constants.GET_SCOPES_FOR_API_LIST_ORACLE;
            }

            // apids are retrieved from the db so no need to protect for sql injection
            sqlQuery = sqlQuery.replace("$paramList", apiIdsString);

            try (PreparedStatement ps = conn.prepareStatement(sqlQuery);
                 ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    String scopeKey = resultSet.getString(1);
                    String apiId = resultSet.getString(2);
                    Set<String> scopeList = apiScopeSet.get(apiId);
                    if (scopeList == null) {
                        scopeList = new LinkedHashSet<>();
                        scopeList.add(scopeKey);
                        apiScopeSet.put(apiId, scopeList);
                    } else {
                        scopeList.add(scopeKey);
                        apiScopeSet.put(apiId, scopeList);
                    }
                }
            }
        } catch (SQLException e) {
            handleException("Failed to retrieve api scopes ", e);
        }
        return apiScopeSet;
    }

}
