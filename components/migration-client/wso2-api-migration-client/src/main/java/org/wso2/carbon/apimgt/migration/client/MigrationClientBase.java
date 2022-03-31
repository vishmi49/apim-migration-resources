/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.apimgt.migration.client;

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
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.internal.APIManagerComponent;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.FileUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;

import static org.wso2.carbon.apimgt.impl.utils.APIUtil.getTenantDomainFromTenantId;
import static org.wso2.carbon.apimgt.migration.util.Constants.*;

public class MigrationClientBase {
    private static final Log log = LogFactory.getLog(MigrationClientBase.class);
    private List<Tenant> tenantsArray;
    private static final String IS_MYSQL_SESION_MODE_EXISTS = "SELECT COUNT(@@SESSION.sql_mode)";
    private static final String GET_MYSQL_SESSION_MODE = "SELECT @@SESSION.sql_mode AS MODE";
    private  static final String NO_ZERO_DATE_MODE = "NO_ZERO_DATE";
    private static final String MIGRATION = "Migration";
    private static final String VERSION_3 = "3.0.0";
    private static final String META = "Meta";
    private final String V400 = "4.0.0";
    private final String V320 = "3.2.0";
    private String tenantArguments;
    private String blackListTenantArguments;
    private final String tenantRange;
    private final TenantManager tenantManager;

    public MigrationClientBase(String tenantArguments, String blackListTenantArguments, String tenantRange,
            TenantManager tenantManager)
            throws UserStoreException {

        this.tenantManager = tenantManager;
        this.tenantRange = tenantRange;
        if (tenantArguments != null) {  // Tenant arguments have been provided so need to load specific ones
            tenantArguments = tenantArguments.replaceAll("\\s", ""); // Remove spaces and tabs

            tenantsArray = new ArrayList<>();

            buildTenantList(tenantManager, tenantsArray, tenantArguments);
            this.tenantArguments = tenantArguments;
        } else if (blackListTenantArguments != null) {
            blackListTenantArguments = blackListTenantArguments.replaceAll("\\s", ""); // Remove spaces and tabs

            List<Tenant> blackListTenants = new ArrayList<>();
            buildTenantList(tenantManager, blackListTenants, blackListTenantArguments);
            this.blackListTenantArguments = blackListTenantArguments;

            List<Tenant> allTenants = new ArrayList<>(Arrays.asList(tenantManager.getAllTenants()));
            Tenant superTenant = new Tenant();
            superTenant.setDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            superTenant.setId(MultitenantConstants.SUPER_TENANT_ID);
            allTenants.add(superTenant);

            tenantsArray = new ArrayList<>();

            for (Tenant tenant : allTenants) {
                boolean isBlackListed = false;
                for (Tenant blackListTenant : blackListTenants) {
                    if (blackListTenant.getId() == tenant.getId()) {
                        isBlackListed = true;
                        break;
                    }
                }

                if (!isBlackListed) {
                    tenantsArray.add(tenant);
                }
            }
        } else if (tenantRange != null) {
            tenantsArray = new ArrayList<Tenant>();
            int l, u;
            try {
                l = Integer.parseInt(tenantRange.split("-")[0].trim());
                u = Integer.parseInt(tenantRange.split("-")[1].trim());
            } catch (Exception e) {
                throw new UserStoreException("TenantRange argument is not properly set. use format 1-12", e);
            }
            log.debug("no of Tenants " + tenantManager.getAllTenants().length);
            int lastIndex = tenantManager.getAllTenants().length - 1;
            log.debug("last Tenant id " + tenantManager.getAllTenants()[lastIndex].getId());
            for (Tenant t : tenantManager.getAllTenants()) {
                if (t.getId() > l && t.getId() < u) {
                    log.debug("using tenants " + t.getDomain() + "(" + t.getId() + ")");
                    tenantsArray.add(t);
                }
            }
        } else {  // Load all tenants
            tenantsArray = new ArrayList<>(Arrays.asList(tenantManager.getAllTenants()));
            Tenant superTenant = new Tenant();
            superTenant.setDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            superTenant.setId(MultitenantConstants.SUPER_TENANT_ID);
            tenantsArray.add(superTenant);
        }
        setAdminUserName(tenantManager);
    }

    /**
     *
     * @param registryService registryService
     * @param migrateFromVersion migrateFromVersion
     * @return
     */
    public TreeMap<String, MigrationClient> getMigrationServiceList(RegistryService registryService,
            String migrateFromVersion) {

        HashMap<String, MigrationClient> serviceList = new HashMap<>();

        MigrateFrom400 migrateFrom400 = null;
        try {
            migrateFrom400 = new MigrateFrom400(tenantArguments, blackListTenantArguments, tenantRange,
                    registryService, tenantManager);
        } catch (UserStoreException e) {
            log.error("User store  exception occurred while creating 400 migration client", e);
        }
        serviceList.put(V400, migrateFrom400);

        if (V320.equals(migrateFromVersion)) {
            MigrateFrom320 migrateFrom320 = null;
            try {
                migrateFrom320 = new MigrateFrom320(tenantArguments, blackListTenantArguments, tenantRange,
                        registryService, tenantManager);
            } catch (UserStoreException e) {
                log.error("User store  exception occurred while creating 320 migration client", e);
            }
            serviceList.put(V320, migrateFrom320);
        }

        return new TreeMap<>(serviceList);
    }

    /**
     *
     * @param migrationServiceList
     * @param continueFromStep
     * @throws APIMigrationException
     * @throws SQLException
     */
    public void doMigration(TreeMap<String, MigrationClient> migrationServiceList, String continueFromStep)
            throws APIMigrationException, SQLException {

        if (continueFromStep == null) {
            continueFromStep = All_STEPS;
        }

        for (Map.Entry<String, MigrationClient> service : migrationServiceList.entrySet()) {
            MigrationClient serviceClient = service.getValue();
            switch (continueFromStep) {
            case REGISTRY_RESOURCE_MIGRATION:
                registryResourceMigration(serviceClient);
                updateScopeRoleMappings(serviceClient);
                migrateTenantConfToDB(serviceClient);
                registryDataPopulation(serviceClient);
                break;
            case SCOPE_ROLE_MAPPING_MIGRATION:
                updateScopeRoleMappings(serviceClient);
                migrateTenantConfToDB(serviceClient);
                registryDataPopulation(serviceClient);
                break;
            case TENANT_CONF_MIGRATION:
                migrateTenantConfToDB(serviceClient);
                registryDataPopulation(serviceClient);
                break;
            case REGISTRY_DATA_POPULATION:
                registryDataPopulation(serviceClient);
                break;
            case All_STEPS:
                databaseMigration(serviceClient);
                registryResourceMigration(serviceClient);
                updateScopeRoleMappings(serviceClient);
                migrateTenantConfToDB(serviceClient);
                registryDataPopulation(serviceClient);
            default:
                log.info("The step: " + continueFromStep + " is not defined");
            }
        }
    }

    public void doValidation(TreeMap<String, MigrationClient> migrationServiceList, String runPreMigrationStep)
            throws APIMigrationException {
        log.info("Executing pre migration step ..........");
        for (Map.Entry<String, MigrationClient> service : migrationServiceList.entrySet()) {
            MigrationClient serviceClient = service.getValue();
            serviceClient.preMigrationValidation(runPreMigrationStep);
        }
        log.info("Successfully executed the pre validation step.");
    }

    private void databaseMigration(MigrationClient serviceClient) throws APIMigrationException, SQLException {
        log.info("Start migrating databases  ..........");
        serviceClient.databaseMigration();
        log.info("Successfully migrated databases.");
    }

    private void registryResourceMigration(MigrationClient serviceClient) throws APIMigrationException {
        log.info("Start migrating api rxt ..........");
        serviceClient.registryResourceMigration();
        log.info("Successfully migrated api rxt.");
    }

    private void updateScopeRoleMappings(MigrationClient serviceClient) throws APIMigrationException {
        log.info("Start migrating Role Scope Tenant Conf Mappings  ..........");
        serviceClient.updateScopeRoleMappings();
        log.info("Successfully migrated Role Scope Tenant Conf Mappings.");
    }

    private void migrateTenantConfToDB(MigrationClient serviceClient) throws APIMigrationException {
        log.info("Start migrating Tenant Conf  ..........");
        serviceClient.migrateTenantConfToDB();
        log.info("Successfully migrated Tenant Conf to Database.");
    }

    private void registryDataPopulation(MigrationClient serviceClient) throws APIMigrationException {
        log.info("Start populating data for new properties in api artifacts ..........");
        serviceClient.registryDataPopulation();
        log.info("Successfully migrated data for api artifacts..........");
    }

    private void buildTenantList(TenantManager tenantManager, List<Tenant> tenantList, String tenantArguments)
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

    private void populateTenants(TenantManager tenantManager, List<Tenant> tenantList, String argument) throws UserStoreException {

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

    private void setAdminUserName(TenantManager tenantManager) throws UserStoreException {
        log.debug("Setting tenant admin names");

        for (int i = 0; i < tenantsArray.size(); ++i) {
            Tenant tenant = tenantsArray.get(i);
            if (tenant.getId() == MultitenantConstants.SUPER_TENANT_ID) {
                tenant.setAdminName("admin");
            }
            else {
                tenantsArray.set(i, tenantManager.getTenant(tenant.getId()));
            }
        }
    }

    protected List<Tenant> getTenantsArray() { return tenantsArray; }

    protected void updateAPIManagerDatabase(String sqlScriptPath) throws SQLException {
        log.info("Database migration for API Manager started");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = APIMgtDBUtil.getConnection();
            connection.setAutoCommit(false);
            String dbType = MigrationDBCreator.getDatabaseType(connection);

            if (Constants.DB_TYPE_MYSQL.equals(dbType)) {
                statement = connection.createStatement();
                resultSet = statement.executeQuery(GET_MYSQL_SESSION_MODE);

                if (resultSet.next()) {
                    String mode = resultSet.getString("MODE");

                    log.info("MySQL Server SQL Mode is : " + mode);

                    if (mode.contains(NO_ZERO_DATE_MODE)) {
                        File timeStampFixScript = new File(sqlScriptPath + dbType + "-timestamp_fix.sql");

                        if (timeStampFixScript.exists()) {
                            log.info(NO_ZERO_DATE_MODE + " mode detected, run schema compatibility script");
                            InputStream is = new FileInputStream(timeStampFixScript);

                            List<String> sqlStatements = readSQLStatements(is, dbType);

                            for (String sqlStatement : sqlStatements) {
                                preparedStatement = connection.prepareStatement(sqlStatement);
                                preparedStatement.execute();
                                connection.commit();
                            }
                        }
                    }
                }
            }

            InputStream is = new FileInputStream(sqlScriptPath + dbType + ".sql");

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
            connection.rollback();
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }

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
        log.info("DB resource migration done for all the tenants");
    }

    /**
     * This method is used to remove the FK constraint which is unnamed
     * This finds the name of the constraint and build the query to delete the constraint and execute it
     *
     * @param sqlScriptPath path of sql script
     * @throws SQLException
     */
    protected void dropFKConstraint(String sqlScriptPath) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            connection = APIMgtDBUtil.getConnection();
            String dbType = MigrationDBCreator.getDatabaseType(connection);
            String queryToExecute = IOUtils.toString(
                    new FileInputStream(new File(sqlScriptPath + "constraint" + File.separator + dbType + ".sql")),
                    "UTF-8");
            String queryArray[] = queryToExecute.split(Constants.LINE_BREAK);
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            if (Constants.DB_TYPE_ORACLE.equals(dbType)) {
                queryArray[0] = queryArray[0].replace(Constants.DELIMITER, "");
                queryArray[0] = queryArray[0].replace("<AM_DB_NAME>", connection.getMetaData().getUserName());
            }
            resultSet = statement.executeQuery(queryArray[0]);
            String constraintName = null;

            while (resultSet.next()) {
                constraintName = resultSet.getString("constraint_name");
            }

            if (constraintName != null) {
                queryToExecute = queryArray[1].replace("<temp_key_name>", constraintName);
                if (Constants.DB_TYPE_ORACLE.equals(dbType)) {
                    queryToExecute = queryToExecute.replace(Constants.DELIMITER, "");
                }

                if (queryToExecute.contains("\\n")) {
                    queryToExecute = queryToExecute.replace("\\n", "");
                }
                preparedStatement = connection.prepareStatement(queryToExecute);
                preparedStatement.execute();
                connection.commit();
            }
        } catch (APIMigrationException e) {
            //Foreign key might be already deleted, log the error and let it continue
            log.error("Error occurred while deleting foreign key", e);
        } catch (IOException e) {
            //If user does not add the file migration will continue and migrate the db without deleting
            // the foreign key reference
            log.error("Error occurred while finding the foreign key deletion query for execution", e);
        } catch (Exception e) {
            /* MigrationDBCreator extends from org.wso2.carbon.utils.dbcreator.DatabaseCreator and in the super class
            method getDatabaseType throws generic Exception */
            log.error("Error occurred while deleting foreign key", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.error("Unable to close the statement", e);
                }
            }
            APIMgtDBUtil.closeAllConnections(preparedStatement, connection, resultSet);
        }
    }

    private List<String> readSQLStatements(InputStream is, String dbType) {
        List<String> sqlStatements = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF8"));
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
                    if ("/".equals(line.trim())) {
                        isFoundQueryEnd = true;
                    } else {
                        isFoundQueryEnd = false;
                    }
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


    /**
     * This method is used to update the API artifacts in the registry
     * - to migrate Publisher Access Control feature related data.
     * - to add overview_type property to API artifacts
     * - to add 'enableStore' rxt field
     *
     * @throws APIMigrationException
     */
    public void updateGenericAPIArtifacts(RegistryService registryService) throws APIMigrationException {
        for (Tenant tenant : getTenantsArray()) {
            try {
                registryService.startTenantFlow(tenant);
                log.debug("Updating APIs for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                GenericArtifact[] artifacts = registryService.getGenericAPIArtifacts();
                for (GenericArtifact artifact : artifacts) {
                    String path = artifact.getPath();
                    if (registryService.isGovernanceRegistryResourceExists(path)) {
                        Object apiResource = registryService.getGovernanceRegistryResource(path);
                        if (apiResource == null) {
                            continue;
                        }
                        registryService.updateGenericAPIArtifactsForAccessControl(path, artifact);
                        registryService.updateGenericAPIArtifact(path, artifact);
                        registryService.updateEnableStoreInRxt(path,artifact);
                    }
                }
                log.info("Completed Updating API artifacts tenant ---- " + tenant.getId() + '(' + tenant.getDomain() + ')');
            } catch (GovernanceException e) {
                log.error("Error while accessing API artifact in registry for tenant " + tenant.getId() + '(' +
                        tenant.getDomain() + ')', e);
            } catch (RegistryException | UserStoreException e) {
                log.error("Error while updating API artifact in the registry for tenant " + tenant.getId() + '(' +
                        tenant.getDomain() + ')', e);
            } finally {
                registryService.endTenantFlow();
            }
        }
    }

    public void migrateFaultSequencesInRegistry(RegistryService registryService) {

        /* change the APIMgtFaultHandler class name in debug_json_fault.xml and json_fault.xml
           this method will read the new *json_fault.xml sequences from
           <APIM_2.1.0_HOME>/repository/resources/customsequences/fault and overwrite what is there in registry for
           all the tenants*/
        log.info("Fault sequence migration from APIM 2.0.0 to 2.1.0 has started");
        String apim210FaultSequencesLocation = CarbonUtils.getCarbonHome() + File.separator + "repository" + File
                .separator + "resources" + File.separator + "customsequences" + File.separator + "fault";
        String apim210FaultSequenceFile = apim210FaultSequencesLocation + File.separator + "json_fault.xml";
        String api210DebugFaultSequenceFile = apim210FaultSequencesLocation + File.separator + "debug_json_fault.xml";

        // read new files
        String apim210FaultSequenceContent = null;
        try {
            apim210FaultSequenceContent = FileUtil.readFileToString(apim210FaultSequenceFile);
        } catch (IOException e) {
            log.error("Error in reading file: " + apim210FaultSequenceFile, e);
        }

        String apim210DebugFaultSequenceContent = null;
        try {
            apim210DebugFaultSequenceContent = FileUtil.readFileToString(api210DebugFaultSequenceFile);
        } catch (IOException e) {
            log.error("Error in reading file: " + api210DebugFaultSequenceFile, e);
        }

        if (StringUtils.isEmpty(apim210FaultSequenceContent) && StringUtils.isEmpty(apim210DebugFaultSequenceContent)) {
            // nothing has been read from <APIM_NEW_HOME>/repository/resources/customsequences/fault
            log.error("No content read from <APIM_NEW_HOME>/repository/resources/customsequences/fault location, "
                    + "aborting migration");
            return;
        }
        for (Tenant tenant : getTenantsArray()) {
            try {
                registryService.startTenantFlow(tenant);
                // update json_fault.xml and debug_json_fault.xml in registry
                if (StringUtils.isNotEmpty(apim210FaultSequenceContent)) {
                    try {
                        final String jsonFaultResourceRegistryLocation = "/apimgt/customsequences/fault/json_fault.xml";
                        if (registryService.isGovernanceRegistryResourceExists(jsonFaultResourceRegistryLocation)) {
                            // update
                            registryService.updateGovernanceRegistryResource(jsonFaultResourceRegistryLocation,
                                    apim210FaultSequenceContent);
                        } else {
                            // add
                            registryService.addGovernanceRegistryResource(jsonFaultResourceRegistryLocation,
                                    apim210FaultSequenceContent, "application/xml");
                        }
                        log.info("Successfully migrated json_fault.xml in registry for tenant: " + tenant.getDomain() +
                                ", tenant id: " + tenant.getId());

                    } catch (UserStoreException e) {
                        log.error("Error in updating json_fault.xml in registry for tenant: " + tenant.getDomain() +
                                ", tenant id: " + tenant.getId(), e);
                    } catch (RegistryException e) {
                        log.error("Error in updating json_fault.xml in registry for tenant: " + tenant.getDomain() +
                                ", tenant id: " + tenant.getId(), e);
                    }
                }
                if (StringUtils.isNotEmpty(apim210DebugFaultSequenceContent)) {
                    try {
                        final String debugJsonFaultResourceRegistryLocation = "/apimgt/customsequences/fault/debug_json_fault.xml";
                        if (registryService.isGovernanceRegistryResourceExists(debugJsonFaultResourceRegistryLocation)) {
                            // update
                            registryService.updateGovernanceRegistryResource(debugJsonFaultResourceRegistryLocation,
                                    apim210DebugFaultSequenceContent);
                        } else {
                            // add
                            registryService.addGovernanceRegistryResource(debugJsonFaultResourceRegistryLocation,
                                    apim210DebugFaultSequenceContent, "application/xml");
                        }
                        log.info("Successfully migrated debug_json_fault.xml in registry for tenant: " +
                                tenant.getDomain() + ", tenant id: " + tenant.getId());
                    } catch (UserStoreException e) {
                        log.error("Error in updating debug_json_fault.xml in registry for tenant: " +
                                tenant.getDomain() + ", tenant id: " + tenant.getId(), e);
                    } catch (RegistryException e) {
                        log.error("Error in updating debug_json_fault.xml in registry for tenant: " +
                                tenant.getDomain() + ", tenant id: " + tenant.getId(), e);
                    }
                }
            } finally {
                registryService.endTenantFlow();
            }
        }
    }

    /**
     * This method is used to migrate rxt
     *
     * @throws APIMigrationException
     */
    public void rxtMigration(RegistryService registryService) throws APIMigrationException {
        log.info("Rxt migration for API Manager started.");

        String rxtName = "api.rxt";
        String rxtDir = CarbonUtils.getCarbonHome() + File.separator + "migration-resources" + File.separator + "rxts"
                + File.separator + rxtName;


        for (Tenant tenant : getTenantsArray()) {
            try {
                registryService.startTenantFlow(tenant);

                log.info("Updating api.rxt for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                //Update api.rxt file
                String rxt = FileUtil.readFileToString(rxtDir);
                registryService.updateRXTResource(rxtName, rxt);
                log.info("End Updating api.rxt for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
            } catch (IOException e) {
                log.error("Error when reading api.rxt from " + rxtDir + " for tenant " + tenant.getId() + '(' + tenant
                        .getDomain() + ')', e);
            } catch (RegistryException e) {
                log.error("Error while updating api.rxt in the registry for tenant " + tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            } catch (UserStoreException e) {
                log.error("Error while updating api.rxt in the registry for tenant " + tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            } finally {
                registryService.endTenantFlow();
            }
        }
        log.info("Rxt resource migration done for all the tenants");
    }
    /**
     * Gets the content of the local tenant-conf.json as a JSON Object
     *
     * @return JSON content of the local tenant-conf.json
     * @throws IOException error while reading local tenant-conf.json
     */
    protected static JSONObject getTenantConfJSONFromFile() throws IOException, APIMigrationException {
        JSONObject tenantConfJson = null;
        try {
            String tenantConfDataStr = new String(getTenantConfFromFile(), Charset.defaultCharset());
            JSONParser parser = new JSONParser();
            tenantConfJson = (JSONObject) parser.parse(tenantConfDataStr);
            if (tenantConfJson == null) {
                throw new APIMigrationException("tenant-conf.json (in file system) content cannot be null");
            }
        } catch (ParseException e) {
            log.error("Error while parsing tenant-conf.json from file system.");
        }
        return tenantConfJson;
    }
    /**
     * Gets the content of the local tenant-conf.json as a JSON Object
     *
     * @return JSON content of the local tenant-conf.json
     * @throws IOException error while reading local tenant-conf.json
     */
    protected static byte[] getTenantConfFromFile() throws IOException {
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

    private static void updateTenantConf(UserRegistry registry, byte[] data) throws RegistryException {

        Resource resource = registry.newResource();
        resource.setMediaType(APIConstants.API_TENANT_CONF_MEDIA_TYPE);
        resource.setContent(data);
        registry.put(APIConstants.API_TENANT_CONF_LOCATION, resource);
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
     * Migrate the newly added scopes to the tenant-conf which is already in the registry identified with tenantId and
     * its byte content is returned. If there were no changes done, an empty Optional will be returned.
     *
     * @param tenantId Tenant Id
     * @return Optional byte content
     * @throws APIManagementException when error occurred while updating the updating the tenant-conf with scopes.
     */
    private static Optional<Byte[]> migrateTenantConfScopes(int tenantId) throws APIMigrationException {

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
                }
            } else {
                log.debug("Scopes in tenant-conf.json in tenant " + tenantId + " are already migrated.");
                return Optional.empty();
            }
        } else {
            log.debug("Scopes in tenant-conf.json in tenant " + tenantId + " are already migrated.");
            return Optional.empty();
        }
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
}
