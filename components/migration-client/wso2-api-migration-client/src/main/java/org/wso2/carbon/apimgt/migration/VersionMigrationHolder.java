package org.wso2.carbon.apimgt.migration;

import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.util.ArrayList;
import java.util.List;

public class VersionMigrationHolder {
    private static VersionMigrationHolder versionMigrationHolder;
    String tenants = System.getProperty(Constants.ARG_MIGRATE_TENANTS);
    String tenantRange = System.getProperty(Constants.ARG_MIGRATE_TENANTS_RANGE);
    String blackListTenants = System.getProperty(Constants.ARG_MIGRATE_BLACKLIST_TENANTS);

    static {
        try {
            versionMigrationHolder = new VersionMigrationHolder();
        } catch (UserStoreException e) {
            e.printStackTrace();
        }
    }

    private List<Migrator> versionMigrationList = new ArrayList<>();

    private VersionMigrationHolder() throws UserStoreException {
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();

        versionMigrationList.add(new V300Migration(tenants, blackListTenants, tenantRange, tenantManager));
        versionMigrationList.add(new V310Migration(tenants, blackListTenants, tenantRange, tenantManager));
        versionMigrationList.add(new V320Migration(tenants, blackListTenants, tenantRange, tenantManager));
        versionMigrationList.add(new V400Migration(tenants, blackListTenants, tenantRange, tenantManager));
        versionMigrationList.add(new V410Migration(tenants, blackListTenants, tenantRange, tenantManager));
    }

    public static VersionMigrationHolder getInstance() {

        return VersionMigrationHolder.versionMigrationHolder;
    }

    public List<Migrator> getVersionMigrationList() {

        return versionMigrationList;
    }
}