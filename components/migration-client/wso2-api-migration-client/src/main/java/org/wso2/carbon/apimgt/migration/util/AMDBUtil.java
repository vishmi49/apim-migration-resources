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


package org.wso2.carbon.apimgt.migration.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.migration.client.MigrationDBCreator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class AMDBUtil {
    private static final Log log = LogFactory.getLog(AMDBUtil.class);

    public static void runSQLScript(String sqlScriptPath, boolean isPathProvided) throws SQLException {
        log.info("Running SQL script in " + sqlScriptPath + " started");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        try {
            connection = APIMgtDBUtil.getConnection();
            connection.setAutoCommit(false);
            String dbType = MigrationDBCreator.getDatabaseType(connection);
            String dbScriptPath;
            if (isPathProvided) {
                dbScriptPath = sqlScriptPath;
            } else {
                dbScriptPath = sqlScriptPath +  dbType + ".sql";
            }
            InputStream is = new FileInputStream(dbScriptPath);
            List<String> sqlStatements = readSQLStatements(is, dbType);
            for (String sqlStatement : sqlStatements) {
                log.debug("SQL to be executed : " + sqlStatement);
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
            log.error("Error occurred while migrating databases", e);
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
        log.info("Running SQL script completed successfully.");
    }

    private static List<String> readSQLStatements(InputStream is, String dbType) {
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
            log.error("Error while reading SQL statements from stream", e);
        }
        return sqlStatements;
    }
}
