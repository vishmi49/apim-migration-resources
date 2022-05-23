package org.wso2.carbon.apimgt.migration.migrator.commonMigrators;

import org.wso2.carbon.apimgt.migration.util.AMDBUtil;

import java.sql.SQLException;

/**
 * Class to run pre-migration DB scripts from version to version
 */
public class PreDBScriptMigrator {
    private String scriptPath;
    public PreDBScriptMigrator(String scriptPath) {
     this.scriptPath = scriptPath;
    }

    public void run() {
        try {
            AMDBUtil.runSQLScript(scriptPath, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
