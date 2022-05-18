package org.wso2.carbon.apimgt.migration.migrator.commonMigrators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.migrator.Utility;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.utils.FileUtil;

import java.io.IOException;
import java.nio.charset.Charset;

public class APIRXTMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(APIRXTMigrator.class);
    private final String rxtPath;

    public APIRXTMigrator(String rxtPath) {
        this.rxtPath = rxtPath;
    }

    @Override
    public void migrate() throws APIMigrationException {
        RegistryService registryService = ServiceReferenceHolder.getInstance().getRegistryService();
        UserRegistry systemRegistry;
        try {
            systemRegistry = registryService.getRegistry(CarbonConstants.REGISTRY_SYSTEM_USERNAME);
        } catch (RegistryException e) {
            throw new APIMigrationException("Failed to get the registry", e);
        }
        try {
            if (systemRegistry.resourceExists(Utility.REGISTRY_API_RXT_PATH)) {
                Resource resource = systemRegistry.get(Utility.REGISTRY_API_RXT_PATH);
                String rxt = FileUtil.readFileToString(rxtPath);
                resource.setContent(rxt.getBytes(Charset.defaultCharset()));
                resource.setMediaType(APIConstants.RXT_MEDIA_TYPE);
                systemRegistry.put(Utility.REGISTRY_API_RXT_PATH, resource);
            } else {
                log.error("api.rxt is not available in the registry");
            }
        } catch (IOException e) {
            log.error("Error file reading the api.rxt in migration resources", e);
        } catch (RegistryException e) {
            log.error("Registry exception occurred when updating the registry api.rxt", e);
        }
    }
}
