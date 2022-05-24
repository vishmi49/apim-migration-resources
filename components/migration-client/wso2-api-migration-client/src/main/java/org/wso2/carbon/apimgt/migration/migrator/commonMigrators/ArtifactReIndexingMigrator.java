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
import org.wso2.carbon.apimgt.migration.dao.SharedDAO;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.util.Constants;

import java.sql.SQLException;

/**
 * Class to run artifact re-indexing scripts post-migration
 */
public class ArtifactReIndexingMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(ArtifactReIndexingMigrator.class);

    @Override
    public void migrate() throws APIMigrationException {
        log.info("Artifact re-indexing migrator started");
        try {
            SharedDAO.getInstance().runSQLScript(Constants.ARTIFACT_REINDEXING_SCRIPT_PATH);
        } catch (SQLException e) {
            log.error("Error running the artifact re-indexing script", e);
        }
        log.info("Artifact re-indexing migrator completed");
    }
}
