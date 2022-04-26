package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.client.MigrationClientBase;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Migrator {
    private static final Log log = LogFactory.getLog(MigrationClientBase.class);
    protected final String PRE_MIGRATION_SCRIPT_DIR = CarbonUtils.getCarbonHome() + File.separator
            + "migration-resources" + File.separator + "pre-migration-scripts" + File.separator;
    protected final String POST_MIGRATION_SCRIPT_DIR = CarbonUtils.getCarbonHome() + File.separator
            + "migration-resources" + File.separator + "post-migration-scripts" + File.separator;
    private List<Tenant> tenantsArray;
    private String tenantArguments;
    private String blackListTenantArguments;
    private final String tenantRange;
    private final TenantManager tenantManager;

    public Migrator(String tenantArguments, String blackListTenantArguments, String tenantRange,
                    TenantManager tenantManager)
            throws UserStoreException {
        this.tenantManager = tenantManager;
        this.tenantRange = tenantRange;
        if (tenantArguments != null) {  // Tenant arguments have been provided so need to load specific ones
            tenantArguments = tenantArguments.replaceAll("\\s", ""); // Remove spaces and tabs

            tenantsArray = new ArrayList<>();

            buildTenantList(tenantManager, tenantsArray, tenantArguments);
            this.tenantArguments = tenantArguments;
        } else if (blackListTenantArguments != null) {
            blackListTenantArguments = blackListTenantArguments.replaceAll("\\s", ""); // Remove spaces and tabs

            List<Tenant> blackListTenants = new ArrayList<>();
            buildTenantList(tenantManager, blackListTenants, blackListTenantArguments);
            this.blackListTenantArguments = blackListTenantArguments;

            List<Tenant> allTenants = new ArrayList<>(Arrays.asList(tenantManager.getAllTenants()));
            Tenant superTenant = new Tenant();
            superTenant.setDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            superTenant.setId(MultitenantConstants.SUPER_TENANT_ID);
            allTenants.add(superTenant);

            tenantsArray = new ArrayList<>();

            for (Tenant tenant : allTenants) {
                boolean isBlackListed = false;
                for (Tenant blackListTenant : blackListTenants) {
                    if (blackListTenant.getId() == tenant.getId()) {
                        isBlackListed = true;
                        break;
                    }
                }

                if (!isBlackListed) {
                    tenantsArray.add(tenant);
                }
            }
        } else if (tenantRange != null) {
            tenantsArray = new ArrayList<Tenant>();
            int l, u;
            try {
                l = Integer.parseInt(tenantRange.split("-")[0].trim());
                u = Integer.parseInt(tenantRange.split("-")[1].trim());
            } catch (Exception e) {
                throw new UserStoreException("TenantRange argument is not properly set. use format 1-12", e);
            }
            log.debug("no of Tenants " + tenantManager.getAllTenants().length);
            int lastIndex = tenantManager.getAllTenants().length - 1;
            log.debug("last Tenant id " + tenantManager.getAllTenants()[lastIndex].getId());
            for (Tenant t : tenantManager.getAllTenants()) {
                if (t.getId() > l && t.getId() < u) {
                    log.debug("using tenants " + t.getDomain() + "(" + t.getId() + ")");
                    tenantsArray.add(t);
                }
            }
        } else {  // Load all tenants
            tenantsArray = new ArrayList<>(Arrays.asList(tenantManager.getAllTenants()));
            Tenant superTenant = new Tenant();
            superTenant.setDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            superTenant.setId(MultitenantConstants.SUPER_TENANT_ID);
            tenantsArray.add(superTenant);
        }
        setAdminUserName(tenantManager);
    }

    public abstract String getPreviousVersion();

    public abstract String getCurrentVersion();

    public abstract void runPreMigrationScripts() throws SQLException;

    public abstract void migrate();

    public abstract void runPostMigrationScripts() throws SQLException;

    public void doMigration() throws SQLException {
        runPreMigrationScripts();
        migrate();
        runPostMigrationScripts();
    }

    private void buildTenantList(TenantManager tenantManager, List<Tenant> tenantList, String tenantArguments)
            throws UserStoreException {
        if (tenantArguments.contains(",")) { // Multiple arguments specified
            String[] parts = tenantArguments.split(",");

            for (String part : parts) {
                if (part.length() > 0) {
                    populateTenants(tenantManager, tenantList, part);
                }
            }
        } else { // Only single argument provided
            populateTenants(tenantManager, tenantList, tenantArguments);
        }
    }
    private void setAdminUserName(TenantManager tenantManager) throws UserStoreException {
        log.debug("Setting tenant admin names");
        for (int i = 0; i < tenantsArray.size(); ++i) {
            Tenant tenant = tenantsArray.get(i);
            if (tenant.getId() == MultitenantConstants.SUPER_TENANT_ID) {
                tenant.setAdminName("admin");
            }
            else {
                tenantsArray.set(i, tenantManager.getTenant(tenant.getId()));
            }
        }
    }

    private void populateTenants(TenantManager tenantManager, List<Tenant> tenantList, String argument) throws UserStoreException {

        if (log.isDebugEnabled()) {
            log.debug("Argument provided : " + argument);
        }

        if (argument.contains("@")) { // Username provided as argument
            int tenantID = tenantManager.getTenantId(argument);

            if (tenantID != -1) {
                tenantList.add(tenantManager.getTenant(tenantID));
            } else {
                log.error("Tenant does not exist for username " + argument);
            }
        } else { // Domain name provided as argument
            if (MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equalsIgnoreCase(argument)) {
                Tenant superTenant = new Tenant();
                superTenant.setDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
                superTenant.setId(MultitenantConstants.SUPER_TENANT_ID);
                tenantList.add(superTenant);
            }
            else {
                Tenant[] tenants = tenantManager.getAllTenantsForTenantDomainStr(argument);

                if (tenants.length > 0) {
                    tenantList.addAll(Arrays.asList(tenants));
                } else {
                    log.error("Tenant does not exist for domain " + argument);
                }
            }
        }
    }
}