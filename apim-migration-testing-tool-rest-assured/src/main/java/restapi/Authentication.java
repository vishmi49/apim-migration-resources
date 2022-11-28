package restapi;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Authentication {
    Response  getClientIdResponse, getAccessTokenResponse;

	FileInputStream input;
	Properties p;
	byte[] authPlayloadJson;
	String authPlayloadString;
    String accessToken;

    String username = "";
    String userpassword = "";
    String endpoint = "";
    String payloadPath = "";
    String tokenUrl = "";
    String scope = "";
    String grantType = "";
    String contentType = "";

    public Authentication(AuthenticationObject authenticationObject) {
        this.username = authenticationObject.getUsername();
        this.userpassword = authenticationObject.getUserpassword();
        this.endpoint = authenticationObject.getEndpoint();
        this.payloadPath = authenticationObject.getPayloadPath();
        this.tokenUrl = authenticationObject.getTokenUrl();
        this.scope = authenticationObject.getScopes();
        this.grantType = authenticationObject.getGrantType();
        this.contentType = authenticationObject.getContentType();
    }

    public String getAccessToken(){
        try {
			authPlayloadJson = Files.readAllBytes(Paths.get(payloadPath));
			authPlayloadString = new String(authPlayloadJson);
            getClientIdResponse = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.preemptive()
				.basic("admin","admin")
				.body(authPlayloadString)
				.contentType(contentType)
				.post(endpoint);
		
		    getAccessTokenResponse = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.basic(getClientIdResponse.jsonPath().get("clientId").toString(), getClientIdResponse.jsonPath().get("clientSecret").toString())  
				.queryParam("grant_type",grantType)
				.queryParam("username",username)
				.queryParam("password",userpassword)
				.queryParam("scope",scope)
				.post(tokenUrl);
	
		    accessToken = getAccessTokenResponse.jsonPath().get("access_token").toString();

		} 
        catch (Exception e) {
			System.out.println(e);
		}
        return accessToken;
		
    }

}
