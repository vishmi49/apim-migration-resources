package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.client.MigrateFrom310;
import org.wso2.carbon.apimgt.migration.client.MigrationClient;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.AMDBUtil;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.io.File;
import java.sql.SQLException;

public class V320Migration extends Migrator {
    private static final Log log = LogFactory.getLog(V320Migration.class);
    private final String PRE_MIGRATION_SCRIPTS_PATH = PRE_MIGRATION_SCRIPT_DIR + "migration-3.1.0_to_3.2.0"
            + File.separator;
    private final String POST_MIGRATION_SCRIPT_REGDB_PATH = POST_MIGRATION_SCRIPT_DIR + "reg_db" + File.separator
            + "reg-index.sql";

    String preMigrationStep = System.getProperty(Constants.PRE_MIGRATION_STEP);
    String tenants = System.getProperty(Constants.ARG_MIGRATE_TENANTS);
    String tenantRange = System.getProperty(Constants.ARG_MIGRATE_TENANTS_RANGE);
    String blackListTenants = System.getProperty(Constants.ARG_MIGRATE_BLACKLIST_TENANTS);

    public V320Migration(String tenantArguments, String blackListTenantArguments, String tenantRange,
                         TenantManager tenantManager) throws UserStoreException {
        super(tenantArguments, blackListTenantArguments, tenantRange, tenantManager);
    }

    @Override
    public String getPreviousVersion() {
        return "3.1.0";
    }

    @Override
    public String getCurrentVersion() {
        return "3.2.0";
    }

    @Override
    public void runPreMigrationScripts() throws SQLException {
        AMDBUtil.runSQLScript(PRE_MIGRATION_SCRIPTS_PATH, false);
    }

    @Override
    public void migrate() {
        RegistryServiceImpl registryService = new RegistryServiceImpl();
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();
        MigrationClient migrateFrom310 = null;
        try {
            migrateFrom310 = new MigrateFrom310(tenants, blackListTenants, tenantRange,
                    registryService, tenantManager);
            migrateFrom310.registryResourceMigration();
            migrateFrom310.scopeMigration();
            migrateFrom310.spMigration();
        } catch (UserStoreException e) {
            e.printStackTrace();
        } catch (APIMigrationException e) {
            e.printStackTrace();
        }
        log.info("Migrated Successfully to 3.2");
    }

    @Override
    public void runPostMigrationScripts() throws SQLException {
        AMDBUtil.runSQLScript(POST_MIGRATION_SCRIPT_REGDB_PATH, true);
    }
}