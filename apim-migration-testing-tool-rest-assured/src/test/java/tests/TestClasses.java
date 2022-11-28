package tests;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.Authentication;
import restapi.AuthenticationObject;
import restapi.ContentTypes;
import restapi.GrantTypes;
import restapi.Scopes;
import restapi.devportal.DevPortal;
import restapi.devportal.DevPortal.KeyManager_Collections;
import restapi.publisher.Publisher;

public class TestClasses {
	String accessToken;
	String apiId="";
    private static Logger logger = LogManager.getLogger(TestClasses.class);

	@Test
	public void dataGeneration() {
				
                AuthenticationObject authenticationObject = new AuthenticationObject();
                authenticationObject.setUsername("admin");
                authenticationObject.setUserpassword("admin");
                authenticationObject.setEndpoint("https://localhost:9443/client-registration/v0.17/register");
                authenticationObject.setTokenUrl("https://localhost:8243/token"); //For API-M 3.2.0
                // authenticationObject.setTokenUrl("https://localhost:9443/oauth2/token"); //For API-M 4.1.0
                authenticationObject.setPayloadPath("./src/test/payloads/payload.json");
                authenticationObject.setScopes(Scopes.API_PUBLISH, Scopes.API_CREATE, Scopes.API_VIEW, Scopes.API_IMPORT_EXPORT, Scopes.API_MANAGE, Scopes.SUBSCRIPTION_VIEW, Scopes.SUBSCRIPTION_BLOCK, Scopes.CLIENT_CERTIFICAE_VIEW, Scopes.SHARED_SCOPE_MANAGE, Scopes.PUBLISHER_SETTINGS);
                authenticationObject.setContentType(ContentTypes.APPLICATION_JSON);
                authenticationObject.setGrantType(GrantTypes.PASSSWORD);

                Authentication authentication = new Authentication(authenticationObject);
                accessToken = authentication.getAccessToken();

                //API
                //PublisherApis api = new PublisherApis(accessToken, ApimVersions.APIM_3_2);
                Publisher.Apis api = new Publisher.Apis(accessToken, ApimVersions.APIM_3_2);
                
                Response createApiRes = api.createApi(ContentTypes.APPLICATION_JSON, "apicretion_payload_TestClasses.json");
                logger.info("Status Code [CREATE API]: "+createApiRes.statusCode());

                Response searchApiRes = api.searchApis();
                logger.info("Status Code [SEARCH API]: "+searchApiRes.statusCode());
                
                String apiId = searchApiRes.jsonPath().get("list[0]['id']");
                logger.info("[SEARCHED API ID]: "+apiId);

                Response uploadApiThumbnailRes = api.uploadThumbnailImage("thumbnail.jpg", apiId);
                logger.info("Status Code [UPLOAD API THUMBNAIL]: "+uploadApiThumbnailRes.statusCode());

                Response changeApiStatusRes = api.changeApiStatus(apiId, "Publish");
                logger.info("Status Code [CHANGE API STATUS]: "+changeApiStatusRes.statusCode());

                Response searchUploadedClientCertificateRes = api.searchUploadedClientCertificate(apiId);
                logger.info("Status Code [SEARCH UPLOADED CLIENT CERTIFICATE]: "+searchUploadedClientCertificateRes.statusCode());

                Response getCertficateInformationRes = api.getCertficateInformation(apiId);
                logger.info("Status Code [GET CERTIFICATE INFORMATION]: "+getCertficateInformationRes.statusCode());

                Response getDeploymentStatusRes = api.getDeploymentStatus(apiId);
                logger.info("Status Code [GET DEPLOYMENT STATUS]: "+getDeploymentStatusRes.statusCode());
                
                //API Product 
                Publisher.ApiProducts apiProd = new Publisher.ApiProducts(accessToken,ApimVersions.APIM_3_2);
                
                
                Response searchApiProductRes = apiProd.searchApiProduct();
                logger.info("Status Code [SEARCH API PRODUCT]: "+searchApiProductRes.statusCode());
                String apiProductId = searchApiProductRes.jsonPath().get("list[0]['id']");

                Response createApiProductRes = apiProd.createApiProduct(ContentTypes.APPLICATION_JSON, "apiproduct_creation_3_2.json");
                logger.info("Status Code [CREATE API PRODUCT]: "+createApiProductRes.statusCode());

                Response updateApiProductRes = apiProd.updateApiProduct(apiProductId, ContentTypes.APPLICATION_JSON, "updateApiProductPayload_3_2.json");
                logger.info("Status Code [UPDATE API PRODUCT]: "+updateApiProductRes.statusCode());

                Response uploadProductThumbnailRes = apiProd.uploadProductThumbnail("thumbnail.jpg", apiProductId);
                logger.info("Status Code [UPDATE API PRODUCT THUMBNAIL]: "+uploadProductThumbnailRes.statusCode());

                Response deletePendingLifecycleStateChangeTasksRes = api.deletePendingLifecycleStateChangeTasks(apiId);
                logger.info("Status Code [DELETE API PRODUCT LIFECYCLE]: "+deletePendingLifecycleStateChangeTasksRes.statusCode());

                //Scopes
                Publisher.Scopes pScopes = new Publisher.Scopes(accessToken, ApimVersions.APIM_3_2);

                Response getSharedScopesrRes = pScopes.getAllSharedScopes();
                logger.info("Status Code [GET ALL SHARED SCOPES]: "+getSharedScopesrRes.statusCode());

                String scopeId = getSharedScopesrRes.jsonPath().get("list[0]['id']");

                Response addNewSharedScopesrRes = pScopes.addNewSharedScopes("addNewSharedScopes.json");
                logger.info("Status Code [ADD NEW SHARED SCOPES]: " + addNewSharedScopesrRes.statusCode()); 

                Response getSharedScopeByIdRes = pScopes.getSharedScopeById(scopeId);
                logger.info("Status Code [ADD NEW SHARED SCOPES]: " + getSharedScopeByIdRes.statusCode());

                Response updateSharedScopeRes = pScopes.updateSharedScope(scopeId,"uploadSharedScope.json");
                logger.info("Status Code [UPDATE SHARED SCOPES]: " + updateSharedScopeRes.statusCode());

                // Response deleteSharedScopeRes = pScopes.deleteSharedScope(scopeId);
                // logger.info("Status Code [DELETE SHARED SCOPES]: " + deleteSharedScopeRes.statusCode());

                // Response checkGivenScopeAlreadyAvailableRes = pScopes.checkGivenScopeAlreadyAvailable(scopeId);
                // logger.info("Status Code [DELETE SHARED SCOPES]: " + checkGivenScopeAlreadyAvailableRes.statusCode());

                Response getUsageRes = pScopes.getUsageOfSharedScope(scopeId);
                logger.info("Status Code [GET USAGE OF SHARED SCOPES]: " + getUsageRes.statusCode());

                //Deployments 
                Publisher.Deployements pDeployement = new Publisher.Deployements(accessToken, ApimVersions.APIM_3_2);

                Response getDeploymentEnvironmentDetailsRes = pDeployement.getDeploymentEnvironmentDetails();
                logger.info("Status Code [GET DEPLOYMENTS ENVIROMENT DETAILS]: " + getDeploymentEnvironmentDetailsRes.statusCode());

                //Key Manager
                Publisher.KeyManager pKeyManager = new Publisher.KeyManager(accessToken, ApimVersions.APIM_3_2);

                Response pKeyManagerRes = pKeyManager.getAllKeyManagers();
                logger.info("Status Code [GET ALL KEY MANAGERS]: " + pKeyManagerRes.statusCode());

                //Settings
                Publisher.Settings pSettings = new Publisher.Settings(accessToken, ApimVersions.APIM_3_2);
                
                Response getPublisherSettingsRes = pSettings.getPublisherSetting();
                logger.info("Status Code [GET PUBLISHER SETTING]: " + getPublisherSettingsRes.statusCode()); 

                Response getAllGatewayEnviromentsRes = pSettings.getPublisherSetting();
                logger.info("Status Code [GET ALL GATEWAY ENVIRONMENTS]: " + getAllGatewayEnviromentsRes.statusCode()); 

                //Tenants
                Publisher.Tenants pTenants = new Publisher.Tenants(accessToken, ApimVersions.APIM_3_2);

                Response getTenantsByStateRes = pTenants.getTenantsByState("active");
                logger.info("Status Code [GET TENANTS]: " + getTenantsByStateRes.statusCode()); 

                //Labels
                Publisher.Labels pLabels = new Publisher.Labels(accessToken, ApimVersions.APIM_3_2);

                Response getAllLabels = pLabels.getAllRegisteredLabels();
                logger.info("Status Code [GET ALL LABELS]: " + getAllLabels.statusCode()); 

                logger.info("Data creation has been done-------------------------");
            
}
	
		@Test
		public void devPortalTest() {
			AuthenticationObject authenticationObject = new AuthenticationObject();
            authenticationObject.setUsername("admin");
            authenticationObject.setUserpassword("admin");
            authenticationObject.setEndpoint("https://localhost:9443/client-registration/v0.17/register");
            authenticationObject.setTokenUrl("https://localhost:8243/token"); //For API-M 3.2.0
            // authenticationObject.setTokenUrl("https://localhost:9443/oauth2/token"); //For API-M 4.1.0
            authenticationObject.setPayloadPath("./src/test/payloads/payload.json");
            authenticationObject.setScopes(Scopes.API_PUBLISH, Scopes.API_CREATE, Scopes.API_VIEW, Scopes.API_IMPORT_EXPORT, Scopes.API_MANAGE, Scopes.SUBSCRIPTION_VIEW, Scopes.SUBSCRIPTION_BLOCK, Scopes.CLIENT_CERTIFICAE_VIEW, Scopes.SHARED_SCOPE_MANAGE, Scopes.PUBLISHER_SETTINGS, Scopes.SUBSCRIBE);
            authenticationObject.setContentType(ContentTypes.APPLICATION_JSON);
            authenticationObject.setGrantType(GrantTypes.PASSSWORD);

            Authentication authentication = new Authentication(authenticationObject);
            accessToken = authentication.getAccessToken();
            
//            DevPortal devPortal = new DevPortal();
			
			DevPortal.Apis dPortalApis = new DevPortal.Apis(accessToken,ApimVersions.APIM_3_2);
            
            Response searchApiRes = dPortalApis.searchApis();
           
            apiId = searchApiRes.jsonPath().get("list[0]['id']");
            
            Response dPortalResponse =  dPortalApis.getSwaggerDefinition();
            logger.info("Status Code [SEARCH APIS DEVPORTAL]: " + dPortalResponse.statusCode());
            
            KeyManager_Collections dGraphQlPolicies  =  new DevPortal.KeyManager_Collections(accessToken, ApimVersions.APIM_3_2);
            Response dGraphQlPoliciesResponse = dGraphQlPolicies.getAllKeyManagers();
            logger.info("Status Code [DEV ALL KEY MANAGERS]: " + dGraphQlPoliciesResponse.statusCode());
            
            
		}

        @Test
        public void validateDataAPIM_3_2(){
                Publisher.Apis api = new Publisher.Apis(accessToken,ApimVersions.APIM_3_2);
                
                Response searchApi = api.searchApis();
                logger.info("Status Code [SEARCH API]: "+searchApi.statusCode());
                String apiId = searchApi.jsonPath().get("list[0]['id']");

                Response getApiDetails = api.getApiDetails(apiId);
                logger.info("Status Code [GET APIS DETAILS]: "+getApiDetails.statusCode());

                Response getApiThumbnail = api.getThumbnailImage(apiId);
                logger.info("Status Code [GET API THUMBNAIL]: "+getApiThumbnail.statusCode());

                Response getApiStatus = api.getApiStatus(apiId);
                logger.info("Status Code [GET API STATUS]: "+getApiStatus.statusCode());

                Response getApiStatusRes = api.getApiStatus(apiId);
                logger.info("Status Code [GET API STATUS]: "+getApiStatusRes.statusCode());

                Response getLifecycleStateDataOfApiRes = api.getLifecycleStateDataOfApi(apiId);
                logger.info("Status Code [GET STATE DATA OF API]: "+getLifecycleStateDataOfApiRes.statusCode());

                Publisher.Subscriptions subs = new Publisher.Subscriptions(accessToken, ApimVersions.APIM_3_2);
                Response getAllSubs = subs.getAllSubscriptions(apiId);
                logger.info("Status Code [GET ALL SUBSCRIPTIONS]: "+getAllSubs.statusCode());
                String subscriptionId  = getAllSubs.jsonPath().get("list[0]['subscriptionId']");
                logger.info("[SUBSCRIPTIONS ID]: "+subscriptionId);

                Response blockSubs = subs.blockSubscription(subscriptionId, "BLOCKED");
                logger.info("Status Code [BLOCK SUBSCRIPTIONS]: "+blockSubs.statusCode());

                Response unblockSubs = subs.unblockSubscription(subscriptionId);
                logger.info("Status Code [UNBLOCK SUBSCRIPTIONS]: "+unblockSubs.statusCode());

                Response getDetailsOfSubscriber = subs.getDetailsOfSubscriber(subscriptionId);
                logger.info("Status Code [GET DETAILS OF A SUBSCRIBER]: "+getDetailsOfSubscriber.statusCode());

                Response getApiThumbnailRes = api.getThumbnailImage(apiId);
                logger.info("Status Code [GET API THUMBNAIL]: "+getApiThumbnailRes.statusCode());

                Response getResourcePathsofApiRes = api.getResourcePathsofApi(apiId);
                logger.info("Status Code [GET RESOURCE PATH OF API]: "+getResourcePathsofApiRes.statusCode());

                Response getResourcePolicyDefinitionsRes = api.getResourcePolicyDefinitions(apiId);
                logger.info("Status Code [GET RESOURCE POLICY DEFINITION API]: "+getResourcePolicyDefinitionsRes.statusCode());

                Response getResourcePolicyForResourceIdentifierRes = api.getResourcePolicyForResourceIdentifier(apiId,"178");
                logger.info("Status Code [GET RESOURCE POLICY FOR RESOURCE IDENTIFIER]: "+getResourcePolicyForResourceIdentifierRes.statusCode());

                Publisher.ApiProducts apiProd = new Publisher.ApiProducts(accessToken, ApimVersions.APIM_3_2);
                Response searchApiProdsRes = apiProd.searchApiProduct();
                logger.info("Status Code [SEARCH API PRODUCTS]: "+searchApiProdsRes.statusCode());
                String apiProductId  = searchApiProdsRes.jsonPath().get("list[0]['id']");

                Response getDetailsOfApiProdRes = apiProd.getDetailsOfApiProduct(apiProductId);
                logger.info("Status Code [GET DETAILS OF API PRODUCT]: "+getDetailsOfApiProdRes.statusCode());
                
                Response getThumbnailOfApiprodRes = apiProd.getProductThumbnail(apiProductId);
                logger.info("Status Code [GET THUMBNAIL OF API PRODUCT]: "+getThumbnailOfApiprodRes.statusCode());

                Response getApiDocumentationRes = apiProd.getDocumentsOfApiProduct(apiProductId);
                logger.info("Status Code [GET DOC OF API PRODUCTS]: "+getApiDocumentationRes.statusCode());
                String documentationId = getApiDocumentationRes.jsonPath().get("list[0]['documentId']");
                logger.info("[DOCUMENTATION ID]: "+documentationId);

                Response updateDocumentsOFApiProductRes = apiProd.updateDocumentsOfApiProduct(apiProductId, documentationId, "uploadApiProductDoc_payload.json");
                logger.info("Status Code [UPDATE DOC OF API PRODUCTS]: "+updateDocumentsOFApiProductRes.statusCode());

                // Response deleteDocumentsOFApiProductRes = apiProd.deleteDocumentsOfApiProduct(apiProductId, documentationId);
                // logger.info("Status Code [DELETE DOC OF API PRODUCTS]: "+deleteDocumentsOFApiProductRes.statusCode());

                Response getContentOfDocumentsOFApiProductRes = apiProd.getContentOfDocumentsOfApiProduct(apiProductId, documentationId);
                logger.info("Status Code [GET CONTENT OF DOC OF API PRODUCTS]: "+getContentOfDocumentsOFApiProductRes.statusCode());

                Publisher.ThrottlingPolicies policies = new Publisher.ThrottlingPolicies(accessToken, ApimVersions.APIM_3_2);
                Response getAllPolicies = policies.getThrottlingPoliciesForGivenType("api");
                logger.info("Status Code [GET THROTTLING POLICIES]: "+getAllPolicies.statusCode());

                String policyLevel = getAllPolicies.jsonPath().get("list[3]['policyLevel']");
                String policyName = getAllPolicies.jsonPath().get("list[3]['name']");

                Response getDetaisOfPolicy = policies.getDetailsOfPolicy(policyLevel, policyName);
                logger.info("Status Code [GET DETAILS OF A POLICY]: "+getDetaisOfPolicy.statusCode());

                Publisher.GlobalMediationPolicies gPolicies = new Publisher.GlobalMediationPolicies(accessToken, ApimVersions.APIM_3_2);
                Response getGlobalMediationPolicyRes = gPolicies.getGlobalMediationPolicies();
                logger.info("Status Code [GET GLOBAL MEDIATION POLICY]: "+getGlobalMediationPolicyRes.statusCode());

        }

        // @Test
        // public void validateDataAPIM_4_1(){
        //         Publisher.Apis api = new Publisher.Apis(accessToken,ApimVersions.APIM_4_1);
                
        //         Response searchApi = api.searchApis();
        //         logger.info("Status Code [SEARCH API]: "+searchApi.statusCode());
        //         String apiId = searchApi.jsonPath().get("list[0]['id']");

        //         Response getApiDetails = api.getApiDetails(apiId);
        //         logger.info("Status Code [GET APIS DETAILS]: "+getApiDetails.statusCode());

        //         Response getApiThumbnail = api.getThumbnailImage(apiId);
        //         logger.info("Status Code [GET API THUMBNAIL]: "+getApiThumbnail.statusCode());

        //         Response getApiStatus = api.getApiStatus(apiId);
        //         logger.info("Status Code [GET API STATUS]: "+getApiStatus.statusCode());

        //         Publisher.ApiProducts apiProd = new Publisher.ApiProducts(accessToken, ApimVersions.APIM_4_1);
        //         Response searchApiProdsRes = apiProd.searchApiProduct();
        //         logger.info("Status Code [SEARCH API PRODUCTS]: "+searchApiProdsRes.statusCode());
        //         String apiProductId  = searchApiProdsRes.jsonPath().get("list[0]['id']");
                
        //         Response getDetailsOfApiProdRes = apiProd.getDetailsOfApiProduct(apiProductId);
        //         logger.info("Status Code [GET DETAILS OF API PRODUCT]: "+getDetailsOfApiProdRes.statusCode());
                
        //         Response getThumbnailOfApiprodRes = apiProd.getProductThumbnail(apiProductId);
        //         logger.info("Status Code [GET THUMBNAIL OF API PRODUCT]: "+getThumbnailOfApiprodRes.statusCode());

        // }
    
}
