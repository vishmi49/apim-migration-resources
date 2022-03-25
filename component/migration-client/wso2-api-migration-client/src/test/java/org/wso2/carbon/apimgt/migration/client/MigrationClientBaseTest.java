/*
 *   Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.apimgt.migration.client;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.mockito.Mockito;
import org.wso2.carbon.apimgt.migration.util.RegistryService;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.Tenant;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.util.TreeMap;

public class MigrationClientBaseTest {

    MigrationClientBase clientBase = null;
    RegistryService mockRegistryService = Mockito.mock(RegistryService.class);

    @Test
    public void testGet400To410MigrationServiceList() throws UserStoreException {
        String migrateFromVersion_400 = "4.0.0";
        TenantManager mockTenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(mockTenantManager.getAllTenants()).thenReturn(new Tenant[]{new Tenant()});
        clientBase = new MigrationClientBase(null, null, null,
                mockTenantManager);
        TreeMap<String, MigrationClient>  list =
                clientBase.getMigrationServiceList(mockRegistryService, migrateFromVersion_400);
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.containsKey(migrateFromVersion_400));
        Assert.assertTrue(list.get(migrateFromVersion_400) instanceof MigrateFrom400);
    }
}