package org.wso2.carbon.apimgt.migration.migrator.commonMigrators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.apimgt.migration.util.AMDBUtil;

import java.io.File;
import java.sql.SQLException;

public class ArtifactReIndexingMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(ArtifactReIndexingMigrator.class);
    private final String artifactReIndexingScriptPath = Utility.POST_MIGRATION_SCRIPT_DIR + "common" + File.separator
            + "reg_db" + File.separator + "reg-index.sql";

    @Override
    public void migrate() throws APIMigrationException {
        try {
            AMDBUtil.runSQLScript(artifactReIndexingScriptPath, true);
        } catch (SQLException e) {
            log.error("Error running the artifact re-indexing script", e);
        }
    }
}
