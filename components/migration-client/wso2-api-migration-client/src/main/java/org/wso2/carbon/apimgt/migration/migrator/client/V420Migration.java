/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.apimgt.migration.migrator.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v420.V420RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.util.APIUtil;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.user.api.UserStoreException;

public class V420Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V420Migration.class);

    @Override public String getPreviousVersion() {
        return "4.1.0";
    }

    @Override public String getCurrentVersion() {
        return "4.2.0";
    }

    @Override public void migrate() throws UserStoreException, APIMigrationException {
        log.info("--------------------------------------------------------------------------------------------------");
        log.info("WSO2 API-M Migration Task : Starting migration from " + getPreviousVersion() + " to "
                + getCurrentVersion() + "...");
        log.info("--------------------------------------------------------------------------------------------------");

        // Setting ExtendedAPIMConfigService as disabled. This extended implementation is only needed and enabled for migrations
        // which are coming from versions before APIM 4.1. Also need to disable this for future migrations such as from APIM 4.2 to APIM 4.3.
        APIUtil.setDisabledExtendedAPIMConfigService(true);
        log.info("WSO2 API-M Migration Task : ExtendedAPIMConfigService is disabled");

        log.info(
                "WSO2 API-M Migration Task : Starting registry resource migration from " + getPreviousVersion() + " to "
                        + getCurrentVersion());
        V420RegistryResourceMigrator v420RegistryResourceMigrator = new V420RegistryResourceMigrator(
                Constants.V420_RXT_PATH);
        v420RegistryResourceMigrator.migrate();
        log.info("WSO2 API-M Migration Task : Completed registry resource migration from " + getPreviousVersion()
                + " to " + getCurrentVersion());
        log.info("WSO2 API-M Migration Task : Completed migration from " + getPreviousVersion() + " to "
                + getCurrentVersion() + "...");
    }
}
