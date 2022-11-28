package tests;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Wso2_Apim {
	
	Response  res1, res2;

	FileInputStream input;
	Properties p;
	byte[] authplj;
	byte[] apicreationplj;
	byte[] createapiproductplj;
	String authpls;
	String apicreationpls;
	String createapiproductpls;
	String accessToken;

	private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(TestClasses.class);
	// LogManager.getLogger(TestClasses.class);

	
	@Test
	public void oauth2() {

		try {
			String path =  "./src/test/resources/config.properties";
			p = new Properties();
			input = new FileInputStream(path);
			p.load(input);
			
			authplj = Files.readAllBytes(Paths.get("./src/test/payloads/payload.json"));
			authpls = new String(authplj);

		} catch (Exception e) {
        	logger.info(e);
		}

		//obtain the consumer key/secret key pair
		res1 = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.preemptive()
				.basic(p.getProperty("adminusername"),p.getProperty("adminpassword"))
				.body(authpls)
				.contentType("application/json")
				.post(p.getProperty("hosturi")+"9443/client-registration/v0.17/register");

		logger.info("Status Code [CONSUMER KEY/SECRET]: "+res1.statusCode());
		
		//obtain the access token
		res2 = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.basic(res1.jsonPath().get("clientId").toString(), res1.jsonPath().get("clientSecret").toString())  
				.queryParam("grant_type","password")
				.queryParam("username",p.getProperty("adminusername"))
				.queryParam("password","admin")
				.queryParam("scope","apim:api_view apim:api_create")
				.post(p.getProperty("hosturi")+"8243/token");

		logger.info("Status Code [ACCESS TOKEN]: "+res1.statusCode());
	
		accessToken = res2.jsonPath().get("access_token").toString();


	}	

}
