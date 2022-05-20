package org.wso2.carbon.apimgt.migration.migrator.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PreDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v320.SPMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v320.ScopeMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v320.V320RegistryResourceMigrator;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.File;

public class V320Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V410Migration.class);
    private final String PRE_MIGRATION_SCRIPTS_PATH = Utility.PRE_MIGRATION_SCRIPT_DIR + "migration-3.1.0_to_3.2.0"
            + File.separator;
    private final String V320_RXT_PATH = Utility.RXT_DIR + "3.2.0" + File.separator;
    Migrator migrator;

    @Override
    public String getPreviousVersion() {
        return "3.1.0";
    }

    @Override
    public String getCurrentVersion() {
        return "3.2.0";
    }

    @Override
    public void migrate() throws APIMigrationException, UserStoreException {
        log.info("Starting migration from " + getPreviousVersion() + " to " + getCurrentVersion() + "...");
        PreDBScriptMigrator preDBScriptMigrator = new PreDBScriptMigrator(PRE_MIGRATION_SCRIPTS_PATH);
        preDBScriptMigrator.run();
        RegistryResourceMigrator registryResourceMigrator= new V320RegistryResourceMigrator(V320_RXT_PATH);
        registryResourceMigrator.migrate();
        ScopeMigrator scopeMigrator = new ScopeMigrator();
        scopeMigrator.migrate();
        SPMigrator spMigrator = new SPMigrator();
        spMigrator.migrate();
        log.info("Starting migration from " + getPreviousVersion() + " to " + getCurrentVersion() + "...");
    }


}