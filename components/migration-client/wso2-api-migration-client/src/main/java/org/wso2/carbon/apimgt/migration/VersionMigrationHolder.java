package org.wso2.carbon.apimgt.migration;

import java.util.ArrayList;
import java.util.List;

public class VersionMigrationHolder {
    private static VersionMigrationHolder versionMigrationHolder = new VersionMigrationHolder();
    private List<VersionMigration> versionMigrationList = new ArrayList<>();

    private VersionMigrationHolder() {

        versionMigrationList.add(new V300Migration());
        versionMigrationList.add(new V310Migration());
        versionMigrationList.add(new V320Migration());
        versionMigrationList.add(new V400Migration());
        versionMigrationList.add(new V410Migration());
    }

    public static VersionMigrationHolder getInstance() {

        return VersionMigrationHolder.versionMigrationHolder;
    }

    public List<VersionMigration> getVersionMigrationList() {

        return versionMigrationList;
    }
}