package org.wso2.carbon.apimgt.migration.migrator.v310;

import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.user.api.UserStoreException;

public class V310RegistryResourceMigrator extends RegistryResourceMigrator {
    public V310RegistryResourceMigrator() throws UserStoreException {
    }
    public void migrate() throws APIMigrationException {
        super.migrate();
    }
}
