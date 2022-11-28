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
import restapi.publisher.Publisher;

public class RunTests{
	String accessToken;
        private static Logger logger = LogManager.getLogger(RunTests.class);

	@Test
	public void dataGeneration() {

        AuthenticationObject authenticationObject = new AuthenticationObject(); 
        authenticationObject.setUsername("admin");
        authenticationObject.setUserpassword("admin");
        authenticationObject.setEndpoint("https://localhost:9443/client-registration/v0.17/register");
        authenticationObject.setTokenUrl("https://localhost:8243/token"); 
        authenticationObject.setPayloadPath("./src/test/payloads/payload.json");
        authenticationObject.setScopes(Scopes.API_PUBLISH, Scopes.API_CREATE, Scopes.API_VIEW, Scopes.API_IMPORT_EXPORT, Scopes.API_MANAGE);
        authenticationObject.setContentType(ContentTypes.APPLICATION_JSON);
        authenticationObject.setGrantType(GrantTypes.PASSSWORD);

        Authentication authentication = new Authentication(authenticationObject);
        accessToken = authentication.getAccessToken();

        //API
        Publisher.Apis api = new Publisher.Apis(accessToken, ApimVersions.APIM_3_2);

        Response createApiRes = api.createApi(ContentTypes.APPLICATION_JSON, "apicretion_payload.json");
        logger.info("Status Code [CREATE API]: "+createApiRes.statusCode());

        Response searchApiRes = api.searchApis();
        logger.info("Status Code [SEARCH API]: "+searchApiRes.statusCode());
        
        String apiId = searchApiRes.jsonPath().get("list[0]['id']");
        logger.info("[SEARCHED API ID]: "+apiId);

        Response uploadApiThumbnailRes = api.uploadThumbnailImage("thumbnail2.jpg", apiId);
        logger.info("Status Code [UPLOAD API THUMBNAIL]: "+uploadApiThumbnailRes.statusCode());
       
        Response changeApiStatusRes = api.changeApiStatus(apiId, "Publish");
        logger.info("Status Code [CHANGE API STATUS]: "+changeApiStatusRes.statusCode());

        Response getApiStatusRes = api.getApiStatus(apiId);
        logger.info("Status Code [GET API STATUS]: "+getApiStatusRes.statusCode());

        Response getApiThumbnailRes = api.getThumbnailImage(apiId);
        logger.info("Status Code [GET API THUMBNAIL]: "+getApiThumbnailRes.statusCode());
        
        //API Product 
        // PublisherApiProducts apiProd = new PublisherApiProducts(accessToken,ApimVersions.APIM_3_2);

        // Response searchApiProductRes = apiProd.searchApiProduct();
        // logger.info("Status Code [SEARCH API PRODUCT]: "+searchApiProductRes.statusCode());
        // Response createApiProductRes = apiProd.createApiProduct(ContentTypes.APPLICATION_JSON, "apiproduct_creation_3_2.json");
        // String apiProductId = searchApiProductRes.jsonPath().get("list[0]['id']");

        // Response updateApiProductRes = apiProd.updateApiProduct(apiProductId, ContentTypes.APPLICATION_JSON, "updateApiProductPayload_3_2.json");
        // logger.info("Status Code [UPDATE API PRODUCT]: "+updateApiProductRes.statusCode());

        // Response uploadProductThumbnailRes = apiProd.uploadProductThumbnail("thumbnail.jpg", apiProductId);
        // logger.info("Status Code [UPDATE API PRODUCT THUMBNAIL]: "+uploadProductThumbnailRes.statusCode());

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

                // PublisherApiProducts apiProd = new PublisherApiProducts(accessToken, ApimVersions.APIM_3_2);
                // Response searchApiProdsRes = apiProd.searchApiProduct();
                // logger.info("Status Code [SEARCH API PRODUCTS]: "+searchApiProdsRes.statusCode());
                // String apiProductId  = searchApiProdsRes.jsonPath().get("list[0]['id']");
                
                // Response getDetailsOfApiProdRes = apiProd.getDetailsOfApiProduct(apiProductId);
                // logger.info("Status Code [GET DETAILS OF API PRODUCT]: "+getDetailsOfApiProdRes.statusCode());
                
                // Response getThumbnailOfApiprodRes = apiProd.getProductThumbnail(apiProductId);
                // logger.info("Status Code [GET THUMBNAIL OF API PRODUCT]: "+getThumbnailOfApiprodRes.statusCode());


        }
    
}
