package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.client.MigrateFrom320;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

public class V400Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V400Migration.class);
    String tenants = System.getProperty(Constants.ARG_MIGRATE_TENANTS);
    String tenantRange = System.getProperty(Constants.ARG_MIGRATE_TENANTS_RANGE);
    String blackListTenants = System.getProperty(Constants.ARG_MIGRATE_BLACKLIST_TENANTS);

    public V400Migration(String tenantArguments, String blackListTenantArguments, String tenantRange,
                         TenantManager tenantManager) throws UserStoreException {
        super(tenantArguments, blackListTenantArguments, tenantRange, tenantManager);
    }

    @Override
    public String getPreviousVersion() {
        return "3.2.0";
    }

    @Override
    public String getCurrentVersion() {
        return "4.0.0";
    }

    @Override
    public void migrate() {
        RegistryServiceImpl registryService = new RegistryServiceImpl();
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();
        try {
//            MigrateUUIDToDB commonMigrationClient = new MigrateUUIDToDB(tenants, blackListTenants, tenantRange,
//                    tenantManager);
//            commonMigrationClient.moveUUIDToDBFromRegistry();
            MigrateFrom320 migrateFrom320 = null;

            migrateFrom320 = new MigrateFrom320(tenants, blackListTenants, tenantRange,
                    registryService, tenantManager);

            log.info("Start migrating WebSocket APIs ..........");
            migrateFrom320.migrateWebSocketAPI();
            log.info("Successfully migrated WebSocket APIs ..........");

            migrateFrom320.migrateLabelsToVhosts();
            log.info("Start migrating API Product Mappings  ..........");
            migrateFrom320.migrateProductMappingTable();
            log.info("Successfully migrated API Product Mappings ..........");

            log.info("Start migrating registry paths of Icon and WSDLs  ..........");
            migrateFrom320.updateRegistryPathsOfIconAndWSDL();
            log.info("Successfully migrated API registry paths of Icon and WSDLs.");

            log.info("Start removing unnecessary fault handlers from fault sequences ..........");
            migrateFrom320.removeUnnecessaryFaultHandlers();
            log.info("Successfully removed the unnecessary fault handlers from fault sequences.");

            log.info("Start API Revision related migration ..........");
            migrateFrom320.apiRevisionRelatedMigration();
            log.info("Successfully done the API Revision related migration.");

            log.info("Start migrating Endpoint Certificates  ..........");
            migrateFrom320.migrateEndpointCertificates();
            log.info("Successfully migrated Endpoint Certificates.");

            log.info("Start replacing KM name by UUID  ..........");
            migrateFrom320.replaceKMNamebyUUID();
            log.info("Successfully replaced KM name by UUID.");

            log.info("Migrated Successfully to 4.0.0");
        } catch (UserStoreException e) {
            e.printStackTrace();
        } catch (APIMigrationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void scopeMigration() throws APIMigrationException {

    }

    @Override
    public void dbMigration() throws APIMigrationException {

    }
}