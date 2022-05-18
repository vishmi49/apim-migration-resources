package org.wso2.carbon.apimgt.migration.migrator.v400.ImportExport;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIProvider;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.APIProduct;
import org.wso2.carbon.apimgt.impl.importexport.APIImportExportException;
import org.wso2.carbon.apimgt.impl.importexport.ExportFormat;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.rest.api.common.RestApiCommonUtil;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.common.mappings.APIMappingUtil;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIDTO;

import java.io.File;
import java.io.InputStream;

/**
 * Osgi Service implementation for import export API.
 */
@Component(
        name = "import.export.service.component",
        immediate = true,
        service = ImportExportAPI.class
)
public class ImportExportAPIServiceImpl implements ImportExportAPI {

    @Override
    public File exportAPI(String apiId, String name, String version, String revisionNum, String providerName,
                          boolean preserveStatus, ExportFormat format, boolean preserveDocs, boolean preserveCredentials,
                          boolean exportLatestRevision, String originalDevPortalUrl)
            throws APIManagementException, APIImportExportException {

        APIIdentifier apiIdentifier;
        APIDTO apiDtoToReturn;
        APIProvider apiProvider = RestApiCommonUtil.getLoggedInUserProvider();
        String userName = RestApiCommonUtil.getLoggedInUsername();
        String tenantDomain = RestApiCommonUtil.getLoggedInUserTenantDomain();
        API api;
        String exportAPIUUID;

        // apiId == null means the path from the API Controller
        if (apiId == null) {
            // Validate API name, version and provider before exporting
            String provider = ExportUtils.validateExportParams(name, version, providerName);
            apiIdentifier = new APIIdentifier(APIUtil.replaceEmailDomain(provider), name, version);
            apiId = APIUtil.getUUIDFromIdentifier(apiIdentifier);
            if (apiId == null) {
                throw new APIImportExportException("API Id not found for the provided details");
            }
        } else {
            apiIdentifier = APIMappingUtil.getAPIIdentifierFromUUID(apiId);
        }

        if (exportLatestRevision) {
            //if a latest revision flag used, latest revision's api object is used
            exportAPIUUID = apiProvider.getLatestRevisionUUID(apiId);
        } else if (StringUtils.isNotBlank(revisionNum)) {
            //if a revision number provided, revision api object is used
            exportAPIUUID = apiProvider.getAPIRevisionUUID(revisionNum, apiId);
        } else {
            //if a revision number is not provided, working copy's id is used
            exportAPIUUID = apiId;
        }
        //If an incorrect revision num provided or revision does not exists, working copy will be exported
        exportAPIUUID = (exportAPIUUID == null) ? apiId : exportAPIUUID;
        api = apiProvider.getAPIbyUUID(exportAPIUUID, tenantDomain);
        apiDtoToReturn = APIMappingUtil.fromAPItoDTO(api, preserveCredentials, apiProvider);
        apiIdentifier.setUuid(exportAPIUUID);
        return ExportUtils.exportApi(apiProvider, apiIdentifier, apiDtoToReturn, api, userName, format, preserveStatus,
                preserveDocs, originalDevPortalUrl);
    }

}

