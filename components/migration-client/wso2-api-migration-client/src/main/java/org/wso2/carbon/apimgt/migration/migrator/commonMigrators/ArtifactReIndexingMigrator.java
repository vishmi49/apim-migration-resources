package org.wso2.carbon.apimgt.migration.migrator.commonMigrators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.util.AMDBUtil;
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
            AMDBUtil.runSQLScript(Constants.ARTIFACT_REINDEXING_SCRIPT_PATH, true);
        } catch (SQLException e) {
            log.error("Error running the artifact re-indexing script", e);
        }
        log.info("Artifact re-indexing migrator completed");
    }
}
