package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.client.MigrateFrom400;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.AMDBUtil;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.io.File;
import java.sql.SQLException;

public class V410Migration extends Migrator {
    private static final Log log = LogFactory.getLog(V410Migration.class);
    private final String PRE_MIGRATION_SCRIPTS_PATH = PRE_MIGRATION_SCRIPT_DIR + "migration-4.0.0_to_4.1.0"
            + File.separator;
    private final String POST_MIGRATION_SCRIPT_REGDB_PATH = POST_MIGRATION_SCRIPT_DIR + "reg_db" + File.separator
            + "reg-index.sql";
    private final String POST_MIGRATION_SCRIPT_AMDB_PATH = POST_MIGRATION_SCRIPT_DIR + "am_db" + File.separator;

    String preMigrationStep = System.getProperty(Constants.PRE_MIGRATION_STEP);
    String tenants = System.getProperty(Constants.ARG_MIGRATE_TENANTS);
    String tenantRange = System.getProperty(Constants.ARG_MIGRATE_TENANTS_RANGE);
    String blackListTenants = System.getProperty(Constants.ARG_MIGRATE_BLACKLIST_TENANTS);
    MigrateFrom400 migrateFrom400 = null;

    public V410Migration(String tenantArguments, String blackListTenantArguments, String tenantRange,
                         TenantManager tenantManager) throws UserStoreException {
        super(tenantArguments, blackListTenantArguments, tenantRange, tenantManager);
    }

    @Override
    public String getPreviousVersion() {
        return "4.0.0";
    }

    @Override
    public String getCurrentVersion() {
        return "4.1.0";
    }

    @Override
    public void runPreMigrationScripts() throws SQLException {
        AMDBUtil.runSQLScript(PRE_MIGRATION_SCRIPTS_PATH, false);
    }

    @Override
    public void migrate() {
        RegistryServiceImpl registryService = new RegistryServiceImpl();
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();
        log.info("Starting Migration from APIM 4.0.0 to 4.1.0.............");
        try {
            migrateFrom400 = new MigrateFrom400(tenants, blackListTenants, tenantRange,
                    registryService, tenantManager);
            if (preMigrationStep != null) {
               // migrateFrom400.preMigrationValidation(preMigrationStep);
            } else {
                migrateFrom400.databaseMigration();
                migrateFrom400.registryResourceMigration();
                migrateFrom400.updateScopeRoleMappings();
                migrateFrom400.migrateTenantConfToDB();
                migrateFrom400.registryDataPopulation();
            }
        } catch (UserStoreException e) {
            e.printStackTrace();
        } catch (APIMigrationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runPostMigrationScripts() throws SQLException {
        AMDBUtil.runSQLScript(POST_MIGRATION_SCRIPT_REGDB_PATH, true);
        AMDBUtil.runSQLScript(POST_MIGRATION_SCRIPT_AMDB_PATH, false);
    }
}