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

package org.wso2.carbon.apimgt.migration.migrator.v320.dao;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.model.Scope;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.dto.APIInfoDTO;
import org.wso2.carbon.identity.core.util.IdentityIOStreamUtils;
import org.wso2.carbon.identity.oauth.common.OAuthConstants;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApiMgtDAO {
    private static final Log log = LogFactory.getLog(ApiMgtDAO.class);

    private static ApiMgtDAO INSTANCE = null;

    private static final String SELECT_SCOPES_QUERY_LEFT = "SELECT IDN_OAUTH2_SCOPE.SCOPE_ID AS SCOPE_ID," +
            "IDN_OAUTH2_SCOPE.NAME AS SCOPE_KEY,IDN_OAUTH2_SCOPE.DISPLAY_NAME AS DISPLAY_NAME,IDN_OAUTH2_SCOPE" +
            ".DESCRIPTION AS DESCRIPTION,IDN_OAUTH2_SCOPE.TENANT_ID AS TENANT_ID," +
            "SCOPE_TYPE AS SCOPE_TYPE,IDN_OAUTH2_SCOPE_BINDING.SCOPE_BINDING AS SCOPE_BINDING " +
            "FROM IDN_OAUTH2_SCOPE LEFT JOIN IDN_OAUTH2_SCOPE_BINDING ON IDN_OAUTH2_SCOPE" +
            ".SCOPE_ID=IDN_OAUTH2_SCOPE_BINDING.SCOPE_ID WHERE IDN_OAUTH2_SCOPE.SCOPE_TYPE = 'OAUTH2' " +
            "AND IDN_OAUTH2_SCOPE.NAME NOT IN (SCOPE_SKIP_LIST)";
    private static final String IDENTITY_PATH = "identity";
    private static final String NAME = "name";
    private static final String UPDATE_API_TYPE_SQL = "UPDATE AM_API SET API_TYPE = ? "
            + "WHERE API_PROVIDER = ? AND API_NAME = ? AND API_VERSION = ?";

    public static ApiMgtDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApiMgtDAO();
        }
        return INSTANCE;
    }

    public Map<Integer, Map<String, Scope>> migrateIdentityScopes(List<String> identityScopes) throws APIMigrationException {
        String query = SELECT_SCOPES_QUERY_LEFT;
        Map<Integer, Map<String, Scope>> scopesMap = new HashMap<>();
        query = query.replaceAll("SCOPE_SKIP_LIST",
                StringUtils.repeat("?", ",", identityScopes.size()));
        try (Connection connection = APIMgtDBUtil.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                for (int i = 0; i < identityScopes.size(); i++) {
                    preparedStatement.setString(i + 1, identityScopes.get(i));
                }
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int scopeId = resultSet.getInt("SCOPE_ID");
                        String scopeKey = resultSet.getString("SCOPE_KEY");
                        String displayName = resultSet.getString("DISPLAY_NAME");
                        String description = resultSet.getString("DESCRIPTION");
                        int tenantId = resultSet.getInt("TENANT_ID");
                        String scopeBinding = resultSet.getString("SCOPE_BINDING");
                        Map<String, Scope> scopeMap = scopesMap.computeIfAbsent(tenantId,
                                k -> new HashMap<>());
                        Scope scope = scopeMap.get(scopeKey);
                        if (scope == null) {
                            scope = new Scope();
                            scope.setId(Integer.toString(scopeId));
                            scope.setKey(scopeKey);
                            scope.setName(displayName);
                            scope.setDescription(description);
                            scopeMap.put(scopeKey, scope);
                        }
                        String roles = scope.getRoles();
                        if (StringUtils.isNotEmpty(scopeBinding)) {
                            if (StringUtils.isEmpty(roles)) {
                                scope.setRoles(scopeBinding);
                            } else {
                                scope.setRoles(scope.getRoles().concat(",").concat(scopeBinding));
                            }
                        }
                    }
                    return scopesMap;
                }
            }
        } catch (SQLException e) {
            throw new APIMigrationException("WSO2 API-M Migration Task : Database error while migrating identity"
                    + " scopes ", e);
        }
    }

    public boolean isScopesMigrated() throws APIMigrationException {

        try (Connection connection = APIMgtDBUtil.getConnection()) {
            try (PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT 1 FROM AM_SCOPE WHERE SCOPE_TYPE = ?")) {
                preparedStatement.setString(1, APIConstants.DEFAULT_SCOPE_TYPE);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new APIMigrationException("WSO2 API-M Migration Task : Error while retrieving database connection", e);
        }
        return false;
    }

    public List<String> retrieveIdentityScopes() {

        List<String> scopes = new ArrayList<>();
        String configDirPath = CarbonUtils.getCarbonConfigDirPath();
        String confXml = Paths.get(configDirPath, IDENTITY_PATH, OAuthConstants.OAUTH_SCOPE_BINDING_PATH)
                .toString();
        File configFile = new File(confXml);
        if (!configFile.exists()) {
            log.warn("WSO2 API-M Migration Task : OAuth scope binding file is not present at: " + confXml);
            return new ArrayList<>();
        }

        XMLStreamReader parser = null;
        InputStream stream = null;

        try {
            stream = new FileInputStream(configFile);
            parser = XMLInputFactory.newInstance()
                    .createXMLStreamReader(stream);
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            OMElement documentElement = builder.getDocumentElement();
            Iterator iterator = documentElement.getChildElements();
            while (iterator.hasNext()) {
                OMElement omElement = (OMElement) iterator.next();
                String scopeName = omElement.getAttributeValue(new QName(NAME));
                scopes.add(scopeName);
            }
        } catch (XMLStreamException e) {
            log.warn("WSO2 API-M Migration Task : Error while parsing scope config.", e);
        } catch (FileNotFoundException e) {
            log.warn("WSO2 API-M Migration Task : Error while loading scope config.", e);
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
                if (stream != null) {
                    IdentityIOStreamUtils.closeInputStream(stream);
                }
            } catch (XMLStreamException e) {
                log.error("WSO2 API-M Migration Task : Error while closing XML stream", e);
            }
        }
        return scopes;
    }

    /**
     * This method is used to update the API_TYPE in AM_API in the DB using API details
     *
     * @param apiInfoDTOList API Information list
     * @param tenantId       tenant ID
     * @param tenantDomain   tenant domain
     * @throws APIMigrationException Migration Exception
     */
    public void updateApiType(List<APIInfoDTO> apiInfoDTOList, int tenantId, String tenantDomain) throws APIMigrationException {
        try (Connection connection = APIMgtDBUtil.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_API_TYPE_SQL)) {
                for (APIInfoDTO apiInfoDTO : apiInfoDTOList) {
                    statement.setString(1, apiInfoDTO.getType());
                    statement.setString(2, apiInfoDTO.getApiProvider());
                    statement.setString(3, apiInfoDTO.getApiName());
                    statement.setString(4, apiInfoDTO.getApiVersion());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();

                log.info("WSO2 API-M Migration Task : Successfully updated API_TYPE in AM_API table for tenant:" +
                        tenantId + '(' + tenantDomain + ") " + "with below changes");
                apiInfoDTOList.stream().forEach((apiInfoDTO) -> {
                    log.info("WSO2 API-M Migration Task : API_TYPE of " + apiInfoDTO.getApiProvider() + "-"
                            + apiInfoDTO.getApiName() + "-" + apiInfoDTO.getApiVersion() + " was updated as "
                            + apiInfoDTO.getType());
                });
            } catch (SQLException e) {
                connection.rollback();
                throw new APIMigrationException("WSO2 API-M Migration Task : SQLException while updating API_TYPE for "
                        + "APIs in tenant:" + tenantId + '(' + tenantDomain + ')', e);
            }
        } catch (SQLException e) {
            throw new APIMigrationException("WSO2 API-M Migration Task : SQLException while updating API_TYPE for APIs "
                    + "in tenant:" + tenantId + '(' + tenantDomain + ')', e);
        }

    }
}
