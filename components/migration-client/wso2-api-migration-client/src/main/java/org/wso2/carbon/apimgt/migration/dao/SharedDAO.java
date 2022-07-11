/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.apimgt.migration.dao;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.client.MigrationDBCreator;
import org.wso2.carbon.apimgt.migration.dto.UserRoleFromPermissionDTO;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.SharedDBUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class represent the SharedDAO.
 */
public class SharedDAO {
    private static final Log log = LogFactory.getLog(SharedDAO.class);
    private static SharedDAO INSTANCE = null;

    private SharedDAO() {
    }

    public void runSQLScript(String sqlScriptPath) throws SQLException {
        log.info("WSO2 API-M Migration Task : Running SQL script at " + sqlScriptPath);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        try {
            connection = SharedDBUtil.getConnection();
            connection.setAutoCommit(false);
            String dbType = MigrationDBCreator.getDatabaseType(connection);
            InputStream is = new FileInputStream(sqlScriptPath);
            List<String> sqlStatements = readSQLStatements(is, dbType);
            for (String sqlStatement : sqlStatements) {
                log.debug("WSO2 API-M Migration Task : SQL to be executed : " + sqlStatement);
                if (Constants.DB_TYPE_ORACLE.equals(dbType)) {
                    statement = connection.createStatement();
                    statement.executeUpdate(sqlStatement);
                } else {
                    preparedStatement = connection.prepareStatement(sqlStatement);
                    preparedStatement.execute();
                }
            }
            connection.commit();
        }  catch (Exception e) {
            /* MigrationDBCreator extends from org.wso2.carbon.utils.dbcreator.DatabaseCreator and in the super class
            method getDatabaseType throws generic Exception */
            log.error("WSO2 API-M Migration Task : Error occurred while migrating databases", e);
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        log.info("WSO2 API-M Migration Task : Successfully executed SQL script at " + sqlScriptPath);
    }

    private List<String> readSQLStatements(InputStream is, String dbType) {
        List<String> sqlStatements = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String sqlQuery = "";
            boolean isFoundQueryEnd = false;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("//") || line.startsWith("--")) {
                    continue;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(line);
                if (stringTokenizer.hasMoreTokens()) {
                    String token = stringTokenizer.nextToken();
                    if ("REM".equalsIgnoreCase(token)) {
                        continue;
                    }
                }
                if (line.contains("\\n")) {
                    line = line.replace("\\n", "");
                }
                sqlQuery += ' ' + line;
                if (line.contains(";")) {
                    isFoundQueryEnd = true;
                }
                if (org.wso2.carbon.apimgt.migration.util.Constants.DB_TYPE_ORACLE.equals(dbType)) {
                    isFoundQueryEnd = "/".equals(line.trim());
                    sqlQuery = sqlQuery.replaceAll("/", "");
                }
                if (org.wso2.carbon.apimgt.migration.util.Constants.DB_TYPE_DB2.equals(dbType)) {
                    sqlQuery = sqlQuery.replace(";", "");
                }
                if (isFoundQueryEnd) {
                    if (sqlQuery.length() > 0) {
                        if (log.isDebugEnabled()) {
                            log.debug("SQL to be executed : " + sqlQuery);
                        }
                        sqlStatements.add(sqlQuery.trim());
                    }
                    // Reset variables to read next SQL
                    sqlQuery = "";
                    isFoundQueryEnd = false;
                }
            }
            bufferedReader.close();
        }  catch (IOException e) {
            log.error("WSO2 API-M Migration Task : Error while reading SQL statements from stream", e);
        }
        return sqlStatements;
    }

    public List<UserRoleFromPermissionDTO> getRoleNamesMatchingPermission(String permission, int tenantId)
            throws APIMigrationException {
        List<UserRoleFromPermissionDTO> userRoleFromPermissionList = new ArrayList<UserRoleFromPermissionDTO>();

        String sqlQuery =
                " SELECT " +
                "   UM_ROLE_NAME, UM_DOMAIN_NAME " +
                " FROM "+
                "   UM_ROLE_PERMISSION, UM_PERMISSION, UM_DOMAIN " +
                " WHERE " +
                "   UM_ROLE_PERMISSION.UM_PERMISSION_ID=UM_PERMISSION.UM_ID " +
                "   AND " +
                "   UM_ROLE_PERMISSION.UM_DOMAIN_ID=UM_DOMAIN.UM_DOMAIN_ID " +
                "   AND " +
                "   UM_RESOURCE_ID = ? " +
                "   AND " +
                "   UM_ROLE_PERMISSION.UM_TENANT_ID = ?";

        try (Connection conn = SharedDBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            ps.setString(1, permission);
            ps.setInt(2, tenantId);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    String userRoleName = resultSet.getString(Constants.UM_ROLE_NAME);
                    String userRoleDomainName = resultSet.getString(Constants.UM_DOMAIN_NAME);
                    UserRoleFromPermissionDTO userRoleFromPermissionDTO = new UserRoleFromPermissionDTO();
                    userRoleFromPermissionDTO.setUserRoleName(userRoleName);
                    userRoleFromPermissionDTO.setUserRoleDomainName(userRoleDomainName);
                    userRoleFromPermissionList.add(userRoleFromPermissionDTO);

                    log.info("WSO2 API-M Migration Task :  User role name: " + userRoleName + ", User domain name: "
                            + userRoleDomainName + " retrieved for " + tenantId);
                }
            } catch (SQLException e) {
                throw new APIMigrationException("WSO2 API-M Migration Task : Failed to get the result set.", e);
            }
        } catch (SQLException e) {
            throw new APIMigrationException("WSO2 API-M Migration Task : Failed to get Roles matching the permission "
                    + permission + " and tenant " + tenantId, e);
        }
        return userRoleFromPermissionList;
    }

    public List<UserRoleFromPermissionDTO> getRoleNamesMatchingPermissions(String permissions, int tenantId) throws APIMigrationException {
        List<UserRoleFromPermissionDTO> userRoleFromPermissionList = new ArrayList<UserRoleFromPermissionDTO>();

        String sqlQuery =
                " SELECT " +
                "   DISTINCT UM_ROLE_NAME, UM_DOMAIN_NAME " +
                " FROM " +
                "   UM_ROLE_PERMISSION, UM_PERMISSION, UM_DOMAIN " +
                " WHERE " +
                "   UM_ROLE_PERMISSION.UM_PERMISSION_ID=UM_PERMISSION.UM_ID " +
                "   AND " +
                "   UM_ROLE_PERMISSION.UM_DOMAIN_ID=UM_DOMAIN.UM_DOMAIN_ID " +
                "   AND " +
                "   UM_RESOURCE_ID IN (" + permissions + ")" +
                "   AND " +
                "   UM_ROLE_PERMISSION.UM_TENANT_ID = ?";

        try (Connection conn = SharedDBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            ps.setInt(1, tenantId);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    String userRoleName = resultSet.getString(Constants.UM_ROLE_NAME);
                    String userRoleDomainName = resultSet.getString(Constants.UM_DOMAIN_NAME);
                    UserRoleFromPermissionDTO userRoleFromPermissionDTO = new UserRoleFromPermissionDTO();
                    userRoleFromPermissionDTO.setUserRoleName(userRoleName);
                    userRoleFromPermissionDTO.setUserRoleDomainName(userRoleDomainName);
                    userRoleFromPermissionList.add(userRoleFromPermissionDTO);

                    log.info("WSO2 API-M Migration Task : User role name: " + userRoleName + ", User domain name: "
                            + userRoleDomainName + " retrieved for " + tenantId);
                }
            } catch (SQLException e) {
                throw new APIMigrationException("WSO2 API-M Migration Task : Failed to get the result set.", e);
            }
        } catch (SQLException e) {
            throw new APIMigrationException("WSO2 API-M Migration Task : Failed to get Roles matching the permission "
                    + permissions + " and tenant " + tenantId, e);
        }
        return userRoleFromPermissionList;
    }

    /**
     * Method to get the instance of the SharedDAO.
     *
     * @return {@link SharedDAO} instance
     */
    public static SharedDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SharedDAO();
        }
        return INSTANCE;
    }
}
