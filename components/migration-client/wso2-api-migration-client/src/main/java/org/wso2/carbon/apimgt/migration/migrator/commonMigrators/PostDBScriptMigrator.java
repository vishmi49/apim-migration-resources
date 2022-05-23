package org.wso2.carbon.apimgt.migration.migrator.commonMigrators;

import org.wso2.carbon.apimgt.migration.util.AMDBUtil;

import java.sql.SQLException;

/**
 * Class to run post-migration DB scripts from version to version
 */
public class PostDBScriptMigrator {
    private String scriptPath;
    public PostDBScriptMigrator(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public void run() {
        try {
            AMDBUtil.runSQLScript(scriptPath, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
