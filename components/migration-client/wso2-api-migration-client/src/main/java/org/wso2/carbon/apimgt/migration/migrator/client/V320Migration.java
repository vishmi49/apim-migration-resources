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
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PreDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v320.V320DBDataMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v320.IdentityScopeMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v320.SPMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v320.ScopeMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v320.V320RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.user.api.UserStoreException;

public class V320Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V410Migration.class);

    @Override
    public String getPreviousVersion() {
        return "3.1.0";
    }

    @Override
    public String getCurrentVersion() {
        return "3.2.0";
    }

    @Override
    public void migrate() throws APIMigrationException, UserStoreException {
        log.info("Starting migration from " + getPreviousVersion() + " to " + getCurrentVersion() + "...");
        PreDBScriptMigrator preDBScriptMigrator = new PreDBScriptMigrator(Constants.V320_PRE_MIGRATION_SCRIPTS_PATH);
        preDBScriptMigrator.run();
        V320DBDataMigrator dbDataMigrator = new V320DBDataMigrator();
        dbDataMigrator.migrate();
        RegistryResourceMigrator registryResourceMigrator= new V320RegistryResourceMigrator(Constants.V320_RXT_PATH);
        registryResourceMigrator.migrate();
        ScopeMigrator scopeMigrator = new ScopeMigrator();
        scopeMigrator.migrate();
        SPMigrator spMigrator = new SPMigrator();
        spMigrator.migrate();
        IdentityScopeMigrator identityScopeMigrator = new IdentityScopeMigrator();
        identityScopeMigrator.migrate();
        log.info("Completed migration from " + getPreviousVersion() + " to " + getCurrentVersion() + "...");
    }


}