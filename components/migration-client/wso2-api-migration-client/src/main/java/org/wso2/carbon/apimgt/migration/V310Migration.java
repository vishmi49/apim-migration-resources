package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.client.MigrateFrom310;
import org.wso2.carbon.apimgt.migration.client.MigrationClient;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

public class V310Migration extends VersionMigration {
    private static final Log log = LogFactory.getLog(V310Migration .class);

    String tenants = System.getProperty(Constants.ARG_MIGRATE_TENANTS);
    String tenantRange = System.getProperty(Constants.ARG_MIGRATE_TENANTS_RANGE);
    String blackListTenants = System.getProperty(Constants.ARG_MIGRATE_BLACKLIST_TENANTS);
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

        RegistryServiceImpl registryService = new RegistryServiceImpl();
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();
        MigrationClient migrateFrom310 = null;
        try {
            migrateFrom310 = new MigrateFrom310(tenants, blackListTenants, tenantRange,
                    registryService, tenantManager);
            migrateFrom310.registryResourceMigration();
            migrateFrom310.scopeMigration();
            migrateFrom310.spMigration();
        } catch (UserStoreException e) {
            e.printStackTrace();
        } catch (APIMigrationException e) {
            e.printStackTrace();
        }
    }
}