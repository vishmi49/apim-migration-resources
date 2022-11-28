package tests;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ataf.actions.BaseTest;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.Authentication;
import restapi.ContentTypes;
import restapi.JsonReadWrite;
import restapi.TenantAdmin;
import restapi.devportal.DevPortal;
import restapi.devportal.DevPortal.GraphQlPolicies;
import restapi.publisher.Publisher;
import soapapi.remoteuserstore.RemoteUserStore;
import soapapi.tenantmanagemant.TenantManagement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DemoDataPopulation extends BaseTest{
    
  String accessToken;
  private static Logger logger = LogManager.getLogger(DataPopulations.class);
  
  @Test
  @Parameters({"createTenantRequest","adminUserName", "adminPassword"})
  public void test1_TenantsManagement(
          String createTenantRequest,
          String adminUserName,
          String adminPassword
          ) throws Exception {
      
      authenticationObject.setUsername(adminUserName);
      authenticationObject.setUserpassword(adminPassword);
      Authentication authentication = new Authentication(authenticationObject);
      accessToken = authentication.getAccessToken();
      
      TenantManagement tManager = new TenantManagement(accessToken,baseURL);
      TenantAdmin tenantAdmin = new TenantAdmin(adminUserName, adminPassword);
      
      tManager.createTenants(createTenantRequest,tenantAdmin);
      logger.info("[TENANT MANAGMENT]: Tenant management related tests were completed");
      
  }  
  
  
  @Test
  @Parameters({"authenticateRequest","addRoleRequest", 
      "addUserRequest", "adminUserName", "adminPassword",
      "tenantAdminUserName","tenantAdminPassword"})   
  public void test2_RemoteUserStore(
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
      
      RemoteUserStore rUserStore = new RemoteUserStore(accessToken,baseURL);
      
      TenantAdmin tenantAdmin = new TenantAdmin(tenantAdminUsername, tenantAdminPassword);
      rUserStore.addRole(addRoleRequest,tenantAdmin);
      rUserStore.addUser(addUserRequest,tenantAdmin);
      
      logger.info("[USER STORE]: User store related tests were completed");
      TimeUnit.SECONDS.sleep(4);
      
  }
  
  @Test
  @Parameters({"tenantUserPublisher","tenantUserPasswordPublisher", "apiCreationPayload",
      "createApiOpenApiDefinition","thumbnailImage","apiLifecycleStatusAction"})  
  public void test3_PublisherPortal(
          String tenantUserPublisher, String tenantUserPasswordPublisher, String apiCreationPayload,
          String createApiOpenApiDefinition, String thumbnailImage, String apiLifecycleStatusAction
          ) throws InterruptedException {
      
      authenticationObject.setUsername(tenantUserPublisher);
      authenticationObject.setUserpassword(tenantUserPasswordPublisher);
      
      Authentication authentication = new Authentication(authenticationObject);
      String accessToken1 = authentication.getAccessToken();
      
      Publisher.Apis api = new Publisher.Apis(accessToken1, ApimVersions.APIM_3_2);
      
      Response createApiOpenApiDefinitionRes = api.imporOpenAPIDefinition(createApiOpenApiDefinition, apiCreationPayload);
      logger.info("Status Code [CREATE OPEN API DEFINITION]: "+createApiOpenApiDefinitionRes.statusCode());
      String apiId = createApiOpenApiDefinitionRes.jsonPath().get("id");
      if(apiId != null && createApiOpenApiDefinitionRes.statusCode()==201) JsonReadWrite.addApiToJson(apiId);

      Response uploadApiThumbnailRes = api.uploadThumbnailImage(thumbnailImage, JsonReadWrite.readApiId(0));
      logger.info("Status Code [UPLOAD API THUMBNAIL]: "+uploadApiThumbnailRes.statusCode());

      Response changeApiStatusRes = api.changeApiStatus(JsonReadWrite.readApiId(0), apiLifecycleStatusAction);
      logger.info("Status Code [CHANGE API STATUS]: "+changeApiStatusRes.statusCode());
      
      Response searchApiRes = api.searchApis();
      logger.info("Status Code [SEARCH API]: "+searchApiRes.statusCode());
      
      logger.info("[PUBLISHER PORTAL]: Publisher Portal tests were completed");
      
  }
  
  @Test
  @Parameters({"tenantUserCreator","tenantUserPasswordCreator","apiSearchingKeyWord",
      "appPayloadListAsString", "genarateKeyPayloadListAsString"})  
  public void test4_DevPortal(
          String tenantUserCreator, String tenantUserPasswordCreator,String apiSearchingKeyWord, 
          String appPayloadListAsString, String genarateKeyPayloadListAsString) {
     
      ArrayList<String> appPayloadList = new ArrayList<String>(Arrays.asList(appPayloadListAsString.split(",")));
      ArrayList<String> genarateKeyPayload = new ArrayList<String>(Arrays.asList(genarateKeyPayloadListAsString.split(",")));
      
      authenticationObject.setUsername(tenantUserCreator);
      authenticationObject.setUserpassword(tenantUserPasswordCreator);
      
      Authentication authentication = new Authentication(authenticationObject);
      accessToken = authentication.getAccessToken();
      
      DevPortal.UnfiedSearch dSearch = new DevPortal.UnfiedSearch(accessToken, ApimVersions.APIM_3_2);
      Response searchApiByName = dSearch.getApiAndApiDocumentByContent(apiSearchingKeyWord);
      logger.info("Status Code [SEARCHED API BY NAME]: "+searchApiByName.statusCode());
      
      DevPortal.Applications applications = new DevPortal.Applications(accessToken, ApimVersions.APIM_3_2);
      DevPortal.ApplicationKeys appKeys = new DevPortal.ApplicationKeys(accessToken, ApimVersions.APIM_3_2);
      DevPortal.Subscriptions subscription = new DevPortal.Subscriptions(accessToken, ApimVersions.APIM_3_2);
      
      for(int i = 0 ; i <appPayloadList.size() ; i++) {
          
          Response createNewApplicationRes = applications.createNewApplications(appPayloadList.get(i));
          String appId = createNewApplicationRes.jsonPath().get("applicationId"); 
          logger.info("Status Code [CREATE NEW APPLICATION "+(i+1)+"]: "+createNewApplicationRes.statusCode());
          if(appId != null && createNewApplicationRes.statusCode()==201) JsonReadWrite.addAppToJson(appId); 
                   
          Response subscribeRes = subscription.addNewSubscription("subscribeToApp.json",JsonReadWrite.readApiId(0),JsonReadWrite.readAppId(i));
          logger.info("Status Code [SUBSCRIBE TO API "+(i+1)+"]: "+subscribeRes.statusCode());
          if(subscribeRes.statusCode()==201) JsonReadWrite.addSubscriptionData(JsonReadWrite.readAppId(i), subscribeRes.jsonPath().prettify());
        
          Response genSandboxKeyRes = appKeys.generateApplicationKeys(JsonReadWrite.readAppId(i), genarateKeyPayload.get(i));
          logger.info("Status Code [GENERATE ACCESS TOKEN "+(i+1)+"]: "+genSandboxKeyRes.statusCode());
          if(genSandboxKeyRes.statusCode()==200) JsonReadWrite.addKeys(JsonReadWrite.readAppId(i), "sandbox", genSandboxKeyRes.jsonPath().prettify());   
           
      }
      
      logger.info("[DEV PORTAL]: Developer Portal tests were completed");
      
      
  }
  

  
}


