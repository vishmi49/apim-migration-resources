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

package org.wso2.carbon.apimgt.migration.migrator.v320;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
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
            if (!consumerKeys.isEmpty()) {
                log.info("WSO2 API-M Migration Task : Updating tokenType property of service providers for JWT "
                        + "typed applications in tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                for (String consumerKey : consumerKeys) {
                    APIMgtDAO.updateTokenTypeToJWT(consumerKey);
                    log.info("WSO2 API-M Migration Task : Updated tokenType property of service provider identified "
                            + "by consumer key " + consumerKey + " as JWT");
                }
                log.info("WSO2 API-M Migration Task : Completed updating tokenType property of service providers for"
                        + " JWT typed applications in tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
            }
        }
    }
}
