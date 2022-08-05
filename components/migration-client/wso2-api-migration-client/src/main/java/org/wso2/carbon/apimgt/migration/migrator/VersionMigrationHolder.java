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


package org.wso2.carbon.apimgt.migration.migrator;

import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.migrator.client.*;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.util.ArrayList;
import java.util.List;

public class VersionMigrationHolder {
    private static VersionMigrationHolder versionMigrationHolder;

    static {
        try {
            versionMigrationHolder = new VersionMigrationHolder();
        } catch (UserStoreException e) {
            e.printStackTrace();
        }
    }

    private List<VersionMigrator> versionMigrationList = new ArrayList<>();

    private VersionMigrationHolder() throws UserStoreException {
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();

        versionMigrationList.add(new V300Migration());
        versionMigrationList.add(new V310Migration());
        versionMigrationList.add(new V320Migration());
        versionMigrationList.add(new V400Migration());
        versionMigrationList.add(new V410Migration());
        versionMigrationList.add(new V420Migration());
    }

    public static VersionMigrationHolder getInstance() {

        return VersionMigrationHolder.versionMigrationHolder;
    }

    public List<VersionMigrator> getVersionMigrationList() {

        return versionMigrationList;
    }
}