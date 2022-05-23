/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.apimgt.migration.migrator.commonMigrators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.apimgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.FileUtil;

import java.io.IOException;
import java.util.List;

/**
 * Class to migrate registry resources from version to version
 */
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

        String rxtPath = rxtDir + Constants.API_RXT_FILE;

        for (Tenant tenant : tenants) {
            try {
                regService.startTenantFlow(tenant);

                log.info("Updating api.rxt for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                //Update api.rxt file
                String rxt = FileUtil.readFileToString(rxtPath);
                regService.updateRXTResource(Constants.API_RXT_FILE, rxt);
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
