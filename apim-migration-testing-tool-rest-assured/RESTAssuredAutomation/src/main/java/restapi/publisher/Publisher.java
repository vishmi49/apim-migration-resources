package restapi.publisher;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.json.simple.JSONObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.ContentTypes;

public class Publisher {
	
	public static class Apis {

	    String accessToken="";
	    String endPoint="";
	    ApimVersions version;

	    Response searchApisResponse;
	    Response createApiResponse;
	    Response uploadThumbnailImageResponse;
	    Response getApiDetailsResponse;
	    Response createNewApiVersiResponse;
	    Response updateApiResponse;
	    Response deleteApiResponse;

	    byte[] apiCreationPayloadJson;
	    String apiCreationPayloadString;

	    byte[] createapiproductplj;
	    String createapiproductpls;

	    byte[] updateApiPayloadJson;
	    String updateApiPayloadString;

	    byte[] payloadplj1;
	    String payloadpls1;

	    byte[] payloadplj2;
	    String payloadpls2;

	    String publisherApisString = "/apis";
	    String resourceParenPath = "./src/test/payloads/";

	    public Apis(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

	        try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	        
	    }

	    //APIs Section-----------------------------------------------------------------------------------------------

	    public Response searchApis(){
	        try {
	            searchApisResponse = RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
					.get(endPoint+publisherApisString);    
	        } catch (Exception e) {
	        }
	        
	        return searchApisResponse;
	    }

	    public Response createApi(String contentType, String jsonPayloadPath){

	        try {
	            apiCreationPayloadJson = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
			    apiCreationPayloadString = new String(apiCreationPayloadJson);

	        createApiResponse  = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .body(apiCreationPayloadString)
	            .contentType(contentType)
	            .post(endPoint+publisherApisString);

	        } catch (Exception e) {
	            
	        }

	        return createApiResponse;

	    } 

	    public Response getApiDetails(String apiId){
	        getApiDetailsResponse = RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
					.get(endPoint+publisherApisString+"/"+apiId);

	        return getApiDetailsResponse;
	    }

	    public Response createNewApiVersion(String apiId, String apiVersion, boolean defaultVersion){
	        
	        
	        createNewApiVersiResponse = RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
	                .contentType("application/json")
					.post(endPoint+publisherApisString+"/copy-api?newVersion="+apiVersion+"&defaultVersion="+defaultVersion+"&apiId="+apiId);
	        return createNewApiVersiResponse;

	    }

	    public Response updateApi(String contentType, String apiId, String jsonPayloadPath){

	        try {
	            updateApiPayloadJson = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
			    updateApiPayloadString = new String(updateApiPayloadJson);

	            updateApiResponse  = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .body(updateApiPayloadString)
	            .contentType(contentType)
	            .put(endPoint+publisherApisString+"/"+apiId);

	        } catch (Exception e) {
	        }
	        
	        return updateApiResponse;

	    }

	    public Response deleteApi(String apiId){
	        deleteApiResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .delete(endPoint+publisherApisString+"/"+apiId);

	        return deleteApiResponse;
	    }

	    public Response getSwaggerDefinition(){
	        Response getSwaggerDefinitionResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherApisString);
	        return getSwaggerDefinitionResponse;
	    }

	    public Response updateSwaggerDefinition(String apiId){
	        Response updateSwaggerDefinitionRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .body(updateApiPayloadString)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .put(endPoint+publisherApisString+"/"+apiId+"/swagger");

	        return updateSwaggerDefinitionRes;
	    }


	    public Response generateMockResponsePayloads(String apiId){
	        Response generateMockResponsePayloadsResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType("application/json")
	        .post(endPoint+publisherApisString+"/"+apiId+"/generate-mock-scripts");
	        
	        return generateMockResponsePayloadsResponse;
	        
	    }

	    public Response getGeneratedMockResponsePayloads(String apiId){
	        Response getGeneratedMockResponsePayloadsResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType("application/json")
	        .get(endPoint+publisherApisString+"/"+apiId+"/generated-mock-scripts");
	        
	        return getGeneratedMockResponsePayloadsResponse;
	        
	    }
	    
	    public Response getThumbnailImage(String apiId){
	        Response getThumbnailImageResponse= RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
					.get(endPoint+publisherApisString+"/"+apiId+"/thumbnail");

	        return getThumbnailImageResponse;
	    }
	    
	    public Response uploadThumbnailImage(String imagePath,  String apiId){
	        Response uploadThumbnailImageResponse= RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
					.multiPart(new File(resourceParenPath+imagePath))
					.put(endPoint+publisherApisString+"/"+apiId+"/thumbnail");

	        return uploadThumbnailImageResponse;
	    }

	    public Response getSubscriptionThrotlling(String apiId){
	        Response getSubscriptionThrotllingResponse = RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
					.get(endPoint+publisherApisString+"/"+apiId+"/subscription-policies");

	        return getSubscriptionThrotllingResponse;
	    }
	    
	    public Response createApiParseJSON(JSONObject json){

	        createApiResponse  = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .body(json.toString())
	            .contentType(ContentTypes.APPLICATION_JSON)
	            .post(endPoint+publisherApisString);

	        

	        return createApiResponse;

	    } 

	    public Response imporOpenAPIDefinition(String openApiJsonPath, String dataPath){

	        Response imporOpenAPIDefinitionRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.MULTIPART_FORMDATA)
	        .multiPart("file",new File(resourceParenPath+openApiJsonPath))
	        .multiPart("additionalProperties",new File(resourceParenPath+dataPath))
	        .post(endPoint+publisherApisString+"/import-openapi");

	        return imporOpenAPIDefinitionRes;
	    }

	    public Response importWSDLDefinition(String apiWSDL, String dataPath){
	        Response importWSDLDefinitionRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.MULTIPART_FORMDATA)
	        .multiPart("file",new File(resourceParenPath+apiWSDL))
	        .multiPart("additionalProperties",new File(resourceParenPath+dataPath))
	        .post(endPoint+publisherApisString+"/import-wsdl");

	        return importWSDLDefinitionRes;
	    }

	    public Response importAPIDefinition(String schemaGraphGl, String dataPath){
	        Response importAPIDefinitionRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.MULTIPART_FORMDATA)
	        .multiPart("file",new File(resourceParenPath+schemaGraphGl))
	        .multiPart("additionalProperties",new File(resourceParenPath+dataPath))
	        .post(endPoint+publisherApisString+"/import-graphql-schema");

	        return importAPIDefinitionRes;
	    }

	    public Response getWsdlMetaInformation(String apiId){
	        Response getWSDLMetaInformationRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/wsdl-info");

	        return getWSDLMetaInformationRes;
	    }

	    public Response getWsdlDefinition(String apiId){
	        Response getWsdlDefinitionRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/wsdl");

	        return getWsdlDefinitionRes;
	    }

	    public Response updateWSDLDefinition(String apiWSDL){
	        Response updateWSDLDefinitionRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.MULTIPART_FORMDATA)
	        .multiPart(new File(resourceParenPath+apiWSDL))
	        .put(endPoint+publisherApisString+"/wsdl");

	        return updateWSDLDefinitionRes;
	    }

	    public Response getResourcePathsofApi(String apiId){
	        Response getResourcePathsofApiRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/resource-paths");

	        return getResourcePathsofApiRes;
	    }

	    //-----------------------------------------------------------------------------------------------------------------
	    

	    //API Resource Policies-----------------------------------------------------------------------------------------------------------------

	    public Response getResourcePolicyDefinitions(String apiId){
	        Response getResourcePolicyDefinitionsRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/resource-policies?resourcePath=checkPhoneNumber&verb=post&sequenceType=in");

	        return getResourcePolicyDefinitionsRes;
	    }

	    public Response getResourcePolicyForResourceIdentifier(String apiId, String policyId){
	        Response getResourcePolicyForResourceIdentifierRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/resource-policies/"+policyId);

	        return getResourcePolicyForResourceIdentifierRes;
	    }

	    public Response updateResourcePolicyForResourceIdentifier(String apiId, String policyId, String dataPath){

	        try {
	            payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+dataPath));
			    payloadpls1 = new String(payloadplj1);
	        } catch (Exception e) {
	        }

	        Response updateResourcePolicyForResourceIdentifierRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .body(payloadpls1)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .put(endPoint+publisherApisString+"/"+apiId+"/resource-policies/"+policyId);

	        return updateResourcePolicyForResourceIdentifierRes;

	    }

	    //-----------------------------------------------------------------------------------------------------------------

	    //API Lifecycle Section------------------------------------------------------------------------------------------------

	    public Response changeApiStatus(String apiId, String action){
	        Response changeApiStatusResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .post(endPoint+publisherApisString+"/change-lifecycle?apiId="+apiId+"&action="+action);

	        return changeApiStatusResponse;
	    }

	    public Response getApiStatus(String apiId){
	        Response changeApiStatusResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/lifecycle-history");

	        return changeApiStatusResponse;
	    }

	    public Response getLifecycleStateDataOfApi(String apiId){
	        Response getLifecycleStateDataOfApiResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/lifecycle-state");

	        return getLifecycleStateDataOfApiResponse;
	    }

	    public Response deletePendingLifecycleStateChangeTasks(String apiId){
	        Response deletePendingLifecycleStateChangeTasksResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .delete(endPoint+publisherApisString+"/"+apiId+"/lifecycle-state/pending-tasks");

	        return deletePendingLifecycleStateChangeTasksResponse;
	    }
	    
	    //Validation Section------------------------------------------------------------------------------------
		public Response validateOpenApiDefinition(String openApiJsonPath){
		      Response downloadApiSpecificMediationPolicyRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)  
		      .multiPart("file", new File(resourceParenPath+openApiJsonPath))
		      .post(endPoint+publisherApisString+"/validate-openapi");   
		
		      return downloadApiSpecificMediationPolicyRes; 
		}
		
		public Response checkGivenEndpointIsValid(String apiId, String endpointUrl){
		      Response checkGivenEndpointIsValidRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)     
		      .post(endPoint+publisherApisString+"/validate-endpoint?apiId="+apiId+"&endpointUrl="+endpointUrl);   
		
		      return checkGivenEndpointIsValidRes; 
		}
		
		public Response checkGivenApiContextNameExists(String apiName){
		      Response checkGivenApiContextNameExistsRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .post(endPoint+publisherApisString+"/validate?query="+apiName);   
		
		      return checkGivenApiContextNameExistsRes; 
		}
		
		public Response validateWsdlDefinition(String apiWsdlPath){
		      Response validateWsdlDefinitionRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .contentType(ContentTypes.MULTIPART_FORMDATA)
		      .multiPart("file", new File(resourceParenPath+apiWsdlPath))
		      .post(endPoint+publisherApisString+"/validate-wsdl");   
		
		      return validateWsdlDefinitionRes; 
		}
		
		public Response validateGraphQlApiDefinitionAndGetSummary(String schemaGraphQlPath){
		      Response validateGraphQlApiDefinitionAndGetSummaryRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .contentType(ContentTypes.MULTIPART_FORMDATA)
		      .multiPart("file", new File(resourceParenPath+schemaGraphQlPath))
		      .post(endPoint+publisherApisString+"/validate-graphql-schema");   
		
		      return validateGraphQlApiDefinitionAndGetSummaryRes; 
		}
		
		//GraphQL Schema (Individual) Section-------------------------------------------------------------------
		public Response getSchemaOfGraphQlApi(String apiId){
		      Response getSchemaOfGraphQlApiRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .get(endPoint+publisherApisString+"/"+apiId+"/graphql-schema");   
		
		      return getSchemaOfGraphQlApiRes; 
		}
		
		//GraphQL Schema Section--------------------------------------------------------------------------------
		public Response addSchemaOfGraphQlApi(String apiId, String schemaGraphQlPath){
		      Response addSchemaOfGraphQlApiRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .contentType(ContentTypes.MULTIPART_FORMDATA)
		      .multiPart("schemaDefinition", new File(resourceParenPath+schemaGraphQlPath))
		      .put(endPoint+publisherApisString+"/"+apiId+"/graphql-schema");   
		
		      return addSchemaOfGraphQlApiRes; 
		}
		
		//AWS Lambda (Individual)-------------------------------------------------------------------------------
		public Response getArnOfAwsLambdaFunction(String apiId){
		      Response getArnOfAwsLambdaFunctionRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)  
		      .body(apiCreationPayloadJson)
		      .get(endPoint+publisherApisString+"/"+apiId+"/amznResourceNames");   
		
		      return getArnOfAwsLambdaFunctionRes; 
		}
		
		//API Monetization--------------------------------------------------------------------------------------
		public Response configureMonatizationForGivenApi(String apiId, String dataPath){
			
			try {
	            payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+dataPath));
			    payloadpls1 = new String(payloadplj1);
	        } catch (Exception e) {
	        }
			
		      Response configureMonatizationForGivenApiRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .contentType(ContentTypes.APPLICATION_JSON)
		      .body(payloadpls1)
		      .post(endPoint+publisherApisString+"/"+apiId+"/monetize");   
		
		      return configureMonatizationForGivenApiRes; 
		}
		
		public Response getMonatizationStatusOfEachTierGivenApi(String apiId){
				
				
		      Response getMonatizationStatusOfEachTierGivenApiRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .contentType(ContentTypes.APPLICATION_JSON)
		      .get(endPoint+publisherApisString+"/"+apiId+"/monetize");   
		
		      return getMonatizationStatusOfEachTierGivenApiRes; 
		}
		
		public Response getTotalRevenueDetailsOfGivenMonatizesApiWithMeteredBill(String apiId){
			
			
		      Response getTotalRevenueDetailsOfGivenMonatizesApiWithMeteredBillRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .contentType(ContentTypes.APPLICATION_JSON)
		      .get(endPoint+publisherApisString+"/"+apiId+"/revenue");   
		
		      return getTotalRevenueDetailsOfGivenMonatizesApiWithMeteredBillRes; 
		}
		
		public Response getDetailsOfPendingInvoiceForMonatizedSubscription(String apiId){
			
			
		      Response getDetailsOfPendingInvoiceForMonatizedSubscriptionRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .contentType(ContentTypes.APPLICATION_JSON)
		      .get(endPoint+publisherApisString+"/"+apiId+"/usage");   
		
		      return getDetailsOfPendingInvoiceForMonatizedSubscriptionRes; 
		}
		
	    
	    //API Documents Section---------------------------------------------------------------------------------
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
	        .contentType(ContentTypes.MULTIPART_FORMDATA)
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
	    
	  //API mediation Policies Section-----------------------------------------------------------------------
	    public Response getAllMediationPoliciesOfAPI(String apiId){
	        Response getAllMediationPoliciesOfAPIRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken)  
	        .contentType(ContentTypes.APPLICATION_JSON)   
	        .get(endPoint+publisherApisString+"/"+apiId+"/mediation-policies");   
	 
	        return getAllMediationPoliciesOfAPIRes; 
	    }
	    
	    public Response addApiSpecificMediationPolicy(String apiId){
	        Response addApiSpecificMediationPolicyRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken)  
	        .contentType(ContentTypes.APPLICATION_JSON)   
	        .post(endPoint+publisherApisString+"/"+apiId+"/mediation-policies");   
	 
	        return addApiSpecificMediationPolicyRes; 
	    }
	    
	    //API mediation Policy Section--------------------------------------------------------------------------
	    public Response getApiSpecificMediationPolicy(String apiId, String policyId){
	        Response getApiSpecificMediationPolicyRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken)  
	        .contentType(ContentTypes.APPLICATION_JSON)   
	        .get(endPoint+publisherApisString+"/"+apiId+"/mediation-policies/"+policyId);   
	 
	        return getApiSpecificMediationPolicyRes; 
	    }
	    
	    public Response deleteApiSpecificMediationPolicy(String apiId, String policyId){
	        Response deleteApiSpecificMediationPolicyRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken)  
	        .contentType(ContentTypes.APPLICATION_JSON)   
	        .delete(endPoint+publisherApisString+"/"+apiId+"/mediation-policies/"+policyId);   
	 
	        return deleteApiSpecificMediationPolicyRes; 
	    }
	    
	    public Response downloadApiSpecificMediationPolicy(String apiId, String policyId){
	        Response downloadApiSpecificMediationPolicyRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken)  
	        .contentType(ContentTypes.APPLICATION_JSON)   
	        .get(endPoint+publisherApisString+"/"+apiId+"/mediation-policies/"+policyId+"/content");   
	 
	        return downloadApiSpecificMediationPolicyRes; 
	    }
	    
	    public Response updateApiSpecificMediationPolicy(String apiId, String policyId, String tokenExchangeXmlPath, String typeTxtPath){
	        Response downloadApiSpecificMediationPolicyRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken) 
	        .contentType(ContentTypes.MULTIPART_FORMDATA)
	        .multiPart("file", new File(resourceParenPath+tokenExchangeXmlPath))
	        .multiPart("type", new File(resourceParenPath+typeTxtPath)) 
	        .put(endPoint+publisherApisString+"/"+apiId+"/mediation-policies/"+policyId+"/content");   
	 
	        return downloadApiSpecificMediationPolicyRes; 
	    }
	   
	    //GraphQL Policies Section------------------------------------------------------------------------------
	    public Response getComplexityRelatedDetailsOfApi(String apiId){
	        Response getComplexityRelatedDetailsOfApiResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/graphql-policies/complexity");

	        return getComplexityRelatedDetailsOfApiResponse;
	    }
	    
	    public Response updateComplexityRelatedDetailsOfApi(String apiId, String jsonPayloadPath){
	    	
	    	try {
	    		payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath+jsonPayloadPath));
	    		payloadpls1 = new String(payloadplj1);

	        } catch (Exception e) {
	        }
	    	
	        Response updateComplexityRelatedDetailsOfApiResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .body(payloadpls1)
	        .put(endPoint+publisherApisString+"/"+apiId+"/graphql-policies/complexity");

	        return updateComplexityRelatedDetailsOfApiResponse;
	    }
	    
	    public Response getTypesAndFieldsOfGraphQlSchema(String apiId){
	        Response getTypesAndFieldsOfGraphQlSchemaResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/graphql-policies/complexity/types");

	        return getTypesAndFieldsOfGraphQlSchemaResponse;
	    }
	    
	    //API Audit Section---------------------------------------------------------------------------------------
	    public Response getSecurityAuditReportOfAuditApi(String apiId){
	        Response getSecurityAuditReportOfAuditApiResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/auditapi");

	        return getSecurityAuditReportOfAuditApiResponse;
	    }   
	    
	    //External Stores Section-------------------------------------------------------------------------------
	    public Response getListOfExternalStoresWhichApiPublished(String apiId){
	        Response getListOfExternalStoresWhichApiPublishedResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/external-stores");

	        return getListOfExternalStoresWhichApiPublishedResponse;
	    }
	    
	    public Response publishApiToExternalStore(String apiId, String storeName){
	        Response publishApiToExternalStoreResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .post(endPoint+publisherApisString+"/publish-to-external-stores?externalStoreId="+storeName);

	        return publishApiToExternalStoreResponse;
	    }
	    
	    public Response getExternalStoresListToPublishApi(String apiId){
	        Response getExternalStoresListToPublishApiResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .post(endPoint+publisherApisString+"/"+apiId+"/external-stores");

	        return getExternalStoresListToPublishApiResponse;
	    }
	    
	    //Import Export Section-------------------------------------------------------------------------------
	    public Response exportAnApi(String apiId, String apiName, String version,String provider, String format){
	        Response exportAnApiResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .post(endPoint+publisherApisString+"/export?apiId="+apiId+"&name="+apiName+"&version="+version+"&provider="+provider+"&format="+format);

	        return exportAnApiResponse;
	    }

	    //Client Certificate Section---------------------------------------------------------------------------- 
	    public Response searchUploadedClientCertificate(String apiId){
	        Response searchUploadedClientCertificateResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisString+"/"+apiId+"/client-certificates?alias=wso2carbon");

	        return searchUploadedClientCertificateResponse;
	    }

	    public Response uploadNewCertificate(String apiId, String certificate, String alias, String wso2Carbon, String tier){
		      Response uploadNewCertificateRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)  
		      .contentType(ContentTypes.MULTIPART_FORMDATA)
		      .multiPart("certificate", new File(resourceParenPath+certificate))
		      .multiPart("alias", new File(resourceParenPath+alias))
		      .multiPart("apiId", new File(resourceParenPath+apiId))
		      .multiPart("tier", new File(resourceParenPath+tier))
		      .post(endPoint+publisherApisString+"/"+apiId+"/client-certificates");   
		
		    
		      return uploadNewCertificateRes; 
		}
	    
	    public Response updateCertificate(String apiId, String certificate, String alias, String tier){
		      Response updateCertificateRes  = RestAssured.given()
		      .relaxedHTTPSValidation()
		      .auth() 
		      .oauth2(accessToken)    
		      .contentType(ContentTypes.MULTIPART_FORMDATA)
		      .multiPart("certificate", new File(resourceParenPath+certificate))
		      .multiPart("alias", new File(resourceParenPath+alias))
		      .multiPart("apiId", new File(resourceParenPath+apiId))
		      .multiPart("tier", new File(resourceParenPath+tier))
		      .put(endPoint+publisherApisString+"/"+apiId+"/client-certificates/"+alias);   
		
		      return updateCertificateRes; 
		}

	    public Response deleteCertficate(String apiId){
	        Response deleteCertficateRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .delete(endPoint+publisherApisString+"/"+apiId+"/client-certificates/wso2carbon");

	        return deleteCertficateRes;
	    }

	    public Response getCertficateInformation(String apiId){
	        Response getCertficateInformationRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .delete(endPoint+publisherApisString+"/"+apiId+"/client-certificates/wso2carbon");

	        return getCertficateInformationRes;
	    }
	    
	    //Deployments Section-----------------------------------------------------------------------------------
	    public Response getDeploymentStatus(String apiId){
	        Response getDeploymentStatusRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken)  
	        .contentType(ContentTypes.APPLICATION_JSON)   
	        .get(endPoint+publisherApisString+"/"+apiId+"/deployments");   
	 
	        return getDeploymentStatusRes; 
	    }
	    
	    //Unified Search (/search)-------------------------------------------------------------------------------
	    public Response searchApiAndDocumentationByContent(String query){
	        Response searchApiAndDocumentationByContentRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken)  
	        .contentType(ContentTypes.APPLICATION_JSON)   
	        .get(endPoint+"/search?query="+query);   
	 
	        return searchApiAndDocumentationByContentRes; 
	    }
	    
	    //Roles (/roles)-------------------------------------------------------------------------------
	    public Response checkRoleAlreadyExists(String roleId){
	        Response checkRoleAlreadyExistsRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken)  
	        .contentType(ContentTypes.APPLICATION_JSON)   
	        .head(endPoint+"/roles/"+roleId);   
	 
	        return checkRoleAlreadyExistsRes; 
	    }
	    
	    public Response validateGivenUserHasGivenRole(String roleId){
	        Response validateGivenUserHasGivenRoleRes  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth() 
	        .oauth2(accessToken)  
	        .contentType(ContentTypes.APPLICATION_JSON)   
	        .get(endPoint+"/me/roles/"+roleId);   
	 
	        return validateGivenUserHasGivenRoleRes; 
	    }
	    
	}
	
	//-----------------------------------------------------------------------
	
	public static class Alerts {

	    String endPoint;
	    String accessToken;
	    ApimVersions version;

	    String publisherAlertsString = "/settings";
	 
	    byte[] payloadJson1;
	    String payloadString1;

	    String resourceParentPath = "./src/test/payloads/";

	    public Alerts(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	    }

	    public Response getPublisherAlertTypes(){
	        Response getPublisherAlertTypesResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+"/alert-types");

	        return getPublisherAlertTypesResponse;
	    }

	    public Response getPublisherAlertTypesSubscribed(){
	        Response getPublisherAlertTypesSubscribedResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+"/alert-subscriptions");

	        return getPublisherAlertTypesSubscribedResponse;
	    }

	    public Response subscribeToSelectedAlertType(String jsonPayloadPath){

	        try {
	            payloadJson1 = Files.readAllBytes(Paths.get(resourceParentPath+jsonPayloadPath));
			    payloadString1 = new String(payloadJson1);

	        } catch (Exception e) {
	            
	        }

	        Response subscribeToSelectedAlertTypeResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .body(payloadString1)
	        .put(endPoint+"/alert-subscriptions");

	        return subscribeToSelectedAlertTypeResponse;
	    }

	    public Response unsubscribeUserAllAlertTypes(){
	        Response unsubscribeUserAllAlertTypesResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .delete(endPoint+"/alert-subscriptions");

	        return unsubscribeUserAllAlertTypesResponse;
	    }

	    public Response getAbnormalRequests(String alertType){
	        Response getAbnormalRequestsResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+"/alerts/"+alertType+"/configurations");

	        return getAbnormalRequestsResponse;
	    }

	    public Response addAbnormalRequests(String alertType, String configurationId, String jsonPayloadPath){

	        try {
	            payloadJson1 = Files.readAllBytes(Paths.get(resourceParentPath+jsonPayloadPath));
			    payloadString1 = new String(payloadJson1);

	        } catch (Exception e) {
	        }

	        Response addAbnormalRequestsResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .body(payloadString1)
	        .put(endPoint+"/alerts/"+alertType+"/configurations/"+configurationId);

	        return addAbnormalRequestsResponse;
	    }

	    public Response deleteAbnormalRequests(String alertType, String configurationId){

	        Response deleteAbnormalRequestsResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .delete(endPoint+"/alerts/"+alertType+"/configurations/"+configurationId);

	        return deleteAbnormalRequestsResponse;
	    }
	   
	}
	
	//-----------------------------------------------------------------------
	
	public static class ApiProducts {

	    String endPoint;
	    String accessToken;
	    ApimVersions version;

	    byte[] createApiProductPayloadJson;
	    String createApiProductPayloadString;
	    Response createApiProductResponse;

	    byte[] apiProductUpdatePayloadJson;
	    String apiProductUpdatePayloadString;

	    String publisherApisProductString = "/api-products";

	    byte[] payloadJson1;
	    String payloadString1;

	    String resouseParentPath = "./src/test/payloads/";

	    public ApiProducts(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	    }

	    public Response searchApiProduct(){
	        Response searchApiProductResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherApisProductString); 

	        return searchApiProductResponse;
	    }

	    public Response createApiProduct(String contentType, String jsonPayloadPath){

	        try {
	            createApiProductPayloadJson = Files.readAllBytes(Paths.get(resouseParentPath+jsonPayloadPath));
			    createApiProductPayloadString = new String(createApiProductPayloadJson);

	            createApiProductResponse  = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .body(createApiProductPayloadString)
	            .contentType(contentType)
	            .post(endPoint+publisherApisProductString);
	        } catch (Exception e) {
	            
	        }

	        return createApiProductResponse;

	    } 

	    public Response deleteApiProduct(String apiId){
	        Response deleteApiProductResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .delete(endPoint+publisherApisProductString+"/"+apiId);

	        return deleteApiProductResponse;
	    }

	    public Response getDetailsOfApiProduct(String apiProductId){
	        Response getDetailsOfApiProductResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType("application/json")
	        .get(endPoint+publisherApisProductString+"/"+apiProductId);

	        return getDetailsOfApiProductResponse;
	    }

	    public Response getSwaggerDefinition(String apiPproductId){
	        Response getSwaggerDefinitionResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherApisProductString+"/"+apiPproductId+"/swagger"); 

	        return getSwaggerDefinitionResponse;
	    }

	    public Response updateApiProduct(String apiProductId, String contentType,  String jsonPayloadPath){

	        try {
	            apiProductUpdatePayloadJson = Files.readAllBytes(Paths.get(resouseParentPath+jsonPayloadPath));
			    apiProductUpdatePayloadString = new String(apiProductUpdatePayloadJson);
	        } catch (Exception e) {
	        }

	        Response updateApiProductResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(contentType)
	        .body(apiProductUpdatePayloadString)
	        .put(endPoint+publisherApisProductString+"/"+apiProductId);

	        return  updateApiProductResponse;

	    }

	    public Response getProductThumbnail(String apiProductId){

	        Response getProductThumbnailResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherApisProductString+"/"+apiProductId+"/thumbnail"); 

	        return getProductThumbnailResponse;
	    }

	    public Response uploadProductThumbnail(String imagePath,  String apiProductId){
	        Response uploadProductThumbnailResponse= RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
	                .contentType(ContentTypes.MULTIPART_FORMDATA)
					.multiPart(new File(resouseParentPath+imagePath))
					.put(endPoint+publisherApisProductString+"/"+apiProductId+"/thumbnail");

	        return uploadProductThumbnailResponse;
	    }

	    public Response isApiProductOutdated(String apiProductId){
	        Response isApiProductOutdatedResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherApisProductString+"/"+apiProductId+"/is-outdated"); 

	        return isApiProductOutdatedResponse;
	    }

	    //API PRODUCT DOCUMENTATION RELATED
	    public Response getDocumentsOfApiProduct(String apiProductId){

	        Response getDocumentsOFApiProductRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherApisProductString+"/"+apiProductId+"/documents"); 

	        return getDocumentsOFApiProductRes;
	    }

	    public Response updateDocumentsOfApiProduct(String apiProductId, String documentId, String jsonPayloadPath){

	        try {
	            payloadJson1 = Files.readAllBytes(Paths.get(resouseParentPath+jsonPayloadPath));
			    payloadString1 = new String(payloadJson1);
	        } catch (Exception e) {
	        }

	        Response updateDocumentsOFApiProductRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .body(payloadString1)
	        .put(endPoint+publisherApisProductString+"/"+apiProductId+"/documents/"+documentId); 

	        return updateDocumentsOFApiProductRes;
	    }

	    public Response deleteDocumentsOfApiProduct(String apiProductId, String documentId){

	        Response deleteDocumentsOFApiProductRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .delete(endPoint+publisherApisProductString+"/"+apiProductId+"/documents/"+documentId); 

	        return deleteDocumentsOFApiProductRes;
	    }

	    public Response getContentOfDocumentsOfApiProduct(String apiProductId, String documentId){

	        Response deleteDocumentsOFApiProductRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherApisProductString+"/"+apiProductId+"/documents/"+documentId+"/content"); 

	        return deleteDocumentsOFApiProductRes;
	    }

	    public Response uploadContentOfDocumentsOfApiProduct(String apiProductId, String documentId, String imagePath){

	        Response uploadContentOfDocumentsOfApiProductRes = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.MULTIPART_FORMDATA)
			.multiPart(new File(resouseParentPath+imagePath))
	        .post(endPoint+publisherApisProductString+"/"+apiProductId+"/documents/"+documentId+"/content"); 

	        return uploadContentOfDocumentsOfApiProductRes;
	    }
	    
	}
	
	//-----------------------------------------------------------------------
	
	public static class Deployements {

	    String endPoint;
	    String accessToken;
	    ApimVersions version;

	    String publisherDeploymentString = "/deployments";
	 
	    byte[] payloadJson1;
	    String payloadString1;

	    String resourceParentPath = "./src/test/payloads/";

	    public Deployements(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	        
	    }

	    public Response getDeploymentEnvironmentDetails(){

	        Response retrieveDeploymentEnvironmentDetailsResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherDeploymentString); 

	        return retrieveDeploymentEnvironmentDetailsResponse;
	    }
	    
	}
	
	//---------------------------------------------------------------------------

	public static class EndpointCertificates {
		
		String accessToken="";
	    String endPoint="";
	    ApimVersions version;

	    Response searchApisResponse;
	    Response createApiResponse;
	    Response uploadThumbnailImageResponse;
	    Response getApiDetailsResponse;
	    Response createNewApiVersiResponse;
	    Response updateApiResponse;
	    Response deleteApiResponse;

	    byte[] apiCreationPayloadJson;
	    String apiCreationPayloadString;

	    byte[] createapiproductplj;
	    String createapiproductpls;

	    byte[] updateApiPayloadJson;
	    String updateApiPayloadString;

	    byte[] payloadplj1;
	    String payloadpls1;

	    byte[] payloadplj2;
	    String payloadpls2;

	    String publisherApisString = "/endpoint-certificates";
	    String resourceParenPath = "./src/test/payloads/";

	    public EndpointCertificates(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+"/api/am/publisher/v1";
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+"/api/am/publisher/v3";
	            }
	            
	        } catch (Exception e) {
	        }
	        
	    }
	    
	    public Response getUplodedCertificates(String alias, String endpoint){
	    	Response getUplodedCertificatesResponse = RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
					.get(endPoint+publisherApisString+"?alias="+alias+"&endpoint="+endpoint);

	        return getUplodedCertificatesResponse;
	    }
	    
	    // Many typos in the documentation below to this method, ask or check it again
	    
//	    public Response uploadNewCertificate(String apiId, String certificate, String alias, String endpoint, String tier){
//		      Response uploadNewCertificateRes  = RestAssured.given()
//		      .relaxedHTTPSValidation()
//		      .auth() 
//		      .oauth2(accessToken)    
//		      .multiPart("certificate", new File(resourceParenPath+certificate))
//		      .multiPart("alias", new File(resourceParenPath+alias))
//		      .multiPart("endpoint", new File(resourceParenPath+endpoint))
//		      .contentType(ContentTypes.MULTIPART_FORMDATA)
//		      .put(endPoint+publisherApisString+"/"+apiId+"/client-certificates");   
	//	
//		    
//		      return uploadNewCertificateRes; 
//		}
	    
	    public Response deleteCertificate(String alias, String endpoint){
	    	Response deleteCertificateResponse = RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
					.delete(endPoint+publisherApisString+"?alias="+alias+"&endpoint="+endpoint);

	        return deleteCertificateResponse;
	    }

	    public Response getCertificateInformation(String alias, String endpoint){
	    	Response getCertificateInformationResponse = RestAssured.given()
					.relaxedHTTPSValidation()
					.auth()
					.oauth2(accessToken)
					.get(endPoint+publisherApisString+"/"+alias);

	        return getCertificateInformationResponse;
	    }
	}
	
	//-----------------------------------------------------------------------

	public static class GlobalMediationPolicies {

	    String accessToken = "";
	    ApimVersions version;
	    String endPoint;

	    String publisherApisString = "/mediation-policies";
	    String resourceParenPath = "./src/test/payloads/";

	    public GlobalMediationPolicies(String accessToken, ApimVersions version){
	        this.accessToken = accessToken; 
	        this.version = version;

	        FileInputStream input;
		    Properties properties;


		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	    }

	    public Response getGlobalMediationPolicies(){
	        Response getGlobalMediationPoliciesRes = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .contentType(ContentTypes.APPLICATION_JSON)
	            .get(endPoint+publisherApisString);

	        return getGlobalMediationPoliciesRes;
	    }
	    
	    public Response downloadGlobalMediationPolicies(){
	        Response downloadGlobalMediationPoliciesRes = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .contentType(ContentTypes.APPLICATION_JSON)
	            .get(endPoint+publisherApisString);

	        return downloadGlobalMediationPoliciesRes;
	    }
	    
	}
	
	//-----------------------------------------------------------------------
	
	public static class KeyManager {

	    String endPoint;
	    String accessToken;
	    ApimVersions version;

	    String publisherKeyManagerString = "/key-managers";

	    byte[] payloadJson1;
	    String payloadString1;

	    String resourceParentPath = "./src/test/payloads/";

	    public KeyManager(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	        
	    }

	    public Response getAllKeyManagers(){

	        Response getAllKeyManagersResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherKeyManagerString); 

	        return getAllKeyManagersResponse;
	    }
 
	}
	
	//---------------------------------------------------------------------------

	public static class Labels {

	    String endPoint;
	    String accessToken;
	    ApimVersions version;

	    String publisherLabelString = "/labels";

	    byte[] payloadJson1;
	    String payloadString1;

	    String resourceParentPath = "./src/test/payloads/";

	    public Labels(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	    }

	    public Response getAllRegisteredLabels(){
	        Response getAllRegisteredLabelsResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherLabelString);

	        return getAllRegisteredLabelsResponse;
	    }

	}
	
	//---------------------------------------------------------------------------

	public static class Scopes {

	    String endPoint;
	    String accessToken;
	    ApimVersions version;

	    String publisherScopesString = "/scopes";

	    byte[] payloadJson1;
	    String payloadString1;

	    String resourceParentPath = "./src/test/payloads/";

	    public Scopes(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	        
	    }

	    public Response getAllSharedScopes(){

	        Response getAllSharedScopesResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherScopesString); 

	        return getAllSharedScopesResponse;
	    }

	    public Response addNewSharedScopes(String jsonPayloadPath){

	        try {
	            payloadJson1 = Files.readAllBytes(Paths.get(resourceParentPath+jsonPayloadPath));
			    payloadString1 = new String(payloadJson1);

	        } catch (Exception e) {
	            

	        }

	        Response getAllSharedScopesResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .body(payloadString1)
	        .post(endPoint+publisherScopesString); 

	        return getAllSharedScopesResponse;
	    }

	    public Response getSharedScopeById(String scopeId){

	        Response getSharedScopeByIdResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherScopesString+"/"+scopeId); 

	        return getSharedScopeByIdResponse;
	    }

	    public Response updateSharedScope(String scopeId, String jsonPayloadPath){

	        try {
	            payloadJson1 = Files.readAllBytes(Paths.get(resourceParentPath+jsonPayloadPath));
			    payloadString1 = new String(payloadJson1);

	        } catch (Exception e) {
	            
	        }

	        Response updateSharedScopeResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .body(payloadString1)
	        .put(endPoint+publisherScopesString+"/"+scopeId); 

	        return updateSharedScopeResponse;

	    }

	    public Response deleteSharedScope(String scopeId){

	        Response deleteSharedScopeResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .delete(endPoint+publisherScopesString+"/"+scopeId); 

	        return deleteSharedScopeResponse;
	    }

	    //-------------This method need to check, Bug found in the documentation--------------
	    public Response checkGivenScopeAlreadyAvailable(String scopeId){

	        Response checkGivenScopeAlreadyAvailableResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .head(endPoint+publisherScopesString+"/"+scopeId); 

	        return checkGivenScopeAlreadyAvailableResponse;
	    }

	    public Response getUsageOfSharedScope(String scopeId){

	        Response getUsageOfSharedScopeResponse = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .get(endPoint+publisherScopesString+"/"+scopeId+"/usage"); 

	        return getUsageOfSharedScopeResponse;
	    }
	    
	    
	}
	
	//-----------------------------------------------------------------------

	public static class Settings {

	    String endPoint;
	    String accessToken;
	    ApimVersions version;

	    String publisherSettingsString = "/settings";

	    byte[] payloadJson1;
	    String payloadString1;

	    String resourceParentPath = "./src/test/payloads/";

	    public Settings(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	        
	    }

	    public Response getPublisherSetting(){
	        Response getPublisherSettinResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherSettingsString);

	        return getPublisherSettinResponse;
	    }

	    public Response getAllGatewayEnvironments(){
	        Response getAllGatewayEnvironmentsResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherSettingsString+"/gateway-environments");

	        return getAllGatewayEnvironmentsResponse;
	    }
	    
	}
	
	//-----------------------------------------------------------------------

	public static class Subscriptions {

	    String accessToken = "";
	    ApimVersions version;
	    String endPoint;

	    String publisherApisString = "/subscriptions";
	    String resourceParenPath = "./src/test/payloads/";

	    public Subscriptions(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	    }

	    public Response getAllSubscriptions(String apiId){
	        Response getAllSubscriptionsRes = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .contentType(ContentTypes.APPLICATION_JSON)
	            .get(endPoint+publisherApisString+"?apiId="+apiId);

	        return getAllSubscriptionsRes;
	    }

	    public Response blockSubscription(String subscriptionId, String blockStatus){
	        Response blockSubscriptionRes = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .contentType(ContentTypes.APPLICATION_JSON)
	            .post(endPoint+publisherApisString+"/block-subscription?subscriptionId="+subscriptionId+"&blockState="+blockStatus);

	        return blockSubscriptionRes;

	    }

	    public Response unblockSubscription(String subscriptionId){
	        Response unblockSubscriptionRes = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .contentType(ContentTypes.APPLICATION_JSON)
	            .post(endPoint+publisherApisString+"/unblock-subscription?subscriptionId="+subscriptionId);

	        return unblockSubscriptionRes;

	    }

	    public Response getDetailsOfSubscriber(String subscriptionId){
	        Response getDetailsOfSubscriberRes = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .contentType(ContentTypes.APPLICATION_JSON)
	            .get(endPoint+publisherApisString+"/"+subscriptionId+"/subscriber-info");

	        return getDetailsOfSubscriberRes;
	    }

	    
	}
	
	//----------------------------------------------------------------------------

	public static class Tenants {

	    String endPoint;
	    String accessToken;
	    ApimVersions version;

	    String publisherTenantsString = "/tenants";

	    byte[] payloadJson1;
	    String payloadString1;

	    String resourceParentPath = "./src/test/payloads/";

	    public Tenants(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	        
	    }

	    public Response getTenantsByState(String state){
	        Response getTenantsByStateResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .get(endPoint+publisherTenantsString+"?state="+state);

	        return getTenantsByStateResponse;
	    }
	    
	    public Response checkTenantAlreadyExists(String tenantDomain){
	        Response checkTenantAlreadyExistsResponse  = RestAssured.given()
	        .relaxedHTTPSValidation()
	        .auth()
	        .oauth2(accessToken)
	        .contentType(ContentTypes.APPLICATION_JSON)
	        .head(endPoint+publisherTenantsString+"/"+tenantDomain);

	        return checkTenantAlreadyExistsResponse;
	    }
	}
	
	//------------------------------------------------------------------------
	
	public static class ThrottlingPolicies {

	    String accessToken = "";
	    ApimVersions version;
	    String endPoint;

	    String publisherApisString = "/throttling-policies";
	    String resourceParenPath = "./src/test/payloads/";

	    public ThrottlingPolicies(String accessToken, ApimVersions version){
	        this.accessToken = accessToken;
	        this.version = version;

	        FileInputStream input;
		    Properties properties;

		    try {
	            String path =  "./src/test/resources/config.properties";
				properties = new Properties();
				input = new FileInputStream(path);
				properties.load(input);
	            if(version == ApimVersions.APIM_3_2){
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_3_2");
	            }
	            else{
	                this.endPoint = properties.getProperty("base_url")+properties.getProperty("publisher_url_4_1");
	            }
	            
	        } catch (Exception e) {
	        }
	    }

	    public Response getThrottlingPoliciesForGivenType(String policyLevel){
	        Response getThrottlingPoliciesForGivenTypeRes = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .get(endPoint+publisherApisString+"/"+policyLevel);

	    return getThrottlingPoliciesForGivenTypeRes;
	    }

	    public Response getDetailsOfPolicy(String policyLevel, String policyName){
	        Response getDetailsOfPolicyRes = RestAssured.given()
	            .relaxedHTTPSValidation()
	            .auth()
	            .oauth2(accessToken)
	            .get(endPoint+publisherApisString+"/"+policyLevel.toLowerCase()+"/"+policyName);

	    return getDetailsOfPolicyRes;
	    }
	    
	}


}




