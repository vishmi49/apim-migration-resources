package org.wso2.carbon.apimgt.migration.migrator.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PostDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PreDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v410.V410DBDataMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v410.V410RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.util.APIUtil;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.File;

public class V410Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V410Migration.class);
    private final String PRE_MIGRATION_SCRIPTS_PATH = Utility.PRE_MIGRATION_SCRIPT_DIR + "migration-4.0.0_to_4.1.0"
            + File.separator;
    private final String POST_MIGRATION_SCRIPT_REGDB_PATH = Utility.POST_MIGRATION_SCRIPT_DIR + "reg_db" + File.separator
            + "reg-index.sql";
    private final String POST_MIGRATION_SCRIPT_AMDB_PATH = Utility.POST_MIGRATION_SCRIPT_DIR + "am_db" + File.separator;

    @Override
    public String getPreviousVersion() {
        return "4.0.0";
    }

    @Override
    public String getCurrentVersion() {
        return "4.1.0";
    }
    @Override
    public void migrate() throws UserStoreException, APIMigrationException {
        PreDBScriptMigrator preDBScriptMigrator = new PreDBScriptMigrator(PRE_MIGRATION_SCRIPTS_PATH);
        preDBScriptMigrator.run();
        V410DBDataMigrator v410DBDataMigrator = new V410DBDataMigrator();
        v410DBDataMigrator.migrate();
        V410RegistryResourceMigrator v410RegistryResourceMigrator = new V410RegistryResourceMigrator();
        v410RegistryResourceMigrator.migrate();
        PostDBScriptMigrator postDBScriptMigratorForRegDB = new PostDBScriptMigrator(POST_MIGRATION_SCRIPT_REGDB_PATH);
        postDBScriptMigratorForRegDB.run();
        PostDBScriptMigrator postDBScriptMigratorForAmDb = new PostDBScriptMigrator(POST_MIGRATION_SCRIPT_AMDB_PATH);
        postDBScriptMigratorForAmDb.run();
        // Setting ExtendedAPIMConfigService as disabled. This extended implementation is only needed and enabled for migrations
        // which are from before APIM 4.1. Also need to disable this for future migrations such as from APIM 4.2 to APIM 4.3.
        APIUtil.setDisabledExtendedAPIMConfigService(true);
        log.info("ExtendedAPIMConfigService is disabled");
    }
}