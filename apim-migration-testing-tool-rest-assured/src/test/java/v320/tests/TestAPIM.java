package v320.tests;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ataf.actions.BaseTest;
import ataf.utils.ExcelReader;
import commons.testdata.AuthenticationData;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.Authentication;
import restapi.ContentTypes;
import restapi.TenantAdmin;
import restapi.admin.Admin;
import restapi.publisher.Publisher;
import soapapi.remoteuserstore.RemoteUserStore;

public class TestAPIM extends BaseTest{

	String adminAccesstoken;
	String bearerToken = "eyJ4NXQiOiJNREpsTmpJeE4yRTFPR1psT0dWbU1HUXhPVEZsTXpCbU5tRmpaalEwWTJZd09HWTBOMkkwWXpFNFl6WmpOalJoWW1SbU1tUTBPRGRpTkRoak1HRXdNQSIsImtpZCI6Ik1ESmxOakl4TjJFMU9HWmxPR1ZtTUdReE9URmxNekJtTm1GalpqUTBZMll3T0dZME4ySTBZekU0WXpaak5qUmhZbVJtTW1RME9EZGlORGhqTUdFd01BX1JTMjU2IiwidHlwIjoiYXQrand0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJmZWQwMDliMC0zZWFmLTQ0OWEtYjZiZS04NjBlYWZiZjg0MmQiLCJhdXQiOiJBUFBMSUNBVElPTiIsImF1ZCI6Im5NRUVyR3hSSlluZFhPSEsxZEZFcGE3MGtoa2EiLCJuYmYiOjE2NzUwNjY2MTAsImF6cCI6Im5NRUVyR3hSSlluZFhPSEsxZEZFcGE3MGtoa2EiLCJzY29wZSI6ImRlZmF1bHQiLCJpc3MiOiJodHRwczpcL1wvbG9jYWxob3N0Ojk0NDNcL29hdXRoMlwvdG9rZW4iLCJleHAiOjE2NzUwNzAyMTAsImlhdCI6MTY3NTA2NjYxMCwianRpIjoiMjliMzAzYTEtZjExOS00MjJjLWE5YzMtNWVmNDE1ZDVlMmUxIiwiY2xpZW50X2lkIjoibk1FRXJHeFJKWW5kWE9ISzFkRkVwYTcwa2hrYSJ9.OXjtVz1o4YuJVnCGIuj-GCfR9298Lm7FMpRzNzTe6h1wQyvmj-osL7iB80PwxWCUx-PukJKk_Gha99KVampscLr01CiNYu10EGyZ8DhtEpypNSgXX100Yv-jGkmIsCdK5FMNhzyovuLCUZNZVDi7tMscEUvSXf5bX7ASYPylGtecY-U4rZpTNhPy5Kqb_99D0aaotpYkAOagaZoNVYgd23uSZ_Nf8N7f2JadIYKR1NkGoqOvHtHJj7uPkj1YU57cAE1AwmA2Y5o-OppF3MCBlJ6sbs124oppmF1-tAaM8kKomFIl979IygD0998WKPr7tjF_yFMlchvxYGuX1eZvgg";

	
    @BeforeClass
    public void getTenantInfo() throws Exception {

//        String tenantAdminUser = "admin";
//        String tenantAdminPassword = "admin";
//		
//		TenantAdmin tenantAdmin = new TenantAdmin(tenantAdminUser, tenantAdminPassword);	    	
//		RemoteUserStore rUserStore = new RemoteUserStore(baseURL);
//    	
//    	// Admin authentication
//        String adminUserScope = "apim:api_view apim:api_publish apim:api_create apim:subscribe apim:subscription_view apim:document_create apim:comment_write apim:admin apim:tier_manage apim:scope_manage apim:admin_operations apim:shared_scope_manage apim:mediation_policy_create apim:api_import_export apim:mediation_policy_view apim:app_manage apim:sub_manage";
//
//        authenticationObject.setPayload(AuthenticationData.getPayloadOfOwner(tenantAdminUser)); // get admin suer from excel
//        authenticationObject.setUsername(tenantAdminUser);
//        authenticationObject.setUserpassword(tenantAdminPassword);
//        authenticationObject.setScopes(adminUserScope);
//        Authentication adminAuthentication = new Authentication(authenticationObject);
//        adminAccesstoken = adminAuthentication.getAccessToken();
//        
//
//        
    	log("Before class");
    }
    
//    @Test()
//	public void invokeAPI() throws Exception {  
//
//		String bearerToken = "eyJ4NXQiOiJNREpsTmpJeE4yRTFPR1psT0dWbU1HUXhPVEZsTXpCbU5tRmpaalEwWTJZd09HWTBOMkkwWXpFNFl6WmpOalJoWW1SbU1tUTBPRGRpTkRoak1HRXdNQSIsImtpZCI6Ik1ESmxOakl4TjJFMU9HWmxPR1ZtTUdReE9URmxNekJtTm1GalpqUTBZMll3T0dZME4ySTBZekU0WXpaak5qUmhZbVJtTW1RME9EZGlORGhqTUdFd01BX1JTMjU2IiwidHlwIjoiYXQrand0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJmZWQwMDliMC0zZWFmLTQ0OWEtYjZiZS04NjBlYWZiZjg0MmQiLCJhdXQiOiJBUFBMSUNBVElPTiIsImF1ZCI6Im5NRUVyR3hSSlluZFhPSEsxZEZFcGE3MGtoa2EiLCJuYmYiOjE2NzUwNjY2MTAsImF6cCI6Im5NRUVyR3hSSlluZFhPSEsxZEZFcGE3MGtoa2EiLCJzY29wZSI6ImRlZmF1bHQiLCJpc3MiOiJodHRwczpcL1wvbG9jYWxob3N0Ojk0NDNcL29hdXRoMlwvdG9rZW4iLCJleHAiOjE2NzUwNzAyMTAsImlhdCI6MTY3NTA2NjYxMCwianRpIjoiMjliMzAzYTEtZjExOS00MjJjLWE5YzMtNWVmNDE1ZDVlMmUxIiwiY2xpZW50X2lkIjoibk1FRXJHeFJKWW5kWE9ISzFkRkVwYTcwa2hrYSJ9.OXjtVz1o4YuJVnCGIuj-GCfR9298Lm7FMpRzNzTe6h1wQyvmj-osL7iB80PwxWCUx-PukJKk_Gha99KVampscLr01CiNYu10EGyZ8DhtEpypNSgXX100Yv-jGkmIsCdK5FMNhzyovuLCUZNZVDi7tMscEUvSXf5bX7ASYPylGtecY-U4rZpTNhPy5Kqb_99D0aaotpYkAOagaZoNVYgd23uSZ_Nf8N7f2JadIYKR1NkGoqOvHtHJj7uPkj1YU57cAE1AwmA2Y5o-OppF3MCBlJ6sbs124oppmF1-tAaM8kKomFIl979IygD0998WKPr7tjF_yFMlchvxYGuX1eZvgg";
//
//		
//		
//        Response response = RestAssured.given()
//        		.relaxedHTTPSValidation()
//        		.headers("Authorization",
//        	              "Bearer " + bearerToken,
//        	              "Content-Type",
//        	              ContentType.JSON,
//        	              "Accept",
//        	              ContentType.JSON)
//                .when()
//                .get("https://localhost:8243/pizzashack/1.0.0/menu")
//                .then()
//                .contentType(ContentType.JSON)
//                .extract()
//                .response();
//        log("=========== REsponse : " + response.body().asPrettyString());
//
//    }
//	@Test()
//	public void revokeAPI() {
//		Response response1 = RestAssured.given().relaxedHTTPSValidation().auth().preemptive().basic("admin", "admin")
//				.body(authenticationObject.getPayload()).contentType(ContentType.JSON)
//				.post(authenticationObject.getEndpoint());
//
//		String clientId = response1.jsonPath().get("clientId").toString();
//		String clientSecret = response1.jsonPath().get("clientSecret").toString();
//
//		Response response2 = RestAssured.given().relaxedHTTPSValidation().auth().basic(clientId, clientSecret)
//				.contentType("application/x-www-form-urlencoded").queryParam("token", bearerToken)
//				.post("https://localhost:9443/oauth2/revoke");
//
//		// log("=========== Response2 : " + response1.body().asPrettyString());
//
//	}
    
    public String getAccessToken() {
    	String tenantAdminUser = "admin";
    	String tenantAdminPassword = "admin";
    	
    	Response response1 = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .preemptive()
                .basic("admin", "admin")
                .body(authenticationObject.getPayload())
                .contentType(ContentType.JSON)
                .post(authenticationObject.getEndpoint());

    	String clientId = response1.jsonPath().get("clientId").toString();
    	String clientSecret = response1.jsonPath().get("clientSecret").toString();
    	//log("=========== Response1 : " + response1.body().asPrettyString());
    	
        //System.out.println("endpoint: "+ endpoint);
        //System.out.println("tokenUrl: "+tokenUrl);
        
    	Response response2 = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .basic(clientId, clientSecret)
                .queryParam("grant_type", authenticationObject.getGrantType())
                .queryParam("username", authenticationObject.getUsername())
                .queryParam("password", authenticationObject.getUserpassword())
                .queryParam("scope", authenticationObject.getScopes())
                .post(authenticationObject.getTokenUrl());
    	String accessToken = response2.jsonPath().get("access_token").toString();
    	log("=========== accessToken : " + accessToken);
    	return accessToken;
    }
    
//    @Test()
//    public void devPortalRestAPI() {
//    	String endpointURL = "https://localhost:9443/api/am/devportal/v3/apis/21beca4d-c9c0-4332-8c4b-3dc6793a48fc";
//        Response response = RestAssured.given()
//                .relaxedHTTPSValidation()
//                .auth()
//                .oauth2(getAccessToken())
//                .contentType(ContentTypes.APPLICATION_JSON)
//                .get(endpointURL );
//      log("=========== Response : " + response.body().asPrettyString());
//    }
    
    @Test()
    public void gatewayRestAPI() {
    	String username = "admin"; //wso2apim
    	String password = "admin";//wso2apim
    	//String endpointURL = "https://localhost:9443/api/am/gateway/v2/server-startup-healthcheck";
    	String endpointURL = "https://localhost:9443/api/am/gateway/v2/end-points?apiName=PizzaShackAPI&version=1.0.0&tenantDomain=carbon.super";
    	Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .preemptive()
                .basic(username, password)
                .contentType(ContentType.JSON)
                .get(endpointURL);
      log("=========== Response : " + response.statusCode());
      log("=========== Response : " + response.body().asPrettyString());
    }
}
