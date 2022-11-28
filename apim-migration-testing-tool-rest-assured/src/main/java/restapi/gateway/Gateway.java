package restapi.gateway;

import java.io.FileInputStream;
import java.util.Properties;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.ContentTypes;

public class Gateway {
	
	public static class ReDeploy{
		
		String accessToken;
        String endPoint;
        
        String publisherApisString = "/redeploy-api";
        String resourceParenPath = "./src/test/payloads/";
        
    	public ReDeploy(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_4_1");
                }
                
            } catch (Exception e) {
            }

    	}
    	
    	public Response reDeployAPIInGateway(String apiName, String version, String tenantDomain){
            Response reDeployAPIInGatewayRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+publisherApisString+"?apiName="+apiName+"&version="+version+"&tenantDomain="+tenantDomain);

            return reDeployAPIInGatewayRes;
        }
    	
	}
	
	public static class Undeploy{
		
		String accessToken;
        String endPoint;
        
        String publisherApisString = "/undeploy-api";
        String resourceParenPath = "./src/test/payloads/";
        
        public Undeploy(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_4_1");
                }
                
            } catch (Exception e) {
            }

    	}
    	
    	public Response UneployAPIFromGateway(String apiName, String version, String tenantDomain){
            Response UneployAPIFromGatewayRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+publisherApisString+"?apiName="+apiName+"&version="+version+"&tenantDomain="+tenantDomain);

            return UneployAPIFromGatewayRes;
        }
    	
	}
	
	public static class GetApiArtifact{
		
		String accessToken;
        String endPoint;
        
        String resourceParenPath = "./src/test/payloads/";
        
        public GetApiArtifact(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_4_1");
                }
                
            } catch (Exception e) {
            }

    	}
    	
    	public Response getAPISynapseDefinitionArtifactFromStorage(String apiName, String version, String tenantDomain){
            Response getAPISynapseDefinitionArtifactFromStorageRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+"/api-artifact?"+"?apiName="+apiName+"&version="+version+"&tenantDomain="+tenantDomain);

            return getAPISynapseDefinitionArtifactFromStorageRes;
        }
    	
    	public Response getLocalEntryFromStorage(String apiName, String version, String tenantDomain){
            Response getLocalEntryFromStorageRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+"/local-entry"+"?apiName="+apiName+"&version="+version+"&tenantDomain="+tenantDomain);

            return getLocalEntryFromStorageRes;
        }
    	
    	public Response getSequencesFromStorage(String apiName, String version, String tenantDomain){
            Response getSequencesFromStorageRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+"/sequence"+"?apiName="+apiName+"&version="+version+"&tenantDomain="+tenantDomain);

            return getSequencesFromStorageRes;
        }
    	
    	public Response getEndPointsFromStorageForAPI(String apiName, String version, String tenantDomain){
            Response getEndPointsFromStorageForAPIRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+"/end-points"+"?apiName="+apiName+"&version="+version+"&tenantDomain="+tenantDomain);

            return getEndPointsFromStorageForAPIRes;
        }
    	
	}
	
	public static class GetApiInfo{
		
		String accessToken;
        String endPoint;
        
        String publisherApisString = "apis";
        String resourceParenPath = "./src/test/payloads/";
        
        public GetApiInfo(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_4_1");
                }
                
            } catch (Exception e) {
            }

    	}
    	
    	public Response getListAPISByProvidingContextAndVersion(String context, String version, String tenantDomain){
            Response getListAPISByProvidingContextAndVersionRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+"/apis?"+"?context="+context+"&version="+version+"&tenantDomain="+tenantDomain);

            return getListAPISByProvidingContextAndVersionRes;
        }
    	
    	public Response getSubscriptionInformationOfApiByProvidingApiUuid(String apiId, String tenantDomain){
            Response getSubscriptionInformationOfApiByProvidingApiUuidRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+"/apis/"+apiId+"?tenantDomain="+tenantDomain);

            return getSubscriptionInformationOfApiByProvidingApiUuidRes;
        }
    	
	}
	
	public static class GetApplicationInfo{
		
		String accessToken;
        String endPoint;
        
        String publisherApisString = "applications";
        String resourceParenPath = "./src/test/payloads/";
        
        public GetApplicationInfo(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_4_1");
                }
                
            } catch (Exception e) {
            }

    	}
    	
    	public Response getListAPISByProvidingContextAndVersion(String applicationName, String version, String tenantDomain){
            Response getListAPISByProvidingContextAndVersionRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+publisherApisString+"?applicationName="+applicationName+"&version="+version+"&tenantDomain="+tenantDomain);

            return getListAPISByProvidingContextAndVersionRes;
        }
    	
	}
	
	public static class GetSubscriptionInfo{
		
		String accessToken;
        String endPoint;
        
        String publisherApisString = "subscriptions";
        String resourceParenPath = "./src/test/payloads/";
        
        public GetSubscriptionInfo(String accessToken, ApimVersions version) {
    		this.accessToken = accessToken;
            
            FileInputStream input;
    	    Properties properties;

            try {
                String path =  "./src/test/resources/config.properties";
    			properties = new Properties();
    			input = new FileInputStream(path);
    			properties.load(input);
                if(version == ApimVersions.APIM_3_2){
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_3_2");
                }
                else{
                    this.endPoint = properties.getProperty("base_url")+properties.getProperty("gateway_url_4_1");
                }
                
            } catch (Exception e) {
            }

    	}
    	
    	public Response getSubscriptionsMetaInformation(String appUUID, String apiUUID, String version, String tenantDomain){
            Response getSubscriptionsMetaInformationRes = RestAssured.given()
            .relaxedHTTPSValidation()
            .auth()
            .oauth2(accessToken)
            .contentType(ContentTypes.APPLICATION_JSON)
            .post(endPoint+publisherApisString+"?apiUUID="+appUUID+"&apiUUID="+apiUUID+"&tenantDomain="+tenantDomain);

            return getSubscriptionsMetaInformationRes;
        }
    	
	}

}
