package org.wso2.carbon.apimgt.migration.migrator.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v420.V420RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.user.api.UserStoreException;

public class V420Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V420Migration.class);

    @Override public String getPreviousVersion() {
        return "4.1.0";
    }

    @Override public String getCurrentVersion() {
        return "4.2.0";
    }

    @Override public void migrate() throws UserStoreException, APIMigrationException {
        log.info("--------------------------------------------------------------------------------------------------");
        log.info("WSO2 API-M Migration Task : Starting migration from " + getPreviousVersion() + " to "
                + getCurrentVersion() + "...");
        log.info("--------------------------------------------------------------------------------------------------");

        log.info(
                "WSO2 API-M Migration Task : Starting registry resource migration from " + getPreviousVersion() + " to "
                        + getCurrentVersion());
        V420RegistryResourceMigrator v420RegistryResourceMigrator = new V420RegistryResourceMigrator(
                Constants.V420_RXT_PATH);
        v420RegistryResourceMigrator.migrate();
        log.info("WSO2 API-M Migration Task : Completed registry resource migration from " + getPreviousVersion()
                + " to " + getCurrentVersion());
        log.info("WSO2 API-M Migration Task : Completed migration from " + getPreviousVersion() + " to "
                + getCurrentVersion() + "...");
    }
}
