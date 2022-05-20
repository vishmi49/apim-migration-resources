package org.wso2.carbon.apimgt.migration.migrator.commonMigrators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RegistryResourceMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(RegistryResourceMigrator.class);
    private RegistryService registryService;
    List<Tenant> tenants;
    private String rxtDir;

    public RegistryResourceMigrator(String rxtDir) throws UserStoreException {
       tenants = loadTenants();
       registryService = new RegistryServiceImpl();
       this.rxtDir = rxtDir;
    }

    @Override
    public void migrate() throws APIMigrationException {
        rxtMigration(registryService);
    }

    private void rxtMigration(RegistryService regService) {
        log.info("Rxt migration for API Manager started.");

        String rxtPath = rxtDir + Utility.API_RXT_FILE;

        for (Tenant tenant : tenants) {
            try {
                regService.startTenantFlow(tenant);

                log.info("Updating api.rxt for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                //Update api.rxt file
                String rxt = FileUtil.readFileToString(rxtPath);
                regService.updateRXTResource(Utility.API_RXT_FILE, rxt);
                log.info("End Updating api.rxt for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
            } catch (IOException e) {
                log.error("Error when reading api.rxt from " + rxtPath + " for tenant " + tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            } catch (RegistryException | UserStoreException e) {
                log.error("Error while updating api.rxt in the registry for tenant " + tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            } finally {
                registryService.endTenantFlow();
            }
        }
        log.info("Rxt resource migration done for all the tenants");
    }
}
