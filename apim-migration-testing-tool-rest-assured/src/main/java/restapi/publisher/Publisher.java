/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package restapi.publisher;

import exceptions.RestAssuredMigrationException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import restapi.ApimVersions;
import restapi.ContentTypes;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * .
 * Publisher class to implement the functionalities in publisher portal
 */
public class Publisher {

    public static class Apis {


        String accessToken = "";
        String endPoint = "";
        String baseURL;
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

        public Apis(String baseURL, String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;
            this.baseURL = baseURL;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = baseURL + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = baseURL + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * This method is for search APIs
         *
         * @return search API Response
         * @throws RestAssuredMigrationException
         */
        public Response searchApis() throws RestAssuredMigrationException {
            try {
                searchApisResponse = RestAssured.given()
                        .relaxedHTTPSValidation()
                        .auth()
                        .oauth2(accessToken)
                        .get(endPoint + publisherApisString);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while searching for API", e);
            }

            return searchApisResponse;
        }

        /**
         * .
         * This method is used to create API
         *
         * @param contentType
         * @param jsonPayloadPath
         * @return Create API Response
         * @throws RestAssuredMigrationException
         */

        public Response createApi(String jsonPayload, boolean isFile) throws RestAssuredMigrationException {

            try {
            	String endPoint = "/apis";

            	if(isFile) {
            		// jsonPayload = getPayloadFile(jsonPayload);
            	}

                createApiResponse = RestAssured.given()
                        .relaxedHTTPSValidation()
                        .auth()
                        .oauth2(accessToken)
                        .body(jsonPayload)
                        .contentType(ContentTypes.APPLICATION_JSON)
                        .post(this.endPoint + endPoint);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while creating the API", e);

            }

            return createApiResponse;
        }

        /**
         * .
         * This method will retrieve API details
         *
         * @param apiId
         * @return Get API data response
         */

        public Response getApiDetails(String apiId) {
            getApiDetailsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + apiId);

            return getApiDetailsResponse;
        }

        /**
         * .
         * This method creates new API version
         *
         * @param apiId
         * @param apiVersion
         * @param defaultVersion
         * @return Create new API version Response
         */

        public Response createNewApiVersion(String apiId, String apiVersion, boolean defaultVersion) {


            createNewApiVersiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType("application/json")
                    .post(this.endPoint + "/apis/copy-api?newVersion=" + apiVersion + "&defaultVersion=" + defaultVersion + "&apiId=" + apiId);
            return createNewApiVersiResponse;

        }

        /**
         * .
         *
         * @param contentType
         * @param apiId
         * @param jsonPayloadPath
         * @return update API response
         */

        public Response updateApi(String contentType, String apiId, String payload) throws RestAssuredMigrationException {

        	updateApiResponse = RestAssured.given()
                        .relaxedHTTPSValidation()
                        .auth()
                        .oauth2(accessToken)
                        .body(payload)
                        .contentType(contentType)
                        .put(this.endPoint + "/apis/" + apiId);

            return updateApiResponse;

        }

        /**
         * .
         * Deletes API
         *
         * @param apiId
         * @return delete API response
         */

        public Response deleteApi(String apiId) {
            deleteApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .delete(endPoint + publisherApisString + "/" + apiId);

            return deleteApiResponse;
        }

        /**
         * .
         * Retrieve the swagger definition
         *
         * @return Response
         */

        public Response getSwaggerDefinition() {
            Response getSwaggerDefinitionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString);
            return getSwaggerDefinitionResponse;
        }

        /**
         * .
         * Updates the swagger definition
         *
         * @param apiId
         * @return Response
         */

        public Response updateSwaggerDefinition(String apiId) {
            Response updateSwaggerDefinitionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .body(updateApiPayloadString)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/" + apiId + "/swagger");

            return updateSwaggerDefinitionRes;
        }

        /**
         * .
         * Generate mock scrips of payloads
         *
         * @param apiId
         * @return Response
         */


        public Response generateMockResponsePayloads(String apiId) {
            Response generateMockResponsePayloadsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType("application/json")
                    .post(endPoint + publisherApisString + "/" + apiId + "/generate-mock-scripts");

            return generateMockResponsePayloadsResponse;

        }

        /**
         * .
         * Retrieve the thumbnail image
         *
         * @param apiId
         * @return Response
         */

        public Response getThumbnailImage(String apiId) {
            Response getThumbnailImageResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + apiId + "/thumbnail");

            return getThumbnailImageResponse;
        }

        /**
         * .
         * Uploads thumbnail image
         *
         * @param imagePath
         * @param apiId
         * @return Response
         */

        public Response uploadThumbnailImage(String imagePath, String apiId) {
            Response uploadThumbnailImageResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .multiPart(new File(resourceParenPath + imagePath))
                    .put(endPoint + publisherApisString + "/" + apiId + "/thumbnail");

            return uploadThumbnailImageResponse;
        }

        /**
         * .
         * Retrieve subscription throttling policies
         *
         * @param apiId
         * @return Response
         */

        public Response getSubscriptionThrotlling(String apiId) {
            Response getSubscriptionThrotllingResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + apiId + "/subscription-policies");

            return getSubscriptionThrotllingResponse;
        }

        /**
         * .
         * Creates an API
         *
         * @param json
         * @return create API Response
         */

        public Response createApiParseJSON(JSONObject json) {

            createApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .body(json.toString())
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString);


            return createApiResponse;

        }

        /**
         * .
         * Import open API definition to create an API
         *
         * @param openApiJsonPath
         * @param dataPath
         * @return Response
         */

        public Response importOpenAPIDefinition(String openApiJsonPath, String dataPath) {

            Response importOpenAPIDefinitionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart("file", new File(resourceParenPath + openApiJsonPath))
                    .multiPart("additionalProperties", new File(resourceParenPath + dataPath))
                    .post(endPoint + publisherApisString + "/import-openapi");

            return importOpenAPIDefinitionRes;
        }

        /**
         * .
         * Import WSDL definition
         *
         * @param apiWSDL
         * @param dataPath
         * @return Response
         */

        public Response importWSDLDefinition(String apiWSDL, String dataPath) {
            Response importWSDLDefinitionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart("file", new File(resourceParenPath + apiWSDL))
                    .multiPart("additionalProperties", new File(resourceParenPath + dataPath))
                    .post(endPoint + publisherApisString + "/import-wsdl");

            return importWSDLDefinitionRes;
        }

        /**
         * .
         * Import graphQL schema to create API throguh Open API Definition
         *
         * @param schemaGraphGl
         * @param dataPath
         * @return
         */

        public Response importAPIDefinition(String schemaGraphGl, String dataPath) {
            Response importAPIDefinitionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart("file", new File(resourceParenPath + schemaGraphGl))
                    .multiPart("additionalProperties", new File(resourceParenPath + dataPath))
                    .post(endPoint + publisherApisString + "/import-graphql-schema");

            return importAPIDefinitionRes;
        }

        /**
         * .
         * Retrieve WSDL meta information
         *
         * @param apiId
         * @return Response
         */
        public Response getWsdlMetaInformation(String apiId) {
            Response getWSDLMetaInformationRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/wsdl-info");

            return getWSDLMetaInformationRes;
        }

        /**
         * .
         * Retrieve WSDL definition
         *
         * @param apiId
         * @return Response
         */

        public Response getWsdlDefinition(String apiId) {
            Response getWsdlDefinitionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/wsdl");

            return getWsdlDefinitionRes;
        }

        /**
         * .
         * Update WSDL definition
         *
         * @param apiWSDL
         * @return
         */

        public Response updateWSDLDefinition(String apiWSDL) {
            Response updateWSDLDefinitionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart(new File(resourceParenPath + apiWSDL))
                    .put(endPoint + publisherApisString + "/wsdl");

            return updateWSDLDefinitionRes;
        }

        /**
         * .
         * Retrieve the resource paths of API
         *
         * @param apiId
         * @return Response
         */

        public Response getResourcePathsofApi(String apiId) {
            Response getResourcePathsofApiRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/resource-paths");

            return getResourcePathsofApiRes;
        }

        /**
         * .
         * Retrieve resource policy definitions
         *
         * @param apiId
         * @return Response
         */
        public Response getResourcePolicyDefinitions(String apiId) {
            Response getResourcePolicyDefinitionsRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/resource-policies?resourcePath=checkPhoneNumber&verb=post&sequenceType=in");

            return getResourcePolicyDefinitionsRes;
        }

        /**
         * .
         * Retrieve resource policy for resource identifier
         *
         * @param apiId
         * @param policyId
         * @return Response
         */

        public Response getResourcePolicyForResourceIdentifier(String apiId, String policyId) {
            Response getResourcePolicyForResourceIdentifierRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/resource-policies/" + policyId);

            return getResourcePolicyForResourceIdentifierRes;
        }

        /**
         * .
         * Update resource policy
         *
         * @param apiId
         * @param policyId
         * @param dataPath
         * @return Response
         */

        public Response updateResourcePolicyForResourceIdentifier(String apiId, String policyId, String dataPath) throws RestAssuredMigrationException {

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + dataPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content");
            }

            Response updateResourcePolicyForResourceIdentifierRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .body(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/" + apiId + "/resource-policies/" + policyId);

            return updateResourcePolicyForResourceIdentifierRes;

        }

        /**
         * .
         * Change the life cycle status of API
         *
         * @param apiId
         * @param action
         * @return
         */
        public Response changeApiStatus(String apiId, String action) {
        	System.out.print("=================== :: "+ accessToken);
            Response changeApiStatusResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + "/apis/change-lifecycle?apiId=" + apiId + "&action=" + action);

            return changeApiStatusResponse;
        }

        /**
         * .
         * Retrieve the current status of API
         *
         * @param apiId
         * @return Response
         */

        public Response getApiStatus(String apiId) {
            Response getApiStatusResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/lifecycle-history");

            return getApiStatusResponse;
        }

        /**
         * .
         * Retrieve the life cycle state data
         *
         * @param apiId
         * @return response
         */

        public Response getLifecycleStateDataOfApi(String apiId) {
            Response getLifecycleStateDataOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/lifecycle-state");

            return getLifecycleStateDataOfApiResponse;
        }

        /**
         * .
         * Delete pending life cycle state change tasks
         *
         * @param apiId
         * @return Response
         */

        public Response deletePendingLifecycleStateChangeTasks(String apiId) {
            Response deletePendingLifecycleStateChangeTasksResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + apiId + "/lifecycle-state/pending-tasks");

            return deletePendingLifecycleStateChangeTasksResponse;
        }

        /**
         * .
         * Download API specific mediation policies.
         *
         * @param openApiJsonPath
         * @return Response
         */
        public Response downloadApiSpecificMediationPolicyRes(String openApiJsonPath) {
            Response downloadApiSpecificMediationPolicyRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .multiPart("file", new File(resourceParenPath + openApiJsonPath))
                    .post(endPoint + publisherApisString + "/validate-openapi");

            return downloadApiSpecificMediationPolicyRes;
        }

        /**
         * .
         * This method validates the endpoint
         *
         * @param apiId
         * @param endpointUrl
         * @return Response
         */
        public Response checkGivenEndpointIsValid(String apiId, String endpointUrl) {
            Response checkGivenEndpointIsValidRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .post(endPoint + publisherApisString + "/validate-endpoint?apiId=" + apiId + "&endpointUrl=" + endpointUrl);

            return checkGivenEndpointIsValidRes;
        }

        /**
         * .
         * This method verifies the existence API context name
         *
         * @param apiName
         * @return Response
         */
        public Response checkGivenApiContextNameExists(String apiName) {
            Response checkGivenApiContextNameExistsRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .post(endPoint + publisherApisString + "/validate?query=" + apiName);

            return checkGivenApiContextNameExistsRes;
        }

        /**
         * .
         * Thois method will validate the WSDL Definition
         *
         * @param apiWsdlPath
         * @return Response
         */
        public Response validateWsdlDefinition(String apiWsdlPath) {
            Response validateWsdlDefinitionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart("file", new File(resourceParenPath + apiWsdlPath))
                    .post(endPoint + publisherApisString + "/validate-wsdl");

            return validateWsdlDefinitionRes;
        }

        /**
         * .
         * This method validate the GraphQL definition and retrieve summary
         *
         * @param schemaGraphQlPath
         * @return Response
         */

        public Response validateGraphQlApiDefinitionAndGetSummary(String schemaGraphQlPath) {
            Response validateGraphQlApiDefinitionAndGetSummaryRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart("file", new File(resourceParenPath + schemaGraphQlPath))
                    .post(endPoint + publisherApisString + "/validate-graphql-schema");

            return validateGraphQlApiDefinitionAndGetSummaryRes;
        }

        /**
         * .
         * This method retrieve GraphQL schema API
         *
         * @param apiId
         * @return Response
         */

        public Response getSchemaOfGraphQlApi(String apiId) {
            Response getSchemaOfGraphQlApiRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + apiId + "/graphql-schema");

            return getSchemaOfGraphQlApiRes;
        }

        /**
         * .
         * This method adds the schema of GraphQL API
         *
         * @param apiId
         * @param schemaGraphQlPath
         * @return Response
         */

        public Response addSchemaOfGraphQlApi(String apiId, String schemaGraphQlPath) {
            Response addSchemaOfGraphQlApiRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart("schemaDefinition", new File(resourceParenPath + schemaGraphQlPath))
                    .put(endPoint + publisherApisString + "/" + apiId + "/graphql-schema");

            return addSchemaOfGraphQlApiRes;
        }

        /**
         * .
         * This method retrie the Arn of AWS lambda funtion
         *
         * @param apiId
         * @return Response
         */
        public Response getArnOfAwsLambdaFunction(String apiId) {
            Response getArnOfAwsLambdaFunctionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .body(apiCreationPayloadJson)
                    .get(endPoint + publisherApisString + "/" + apiId + "/amznResourceNames");

            return getArnOfAwsLambdaFunctionRes;
        }

        /**
         * .
         * This method configures the monetization for an API
         *
         * @param apiId
         * @param dataPath
         * @return Response
         */
        public Response configureMonetizationForGivenApi(String apiId, String dataPath) throws RestAssuredMigrationException {

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + dataPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {

                throw new RestAssuredMigrationException("Error occurred while configuring monetizationL", e);
            }

            Response configureMonetizationForGivenApiRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/" + apiId + "/monetize");

            return configureMonetizationForGivenApiRes;
        }

        /**
         * .
         * <p>
         * Retrieve the monetization status of each tier for a given API
         *
         * @param apiId
         * @return Response
         */

        public Response getMonetizationStatusOfEachTierGivenApi(String apiId) {


            Response getMonetizationStatusOfEachTierGivenApiRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/monetize");

            return getMonetizationStatusOfEachTierGivenApiRes;
        }

        /**
         * .
         * <p>
         * Retrieve the revenue details of monetized API with metered Bill
         *
         * @param apiId
         * @return Response
         */

        public Response getTotalRevenueDetailsOfGivenMonetizesApiWithMeteredBill(String apiId) {


            Response getTotalRevenueDetailsOfGivenMonetizesApiWithMeteredBillRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/revenue");

            return getTotalRevenueDetailsOfGivenMonetizesApiWithMeteredBillRes;
        }

        /**
         * .
         * <p>
         * Retrieve the details of pending invoice for monetized subscriptions
         *
         * @param apiId
         * @return Response
         */

        public Response getDetailsOfPendingInvoiceForMonetizedSubscription(String apiId) {


            Response getDetailsOfPendingInvoiceForMonetizedSubscriptionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/usage");

            return getDetailsOfPendingInvoiceForMonetizedSubscriptionRes;
        }

        /**
         * .
         * <p>
         * Retrive API docs list
         *
         * @param apiId
         * @return Response
         */
        public Response getListOfDocOfApi(String apiId) {
            Response getListOfDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/documents");

            return getListOfDocOfApiResponse;
        }

        /**
         * .
         * <p>
         * Create new API doc
         *
         * @param apiId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response addNewDocToApi(String apiId, String jsonPayload, boolean isFile) throws RestAssuredMigrationException {

        	String endPoint = "/documents";

        	if(isFile) {
        		// jsonPayload = getPayloadFile(jsonPayload);
        	}
            Response addNewDocToApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(jsonPayload)
                    .post(this.endPoint + "/apis/" + apiId + "/documents");


            return addNewDocToApiResponse;
        }

        /**
         * .
         * <p>
         * Retrieve the API document
         *
         * @param apiId
         * @param documenetId
         * @return Response
         */

        public Response getDocOfApi(String apiId, String documenetId) {
            Response getDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/documents/" + documenetId);

            return getDocOfApiResponse;
        }

        /**
         * .
         * <p>
         * Update API Document
         *
         * @param apiId
         * @param documenetId
         * @return Response
         */
        public Response updateDocOfApi(String apiId, String documenetId) {
            Response updateDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/" + apiId + "/documents/" + documenetId);

            return updateDocOfApiResponse;
        }

        /**
         * .
         * <p>
         * Delete API document
         *
         * @param apiId
         * @param documenetId
         * @return Response
         */

        public Response deleteDocOfApi(String apiId, String documenetId) {
            Response deleteDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + apiId + "/documents/" + documenetId);

            return deleteDocOfApiResponse;
        }

        /**
         * .
         * <p>
         * Retrieve the content of the API document
         *
         * @param apiId
         * @param documenetId
         * @return Response
         */

        public Response getContentOfDocOfApi(String apiId, String documenetId) {
            Response getContentOfDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(this.endPoint + "/apis/" + apiId + "/documents/" + documenetId + "/content");

            return getContentOfDocOfApiResponse;
        }
        
        public Response addContentOfDocOfApi(String apiId, String documenetId, String content) {
            Response getContentOfDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType("multipart/form-data")
                    .multiPart("inlineContent", content)
                    .post(this.endPoint + "/apis/" + apiId + "/documents/" + documenetId + "/content");

            return getContentOfDocOfApiResponse;
        }

        /**
         * .
         * <p>
         * Update the content of API document
         *
         * @param apiId
         * @param documenetId
         * @param dataPath
         * @return Response
         */

        public Response uploadContentOfDocOfApi(String apiId, String documenetId, String filePath) {
            Response uploadContentOfDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart(new File(filePath))
                    .post(this.endPoint + "/apis/" + apiId + "/documents/" + documenetId + "/content");

            return uploadContentOfDocOfApiResponse;
        }

        /**
         * .
         * <p>
         * Check whether document exist when searching by name
         *
         * @param apiId
         * @param documenetId
         * @param docName
         * @return Response
         */

        public Response checkDocExistsByName(String apiId, String documenetId, String docName) {
            Response checkDocExistsByNameResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/" + apiId + "/documents/" + documenetId + "/validate?name=" + docName);

            return checkDocExistsByNameResponse;
        }

        /**
         * .
         * <p>
         * Retrieve all mediation policies of API
         *
         * @param apiId
         * @return Response
         */
        public Response getAllMediationPoliciesOfAPI(String apiId) {
            Response getAllMediationPoliciesOfAPIRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/mediation-policies");

            return getAllMediationPoliciesOfAPIRes;
        }
        
        public Response getAllMediationPolicies() {
            Response getAllMediationPoliciesOfAPIRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(this.endPoint + "/mediation-policies");

            return getAllMediationPoliciesOfAPIRes;
        }

        /**
         * .
         * <p>
         * Add mediation policy to the API
         *
         * @param apiId
         * @return
         */

        public Response addApiSpecificMediationPolicy(String apiId, String filePath) {
            Response addApiSpecificMediationPolicyRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType("multipart/form-data")
                    .multiPart("mediationPolicyFile", new File(filePath))
                    .multiPart("type", "in")
                    .post(this.endPoint + "/apis/" + apiId + "/mediation-policies");

            return addApiSpecificMediationPolicyRes;
        }

        /**
         * .
         * <p>
         * Retrieve a specific mediation policy
         *
         * @param apiId
         * @param policyId
         * @return Response
         */
        public Response getApiSpecificMediationPolicy(String apiId, String policyId) {
            Response getApiSpecificMediationPolicyRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/mediation-policies/" + policyId);

            return getApiSpecificMediationPolicyRes;
        }

        /**
         * .
         * <p>
         * Delete a specific mediation policy
         *
         * @param apiId
         * @param policyId
         * @return Response
         */
        public Response deleteApiSpecificMediationPolicy(String apiId, String policyId) {
            Response deleteApiSpecificMediationPolicyRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + apiId + "/mediation-policies/" + policyId);

            return deleteApiSpecificMediationPolicyRes;
        }

        /**
         * .
         * <p>
         * Download specific mediation policy
         *
         * @param apiId
         * @param policyId
         * @return Response
         */

        public Response downloadApiSpecificMediationPolicy(String apiId, String policyId) {
            Response downloadApiSpecificMediationPolicyRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/mediation-policies/" + policyId + "/content");

            return downloadApiSpecificMediationPolicyRes;
        }

        /**
         * .
         * <p>
         * Update a specific mediation policy
         *
         * @param apiId
         * @param policyId
         * @param tokenExchangeXmlPath
         * @param typeTxtPath
         * @returnn Response
         */

        public Response updateApiSpecificMediationPolicy(String apiId, String policyId, String tokenExchangeXmlPath, String typeTxtPath) {
            Response downloadApiSpecificMediationPolicyRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart("file", new File(resourceParenPath + tokenExchangeXmlPath))
                    .multiPart("type", new File(resourceParenPath + typeTxtPath))
                    .put(endPoint + publisherApisString + "/" + apiId + "/mediation-policies/" + policyId + "/content");

            return downloadApiSpecificMediationPolicyRes;
        }

        /**
         * .
         * <p>
         * Retrieve the complexity related details of API
         *
         * @param apiId
         * @return Response
         */
        public Response getComplexityRelatedDetailsOfApi(String apiId) {
            Response getComplexityRelatedDetailsOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/graphql-policies/complexity");

            return getComplexityRelatedDetailsOfApiResponse;
        }

        /**
         * .
         * <p>
         * Update the complexity related details of API
         *
         * @param apiId
         * @param jsonPayloadPath
         * @return
         */

        public Response updateComplexityRelatedDetailsOfApi(String apiId, String jsonPayloadPath) throws RestAssuredMigrationException {

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while updating the complexity related details", e);
            }

            Response updateComplexityRelatedDetailsOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .put(endPoint + publisherApisString + "/" + apiId + "/graphql-policies/complexity");

            return updateComplexityRelatedDetailsOfApiResponse;
        }

        /**
         * .
         * <p>
         * Retrieve the types and fields of graphQL schema
         *
         * @param apiId
         * @return Response
         */

        public Response getTypesAndFieldsOfGraphQlSchema(String apiId) {
            Response getTypesAndFieldsOfGraphQlSchemaResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/graphql-policies/complexity/types");

            return getTypesAndFieldsOfGraphQlSchemaResponse;
        }

        /**
         * .
         * <p>
         * Retrieve the security audit report
         *
         * @param apiId
         * @return Response
         */

        public Response getSecurityAuditReportOfAuditApi(String apiId) {
            Response getSecurityAuditReportOfAuditApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/auditapi");

            return getSecurityAuditReportOfAuditApiResponse;
        }

        /**
         * .
         * <p>
         * Retrieve the list of external stores
         *
         * @param apiId
         * @return Response
         */

        public Response getListOfExternalStoresWhichApiPublished(String apiId) {
            Response getListOfExternalStoresWhichApiPublishedResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/external-stores");

            return getListOfExternalStoresWhichApiPublishedResponse;
        }

        /**
         * .
         * <p>
         * Publish the API to external store
         *
         * @param apiId
         * @param storeName
         * @return Response
         */

        public Response publishApiToExternalStore(String apiId, String storeName) {
            Response publishApiToExternalStoreResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/publish-to-external-stores?externalStoreId=" + storeName);

            return publishApiToExternalStoreResponse;
        }

        /**
         * .
         * <p>
         * Get the list of external stores
         *
         * @param apiId
         * @return Response
         */

        public Response getExternalStoresListToPublishApi(String apiId) {
            Response getExternalStoresListToPublishApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/" + apiId + "/external-stores");

            return getExternalStoresListToPublishApiResponse;
        }

        /**
         * .
         * <p>
         * Export an API
         *
         * @param apiId
         * @param apiName
         * @param version
         * @param provider
         * @param format
         * @return
         */
        public Response exportAnApi(String apiId, String apiName, String version, String provider, String format) {
            Response exportAnApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/export?apiId=" + apiId + "&name=" + apiName + "&version=" + version + "&provider=" + provider + "&format=" + format);

            return exportAnApiResponse;
        }

        /**
         * .
         * <p>
         * Search for the existing client certification
         *
         * @param apiId
         * @return Response
         */
        public Response searchUploadedClientCertificate(String apiId) {
            Response searchUploadedClientCertificateResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/client-certificates?alias=wso2carbon");

            return searchUploadedClientCertificateResponse;
        }

        /**
         * .
         * <p>
         * Upload new client certificate
         *
         * @param apiId
         * @param certificate
         * @param alias
         * @param wso2Carbon
         * @param tier
         * @return Response
         */

        public Response uploadNewCertificate(String apiId, String certificate, String alias, String wso2Carbon, String tier) {
            Response uploadNewCertificateRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart("certificate", new File(resourceParenPath + certificate))
                    .multiPart("alias", new File(resourceParenPath + alias))
                    .multiPart("apiId", new File(resourceParenPath + apiId))
                    .multiPart("tier", new File(resourceParenPath + tier))
                    .post(endPoint + publisherApisString + "/" + apiId + "/client-certificates");


            return uploadNewCertificateRes;
        }

        /**
         * .
         * <p>
         * Update client certificate
         *
         * @param apiId
         * @param certificate
         * @param alias
         * @param tier
         * @return Response
         */

        public Response updateCertificate(String apiId, String certificate, String alias, String tier) {
            Response updateCertificateRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart("certificate", new File(resourceParenPath + certificate))
                    .multiPart("alias", new File(resourceParenPath + alias))
                    .multiPart("apiId", new File(resourceParenPath + apiId))
                    .multiPart("tier", new File(resourceParenPath + tier))
                    .put(endPoint + publisherApisString + "/" + apiId + "/client-certificates/" + alias);

            return updateCertificateRes;
        }

        /**
         * .
         * <p>
         * Delete client certificate
         *
         * @param apiId
         * @return Response
         */

        public Response deleteCertificate(String apiId) {
            Response deleteCertificateRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + apiId + "/client-certificates/wso2carbon");

            return deleteCertificateRes;
        }

        /**
         * .
         * <p>
         * Retrieve certificate information
         *
         * @param apiId
         * @return Response
         */

        public Response getCertficateInformation(String apiId) {
            Response getCertficateInformationRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + apiId + "/client-certificates/wso2carbon");

            return getCertficateInformationRes;
        }

        /**
         * .
         * <p>
         * Retrieve the deployment status of an API
         *
         * @param apiId
         * @return
         */
        public Response getDeploymentStatus(String apiId) {
            Response getDeploymentStatusRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/deployments");

            return getDeploymentStatusRes;
        }

        /**
         * .
         * <p>
         * Search for the API documentation by content
         *
         * @param query
         * @return Response
         */

        public Response searchApiAndDocumentationByContent(String query) {
            Response searchApiAndDocumentationByContentRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + "/search?query=" + query);

            return searchApiAndDocumentationByContentRes;
        }

        /**
         * .
         * <p>
         * Check the exisiting user roles
         *
         * @param roleId
         * @return Response
         */

        public Response checkRoleAlreadyExists(String roleId) {
            Response checkRoleAlreadyExistsRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .head(endPoint + "/roles/" + roleId);

            return checkRoleAlreadyExistsRes;
        }

        /**
         * .
         * <p>
         * Validate a given user has the assigned role
         *
         * @param roleId
         * @return Response
         */

        public Response validateGivenUserHasGivenRole(String roleId) {
            Response validateGivenUserHasGivenRoleRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + "/me/roles/" + roleId);

            return validateGivenUserHasGivenRoleRes;
        }

    }

    /**
     * .
     * <p>
     * Alerts Class to implement notification functionalities
     */

    public static class Alerts {

        String endPoint;
        String accessToken;
        ApimVersions version;

        String publisherAlertsString = "/settings";

        byte[] payloadJson1;
        String payloadString1;

        String resourceParentPath = "./src/test/payloads/";

        public Alerts(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving properties ", e);
            }
        }

        /**
         * .
         * <p>
         * Retrieve the alert types in publisher
         *
         * @return Response
         */

        public Response getPublisherAlertTypes() {
            Response getPublisherAlertTypesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + "/alert-types");

            return getPublisherAlertTypesResponse;
        }

        /**
         * .
         * <p>
         * Retrieve the subscribed alert types
         *
         * @return Response
         */

        public Response getPublisherAlertTypesSubscribed() {
            Response getPublisherAlertTypesSubscribedResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + "/alert-subscriptions");

            return getPublisherAlertTypesSubscribedResponse;
        }

        /**
         * .
         * Subscribe a selected alert type
         *
         * @param jsonPayloadPath
         * @return Response
         */

        public Response subscribeToSelectedAlertType(String jsonPayloadPath) throws RestAssuredMigrationException {

            try {
                payloadJson1 = Files.readAllBytes(Paths.get(resourceParentPath + jsonPayloadPath));
                payloadString1 = new String(payloadJson1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while subscribing to an alert type ", e);

            }

            Response subscribeToSelectedAlertTypeResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadString1)
                    .put(endPoint + "/alert-subscriptions");

            return subscribeToSelectedAlertTypeResponse;
        }

        /**
         * .
         * Unsubscribe from alert types
         *
         * @return Response
         */

        public Response unsubscribeUserAllAlertTypes() {
            Response unsubscribeUserAllAlertTypesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + "/alert-subscriptions");

            return unsubscribeUserAllAlertTypesResponse;
        }

        /**
         * .
         * Retrieve abnormal requests
         *
         * @param alertType
         * @return Response
         */

        public Response getAbnormalRequests(String alertType) {
            Response getAbnormalRequestsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + "/alerts/" + alertType + "/configurations");

            return getAbnormalRequestsResponse;
        }

        /**
         * .
         * <p>
         * Add an abnormal request
         *
         * @param alertType
         * @param configurationId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response addAbnormalRequests(String alertType, String configurationId, String jsonPayloadPath) throws RestAssuredMigrationException {

            try {
                payloadJson1 = Files.readAllBytes(Paths.get(resourceParentPath + jsonPayloadPath));
                payloadString1 = new String(payloadJson1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while adding an abnormal request", e);
            }

            Response addAbnormalRequestsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadString1)
                    .put(endPoint + "/alerts/" + alertType + "/configurations/" + configurationId);

            return addAbnormalRequestsResponse;
        }

        /**
         * .
         * Delete an abnormal request
         *
         * @param alertType
         * @param configurationId
         * @return Response
         */

        public Response deleteAbnormalRequests(String alertType, String configurationId) {

            Response deleteAbnormalRequestsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + "/alerts/" + alertType + "/configurations/" + configurationId);

            return deleteAbnormalRequestsResponse;
        }

    }

    /**
     * .
     * <p>
     * APIProducts class implements the functionalities related the API products
     */

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

        String resourceParentPath = "./src/test/payloads/";

        public ApiProducts(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL ", e);
            }
        }

        /**
         * .
         * <p>
         * Search API Product
         *
         * @return Response
         */

        public Response searchApiProduct() {
            Response searchApiProductResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisProductString);

            return searchApiProductResponse;
        }

        /**
         * .
         * <p>
         * Create API Product
         *
         * @param contentType
         * @param jsonPayloadPath
         * @return
         */

        public Response createApiProduct(String contentType, String jsonPayloadPath) throws RestAssuredMigrationException {

            try {
                createApiProductPayloadJson = Files.readAllBytes(Paths.get(resourceParentPath + jsonPayloadPath));
                createApiProductPayloadString = new String(createApiProductPayloadJson);

                createApiProductResponse = RestAssured.given()
                        .relaxedHTTPSValidation()
                        .auth()
                        .oauth2(accessToken)
                        .body(createApiProductPayloadString)
                        .contentType(contentType)
                        .post(endPoint + publisherApisProductString);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while creating an api product", e);
            }

            return createApiProductResponse;

        }

        /**
         * .
         * Delete API Product
         *
         * @param apiId
         * @return Response
         */

        public Response deleteApiProduct(String apiId) {
            Response deleteApiProductResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .delete(endPoint + publisherApisProductString + "/" + apiId);

            return deleteApiProductResponse;
        }

        /**
         * .
         * <p>
         * Retrieve details of API product
         *
         * @param apiProductId
         * @return Response
         */

        public Response getDetailsOfApiProduct(String apiProductId) {
            Response getDetailsOfApiProductResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType("application/json")
                    .get(endPoint + publisherApisProductString + "/" + apiProductId);

            return getDetailsOfApiProductResponse;
        }

        /**
         * .
         * <p>
         * Retrieve the swagger definition of API product
         *
         * @param apiProductId
         * @return Response
         */

        public Response getSwaggerDefinition(String apiProductId) {
            Response getSwaggerDefinitionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisProductString + "/" + apiProductId + "/swagger");

            return getSwaggerDefinitionResponse;
        }

        /**
         * .
         * <p>
         * Update API product
         *
         * @param apiProductId
         * @param contentType
         * @param jsonPayloadPath
         * @return Response
         */

        public Response updateApiProduct(String apiProductId, String contentType, String jsonPayloadPath) throws RestAssuredMigrationException {

            try {
                apiProductUpdatePayloadJson = Files.readAllBytes(Paths.get(resourceParentPath + jsonPayloadPath));
                apiProductUpdatePayloadString = new String(apiProductUpdatePayloadJson);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while updating an api product", e);
            }

            Response updateApiProductResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(contentType)
                    .body(apiProductUpdatePayloadString)
                    .put(endPoint + publisherApisProductString + "/" + apiProductId);

            return updateApiProductResponse;

        }

        /**
         * .
         * <p>
         * Retrieve product thumbnail image
         *
         * @param apiProductId
         * @return Response
         */

        public Response getProductThumbnail(String apiProductId) {

            Response getProductThumbnailResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisProductString + "/" + apiProductId + "/thumbnail");

            return getProductThumbnailResponse;
        }

        /**
         * .
         * Upload product thumbnail image
         *
         * @param imagePath
         * @param apiProductId
         * @return Response
         */

        public Response uploadProductThumbnail(String imagePath, String apiProductId) {
            Response uploadProductThumbnailResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart(new File(resourceParentPath + imagePath))
                    .put(endPoint + publisherApisProductString + "/" + apiProductId + "/thumbnail");

            return uploadProductThumbnailResponse;
        }

        /**
         * .
         * <p>
         * Validate whether the api product is outdated
         *
         * @param apiProductId
         * @return Response
         */

        public Response isApiProductOutdated(String apiProductId) {
            Response isApiProductOutdatedResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisProductString + "/" + apiProductId + "/is-outdated");

            return isApiProductOutdatedResponse;
        }

        /**
         * .
         * Retrieve api product document
         *
         * @param apiProductId
         * @return Response
         */


        public Response getDocumentsOfApiProduct(String apiProductId) {

            Response getDocumentsOFApiProductRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisProductString + "/" + apiProductId + "/documents");

            return getDocumentsOFApiProductRes;
        }

        /**
         * .
         * <p>
         * Update API product document
         *
         * @param apiProductId
         * @param documentId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response updateDocumentsOfApiProduct(String apiProductId, String documentId, String jsonPayloadPath) throws RestAssuredMigrationException {

            try {
                payloadJson1 = Files.readAllBytes(Paths.get(resourceParentPath + jsonPayloadPath));
                payloadString1 = new String(payloadJson1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while updating api product document", e);
            }

            Response updateDocumentsOFApiProductRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadString1)
                    .put(endPoint + publisherApisProductString + "/" + apiProductId + "/documents/" + documentId);

            return updateDocumentsOFApiProductRes;
        }

        /**
         * .
         * <p>
         * Delete API prodcut document
         *
         * @param apiProductId
         * @param documentId
         * @return Response
         */

        public Response deleteDocumentsOfApiProduct(String apiProductId, String documentId) {

            Response deleteDocumentsOFApiProductRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisProductString + "/" + apiProductId + "/documents/" + documentId);

            return deleteDocumentsOFApiProductRes;
        }

        /**
         * .
         * Retrieve the  content of api product document
         *
         * @param apiProductId
         * @param documentId
         * @return Response
         */

        public Response getContentOfDocumentsOfApiProduct(String apiProductId, String documentId) {

            Response getContentOfApiProductDocumentRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisProductString + "/" + apiProductId + "/documents/" + documentId + "/content");

            return getContentOfApiProductDocumentRes;
        }

        /**
         * .
         * <p>
         * Upload the content of API product document
         *
         * @param apiProductId
         * @param documentId
         * @param imagePath
         * @return Response
         */

        public Response uploadContentOfDocumentsOfApiProduct(String apiProductId, String documentId, String imagePath) {

            Response uploadContentOfDocumentsOfApiProductRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.MULTIPART_FORMDATA)
                    .multiPart(new File(resourceParentPath + imagePath))
                    .post(endPoint + publisherApisProductString + "/" + apiProductId + "/documents/" + documentId + "/content");

            return uploadContentOfDocumentsOfApiProductRes;
        }

    }

    /**
     * .
     * <p>
     * Deployment class to implement API deployment related functionalities
     */

    public static class Deployments {

        String endPoint;
        String accessToken;
        ApimVersions version;

        String publisherDeploymentString = "/deployments";

        byte[] payloadJson1;
        String payloadString1;

        String resourceParentPath = "./src/test/payloads/";

        public Deployments(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL ", e);
            }

        }

        /**
         * .
         * <p>
         * Retrieve deployment environment details
         *
         * @return Response
         */

        public Response getDeploymentEnvironmentDetails() {

            Response retrieveDeploymentEnvironmentDetailsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherDeploymentString);

            return retrieveDeploymentEnvironmentDetailsResponse;
        }

    }

    /**
     * .
     * EndpointCertificate class to implement certificate related functionalities
     */

    public static class EndpointCertificates {

        String accessToken = "";
        String endPoint = "";
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

        public EndpointCertificates(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + "/api/am/publisher/v1";
                } else {
                    this.endPoint = properties.getProperty("base_url") + "/api/am/publisher/v3";
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL ", e);
            }

        }

        /**
         * .
         * Retrieve existing certificates
         *
         * @param alias
         * @param endpoint
         * @return Response
         */

        public Response getUploadedCertificates(String alias, String endpoint) {
            Response getUplodedCertificatesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "?alias=" + alias + "&endpoint=" + endpoint);

            return getUplodedCertificatesResponse;
        }

        /**
         * .
         * Delete certificates
         *
         * @param alias
         * @param endpoint
         * @return Response
         */


        public Response deleteCertificate(String alias, String endpoint) {
            Response deleteCertificateResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .delete(endPoint + publisherApisString + "?alias=" + alias + "&endpoint=" + endpoint);

            return deleteCertificateResponse;
        }

        /**
         * .
         * Retrieve certificate information
         *
         * @param alias
         * @param endpoint
         * @return Response
         */

        public Response getCertificateInformation(String alias, String endpoint) {
            Response getCertificateInformationResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + alias);

            return getCertificateInformationResponse;
        }
    }

    /**
     * .
     * <p>
     * Class to implement global mediation policies related functionalities
     */

    public static class GlobalMediationPolicies {

        String accessToken = "";
        ApimVersions version;
        String endPoint;

        String publisherApisString = "/mediation-policies";
        String resourceParenPath = "./src/test/payloads/";

        public GlobalMediationPolicies(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;


            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving properties ", e);
            }
        }

        /**
         * .
         * Retrieve existing global mediation policies
         *
         * @return Response
         */

        public Response getGlobalMediationPolicies() {
            Response getGlobalMediationPoliciesRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getGlobalMediationPoliciesRes;
        }

        /**
         * Download global mediation policies
         *
         * @return Response
         */

        public Response downloadGlobalMediationPolicies() {
            Response downloadGlobalMediationPoliciesRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return downloadGlobalMediationPoliciesRes;
        }

    }

    /**
     * .
     * KeyManger class implemented the functionalities related to key manager
     */
    public static class KeyManager {

        String endPoint;
        String accessToken;
        ApimVersions version;

        String publisherKeyManagerString = "/key-managers";

        byte[] payloadJson1;
        String payloadString1;

        String resourceParentPath = "./src/test/payloads/";

        public KeyManager(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving properties ", e);
            }

        }

        /**
         * .
         * Retrieve existing key mangers
         *
         * @return Response
         */

        public Response getAllKeyManagers() {

            Response getAllKeyManagersResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherKeyManagerString);

            return getAllKeyManagersResponse;
        }

    }

    /**
     * .
     * Lable Class implemented the Label related functionalities
     */
    public static class Labels {

        String endPoint;
        String accessToken;
        ApimVersions version;

        String publisherLabelString = "/labels";

        byte[] payloadJson1;
        String payloadString1;

        String resourceParentPath = "./src/test/payloads/";

        public Labels(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving properties ", e);
            }
        }

        /**
         * .
         * <p>
         * Retrieve all the registered labels
         *
         * @return Response
         */

        public Response getAllRegisteredLabels() {
            Response getAllRegisteredLabelsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherLabelString);

            return getAllRegisteredLabelsResponse;
        }

    }

    /**
     * .
     * Scopes implemented scope related functionalities
     */

    public static class Scopes {

        String endPoint;
        String accessToken;
        ApimVersions version;
        String baseURL;

        String publisherScopesString = "/scopes";

        byte[] payloadJson1;
        String payloadString1;

        String resourceParentPath = "./src/test/payloads/";

        public Scopes(String baseURL, String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;
            this.baseURL = baseURL;
            
            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = baseURL+ properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = baseURL + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving properties ", e);
            }

        }

        /**
         * .
         * <p>
         * Retrieve all shared scopes
         *
         * @return Response
         */

        public Response getAllSharedScopes() {

            Response getAllSharedScopesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherScopesString);

            return getAllSharedScopesResponse;
        }

        /**
         * .
         * Add new shared scope
         *
         * @param jsonPayloadPath
         * @return
         */

        public Response addNewSharedScopes(String jsonPayload, boolean isFile) throws RestAssuredMigrationException {

        	String endPoint = "/scopes";

        	if(isFile) {
        		// jsonPayload = getPayloadFile(jsonPayload);
        	}
            Response getAllSharedScopesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(jsonPayload)
                    .post(this.endPoint + endPoint);

            return getAllSharedScopesResponse;
        }

        /**
         * .
         * Retrieve shared scope by Id
         *
         * @param scopeId
         * @return Response
         */

        public Response getSharedScopeById(String scopeId) {

            Response getSharedScopeByIdResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherScopesString + "/" + scopeId);

            return getSharedScopeByIdResponse;
        }

        /**
         * .
         * Update shared scopes
         *
         * @param scopeId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response updateSharedScope(String scopeId, String jsonPayloadPath) throws RestAssuredMigrationException {

            try {
                payloadJson1 = Files.readAllBytes(Paths.get(resourceParentPath + jsonPayloadPath));
                payloadString1 = new String(payloadJson1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while updating shared scopes", e);
            }

            Response updateSharedScopeResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadString1)
                    .put(endPoint + publisherScopesString + "/" + scopeId);

            return updateSharedScopeResponse;

        }

        /**
         * .
         * <p>
         * Delete shared scopes
         *
         * @param scopeId
         * @return Response
         */

        public Response deleteSharedScope(String scopeId) {

            Response deleteSharedScopeResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .delete(endPoint + publisherScopesString + "/" + scopeId);

            return deleteSharedScopeResponse;
        }

        //-------------This method need to check, Bug found in the documentation--------------
        public Response checkGivenScopeAlreadyAvailable(String scopeId) {

            Response checkGivenScopeAlreadyAvailableResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .head(endPoint + publisherScopesString + "/" + scopeId);

            return checkGivenScopeAlreadyAvailableResponse;
        }

        /**
         * .
         * <p>
         * Retrieve the usage of shared scope
         *
         * @param scopeId
         * @return Response
         */

        public Response getUsageOfSharedScope(String scopeId) {

            Response getUsageOfSharedScopeResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherScopesString + "/" + scopeId + "/usage");

            return getUsageOfSharedScopeResponse;
        }


    }

    /**
     * .
     * <p>
     * Class implements API related settings functionalities
     */

    public static class Settings {

        String endPoint;
        String accessToken;
        ApimVersions version;

        String publisherSettingsString = "/settings";

        byte[] payloadJson1;
        String payloadString1;

        String resourceParentPath = "./src/test/payloads/";

        public Settings(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * <p>
         * Retrieve publisher settings
         *
         * @return
         */

        public Response getPublisherSetting() {
            Response getPublisherSettinResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherSettingsString);

            return getPublisherSettinResponse;
        }

        /**
         * .
         * <p>
         * Retrieve all the gateway environments
         *
         * @return Response
         */

        public Response getAllGatewayEnvironments() {
            Response getAllGatewayEnvironmentsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherSettingsString + "/gateway-environments");

            return getAllGatewayEnvironmentsResponse;
        }

    }

    /**
     * .
     * Subscription Class to implement subscription functionality in publisher portal
     */
    public static class Subscriptions {

        String accessToken = "";
        ApimVersions version;
        String endPoint;

        String publisherApisString = "/subscriptions";
        String resourceParenPath = "./src/test/payloads/";

        public Subscriptions(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * <p>
         * Retrieve all subscriptions
         *
         * @param apiId
         * @return Response
         */

        public Response getAllSubscriptions(String apiId) {
            Response getAllSubscriptionsRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "?apiId=" + apiId);

            return getAllSubscriptionsRes;
        }

        /**
         * .
         * <p>
         * Block subscription to an API
         *
         * @param subscriptionId
         * @param blockStatus
         * @return Response
         */

        public Response blockSubscription(String subscriptionId, String blockStatus) {
            Response blockSubscriptionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/block-subscription?subscriptionId=" + subscriptionId + "&blockState=" + blockStatus);

            return blockSubscriptionRes;

        }

        /**
         * .
         * <p>
         * Unblock subscription to a specific API
         *
         * @param subscriptionId
         * @return Response
         */

        public Response unblockSubscription(String subscriptionId) {
            Response unblockSubscriptionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/unblock-subscription?subscriptionId=" + subscriptionId);

            return unblockSubscriptionRes;

        }

        /**
         * .
         * <p>
         * Retrieve the details of the subscriber
         *
         * @param subscriptionId
         * @return Response
         */

        public Response getDetailsOfSubscriber(String subscriptionId) {
            Response getDetailsOfSubscriberRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + subscriptionId + "/subscriber-info");

            return getDetailsOfSubscriberRes;
        }


    }

    /**
     * .
     * <p>
     * Tenant class implemented  the Tenant related functionalities
     */

    public static class Tenants {

        String endPoint;
        String accessToken;
        ApimVersions version;

        String publisherTenantsString = "/tenants";

        byte[] payloadJson1;
        String payloadString1;

        String resourceParentPath = "./src/test/payloads/";

        public Tenants(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL ", e);
            }

        }

        /**
         * .
         * <p>
         * Retrieve the tenants status
         *
         * @param state
         * @return Response
         */

        public Response getTenantsByState(String state) {
            Response getTenantsByStateResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherTenantsString + "?state=" + state);

            return getTenantsByStateResponse;
        }

        /**
         * .
         * <p>
         * Check the existing tenants
         *
         * @param tenantDomain
         * @return
         */
        public Response checkTenantAlreadyExists(String tenantDomain) {
            Response checkTenantAlreadyExistsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .head(endPoint + publisherTenantsString + "/" + tenantDomain);

            return checkTenantAlreadyExistsResponse;
        }
    }

    /**
     * .
     * ThrottlingPolicies class implemented the functionalities related to throttling policies
     */

    public static class ThrottlingPolicies {

        String accessToken = "";
        ApimVersions version;
        String endPoint;

        String publisherApisString = "/throttling-policies";
        String resourceParenPath = "./src/test/payloads/";

        public ThrottlingPolicies(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.version = version;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("publisher_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * <p>
         * Retrieve the throttling policies of a given type
         *
         * @param policyLevel
         * @return
         */

        public Response getThrottlingPoliciesForGivenType(String policyLevel) {
            Response getThrottlingPoliciesForGivenTypeRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + policyLevel);

            return getThrottlingPoliciesForGivenTypeRes;
        }

        /**
         * .
         * <p>
         * Retrieve the details of policy
         *
         * @param policyLevel
         * @param policyName
         * @return
         */

        public Response getDetailsOfPolicy(String policyLevel, String policyName) {
            Response getDetailsOfPolicyRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + policyLevel.toLowerCase() + "/" + policyName);

            return getDetailsOfPolicyRes;
        }

    }


}




