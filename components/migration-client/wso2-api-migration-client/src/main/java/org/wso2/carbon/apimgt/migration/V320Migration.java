package org.wso2.carbon.apimgt.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.migration.CommonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.v320.SPMigrator;
import org.wso2.carbon.apimgt.migration.v320.ScopeMigrator;
import org.wso2.carbon.apimgt.migration.v320.V320RegistryResourceMigrator;
import org.wso2.carbon.user.api.UserStoreException;

public class V320Migration extends VersionMigrator {
    private static final Log log = LogFactory.getLog(V410Migration.class);
    Migrator migrator;

    @Override
    public String getPreviousVersion() {
        return "3.1.0";
    }

    @Override
    public String getCurrentVersion() {
        return "3.2.0";
    }

    @Override
    public void migrate() throws APIMigrationException, UserStoreException {
        RegistryResourceMigrator registryResourceMigrator= new V320RegistryResourceMigrator();
        registryResourceMigrator.migrate();
        ScopeMigrator scopeMigrator = new ScopeMigrator();
        scopeMigrator.migrate();
        SPMigrator spMigrator = new SPMigrator();
        spMigrator.migrate();
        log.info("Migrated Successfully to 3.2");
    }


}