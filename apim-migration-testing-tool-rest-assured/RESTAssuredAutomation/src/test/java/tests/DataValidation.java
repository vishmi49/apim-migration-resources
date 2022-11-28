package tests;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ataf.actions.BaseTest;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.Authentication;
import restapi.JsonReadWrite;
import restapi.SimpleRequests;
import restapi.devportal.DevPortal;

public class DataValidation extends BaseTest{
    
    String accessToken;
    private static Logger logger = LogManager.getLogger(DataValidation.class);
    
    @Test
    @Parameters({"tenantAdminUser","tenantAdminUserPassword","apiSearchingKeyWord",
        "appPayloadListAsString", "genarateKeyPayloadListAsString"})
    public void testDataValidation(String tenantAdminUser, String tenantAdminUserPassword, String apiSearchingKeyWord,
            String appPayloadListAsString, String genarateKeyPayloadListAsString) {
        
        ArrayList<String> appPayloadList = new ArrayList<String>(Arrays.asList(appPayloadListAsString.split(",")));
        ArrayList<String> genarateKeyPayload = new ArrayList<String>(Arrays.asList(genarateKeyPayloadListAsString.split(",")));
        
        authenticationObject.setUsername(tenantAdminUser);
        authenticationObject.setUserpassword(tenantAdminUserPassword);
        
        Authentication authentication = new Authentication(authenticationObject);
        accessToken = authentication.getAccessToken();
        
        DevPortal.Applications applications = new DevPortal.Applications(accessToken, ApimVersions.APIM_3_2);
        DevPortal.ApplicationKeys appKeys = new DevPortal.ApplicationKeys(accessToken, ApimVersions.APIM_3_2);
        DevPortal.Subscriptions subscription = new DevPortal.Subscriptions(accessToken, ApimVersions.APIM_3_2);
        
        for(int i = 0 ; i < appPayloadList.size() ; i++) {
            
            Response getApplicationRes = applications.getDetailsOfApplication(JsonReadWrite.readAppId(i));
            logger.info("Status Code [AVAILABILITY OF APPLICATION "+(i+1)+"]: "+getApplicationRes.statusCode());
            
//            Response createNewApplicationRes = applications.createNewApplications(appPayloadList.get(i));
//            logger.info("Status Code [CREATE NEW APPLICATION "+(i+1)+"]: "+createNewApplicationRes.statusCode());
//            String appId = createNewApplicationRes.jsonPath().get("applicationId"); 
//            if(appId != null && createNewApplicationRes.statusCode()==201) JsonReadWrite.addAppToJson(appId);
//            
//            Response subscribeRes = subscription.addNewSubscription("subscribeToApp.json",JsonReadWrite.readApiId(0),JsonReadWrite.readAppId(i));
//            logger.info("Status Code [SUBSCRIBE TO API "+(i+1)+"]: "+subscribeRes.statusCode());
//            
//            Response subscribeGraphQlRes = subscription.addNewSubscription("subscribeToApp.json",JsonReadWrite.readApiId(1),JsonReadWrite.readAppId(i));
//            logger.info("Status Code [SUBSCRIBE TO GRAPHQL API "+(i+1)+"]: "+subscribeGraphQlRes.statusCode());
//          
//            Response genSandboxKeyRes = appKeys.generateApplicationKeys(JsonReadWrite.readAppId(i), genarateKeyPayload.get(i));
//            logger.info("Status Code [GENERATE ACCESS TOKEN "+(i+1)+"]: "+genSandboxKeyRes.statusCode());
//            if(genSandboxKeyRes.statusCode()==200) JsonReadWrite.addKeys(JsonReadWrite.readAppId(i), "sandbox", genSandboxKeyRes.jsonPath().prettify());
        }
        
        Response getResponse = SimpleRequests.get(JsonReadWrite.getAccessTokenOfApiFromApp(JsonReadWrite.readAppId(0)), "http://localhost:8280/t/test1_tenant.com/testapi1/1.0.0/menu");
        logger.info("Status Code [INVOKE REST API]: "+getResponse.statusCode());
        System.out.println(JsonReadWrite.getAccessTokenOfApiFromApp(JsonReadWrite.readAppId(0)));
        
        DevPortal.UnfiedSearch dSearch = new DevPortal.UnfiedSearch(accessToken, ApimVersions.APIM_3_2);
        Response searchApiByName = dSearch.getApiAndApiDocumentByContent(apiSearchingKeyWord);
        logger.info("Status Code [SEARCHED API BY NAME]: "+searchApiByName.statusCode());
        
        Response getAllSubscriptionRes = subscription.getAllSubscriptons(JsonReadWrite.readAppId(0));
        logger.info("Status Code [GET ALL SUBSCRIPTION]: "+getAllSubscriptionRes.jsonPath().prettify());
        
//        Response getAppScopesRes =  applications.getDetailsOfApplication(JsonReadWrite.readAppId(0));
//        logger.info("Status Code [APP SCOPES]: "+searchApiByName.jsonPath().prettify());
        
    }

}
