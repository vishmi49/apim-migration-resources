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

package org.wso2.carbon.apimgt.migration.migrator.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PostDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PreDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v410.V410DBDataMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v410.V410RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.user.api.UserStoreException;

public class V410Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V410Migration.class);

    @Override
    public String getPreviousVersion() {
        return "4.0.0";
    }

    @Override
    public String getCurrentVersion() {
        return "4.1.0";
    }
    @Override
    public void migrate() throws UserStoreException, APIMigrationException {
        log.info("--------------------------------------------------------------------------------------------------");
        log.info("WSO2 API-M Migration Task : Starting migration from " + getPreviousVersion() + " to "
                + getCurrentVersion() + "...");
        log.info("--------------------------------------------------------------------------------------------------");

        log.info("WSO2 API-M Migration Task : Starting AM_DB schema migration from 4.0.0 to 4.1.0");
        PreDBScriptMigrator preDBScriptMigrator = new PreDBScriptMigrator(Constants.V410_PRE_MIGRATION_SCRIPTS_PATH);
        preDBScriptMigrator.run();
        log.info("WSO2 API-M Migration Task : Starting AM_DB schema migration from 4.0.0 to 4.1.0");

        log.info("WSO2 API-M Migration Task : Starting AM_DB data migration from 4.0.0 to 4.1.0");
        V410DBDataMigrator v410DBDataMigrator = new V410DBDataMigrator();
        v410DBDataMigrator.migrate();
        log.info("WSO2 API-M Migration Task : Completed AM_DB data migration from 4.0.0 to 4.1.0");

        log.info("WSO2 API-M Migration Task : Starting registry resource migration from 4.0.0 to 4.1.0");
        V410RegistryResourceMigrator v410RegistryResourceMigrator =
                new V410RegistryResourceMigrator(Constants.V410_RXT_PATH);
        v410RegistryResourceMigrator.migrate();
        log.info("WSO2 API-M Migration Task : Completed registry resource migration from 4.0.0 to 4.1.0");

        log.info("WSO2 API-M Migration Task : Running post migration DB scripts");
        PostDBScriptMigrator postDBScriptMigratorForAmDb =
                new PostDBScriptMigrator(Constants.V410_POST_MIGRATION_SCRIPT_AMDB_PATH);
        postDBScriptMigratorForAmDb.run();
        log.info("WSO2 API-M Migration Task : Successfully executed post migration DB scripts");

        log.info("WSO2 API-M Migration Task : Completed migration from " + getPreviousVersion() + " to " + getCurrentVersion() + "...");
    }
}