/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.migration.migrator.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.VersionMigrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.PreDBScriptMigrator;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v300.PopulateScopeRoleMappingMigrator;
import org.wso2.carbon.apimgt.migration.migrator.v300.V300RegistryResourceMigrator;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.File;

public class V300Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V300Migration.class);
    private final String PRE_MIGRATION_SCRIPTS_PATH = Utility.PRE_MIGRATION_SCRIPT_DIR + "migration-2.6.0_to_3.0.0"
            + File.separator;
    private final String V300_RXT_DIR = Utility.RXT_DIR + "3.0.0" + File.separator;

    @Override
    public String getPreviousVersion() {
        return "2.6.0";
    }

    @Override
    public String getCurrentVersion() {
        return "3.0.0";
    }

    @Override
    public void migrate() throws APIMigrationException, UserStoreException {
        log.info("Starting migration from " + getPreviousVersion() + " to " + getCurrentVersion() + "...");
        PreDBScriptMigrator v300preMigrator = new PreDBScriptMigrator(PRE_MIGRATION_SCRIPTS_PATH);
        v300preMigrator.run();
        RegistryResourceMigrator registryResourceMigrator = new V300RegistryResourceMigrator(V300_RXT_DIR);
        registryResourceMigrator.migrate();
        PopulateScopeRoleMappingMigrator populateScopeRoleMappingMigrator = new PopulateScopeRoleMappingMigrator();
        populateScopeRoleMappingMigrator.migrate();
        log.info("Completed migration from " + getPreviousVersion() + " to " + getCurrentVersion() + "...");
    }
}
