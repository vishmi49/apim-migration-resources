/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package tests;

import exceptions.RestAssuredMigrationException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ataf.actions.BaseTest;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.Authentication;
import restapi.JsonReadWrite;
import restapi.TenantAdmin;
import restapi.devportal.DevPortal;
import restapi.publisher.Publisher;
import soapapi.remoteuserstore.RemoteUserStore;
import soapapi.tenantmanagemant.TenantManagement;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DataPopulations extends BaseTest {

    String accessToken;
    private static Logger logger = LogManager.getLogger(DataPopulations.class);


    @Test
    @Parameters({"authenticateRequest", "addRoleRequest",
            "addUserRequest", "adminUserName", "adminPassword",
            "tenantAdminUserName", "tenantAdminPassword"})
    public void remoteUserStore(
            String authenticateRequest,
            String addRoleRequest,
            String addUserRequest,
            String adminUserName,
            String adminPassword,
            String tenantAdminUsername,
            String tenantAdminPassword
    ) throws Exception {

        authenticationObject.setUsername(adminUserName);
        authenticationObject.setUserpassword(adminPassword);
        Authentication authentication = new Authentication(authenticationObject);
        accessToken = authentication.getAccessToken();
        System.out.println(">>>>>>>>>>>>>>> : Acess token" + accessToken);
        System.out.println(">>>>>>>>>>>>>>> : baseURL" + baseURL);
        System.out.println(">>>>>>>>>>>>>>> : tenantAdminUsername" + tenantAdminUsername);
        
        RemoteUserStore rUserStore = new RemoteUserStore(accessToken, baseURL);

        TenantAdmin tenantAdmin = new TenantAdmin(tenantAdminUsername, tenantAdminPassword);
        rUserStore.addRole(addRoleRequest, tenantAdmin, true);
        rUserStore.addUser(addUserRequest, tenantAdmin,true);

        logger.info("[USER STORE]: User store related tests were completed");

    }

//    @Test
//    @Parameters({"createTenantRequest", "deactivateTenantRequest",
//            "retrieveTenantsRequest", "checkDomainAvailabilityRequest",
//            "deleteTenantSoapRequest", "adminUserName", "adminPassword"})
//    public void tenantsManagement(
//            String createTenantRequest,
//            String deactivateTenantRequest,
//            String retrieveTenantsRequest,
//            String checkDomainAvailabilityRequest,
//            String deleteTenantSoapRequest,
//            String adminUserName,
//            String adminPassword
//    ) throws Exception {
//
//        authenticationObject.setUsername(adminUserName);
//        authenticationObject.setUserpassword(adminPassword);
//        Authentication authentication = new Authentication(authenticationObject);
//        accessToken = authentication.getAccessToken();
//
//        TenantManagement tManager = new TenantManagement(accessToken, baseURL);
//        TenantAdmin tenantAdmin = new TenantAdmin(adminUserName, adminPassword);
//
//        tManager.createTenants(createTenantRequest, tenantAdmin);
//        tManager.retrieveTenants(retrieveTenantsRequest, tenantAdmin);
//        tManager.checkDomainAvailability(checkDomainAvailabilityRequest, tenantAdmin);
//
//        logger.info("[TENANT MANAGMENT]: Tenant management related tests were completed");
//
//    }
//
//    @Test
//    @Parameters({"tenantAdminUser", "tenantAdminUserPassword", "apiCreationPayload",
//            "createApiOpenApiDefinition", "thumbnailImage", "apiLifecycleStatusAction",
//            "schemaGraphQlPayload", "apiCretionPayloadGraphQL"})
//    public void publisherPortal(
//            String tenantAdminUser, String tenantAdminUserPassword, String apiCreationPayload,
//            String createApiOpenApiDefinition, String thumbnailImage, String apiLifecycleStatusAction,
//            String schemaGraphQlPayload, String apiCretionPayloadGraphQL
//    ) throws InterruptedException, RestAssuredMigrationException {
//
//        authenticationObject.setUsername(tenantAdminUser);
//        authenticationObject.setUserpassword(tenantAdminUserPassword);
//
//        Authentication authentication = new Authentication(authenticationObject);
//        String accessToken1 = authentication.getAccessToken();
//
//        Publisher.Apis api = new Publisher.Apis(accessToken1, ApimVersions.APIM_3_2);
//
//
//        Response createApiOpenApiDefinitionRes = api.importOpenAPIDefinition(createApiOpenApiDefinition, apiCreationPayload);
//        logger.info("Status Code [CREATE OPEN API DEFINITION]: " + createApiOpenApiDefinitionRes.statusCode());
//        String apiId = createApiOpenApiDefinitionRes.jsonPath().get("id");
//        if (apiId != null && createApiOpenApiDefinitionRes.statusCode() == 201) JsonReadWrite.addApiToJson(apiId);
//
//        Response createGraphQlApiRes = api.importAPIDefinition(schemaGraphQlPayload, apiCretionPayloadGraphQL);
//        logger.info("Status Code [CREATE GRAPHQL API]: " + createGraphQlApiRes.statusCode());
//        String apiIdGraphQL = createGraphQlApiRes.jsonPath().get("id");
//        if (apiIdGraphQL != null && createGraphQlApiRes.statusCode() == 201) JsonReadWrite.addApiToJson(apiIdGraphQL);
//
//        Response changeGraphQlApiStatusRes = api.changeApiStatus(JsonReadWrite.readApiId(1), apiLifecycleStatusAction);
//        logger.info("Status Code [CHANGE GRAPHQL API STATUS]: " + changeGraphQlApiStatusRes.statusCode());
//
//        Response searchApiRes = api.searchApis();
//        logger.info("Status Code [SEARCH API]: " + searchApiRes.statusCode());
//
//        Response uploadApiThumbnailRes = api.uploadThumbnailImage(thumbnailImage, JsonReadWrite.readApiId(0));
//        logger.info("Status Code [UPLOAD API THUMBNAIL]: " + uploadApiThumbnailRes.statusCode());
//
//        Response changeApiStatusRes = api.changeApiStatus(JsonReadWrite.readApiId(0), apiLifecycleStatusAction);
//        logger.info("Status Code [CHANGE API STATUS]: " + changeApiStatusRes.statusCode());
//
//        logger.info("[PUBLISHER PORTAL]: Dev Portal tests were completed");
//
//    }
//
//    @Test
//    @Parameters({"tenantAdminUser", "tenantAdminUserPassword", "apiSearchingKeyWord",
//            "appPayloadListAsString", "genarateKeyPayloadListAsString"})
//    public void DevPortal(
//            String tenantAdminUser, String tenantAdminUserPassword, String apiSearchingKeyWord,
//            String appPayloadListAsString, String genarateKeyPayloadListAsString) throws RestAssuredMigrationException {
//
//        ArrayList<String> appPayloadList = new ArrayList<String>(Arrays.asList(appPayloadListAsString.split(",")));
//        ArrayList<String> genarateKeyPayload = new ArrayList<String>(Arrays.asList(genarateKeyPayloadListAsString.split(",")));
//
//        authenticationObject.setUsername(tenantAdminUser);
//        authenticationObject.setUserpassword(tenantAdminUserPassword);
//
//        Authentication authentication = new Authentication(authenticationObject);
//        accessToken = authentication.getAccessToken();
//
//        DevPortal.UnfiedSearch dSearch = new DevPortal.UnfiedSearch(accessToken, ApimVersions.APIM_3_2);
//        Response searchApiByName = dSearch.getApiAndApiDocumentByContent(apiSearchingKeyWord);
//        logger.info("Status Code [SEARCHED API BY NAME]: " + searchApiByName.statusCode());
//
//        DevPortal.Applications applications = new DevPortal.Applications(accessToken, ApimVersions.APIM_3_2);
//        DevPortal.ApplicationKeys appKeys = new DevPortal.ApplicationKeys(accessToken, ApimVersions.APIM_3_2);
//        DevPortal.Subscriptions subscription = new DevPortal.Subscriptions(accessToken, ApimVersions.APIM_3_2);
//
//        for (int i = 0; i < appPayloadList.size(); i++) {
//
//            Response createNewApplicationRes = applications.createNewApplications(appPayloadList.get(i));
//            logger.info("Status Code [CREATE NEW APPLICATION " + (i + 1) + "]: " + createNewApplicationRes.statusCode());
//            String appId = createNewApplicationRes.jsonPath().get("applicationId");
//            if (appId != null && createNewApplicationRes.statusCode() == 201) JsonReadWrite.addAppToJson(appId);
//
//            Response subscribeRes = subscription.addNewSubscription("subscribeToApp.json", JsonReadWrite.readApiId(0), JsonReadWrite.readAppId(i));
//            logger.info("Status Code [SUBSCRIBE TO API " + (i + 1) + "]: " + subscribeRes.statusCode());
//            if (subscribeRes.statusCode() == 201)
//                JsonReadWrite.addSubscriptionData(JsonReadWrite.readAppId(i), subscribeRes.jsonPath().prettify());
//
//            Response subscribeGraphQlRes = subscription.addNewSubscription("subscribeToApp.json", JsonReadWrite.readApiId(1), JsonReadWrite.readAppId(i));
//            logger.info("Status Code [SUBSCRIBE TO GRAPHQL API " + (i + 1) + "]: " + subscribeGraphQlRes.statusCode());
//
//            Response genSandboxKeyRes = appKeys.generateApplicationKeys(JsonReadWrite.readAppId(i), genarateKeyPayload.get(i));
//            logger.info("Status Code [GENERATE ACCESS TOKEN " + (i + 1) + "]: " + genSandboxKeyRes.statusCode());
//            if (genSandboxKeyRes.statusCode() == 200)
//                JsonReadWrite.addKeys(JsonReadWrite.readAppId(i), "sandbox", genSandboxKeyRes.jsonPath().prettify());
//        }
//
//        logger.info("[DEV PORTAL]: Dev Portal tests were completed");
//
//
//    }


}


