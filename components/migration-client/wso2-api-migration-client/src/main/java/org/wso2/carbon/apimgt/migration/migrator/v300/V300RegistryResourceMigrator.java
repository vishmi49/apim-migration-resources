package org.wso2.carbon.apimgt.migration.migrator.v300;

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

public class V300RegistryResourceMigrator extends RegistryResourceMigrator {
    private static final Log log = LogFactory.getLog(V300RegistryResourceMigrator.class);
    private RegistryService registryService;
    List<Tenant> tenants;

    public V300RegistryResourceMigrator(String rxtDir) throws UserStoreException {
        super(rxtDir);
        tenants = loadTenants();
        registryService = new RegistryServiceImpl();
    }

    @Override
    public void migrate() throws APIMigrationException {
        super.migrate();
        updateGenericAPIArtifacts(registryService);
    }

    /**
     * This method is used to update the API artifacts in the registry
     * - to migrate Publisher Access Control feature related data.
     * - to add overview_type property to API artifacts
     * - to add 'enableStore' rxt field
     *
     * @throws APIMigrationException
     */
    private void updateGenericAPIArtifacts(RegistryService registryService) {
        for (Tenant tenant : tenants) {
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
                        registryService.updateGenericAPIArtifactsForAccessControl(path, artifact);
                        registryService.updateGenericAPIArtifact(path, artifact);
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
