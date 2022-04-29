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

package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.CommonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.v300.PopulateScopeRoleMappingMigrator;
import org.wso2.carbon.apimgt.migration.v300.V300RegistryResourceMigrator;
import org.wso2.carbon.user.api.UserStoreException;

public class V300Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V300Migration.class);

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
       RegistryResourceMigrator registryResourceMigrator= new V300RegistryResourceMigrator();
       registryResourceMigrator.migrate();
       PopulateScopeRoleMappingMigrator populateScopeRoleMappingMigrator = new PopulateScopeRoleMappingMigrator();
       populateScopeRoleMappingMigrator.migrate();
    }

}