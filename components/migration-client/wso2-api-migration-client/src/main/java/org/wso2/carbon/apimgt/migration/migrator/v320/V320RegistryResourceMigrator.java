package org.wso2.carbon.apimgt.migration.migrator.v320;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.List;

public class V320RegistryResourceMigrator extends RegistryResourceMigrator {
    private static final Log log = LogFactory.getLog(V320RegistryResourceMigrator.class);
    private RegistryService registryService;
    List<Tenant> tenants;

    public V320RegistryResourceMigrator(String rxtDir) throws UserStoreException {
        super(rxtDir);
        tenants = loadTenants();
        registryService = new RegistryServiceImpl();
    }
    public void migrate() throws APIMigrationException {
        super.migrate();
        updateEnableStoreInRxt();
    }

    private void updateEnableStoreInRxt() {

        for (Tenant tenant : tenants ){
            try {
                registryService.startTenantFlow(tenant);
                log.debug("Updating APIs for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                GenericArtifact[] artifacts = registryService.getGenericAPIArtifacts();
                for (GenericArtifact artifact : artifacts) {
                    String path = artifact.getPath();
                    if (registryService.isGovernanceRegistryResourceExists(path)) {
                        Object apiResource = registryService.getGovernanceRegistryResource(path);
                        if (apiResource == null) {
                            continue;
                        }
                        registryService.updateEnableStoreInRxt(path, artifact);
                    }
                }
                log.info("Completed Updating API artifacts tenant ---- " + tenant.getId() + '(' + tenant.getDomain() + ')');
            } catch (GovernanceException e) {
                log.error("Error while accessing API artifact in registry for tenant " + tenant.getId() + '(' +
                        tenant.getDomain() + ')', e);
            } catch (RegistryException | UserStoreException e) {
                log.error("Error while updating API artifact in the registry for tenant " + tenant.getId() + '(' +
                        tenant.getDomain() + ')', e);
            } finally {
                registryService.endTenantFlow();
            }
        }
    }
}
