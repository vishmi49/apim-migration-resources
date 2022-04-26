package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.client.MigrationClient;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.AMDBUtil;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.io.File;
import java.sql.SQLException;

public class V300Migration extends Migrator {
    private static final Log log = LogFactory.getLog(V300Migration.class);
    private final String PRE_MIGRATION_SCRIPTS_PATH = PRE_MIGRATION_SCRIPT_DIR + "migration-2.6.0_to_3.0.0"
            + File.separator;
    private final String POST_MIGRATION_SCRIPT_REGDB_PATH = POST_MIGRATION_SCRIPT_DIR + "reg_db" + File.separator
            + "reg-index.sql";
    MigrationClient migrateFrom260;

    public V300Migration(String tenantArguments, String blackListTenantArguments, String tenantRange,
                         TenantManager tenantManager) throws UserStoreException {
        super(tenantArguments, blackListTenantArguments, tenantRange, tenantManager);
    }

    @Override
    public String getPreviousVersion() {
        return "2.6.0";
    }

    @Override
    public String getCurrentVersion() {
        return "3.0.0";
    }

    @Override
    public void runPreMigrationScripts() throws SQLException {
        AMDBUtil.runSQLScript(PRE_MIGRATION_SCRIPTS_PATH, false);
    }

    @Override
    public void migrate() {
        log.info("Start migration from APIM 2.6 to 3.0.0  ..........");
        RegistryServiceImpl registryService = new RegistryServiceImpl();
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();

        try {
            log.info("Migrating WSO2 API Manager registry resources ..........");
            migrateFrom260.registryResourceMigration();
            log.info("Successfully migrated registry resources .");
        } catch (APIMigrationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void runPostMigrationScripts() throws SQLException {
        AMDBUtil.runSQLScript(POST_MIGRATION_SCRIPT_REGDB_PATH, true);
    }
}