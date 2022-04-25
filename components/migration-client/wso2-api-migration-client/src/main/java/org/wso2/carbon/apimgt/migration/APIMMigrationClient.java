package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.migration.client.sp_migration.APIMStatMigrationConstants;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.SharedDBUtil;
import org.wso2.carbon.core.ServerStartupObserver;

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
            //APIMgtDBUtil.initialize() throws generic exception
            log.error("Error occurred while initializing DB Util ", e);
        }

        String migrateFromVersion = System.getProperty(Constants.ARG_MIGRATE_FROM_VERSION);
        String migratedVersion = System.getProperty(Constants.ARG_MIGRATED_VERSION);
        VersionMigrationHolder versionMigrationHolder = VersionMigrationHolder.getInstance();
        List<VersionMigration> versionMigrationList = versionMigrationHolder.getVersionMigrationList();
        boolean isMigrationStarted = false;

        for (VersionMigration versionMigration : versionMigrationList) {
            if (!isMigrationStarted && versionMigration.getPreviousVersion().equals(migrateFromVersion)) {
                if (migrateFromVersion.equals("2.6.0")) {
                    V300Migration v300Migration = new V300Migration();
                    v300Migration.migrate();
                } else if (migrateFromVersion.equals("3.0.0")) {
                    V310Migration v310Migration = new V310Migration();
                    v310Migration.migrate();
                } else if (migrateFromVersion.equals("3.1.0")) {
                    V320Migration v320Migration = new V320Migration();
                    v320Migration.migrate();
                } else if (migrateFromVersion.equals("3.2.0")) {
                    V400Migration v400Migration = new V400Migration();
                    v400Migration.migrate();
                } else if (migrateFromVersion.equals("4.0.0")) {
                    V410Migration v410Migration = new V410Migration();
                    v410Migration.migrate();
                }
            }
            if (versionMigration.getCurrentVersion().equals(migratedVersion)) {
                break;
            }
            continue;
        }

    }
}