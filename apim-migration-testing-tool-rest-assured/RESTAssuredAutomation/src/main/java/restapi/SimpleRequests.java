package restapi;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class SimpleRequests {

    public static Response get(String accessToken, String endpointUrl){
        Response getResponse;
        getResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .oauth2(accessToken)
                .get(endpointUrl);
        
    return getResponse;
    }
    
}
