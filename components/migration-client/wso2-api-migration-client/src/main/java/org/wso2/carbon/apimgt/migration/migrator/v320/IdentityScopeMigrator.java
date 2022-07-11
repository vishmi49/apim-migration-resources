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
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.Scope;
import org.wso2.carbon.apimgt.impl.dao.ScopesDAO;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.migrator.v320.dao.ApiMgtDAO;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * This class is used to migrate scopes from IDN_OAUTH2_SCOPE table to AM_SCOPE table during 3.1.0 to 3.2.0 migration
 */
public class IdentityScopeMigrator extends Migrator {

    private static final Log log = LogFactory.getLog(IdentityScopeMigrator.class);

    @Override
    public void migrate() throws APIMigrationException {
        boolean scopesMigrated = ApiMgtDAO.getInstance().isScopesMigrated();
        if (!scopesMigrated) {
            log.info("WSO2 API-M Migration Task : Starting identity scope migration");
            List<String> identityScopes = ApiMgtDAO.getInstance().retrieveIdentityScopes();
            Map<Integer, Map<String, Scope>> scopesMap = ApiMgtDAO.getInstance().migrateIdentityScopes(identityScopes);

            try {
                for (Map.Entry<Integer, Map<String, Scope>> scopesMapEntry : scopesMap.entrySet()) {
                    ScopesDAO scopesDAO = ScopesDAO.getInstance();
                    Map<String, Scope> scopeMap = scopesMapEntry.getValue();
                    if (scopeMap != null) {
                        Set<Scope> scopeSet = new HashSet<>(scopeMap.values());
                        scopesDAO.addScopes(scopeSet, scopesMapEntry.getKey());
                        String scopeStr = scopeSet.stream().map(scope -> scope.getKey())
                                .collect(Collectors.joining(", "));
                        log.info("WSO2 API-M Migration Task : Successfully migrated scopes ("
                                + scopeStr + ") of tenant: "+ scopesMapEntry.getKey() +" from IDN_OAUTH2_SCOPE table "
                                + "to AM_SCOPE table");
                    }
                }
            } catch (APIManagementException e) {
                throw new APIMigrationException("WSO2 API-M Migration Task : Error occurred while migrating identity "
                        + "scopes", e);
            }
        } else {
            log.info("WSO2 API-M Migration Task : Scopes are already migrated, hence skipping this step");
        }
    }

}
