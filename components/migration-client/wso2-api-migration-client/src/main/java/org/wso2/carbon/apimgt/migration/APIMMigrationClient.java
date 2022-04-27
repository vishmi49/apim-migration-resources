package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.SharedDBUtil;
import org.wso2.carbon.core.ServerStartupObserver;

import java.sql.SQLException;
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
        List<Migrator> versionMigrationList = versionMigrationHolder.getVersionMigrationList();
        boolean isMigrationStarted = false;
            for (Migrator versionMigration : versionMigrationList) {
                if (!isMigrationStarted && versionMigration.getPreviousVersion().equals(migrateFromVersion)) {
                    try {
                        versionMigration.doMigration();
                    } catch (SQLException e) {
                        log.error("Error occurred when running the SQL scripts ", e);
                    }
                    isMigrationStarted = true;
                    migrateFromVersion = versionMigration.getCurrentVersion();
                }
                if (versionMigration.getCurrentVersion().equals(migratedVersion)) {
                    break;
                }
            }
    }
}