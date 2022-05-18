package org.wso2.carbon.apimgt.migration.migrator.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.APIRXTMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PostDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PreDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v400.V400DBDataMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v400.V400RegistryResourceMigrator;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.File;

public class V400Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V400Migration.class);
    private final String PRE_MIGRATION_SCRIPTS_PATH = Utility.PRE_MIGRATION_SCRIPT_DIR + "migration-3.2.0_to_4.0.0"
            + File.separator;
    private final String RXT_PATH = Utility.RXT_DIR + "4.0.0" + File.separator + Utility.API_RXT_FILE;

    public V400Migration() throws UserStoreException {
    }

    @Override
    public String getPreviousVersion() {
        return "3.2.0";
    }

    @Override
    public String getCurrentVersion() {
        return "4.0.0";
    }

    @Override
    public void migrate() throws UserStoreException, APIMigrationException {
        PreDBScriptMigrator preDBScriptMigrator = new PreDBScriptMigrator(PRE_MIGRATION_SCRIPTS_PATH);
        preDBScriptMigrator.run();
        APIRXTMigrator apirxtMigrator = new APIRXTMigrator(RXT_PATH);
        apirxtMigrator.migrate();
        V400DBDataMigrator v400DBDataMigrator = new V400DBDataMigrator();
        v400DBDataMigrator.migrate();
        V400RegistryResourceMigrator v400RegistryResourceMigrator = new V400RegistryResourceMigrator();
        v400RegistryResourceMigrator.migrate();
    }
}