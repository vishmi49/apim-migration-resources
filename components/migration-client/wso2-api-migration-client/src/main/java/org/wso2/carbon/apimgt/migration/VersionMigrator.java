package org.wso2.carbon.apimgt.migration;

import org.wso2.carbon.user.api.UserStoreException;

public abstract class VersionMigrator {

    public abstract String getPreviousVersion();

    public abstract String getCurrentVersion();

    public abstract void migrate() throws APIMigrationException, UserStoreException;
}
