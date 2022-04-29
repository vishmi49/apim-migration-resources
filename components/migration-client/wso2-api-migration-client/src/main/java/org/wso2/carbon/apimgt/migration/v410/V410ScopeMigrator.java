package org.wso2.carbon.apimgt.migration.v410;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.Migrator;
import org.wso2.carbon.apimgt.migration.Utility;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.apimgt.migration.v320.V320RegistryResourceMigrator;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class V410ScopeMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(V410ScopeMigrator.class);
    private RegistryService registryService;
    List<Tenant> tenants;


    public V410ScopeMigrator() throws UserStoreException {
        tenants = loadTenants();
    }
    @Override
    public void migrate() throws APIMigrationException {

        for (Tenant tenant : tenants) {
            loadAndSyncTenantConf(tenant.getId());
        }
    }

    /**
     * Loads tenant-conf.json (tenant config) to registry from the tenant-conf.json available in the file system.
     * If any REST API scopes are added to the local tenant-conf.json, they will be updated in the registry.
     *
     * @param tenantID tenant Id
     * @throws APIManagementException when error occurred while loading the tenant-conf to registry
     */
    public static void loadAndSyncTenantConf(int tenantID) throws APIMigrationException {

        org.wso2.carbon.registry.core.service.RegistryService registryService = ServiceHolder.getRegistryService();
        try {
            UserRegistry registry = registryService.getConfigSystemRegistry(tenantID);
            byte[] data = Utility.getTenantConfFromFile();
            if (registry.resourceExists(APIConstants.API_TENANT_CONF_LOCATION)) {
                log.debug("tenant-conf of tenant " + tenantID + " is  already uploaded to the registry");
                Optional<Byte[]> migratedTenantConf = Utility.migrateTenantConfScopes(tenantID);
                if (migratedTenantConf.isPresent()) {
                    log.debug("Detected new additions to tenant-conf of tenant " + tenantID);
                    data = ArrayUtils.toPrimitive(migratedTenantConf.get());
                } else {
                    log.debug("No changes required in tenant-conf.json of tenant " + tenantID);
                    return;
                }
            }
            log.debug("Adding/updating tenant-conf.json to the registry of tenant " + tenantID);
            Utility.updateTenantConf(registry, data);
            log.debug("Successfully added/updated tenant-conf.json of tenant  " + tenantID);
        } catch (RegistryException e) {
            throw new APIMigrationException("Error while saving tenant conf to the registry of tenant " + tenantID, e);
        } catch (IOException e) {
            throw new APIMigrationException("Error while reading tenant conf file content of tenant " + tenantID, e);
        } catch (APIMigrationException e) {
            e.printStackTrace();
        }
    }
}
