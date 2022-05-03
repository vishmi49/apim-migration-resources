package org.wso2.carbon.apimgt.migration.migrator.v320;

import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.migrator.Migrator;
import org.wso2.carbon.apimgt.migration.dao.APIMgtDAO;
import org.wso2.carbon.apimgt.migration.dto.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScopeMigrator extends Migrator {

    @Override
    public void migrate() throws APIMigrationException {
        APIMgtDAO apiMgtDAO = APIMgtDAO.getInstance();
        // Step 1: remove duplicate entries
        ArrayList<APIScopeMappingDTO> duplicateList = new ArrayList<>();
        ArrayList<APIScopeMappingDTO> scopeAMData = apiMgtDAO.getAMScopeData();
        ArrayList<ResourceScopeInfoDTO> scopeResourceData = apiMgtDAO.getResourceScopeData();
        for (APIScopeMappingDTO scopeAMDataDTO : scopeAMData) {
            int flag = 0;
            for (ResourceScopeInfoDTO resourceScopeInfoDTO : scopeResourceData) {
                if (scopeAMDataDTO.getScopeId() == Integer.parseInt(resourceScopeInfoDTO.getScopeId())) {
                    flag += 1;
                }
            }
            if (flag == 0) {
                duplicateList.add(scopeAMDataDTO);
            }
        }
        apiMgtDAO.removeDuplicateScopeEntries(duplicateList);

        // Step 2: Remove duplicate versioned scopes registered for versioned APIs
        ArrayList<APIInfoScopeMappingDTO> apiInfoScopeMappingDTOS = apiMgtDAO.getAPIInfoScopeData();
        Map<String, Integer> apiScopeToScopeIdMapping = new HashMap<>();
        for (APIInfoScopeMappingDTO scopeInfoDTO : apiInfoScopeMappingDTOS) {
            String apiScopeKey = scopeInfoDTO.getApiName() + ":" + scopeInfoDTO.getApiProvider() +
                    ":" + scopeInfoDTO.getScopeName();
            if (apiScopeToScopeIdMapping.containsKey(apiScopeKey)) {
                int scopeId = apiScopeToScopeIdMapping.get(apiScopeKey);
                if (scopeId != scopeInfoDTO.getScopeId()) {
                    apiMgtDAO.updateScopeResource(scopeId, scopeInfoDTO.getResourcePath(), scopeInfoDTO.getScopeId());
                    APIScopeMappingDTO apiScopeMappingDTO = new APIScopeMappingDTO();
                    apiScopeMappingDTO.setApiId(scopeInfoDTO.getApiId());
                    apiScopeMappingDTO.setScopeId(scopeInfoDTO.getScopeId());
                    ArrayList<APIScopeMappingDTO> scopeRemovalList = new ArrayList<>();
                    scopeRemovalList.add(apiScopeMappingDTO);
                    apiMgtDAO.removeDuplicateScopeEntries(scopeRemovalList);
                }
            } else {
                apiScopeToScopeIdMapping.put(apiScopeKey, scopeInfoDTO.getScopeId());
            }
        }

        // Step 3: Move entries in IDN_RESORCE_SCOPE_MAPPING table to AM_API_RESOURCE_SCOPE_MAPPING table
        ArrayList<APIInfoDTO> apiData = apiMgtDAO.getAPIData();
        ArrayList<APIURLMappingInfoDTO> urlMappingData = apiMgtDAO.getAPIURLMappingData();
        List<AMAPIResourceScopeMappingDTO> amapiResourceScopeMappingDTOList = new ArrayList<>();
        for (APIInfoDTO apiInfoDTO : apiData) {
            String context = apiInfoDTO.getApiContext();
            String version = apiInfoDTO.getApiVersion();
            for (APIURLMappingInfoDTO apiurlMappingInfoDTO : urlMappingData) {
                if (apiurlMappingInfoDTO.getApiId() == apiInfoDTO.getApiId()) {
                    String resourcePath = context + "/" + version + apiurlMappingInfoDTO.getUrlPattern() + ":" +
                            apiurlMappingInfoDTO.getHttpMethod();
                    int urlMappingId = apiurlMappingInfoDTO.getUrlMappingId();
                    int scopeId = apiMgtDAO.getScopeId(resourcePath);
                    if (scopeId != -1) {
                        ScopeInfoDTO scopeInfoDTO = apiMgtDAO.getScopeInfoByScopeId(scopeId);
                        String scopeName = scopeInfoDTO.getScopeName();
                        int tenantId = scopeInfoDTO.getTenantID();
                        AMAPIResourceScopeMappingDTO amapiResourceScopeMappingDTO = new AMAPIResourceScopeMappingDTO();
                        amapiResourceScopeMappingDTO.setScopeName(scopeName);
                        amapiResourceScopeMappingDTO.setUrlMappingId(urlMappingId);
                        amapiResourceScopeMappingDTO.setTenantId(tenantId);
                        amapiResourceScopeMappingDTOList.add(amapiResourceScopeMappingDTO);
                    }
                }
            }
        }
        apiMgtDAO.addDataToResourceScopeMapping(amapiResourceScopeMappingDTOList);
    }
}
