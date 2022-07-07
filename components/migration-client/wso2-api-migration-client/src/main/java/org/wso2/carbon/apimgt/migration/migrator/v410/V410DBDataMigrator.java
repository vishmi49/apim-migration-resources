package org.wso2.carbon.apimgt.migration.migrator.v410;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.SystemConfigurationsDAO;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.apimgt.migration.dao.APIMgtDAO;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class V410DBDataMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(V410DBDataMigrator.class);
    List<Tenant> tenants;
    protected Registry registry;
    private TenantManager tenantManager;
    SystemConfigurationsDAO systemConfigurationsDAO = SystemConfigurationsDAO.getInstance();

    public V410DBDataMigrator() throws UserStoreException {
        tenants = loadTenants();
    }
    APIMgtDAO apiMgtDAO = APIMgtDAO.getInstance();

    @Override
    public void migrate() throws APIMigrationException {
        populateApiCategoryOrganizations();
        populateApplicationOrganizations();
        populateGWEnvironmentOrganizations();
        updateAPIOrganizations();
        migrateTenantConfToDB();
    }

    private void updateAPIOrganizations() throws APIMigrationException {
        apiMgtDAO.updateApiOrganizations();
    }
    private void populateApiCategoryOrganizations() throws APIMigrationException {

        try {
            Map<Integer, String> tenantIdsAndOrganizations = APIUtil.getAllTenantsWithSuperTenant().stream()
                    .collect(Collectors.toMap(Tenant::getId, Tenant::getDomain));
            apiMgtDAO.updateApiCategoryOrganizations(tenantIdsAndOrganizations);
        } catch (UserStoreException e) {
            throw new APIMigrationException("WSO2 API-M Migration Task : Failed to retrieve tenants");
        }
    }

    private void populateGWEnvironmentOrganizations() throws APIMigrationException {
        apiMgtDAO.populateGWEnvironmentOrganizations();
    }

    private void populateApplicationOrganizations() throws APIMigrationException {

        Map<Integer, String> subscriberOrganizations = new HashMap<>();
        Map<Integer, Integer> subscriberIdsAndTenantIds = apiMgtDAO.getSubscriberIdsAndTenantIds();
        for (Map.Entry<Integer, Integer> subscriberIdAndTenantId : subscriberIdsAndTenantIds.entrySet()) {
            String organization = APIUtil.getTenantDomainFromTenantId(subscriberIdAndTenantId.getValue());
            subscriberOrganizations.put(subscriberIdAndTenantId.getKey(), organization);
        }
        apiMgtDAO.updateApplicationOrganizations(subscriberOrganizations);
    }

    public void migrateTenantConfToDB() throws APIMigrationException {
        for (Tenant tenant : tenants) {
            V410ScopeMigrator.loadAndSyncTenantConf(tenant.getId());
            addTenantConfToDB(tenant);
        }
    }

    public void addTenantConfToDB(Tenant tenant) throws APIMigrationException {
        int tenantId = tenant.getId();
        String organization = APIUtil.getTenantDomainFromTenantId(tenantId);
        JSONObject tenantConf = Utility.getTenantConfigFromRegistry(tenant.getId());
        ObjectMapper mapper = new ObjectMapper();
        String formattedTenantConf = null;

        try {
            if (tenantConf != null) {
                formattedTenantConf = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tenantConf);
            }
        } catch (JsonProcessingException jse) {
            log.error("WSO2 API-M Migration Task : Error while JSON Processing tenant conf :" + jse);
            log.info("WSO2 API-M Migration Task : Hence, skipping tenant conf to db migration for tenant Id :"
                    + tenantId);
        } catch (IOException e) {
            log.error("WSO2 API-M Migration Task : Error occurred while writing tenant-conf.json value to string."
                    + tenantId, e);
        }

        if (formattedTenantConf != null) {
            try {
                String tenantConfig = systemConfigurationsDAO
                        .getSystemConfig(organization, APIConstants.ConfigType.TENANT.toString());
                if (StringUtils.isEmpty(tenantConfig)) {
                    systemConfigurationsDAO
                            .addSystemConfig(organization, APIConstants.ConfigType.TENANT.toString(), formattedTenantConf);
                    log.info("WSO2 API-M Migration Task : Added tenant-conf.json content to DB for tenant "
                            + tenant.getId() + '(' + tenant.getDomain() + ')');
                } else {
                    systemConfigurationsDAO
                            .updateSystemConfig(organization, APIConstants.ConfigType.TENANT.toString(), formattedTenantConf);
                    log.info("WSO2 API-M Migration Task : Updated tenant-conf.json content in DB for tenant "
                            + tenant.getId() + '(' + tenant.getDomain() + ')');
                }
            } catch (APIManagementException e) {
                log.info("WSO2 API-M Migration Task : Error while adding to tenant conf to database for tenant: "
                        + tenantId + "with Error :" + e);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("WSO2 API-M Migration Task : tenant-conf content is empty.");
            }
        }
    }
}
