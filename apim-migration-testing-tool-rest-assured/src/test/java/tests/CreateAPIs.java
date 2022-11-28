package tests;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.Test;

import ataf.actions.BaseTest;
import restapi.ApimVersions;
import restapi.publisher.Publisher;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateAPIs extends BaseTest{

//	private static Logger logger = LogManager.getLogger(TestClasses.class);
//	
//	@Test
//	public void dataGeneration() {
//		System.out.println(this.apiCount);
//		for(int i=1 ; i<=apiCount ; i++) {
//			Publisher.Apis  pApi = new Publisher.Apis(accessToken, ApimVersions.APIM_3_2);
//			Response createApiResponse  = pApi.createApiParseJSON(getPayload(i));
//	        JsonPath jsonPathEvaluator = createApiResponse.jsonPath();
//			String apiID = jsonPathEvaluator.get("id");
////	        System.out.println("API " + i + " : "+ createApiResponse.statusCode() + " | " + apiID);
//	        
//	        logger.info("[API " +i+ " CREATED]: API ID ==>> "+apiID);
//	        logger.info("Status Code [CREATE API " +i+ "]: "+createApiResponse.statusCode());
//		}
//	}
//	
//	
//  static JSONObject getPayload(int apiIndex) {
//
//  byte[] payloadJson1;
//  String payloadString1;
//  String payload="";
//  JSONObject jsonObject = new JSONObject();
//
//  try {
//	  JSONParser parser = new JSONParser();
//	  Object obj = parser.parse(new FileReader("./src/test/payloads/apicretion_payload.json"));
//      jsonObject = (JSONObject) obj;
//      jsonObject.put("name","PizzaShackAPI_"+String.valueOf(apiIndex));
//      jsonObject.put("context", "pizza_"+String.valueOf(apiIndex));
//      payload = jsonObject.toString();
//  } catch (Exception e) {
//	  
//  }
//	  return jsonObject;
//	  
//	}
}

//@Test
//public void dataGeneration() {
//  System.out.println(this.apiCount);
//  for(int i=1 ; i<=apiCount ; i++) {
//      Publisher.Apis  pApi = new Publisher.Apis(accessToken, ApimVersions.APIM_3_2);
//      Response createApiResponse  = pApi.createApiParseJSON(getPayload(i));
//      JsonPath jsonPathEvaluator = createApiResponse.jsonPath();
//      String apiID = jsonPathEvaluator.get("id");
////        System.out.println("API " + i + " : "+ createApiResponse.statusCode() + " | " + apiID);
//      
//      logger.info("[API " +i+ " CREATED]: API ID ==>> "+apiID);
//      logger.info("Status Code [CREATE API " +i+ "]: "+createApiResponse.statusCode());
//  }
//}

//static JSONObject getPayload(int apiIndex) {
//
//      byte[] payloadJson1;
//      String payloadString1;
//      String payload="";
//      JSONObject jsonObject = new JSONObject();
//    
//      try {
//          JSONParser parser = new JSONParser();
//          Object obj = parser.parse(new FileReader("./src/test/payloads/apicretion_payload.json"));
//          jsonObject = (JSONObject) obj;
//          jsonObject.put("name","PizzaShackAPI_"+String.valueOf(apiIndex));
//          jsonObject.put("context", "pizza_"+String.valueOf(apiIndex));
//          payload = jsonObject.toString();
//      } catch (Exception e) {
//          
//      }
//          return jsonObject;
//
//}

//@Test
//public void devPortalDataPopulation() {
//  DevPortal.Apis devApis = new DevPortal.Apis(accessToken, ApimVersions.APIM_3_2);
//  Response searchApiRes = devApis.searchApis();
//  
//  System.out.println(searchApiRes.jsonPath().prettify());
//}
