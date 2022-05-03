package org.wso2.carbon.apimgt.migration.migrator.v410;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.impl.utils.APIVersionComparator;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.dao.APIMgtDAO;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifactImpl;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.*;

import static org.wso2.carbon.utils.multitenancy.MultitenantUtils.getTenantAwareUsername;

public class V410RegistryResourceMigrator extends RegistryResourceMigrator {
    private static final Log log = LogFactory.getLog(V410RegistryResourceMigrator.class);
    private RegistryService registryService;
    APIMgtDAO apiMgtDAO = APIMgtDAO.getInstance();
    List<Tenant> tenants;

    public V410RegistryResourceMigrator() throws UserStoreException {
        registryService = new RegistryServiceImpl();
        tenants = loadTenants();
    }

    public void migrate() throws APIMigrationException {
        super.migrate();
        registryDataPopulation();
    }

    private void registryDataPopulation() throws APIMigrationException {

        log.info("Registry data population for API Manager " + Constants.VERSION_4_0_0 + " started.");

        boolean isTenantFlowStarted = false;
        for (Tenant tenant : tenants) {
            if (log.isDebugEnabled()) {
                log.debug("Start rxtMigration for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
            }
            try {
                PrivilegedCarbonContext.startTenantFlow();
                isTenantFlowStarted = true;

                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenant.getDomain(), true);
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenant.getId(), true);

                String adminName = getTenantAwareUsername(APIUtil.replaceEmailDomainBack(tenant.getAdminName()));

                if (log.isDebugEnabled()) {
                    log.debug("Tenant admin username : " + adminName);
                }

                ServiceHolder.getTenantRegLoader().loadTenantRegistry(tenant.getId());
                UserRegistry registry = ServiceHolder.getRegistryService().getGovernanceSystemRegistry(tenant.getId());
                GenericArtifactManager artifactManager = APIUtil.getArtifactManager(registry, APIConstants.API_KEY);

                if (artifactManager != null) {
                    GovernanceUtils.loadGovernanceArtifacts(registry);
                    GenericArtifact[] artifacts = artifactManager.getAllGenericArtifacts();
                    Map<String, List<API>> apisMap = new TreeMap<>();
                    Map<API, GenericArtifact> apiToArtifactMapping = new HashMap<>();

                    for (GenericArtifact artifact : artifacts) {
                        try {
                            String artifactPath = ((GenericArtifactImpl) artifact).getArtifactPath();
                            if (artifactPath.contains("/apimgt/applicationdata/apis/")) {
                                continue;
                            }
                            API api = APIUtil.getAPI(artifact, registry);
                            if (StringUtils.isNotEmpty(api.getVersionTimestamp())) {
                                if (log.isDebugEnabled()) {
                                    log.info(
                                            "VersionTimestamp already available in APIName: " + api.getId().getApiName()
                                                    + api.getId().getVersion());
                                }
                            }
                            if (api == null) {
                                log.error("Cannot find corresponding api for registry artifact " + artifact
                                        .getAttribute("overview_name") + '-' + artifact.getAttribute("overview_version")
                                        + '-' + artifact.getAttribute("overview_provider") + " of tenant " + tenant
                                        .getId() + '(' + tenant.getDomain() + ") in AM_DB");
                                continue;
                            }

                            if (log.isDebugEnabled()) {
                                log.debug("Doing the RXT migration for API : " + artifact.getAttribute("overview_name")
                                        + '-' + artifact.getAttribute("overview_version") + '-' + artifact
                                        .getAttribute("overview_provider") + '-' + artifact
                                        .getAttribute("overview_versionComparable") + '-' + " of tenant " + tenant.getId() + '('
                                        + tenant.getDomain() + ")");
                            }
                            if (!apisMap.containsKey(api.getId().getApiName())) {
                                List<API> versionedAPIsList = new ArrayList<>();
                                apisMap.put(api.getId().getApiName(), versionedAPIsList);

                            }
                            apisMap.get(api.getId().getApiName()).add(api);
                            if (!apiToArtifactMapping.containsKey(api)) {
                                apiToArtifactMapping.put(api, artifact);
                            }
                        } catch (Exception e) {
                            // we log the error and continue to the next resource.
                            throw new APIMigrationException("Unable to migrate api metadata definition of API : " + artifact
                                    .getAttribute("overview_name") + '-' + artifact
                                    .getAttribute("overview_version") + '-' + artifact
                                    .getAttribute("overview_provider"), e);
                        }
                    }

                    // set the versionTimestamp for each API
                    for (String apiName : apisMap.keySet()) {
                        List<API> versionedAPIList = apisMap.get(apiName);
                        versionedAPIList.sort(new APIVersionComparator());
                        long versionTimestamp = System.currentTimeMillis();
                        long oneDay = 86400;
                        for (int i = versionedAPIList.size(); i > 0; i--) {
                            API apiN = versionedAPIList.get(i - 1);
                            apiN.setVersionTimestamp(versionTimestamp + "");
                            apiToArtifactMapping.get(apiN)
                                    .setAttribute("overview_versionComparable", String.valueOf(versionTimestamp));
                            log.info("Setting Version Comparable for API " + apiN.getUuid());
                            try {
                                artifactManager.updateGenericArtifact(apiToArtifactMapping.get(apiN));
                            } catch (GovernanceException e) {
                                throw new APIMigrationException(
                                        "Failed to update versionComparable for API: " + apiN.getId().getApiName()
                                                + " version: " + apiN.getId().getVersion() + " versionComparable: "
                                                + apiN.getVersionTimestamp() + " at registry");
                            }
                            versionTimestamp -= oneDay;
                            GenericArtifact artifact;
                            try {
                                artifact = artifactManager.getGenericArtifact(apiN.getUuid());
                            } catch (GovernanceException e) {
                                throw new APIMigrationException(
                                        "Failed to retrieve API: " + apiN.getId().getApiName() + " version: " + apiN
                                                .getId().getVersion() + " from registry.");
                            }
                            // validate registry update
                            API api = APIUtil.getAPI(artifact, registry);
                            if (StringUtils.isEmpty(api.getVersionTimestamp())) {
                                log.error("VersionComparable is empty for API: " + apiN.getId().getApiName()
                                        + " version: " + apiN.getId().getVersion() + " versionComparable: " + api
                                        .getVersionTimestamp() + " at registry.");
                            } else {
                                log.info("VersionTimestamp successfully updated API: " + apiN.getId().getApiName()
                                        + " version: " + apiN.getId().getVersion() + " versionComparable: " + api
                                        .getVersionTimestamp());
                            }
                        }
                        try {
                            apiMgtDAO.populateApiVersionTimestamp(versionedAPIList);
                        } catch (APIMigrationException e) {
                            throw new APIMigrationException("Exception while populating versionComparable for api "
                                    + apiName + " tenant: " + tenant.getDomain() + "at database");
                        }
                    }
                    log.info("Successfully migrated data for api rxts to include versionComparable..........");
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("No api artifacts found in registry for tenant " + tenant.getId() + '(' + tenant
                                .getDomain() + ')');
                    }
                }
            } catch (APIManagementException e) {
                throw new APIMigrationException("Error occurred while reading API from the artifact ", e);
            } catch (RegistryException e) {
                throw new APIMigrationException("Error occurred while accessing the registry ", e);
            } finally {
                if (isTenantFlowStarted) {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("End rxtMigration for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
            }
        }
        log.info("Rxt resource migration done for all the tenants");
    }
}
