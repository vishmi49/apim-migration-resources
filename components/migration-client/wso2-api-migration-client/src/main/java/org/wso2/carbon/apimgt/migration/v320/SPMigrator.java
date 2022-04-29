package org.wso2.carbon.apimgt.migration.v320;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.Migrator;
import org.wso2.carbon.apimgt.migration.dao.APIMgtDAO;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.ArrayList;
import java.util.List;

public class SPMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(SPMigrator.class);

    private RegistryService registryService;
    List<Tenant> tenants;
    public SPMigrator() throws UserStoreException {
        tenants = loadTenants();
        registryService = new RegistryServiceImpl();
    }
    @Override
    public void migrate() throws APIMigrationException {

        List<Tenant> tenantList = tenants;
        // Iterate for each tenant. The reason we do this migration step wise for each tenant is so that, we do not
        // overwhelm the amount of rows returned for each database call in systems with a large tenant count.
        for (Tenant tenant : tenantList) {
            ArrayList<String> consumerKeys = APIMgtDAO.getAppsOfTypeJWT(tenant.getId());
            if (consumerKeys != null) {
                for (String consumerKey : consumerKeys) {
                    APIMgtDAO.updateTokenTypeToJWT(consumerKey);
                }
            }
        }
    }
}
