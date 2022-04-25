package org.wso2.carbon.apimgt.migration;

public abstract class VersionMigration {

    public abstract String getPreviousVersion();

    public abstract String getCurrentVersion();

    public abstract void migrate();

}