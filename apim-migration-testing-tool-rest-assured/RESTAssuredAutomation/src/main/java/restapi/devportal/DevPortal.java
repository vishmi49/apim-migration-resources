package restapi.devportal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.ContentTypes;

public class DevPortal {
    
    public static class Apis{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Apis(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public  Response searchApis(){
	        Response searchApisResponse;
	        searchApisResponse = RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
					.get(endPoint+publisherApisString);
	        
	    return searchApisResponse;
	    }

        public Response getApiDetails(String apiId){
            Response getApiDetailsResponse = RestAssured.given()
    				.relaxedHTTPSValidation()
    				.auth()
    				.oauth2(accessToken)
    				.get(endPoint+publisherApisString+"/"+apiId);

            return getApiDetailsResponse;
        }


        public Response getSwaggerDefinition(){
            Response getSwaggerDefinitionResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .get(endPoint+publisherApisString);
            return getSwaggerDefinitionResponse;
        }


        public Response getGraphQLDefinition(String apiId){
            Response getGraphQLDefinitionResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .get(endPoint+publisherApisString+"/"+apiId+"/graphql-schema");
            return getGraphQLDefinitionResponse;
        }

        
        public Response getApiWsdlDefinition(String apiId){
            Response getApiWsdlDefinitionResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .get(endPoint+publisherApisString+"/"+apiId+"/wsdl");
            return getApiWsdlDefinitionResponse;
        }

        public Response getThumbnailImage(String apiId){
            Response getThumbnailImageResponse= RestAssured.given()
    				.relaxedHTTPSValidation()
    				.auth()
    				.oauth2(accessToken)
    				.get(endPoint+publisherApisString+"/"+apiId+"/thumbnail");

            return getThumbnailImageResponse;
        }

        public Response getSubscriptionThrotlling(String apiId){
            Response getSubscriptionThrotllingResponse = RestAssured.given()
    				.relaxedHTTPSValidation()
    				.auth()
    				.oauth2(accessToken)
    				.get(endPoint+publisherApisString+"/"+apiId+"/subscription-policies");

            return getSubscriptionThrotllingResponse;
        }
	
    }
    
    public static class Sdks{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";
        
        
        
    	public Sdks(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response generateSDKForAPI(String apiId){
            Response generateSDKForAPIResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+apiId+"/sdks/java");

            return generateSDKForAPIResponse;
        }
    	
    	public Response getListOfSupportedSDKLanguages(){
            Response getListOfSupportedSDKLanguagesResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+"/sdk-gen/languages");

            return getListOfSupportedSDKLanguagesResponse;
        }
    	
    	
    	
    }
    
    public static class ApiDocumentation{
    
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";
        
        
        
    	public ApiDocumentation(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getListOfDocOfApi(String apiId){
            Response getListOfDocOfApiResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+apiId+"/documents");

            return getListOfDocOfApiResponse;
        }
        
        public Response addNewDocToApi(String apiId, String jsonPayloadPath){
        	
        	byte[] payloadplj1;
            String payloadpls1="";
        	
        	try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
            }
        	
            Response addNewDocToApiResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/"+apiId+"/documents");

            return addNewDocToApiResponse;
        }
        
        public Response getDocOfApi(String apiId, String documenetId){
            Response getDocOfApiResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+apiId+"/documents/"+documenetId);

            return getDocOfApiResponse;
        }
        
        public Response updateDocOfApi(String apiId, String documenetId){
            Response updateDocOfApiResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .put(endPoint+publisherApisString+"/"+apiId+"/documents/"+documenetId);

            return updateDocOfApiResponse;
        }
        
        public Response deleteDocOfApi(String apiId, String documenetId){
            Response deleteDocOfApiResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .delete(endPoint+publisherApisString+"/"+apiId+"/documents/"+documenetId);

            return deleteDocOfApiResponse;
        }
        
        public Response getContentOfDocOfApi(String apiId, String documenetId){
            Response getContentOfDocOfApiResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+apiId+"/documents/"+documenetId+"/content");

            return getContentOfDocOfApiResponse;
        }
        
        public Response uploadContentOfDocOfApi(String apiId, String documenetId, String dataPath){
            Response uploadContentOfDocOfApiResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .multiPart(new File(resourceParenPath+dataPath))
            .post(endPoint+publisherApisString+"/"+apiId+"/documents/"+documenetId+"/content");

            return uploadContentOfDocOfApiResponse;
        }
        
        public Response checkDocExistsByName(String apiId, String documenetId, String docName){
            Response checkDocExistsByNameResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+publisherApisString+"/"+apiId+"/documents/"+documenetId+"/validate?name="+docName);

            return checkDocExistsByNameResponse;
        }
    	
    }
    
    public static class Rating{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Rating(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getApiRatings(String apiId){
            Response getApiRatingsResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+apiId+"/ratings");

            return getApiRatingsResponse;
        }
    	
    	public Response getApiRatingOfUser(String apiId){
            Response getApiRatingOfUserResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+apiId+"/user-rating");

            return getApiRatingOfUserResponse;
        }
    	
    	public Response addOrUpdateLoggedUserRatingOfApi(String apiId, String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response addOrUpdateLoggedUserRatingOfApiResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .put(endPoint+publisherApisString+"/"+apiId+"/user-rating");

            return addOrUpdateLoggedUserRatingOfApiResponse;
        }
    	
    	public Response deleteUserApirating(String apiId){
            Response deleteUserApiratingResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .delete(endPoint+publisherApisString+"/"+apiId+"/user-rating");

            return deleteUserApiratingResponse;
        }
    }
    
    public static class Comments{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Comments(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getApiComments(String apiId){
            Response getApiCommentsResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+apiId+"/comments");

            return getApiCommentsResponse;
        }
    	
    	public Response addApiComment(String apiId, String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response addApiCommentResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/"+apiId+"/comments");

            return addApiCommentResponse;
        }
    	
    	public Response getDetailsOfApiComment(String apiId, String commentId){
            Response getDetailsOfApiCommentResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+apiId+"/comments/"+commentId);

            return getDetailsOfApiCommentResponse;
        }
    	
    	public Response deleteApiComment(String apiId, String commentId){
            Response deleteApiCommentResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .delete(endPoint+publisherApisString+"/"+apiId+"/comments/"+commentId);

            return deleteApiCommentResponse;
        }
    	
    }
    
    public static class Applications{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Applications(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response searchApplications(){
            Response searchApplicationsResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/applications");
            System.out.println(endPoint+publisherApisString+"/applications");

            return searchApplicationsResponse;
        }
    	
    	public Response createNewApplications(String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response createNewApplicationsResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/applications");

            return createNewApplicationsResponse;
        }
    	
    	public Response getDetailsOfApplication(String  applicationId){
            Response searchApplicationsResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/applications/"+applicationId);

            return searchApplicationsResponse;
        }
    	
    	public Response updateApplications(String applicationId,String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response updateApplicationsResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .put(endPoint+publisherApisString+"/applications/"+applicationId);

            return updateApplicationsResponse;
        }
    	
    	public Response deleteApplication(String  applicationId){
            Response deleteApplicationResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/applications/"+applicationId);

            return deleteApplicationResponse;
        }
    }
    
    public static class ApplicationKeys{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/applications";
        String resourceParenPath = "./src/test/payloads/";
        
    	public ApplicationKeys(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response generateApplicationKeys(String appclicationId, String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response generateApplicationKeysResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/"+appclicationId+"/generate-keys");

            return generateApplicationKeysResponse;
        }

    	public Response mapApplicationKeys(String appclicationId, String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response mapApplicationKeysResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/"+appclicationId+"/map-keys");

            return mapApplicationKeysResponse;
        }
    	
    	public Response getAllApplicationKeys(String  applicationId){
            Response getAllApplicationKeysResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+applicationId+"/oauth-keys");

            return getAllApplicationKeysResponse;
        }
    	
    	public Response getKeyDetailsOfGivenType(String  applicationId, String keyMappingId){
            Response getKeyDetailsOfGivenTypeResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+applicationId+"/oauth-keys/"+keyMappingId);

            return getKeyDetailsOfGivenTypeResponse;
        }
    	
    	public Response updateGrantTypesAndCallbackUrlOfApplication(String applicationId, String keyMappingId){
            Response updateGrantTypesAndCallbackUrlOfApplicationResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .put(endPoint+publisherApisString+"/"+applicationId+"/oauth-keys/"+keyMappingId);

            return updateGrantTypesAndCallbackUrlOfApplicationResponse;
        }
    	
    	public Response regenerateConsumerSecret(String applicationId, String keyMappingId){
            Response regenerateConsumerSecretResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .put(endPoint+publisherApisString+"/"+applicationId+"/oauth-keys/"+keyMappingId+"/regenerate-secret");

            return regenerateConsumerSecretResponse;
        }
    	
    	public Response cleanUpApplicationKeys(String applicationId, String keyMappingId){
            Response cleanUpApplicationKeysResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .put(endPoint+publisherApisString+"/"+applicationId+"/oauth-keys/"+keyMappingId+"/clean-up");

            return cleanUpApplicationKeysResponse;
        }
    }
    
    public static class ApplicationTokens{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/applications";
        String resourceParenPath = "./src/test/payloads/";
        
    	public ApplicationTokens(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response generateApplicationTokens(String appclicationId, String keyMappingId, String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response generateApplicationTokensResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/"+appclicationId+"/oauth-keys/"+keyMappingId+"/generate-token");

            return generateApplicationTokensResponse;
        }
    }
    
    public static class ApiKeys{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/applications";
        String resourceParenPath = "./src/test/payloads/";
        
    	public ApiKeys(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response generateApiKeys(String appclicationId, String keyType, String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response generateApiKeysResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/"+appclicationId+"/api-keys/"+keyType+"/generate");

            return generateApiKeysResponse;
        }
    	
    	public Response revokeApiKeys(String appclicationId, String keyType, String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response revokeApiKeysResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/"+appclicationId+"/api-keys/"+keyType+"/revoke");

            return revokeApiKeysResponse;
        }
    	
    }
    
    public static class Subscriptions{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/subscriptions";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Subscriptions(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getAllSubscriptons(String apiId){
    		
            Response generateApiKeysResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"?apiId="+apiId);

            return generateApiKeysResponse;
        }
    	
    	public Response addNewSubscription(String jsonPayloadPath, String apiId, String appId){
    	    
    	    JSONObject jsonObject = new JSONObject();
    	       
    	       try {
    	           
    	         JSONParser parser = new JSONParser();
    	         Object obj = parser.parse(new FileReader(resourceParenPath+jsonPayloadPath));
    	           jsonObject = (JSONObject) obj;
    	           jsonObject.put("apiId", apiId);
    	           jsonObject.put("applicationId", appId);
    	           try (FileWriter file = new FileWriter(resourceParenPath+jsonPayloadPath)) {
    	             file.write(jsonObject.toJSONString()); 
    	             file.flush();
    	  
    	         } catch (IOException e) {
    	             e.printStackTrace();
    	         }
    	       } catch (Exception e) {
    	         
    	       }
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response addNesSubscriptionResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString);

            return addNesSubscriptionResponse;
        }
    	
    	public Response addNesSubscriptions(String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response addNesSubscriptionResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/multiple");

            return addNesSubscriptionResponse;
        }
    	
    	public Response getDetailsOfSubscription(String subscriptionId){
    	
            Response addNesSubscriptionResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+subscriptionId);

            return addNesSubscriptionResponse;
        }
    	
    	public Response updateExisitingSubscription(String subscriptionId, String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
    		
            Response updateExisitingSubscriptionResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .put(endPoint+publisherApisString+"/"+subscriptionId);

            return updateExisitingSubscriptionResponse;
        }
    	
    	public Response removeSubscription(String subscriptionId){
        	
            Response removeSubscriptionResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .delete(endPoint+publisherApisString+"/"+subscriptionId);

            return removeSubscriptionResponse;
        }
    	
    }
    
    public static class ApiMonetization{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/subscriptions";
        String resourceParenPath = "./src/test/payloads/";
        
    	public ApiMonetization(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getDetailsOfPendingInvoice(String subscriptionId){
        	
            Response getDetailsOfPendingInvoiceResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+subscriptionId+"/usage");

            return getDetailsOfPendingInvoiceResponse;
        }
    	
    }
    
    public static class ThrottlingPolicies{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/throttling-policies";
        String resourceParenPath = "./src/test/payloads/";
        
    	public ThrottlingPolicies(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getAllAvailableThrottlingPolicies(String policyLevel){
        	
            Response getAllAvailableThrottlingPoliciesResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+policyLevel);

            return getAllAvailableThrottlingPoliciesResponse;
        }
    	
    	public Response getDetailsOfThrottlingPolicies(String policyLevel, String policyId){
        	
            Response getDetailsOfThrottlingPoliciesResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+policyLevel+"/"+policyId);

            return getDetailsOfThrottlingPoliciesResponse;
        }
    	
    }
    
    public static class Tags{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/tags";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Tags(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getAllTags(){
        	
            Response getAllTagsResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString);

            return getAllTagsResponse;
        }
    	
    }
    
    public static class UnfiedSearch{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/search";
        String resourceParenPath = "./src/test/payloads/";
        
    	public UnfiedSearch(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getApiAndApiDocumentByContent(String query){
        	
            Response getApiAndApiDocumentByContentResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"?query="+query);

            return getApiAndApiDocumentByContentResponse;
        }
    	
    }
    
    public static class Settings{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/settings";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Settings(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getStoreSetting(){
        	
            Response getStoreSettingResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString);

            return getStoreSettingResponse;
        }
    	
    	public Response getAllApplicationAttributesFromConfiguration(){
        	
            Response getAllApplicationAttributesFromConfigurationResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/application-attributes");

            return getAllApplicationAttributesFromConfigurationResponse;
        }
    	
    }
    
    public static class Tenants{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/tenants";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Tenants(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getTenantByState(){
        	
            Response getTenantByStateResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString);

            return getTenantByStateResponse;
        }
    	
    }
    
    public static class Recommendations{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/recommendations";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Recommendations(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response giveApiRecommendationsForUser(){
        	
            Response giveApiRecommendationsForUserResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString);

            return giveApiRecommendationsForUserResponse;
        }
    	
    }
    
    public static class Alerts{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/alert-types";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Alerts(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getListOfApiDeveloperPortalAlertTypes(){
        	
            Response getListOfApiDeveloperPortalAlertTypesResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString);

            return getListOfApiDeveloperPortalAlertTypesResponse;
        }
    	
    }
    
    public static class AlertSubscriptions{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/alert-subscriptions";
        String resourceParenPath = "./src/test/payloads/";
        
    	public AlertSubscriptions(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getListOfApiDeveloperPortalAlertTypesSubscribedByUser(){
        	
            Response getListOfApiDeveloperPortalAlertTypesSubscribedByUserResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString);

            return getListOfApiDeveloperPortalAlertTypesSubscribedByUserResponse;
        }
    	
    	public Response subscribeToSelectedAlertTypesByUser(String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
        	
            Response subscribeToSelectedAlertTypesByUserResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .put(endPoint+publisherApisString);

            return subscribeToSelectedAlertTypesByUserResponse;
        }
    	
    	public Response unsubscribeUserFromAllAlertTypes(){
        	
            Response unsubscribeUserFromAllAlertTypesResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .delete(endPoint+publisherApisString);

            return unsubscribeUserFromAllAlertTypesResponse;
        }
    	
    }
    
    public static class ApiConfigurations{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/alerts";
        String resourceParenPath = "./src/test/payloads/";
        
    	public ApiConfigurations(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getAllAbnormalRequestsPerMinAlertConfigurations(String alertType){
        	
            Response getAllAbnormalRequestsPerMinAlertConfigurationsResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+alertType+"/configurations");

            return getAllAbnormalRequestsPerMinAlertConfigurationsResponse;
        }
    	
    	public Response addAbnormalRequestsPerMinAlertConfigurations(String alertType, String configurationId, String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
        	
            Response addAbnormalRequestsPerMinAlertConfigurationsResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .put(endPoint+publisherApisString+"/"+alertType+"/configurations/"+configurationId);

            return addAbnormalRequestsPerMinAlertConfigurationsResponse;
        }
    	
    	public Response deleteSelectedConfigurationFromAbnormalRequestsPerMinAlertType(String alertType, String configurationId){
        	
            Response deleteSelectedConfigurationFromAbnormalRequestsPerMinAlertTypeResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .delete(endPoint+publisherApisString+"/"+alertType+"/configurations/"+configurationId);

            return deleteSelectedConfigurationFromAbnormalRequestsPerMinAlertTypeResponse;
        }
    	
    }
    
    public static class ApiCategory_Collections{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/api-categories";
        String resourceParenPath = "./src/test/payloads/";
        
    	public ApiCategory_Collections(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getAllApiCategories(){
        	
            Response getAllApiCategoriesResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString);

            return getAllApiCategoriesResponse;
        }
    	
    }
    
    public static class KeyManager_Collections{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/key-managers";
        String resourceParenPath = "./src/test/payloads/";
        
    	public KeyManager_Collections(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getAllKeyManagers(){
        	
            Response getAllKeyManagersResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString);

            return getAllKeyManagersResponse;
        }
    	
    }
    
    public static class GraphQlPolicies{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";
        
    	public GraphQlPolicies(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response getComplexityRelatedDetailsOfAPI(String apiId){
        	
            Response getComplexityRelatedDetailsOfAPIResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .get(endPoint+publisherApisString+"/"+apiId+"/graphql-policies/complexity");

            return getComplexityRelatedDetailsOfAPIResponse;
        }
    	
    }
    
    public static class Users{
    	
    	String accessToken;
        String endPoint;
        
        String publisherApisString = "/me";
        String resourceParenPath = "./src/test/payloads/";
        
    	public Users(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("devportal_url_4_1");
                }
                
            } catch (Exception e) {
            }
    	}
    	
    	public Response changePasswordOfUser(String jsonPayloadPath){
    		
    		byte[] payloadplj1;
            String payloadpls1="";
    		
    		try {
        		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
        		payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }
        	
            Response changePasswordOfUserResponse  = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .body(payloadpls1)
            .post(endPoint+publisherApisString+"/change-password");

            return changePasswordOfUserResponse;
        }
    	
    }
    
    
}
