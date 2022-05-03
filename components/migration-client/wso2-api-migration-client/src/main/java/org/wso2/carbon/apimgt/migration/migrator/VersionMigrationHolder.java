package org.wso2.carbon.apimgt.migration.migrator;

import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.migrator.client.*;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.util.ArrayList;
import java.util.List;

public class VersionMigrationHolder {
    private static VersionMigrationHolder versionMigrationHolder;

    static {
        try {
            versionMigrationHolder = new VersionMigrationHolder();
        } catch (UserStoreException e) {
            e.printStackTrace();
        }
    }

    private List<VersionMigrator> versionMigrationList = new ArrayList<>();

    private VersionMigrationHolder() throws UserStoreException {
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();

        versionMigrationList.add(new V300Migration());
        versionMigrationList.add(new V310Migration());
        versionMigrationList.add(new V320Migration());
        versionMigrationList.add(new V400Migration());
        versionMigrationList.add(new V410Migration());
    }

    public static VersionMigrationHolder getInstance() {

        return VersionMigrationHolder.versionMigrationHolder;
    }

    public List<VersionMigrator> getVersionMigrationList() {

        return versionMigrationList;
    }
}