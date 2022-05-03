package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrationHolder;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrator;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.SharedDBUtil;
import org.wso2.carbon.core.ServerStartupObserver;
import org.wso2.carbon.user.api.UserStoreException;
import java.util.List;

public class APIMMigrationClient implements ServerStartupObserver {
    private static final Log log = LogFactory.getLog(APIMMigrationClient.class);

    @Override
    public void completingServerStartup() {

    }

    @Override
    public void completedServerStartup() {
        try {
            APIMgtDBUtil.initialize();
            SharedDBUtil.initialize();
        } catch (Exception e) {
            log.error("Error occurred while initializing DB Util ", e);
        }
        String migrateFromVersion = System.getProperty(Constants.ARG_MIGRATE_FROM_VERSION);
        String migratedVersion = System.getProperty(Constants.ARG_MIGRATED_VERSION);
        VersionMigrationHolder versionMigrationHolder = VersionMigrationHolder.getInstance();
        List<VersionMigrator> versionMigrationList = versionMigrationHolder.getVersionMigrationList();

        boolean isMigrationStarted = false;
        for (VersionMigrator versionMigration : versionMigrationList) {
            if (!isMigrationStarted && versionMigration.getPreviousVersion().equals(migrateFromVersion)) {
                try {
                    versionMigration.migrate();
                } catch (APIMigrationException e) {
                    e.printStackTrace();
                } catch (UserStoreException e) {
                    e.printStackTrace();
                }
                isMigrationStarted = true;
                migrateFromVersion = versionMigration.getCurrentVersion();
                if (versionMigration.getCurrentVersion().equals(migratedVersion)) {
                    break;
                }
            }
            if (isMigrationStarted) {
                try {
                    versionMigration.migrate();
                } catch (APIMigrationException e) {
                    e.printStackTrace();
                } catch (UserStoreException e) {
                    e.printStackTrace();
                }
                if (versionMigration.getCurrentVersion().equals(migrateFromVersion)) {
                    break;
                }
            }
        }
    }
}