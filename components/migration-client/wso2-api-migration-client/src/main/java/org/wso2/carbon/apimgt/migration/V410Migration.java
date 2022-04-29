package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.client.MigrateFrom400;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

public class V410Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V410Migration.class);
    String preMigrationStep = System.getProperty(Constants.PRE_MIGRATION_STEP);
    String tenants = System.getProperty(Constants.ARG_MIGRATE_TENANTS);
    String tenantRange = System.getProperty(Constants.ARG_MIGRATE_TENANTS_RANGE);
    String blackListTenants = System.getProperty(Constants.ARG_MIGRATE_BLACKLIST_TENANTS);
    MigrateFrom400 migrateFrom400 = null;

    public V410Migration() throws UserStoreException {
    }

    @Override
    public String getPreviousVersion() {
        return "4.0.0";
    }

    @Override
    public String getCurrentVersion() {
        return "4.1.0";
    }

    @Override
    public void migrate() {
        RegistryServiceImpl registryService = new RegistryServiceImpl();
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();
        log.info("Starting Migration from APIM 4.0.0 to 4.1.0.............");
        try {
            migrateFrom400 = new MigrateFrom400(tenants, blackListTenants, tenantRange,
                    registryService, tenantManager);
            if (preMigrationStep != null) {
               // migrateFrom400.preMigrationValidation(preMigrationStep);
            } else {
                migrateFrom400.databaseMigration();
                migrateFrom400.registryResourceMigration();
                migrateFrom400.updateScopeRoleMappings();
                migrateFrom400.migrateTenantConfToDB();
                migrateFrom400.registryDataPopulation();
            }
        } catch (UserStoreException e) {
            e.printStackTrace();
        } catch (APIMigrationException e) {
            e.printStackTrace();
        }
    }

}