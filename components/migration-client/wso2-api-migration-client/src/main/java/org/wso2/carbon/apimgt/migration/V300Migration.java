package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.client.MigrateFrom210;
import org.wso2.carbon.apimgt.migration.client.MigrateFrom260;
import org.wso2.carbon.apimgt.migration.client.MigrationClient;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

public class V300Migration extends VersionMigration {
    private static final Log log = LogFactory.getLog(V300Migration.class);
    String tenants = System.getProperty(Constants.ARG_MIGRATE_TENANTS);
    String tenantRange = System.getProperty(Constants.ARG_MIGRATE_TENANTS_RANGE);
    String blackListTenants = System.getProperty(Constants.ARG_MIGRATE_BLACKLIST_TENANTS);

    @Override
    public String getPreviousVersion() {
        return "2.6.0";
    }

    @Override
    public String getCurrentVersion() {
        return "3.0.0";
    }

    @Override
    public void migrate() {
        log.info("Start migration from APIM 2.6 to 3.0.0  ..........");
        RegistryServiceImpl registryService = new RegistryServiceImpl();
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();

        try {
            MigrationClient migrateFrom260 = new MigrateFrom260(tenants, blackListTenants, tenantRange,
                    registryService, tenantManager);
            log.info("Migrating WSO2 API Manager registry resources ..........");
            migrateFrom260.registryResourceMigration();
            log.info("Successfully migrated registry resources .");
        } catch (UserStoreException e) {
            e.printStackTrace();
        } catch (APIMigrationException e) {
            e.printStackTrace();
        }

    }
}