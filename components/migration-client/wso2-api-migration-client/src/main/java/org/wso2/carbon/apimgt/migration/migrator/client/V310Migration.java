package org.wso2.carbon.apimgt.migration.migrator.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.APIRXTMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PostDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PreDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.File;

public class V310Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V310Migration .class);
    private final String PRE_MIGRATION_SCRIPTS_PATH = Utility.PRE_MIGRATION_SCRIPT_DIR + "migration-3.0.0_to_3.1.0"
            + File.separator;
    private final String RXT_PATH = Utility.RXT_DIR + "3.1.0" + File.separator + Utility.API_RXT_FILE;

    @Override
    public String getPreviousVersion() {
        return "3.0.0";
    }

    @Override
    public String getCurrentVersion() {
        return "3.1.0";
    }


    @Override
    public void migrate() {

        RegistryResourceMigrator registryResourceMigrator = null;
        try {
            PreDBScriptMigrator preDBScriptMigrator = new PreDBScriptMigrator(PRE_MIGRATION_SCRIPTS_PATH);
            preDBScriptMigrator.run();
            APIRXTMigrator apirxtMigrator = new APIRXTMigrator(RXT_PATH);
            apirxtMigrator.migrate();
            registryResourceMigrator = new RegistryResourceMigrator();
            registryResourceMigrator.migrate();
        } catch (APIMigrationException e) {
            e.printStackTrace();
        } catch (UserStoreException e) {
        e.printStackTrace();
    }
    }
}