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

package restapi.devportal;

import exceptions.RestAssuredMigrationException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import restapi.ApimVersions;
import restapi.ContentTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * .
 * Devportal class implemented for developer portal functionalities
 */
public class DevPortal {

    public static class Apis {

        String accessToken;
        String endPoint;

        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";

        public Apis(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when retrieving the URL", e);
            }
        }

        /**
         * .
         * Search apis
         *
         * @return Response
         */
        public Response searchApis() {
            Response searchApisResponse;
            searchApisResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString);

            return searchApisResponse;
        }

        /**
         * .
         * Retrieve API details
         *
         * @param apiId
         * @return Response
         */
        public Response getApiDetails(String apiId) {
            Response getApiDetailsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + apiId);

            return getApiDetailsResponse;
        }

        /**
         * .
         * Retrieve swagger definition
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
         * Retrieve GraphQL definition
         *
         * @param apiId
         * @return Response
         */

        public Response getGraphQLDefinition(String apiId) {
            Response getGraphQLDefinitionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + apiId + "/graphql-schema");
            return getGraphQLDefinitionResponse;
        }

        /**
         * .
         * Retrieve WSDL definition
         *
         * @param apiId
         * @return
         */
        public Response getApiWsdlDefinition(String apiId) {
            Response getApiWsdlDefinitionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + apiId + "/wsdl");
            return getApiWsdlDefinitionResponse;
        }

        /**
         * .
         * Retrieve thumbnail image
         *
         * @param apiId
         * @return
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
         * Retrieve subscription throttling policies
         *
         * @param apiId
         * @return
         */
        public Response getSubscriptionThrotlling(String apiId) {
            Response getSubscriptionThrotllingResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + publisherApisString + "/" + apiId + "/subscription-policies");

            return getSubscriptionThrotllingResponse;
        }

    }

    public static class Sdks {

        String accessToken;
        String endPoint;

        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";


        public Sdks(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        public Response generateSDKForAPI(String apiId) {
            Response generateSDKForAPIResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/sdks/java");

            return generateSDKForAPIResponse;
        }

        public Response getListOfSupportedSDKLanguages() {
            Response getListOfSupportedSDKLanguagesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + "/sdk-gen/languages");

            return getListOfSupportedSDKLanguagesResponse;
        }


    }

    /**
     * .
     * Class implemented the API documentation related functionalities
     */

    public static class ApiDocumentation {

        String accessToken;
        String endPoint;

        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";


        public ApiDocumentation(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve list of API documents
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
         * Add new document to API
         *
         * @param apiId
         * @param jsonPayloadPath
         * @return
         */

        public Response addNewDocToApi(String apiId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while adding new document to API", e);
            }

            Response addNewDocToApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/" + apiId + "/documents");

            return addNewDocToApiResponse;
        }

        /**
         * .
         * Retrieve the document of an API
         *
         * @param apiId
         * @param documentId
         * @return
         */

        public Response getDocOfApi(String apiId, String documentId) {
            Response getDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/documents/" + documentId);

            return getDocOfApiResponse;
        }

        /**
         * .
         * Update the document of API
         *
         * @param apiId
         * @param documentId
         * @return Response
         */

        public Response updateDocOfApi(String apiId, String documentId) {
            Response updateDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/" + apiId + "/documents/" + documentId);

            return updateDocOfApiResponse;
        }

        /**
         * .
         * Delete the API document
         *
         * @param apiId
         * @param documentId
         * @return Response
         */
        public Response deleteDocOfApi(String apiId, String documentId) {
            Response deleteDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + apiId + "/documents/" + documentId);

            return deleteDocOfApiResponse;
        }

        /**
         * .
         * Retrieve the content of a document of an API
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
                    .get(endPoint + publisherApisString + "/" + apiId + "/documents/" + documenetId + "/content");

            return getContentOfDocOfApiResponse;
        }

        /**
         * .
         * Upload content of document of API
         *
         * @param apiId
         * @param documentId
         * @param dataPath
         * @return Response
         */
        public Response uploadContentOfDocOfApi(String apiId, String documentId, String dataPath) {
            Response uploadContentOfDocOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .multiPart(new File(resourceParenPath + dataPath))
                    .post(endPoint + publisherApisString + "/" + apiId + "/documents/" + documentId + "/content");

            return uploadContentOfDocOfApiResponse;
        }

        /**
         * .
         * Check if the document is available
         *
         * @param apiId
         * @param documentId
         * @param docName
         * @return Response
         */
        public Response checkDocExistsByName(String apiId, String documentId, String docName) {
            Response checkDocExistsByNameResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/" + apiId + "/documents/" + documentId + "/validate?name=" + docName);

            return checkDocExistsByNameResponse;
        }

    }

    /**
     * .
     * Class implemented the API rating related functionalities
     */

    public static class Rating {

        String accessToken;
        String endPoint;

        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";

        public Rating(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve the API ratings
         *
         * @param apiId
         * @return
         */

        public Response getApiRatings(String apiId) {
            Response getApiRatingsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/ratings");

            return getApiRatingsResponse;
        }

        /**
         * .
         * Retrieve API ratings of an user
         *
         * @param apiId
         * @return Response
         */

        public Response getApiRatingOfUser(String apiId) {
            Response getApiRatingOfUserResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/user-rating");

            return getApiRatingOfUserResponse;
        }

        /**
         * .
         * Add or update logged user ratings of an API
         *
         * @param apiId
         * @param jsonPayloadPath
         * @return Response
         */
        public Response addOrUpdateLoggedUserRatingOfApi(String apiId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while adding or updating ratings of an API ", e);
            }

            Response addOrUpdateLoggedUserRatingOfApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .put(endPoint + publisherApisString + "/" + apiId + "/user-rating");

            return addOrUpdateLoggedUserRatingOfApiResponse;
        }

        /**
         * .
         * Delete user API ratings
         *
         * @param apiId
         * @return
         */

        public Response deleteUserApirating(String apiId) {
            Response deleteUserApiratingResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + apiId + "/user-rating");

            return deleteUserApiratingResponse;
        }
    }

    /**
     * .
     * This class implemented the comments related functionalities
     */

    public static class Comments {

        String accessToken;
        String endPoint;

        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";

        public Comments(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve API comments
         *
         * @param apiId
         * @return Response
         */

        public Response getApiComments(String apiId) {
            Response getApiCommentsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/comments");

            return getApiCommentsResponse;
        }

        /**
         * .
         * Add API comments
         *
         * @param apiId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response addApiComment(String apiId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while adding comments to an API", e);
            }

            Response addApiCommentResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/" + apiId + "/comments");

            return addApiCommentResponse;
        }

        /**
         * Retrieve the details of API comment
         *
         * @param apiId
         * @param commentId
         * @return Response
         */

        public Response getDetailsOfApiComment(String apiId, String commentId) {
            Response getDetailsOfApiCommentResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/comments/" + commentId);

            return getDetailsOfApiCommentResponse;
        }

        /**
         * Delete API comments
         *
         * @param apiId
         * @param commentId
         * @return Response
         */

        public Response deleteApiComment(String apiId, String commentId) {
            Response deleteApiCommentResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + apiId + "/comments/" + commentId);

            return deleteApiCommentResponse;
        }

    }

    /**
     * This class implemented the application related functionalities
     */

    public static class Applications {

        String accessToken;
        String endPoint;

        String publisherApisString = "";
        String resourceParenPath = "./src/test/payloads/";

        public Applications(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * Search applications
         *
         * @return Response
         */

        public Response searchApplications() {
            Response searchApplicationsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/applications");
            System.out.println(endPoint + publisherApisString + "/applications");

            return searchApplicationsResponse;
        }

        public Response createNewApplications(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while creating a new application", e);
            }

            Response createNewApplicationsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/applications");

            return createNewApplicationsResponse;
        }

        /**
         * .
         * Retrieve the details of applications
         *
         * @param applicationId
         * @return Response
         */

        public Response getDetailsOfApplication(String applicationId) {
            Response searchApplicationsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/applications/" + applicationId);

            return searchApplicationsResponse;
        }

        /**
         * .
         * Update application
         *
         * @param applicationId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response updateApplications(String applicationId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while updating thr application", e);
            }

            Response updateApplicationsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .put(endPoint + publisherApisString + "/applications/" + applicationId);

            return updateApplicationsResponse;
        }

        /**
         * .
         * Delete application
         *
         * @param applicationId
         * @return Response
         */

        public Response deleteApplication(String applicationId) {
            Response deleteApplicationResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/applications/" + applicationId);

            return deleteApplicationResponse;
        }
    }

    /**
     * This class implemented the application keys related functionalities
     */

    public static class ApplicationKeys {

        String accessToken;
        String endPoint;

        String publisherApisString = "/applications";
        String resourceParenPath = "./src/test/payloads/";

        public ApplicationKeys(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * Generate application keys
         *
         * @param applicationId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response generateApplicationKeys(String applicationId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while generating application keys", e);
            }

            Response generateApplicationKeysResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/" + applicationId + "/generate-keys");

            return generateApplicationKeysResponse;
        }

        /**
         * .
         * Map application keys
         *
         * @param appclicationId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response mapApplicationKeys(String appclicationId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while mapping application keys", e);
            }

            Response mapApplicationKeysResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/" + appclicationId + "/map-keys");

            return mapApplicationKeysResponse;
        }

        /**
         * .
         * Retrieve getting all application keys
         *
         * @param applicationId
         * @return Response
         */

        public Response getAllApplicationKeys(String applicationId) {
            Response getAllApplicationKeysResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + applicationId + "/oauth-keys");

            return getAllApplicationKeysResponse;
        }

        /**
         * Retrieve key details of  a given type
         *
         * @param applicationId
         * @param keyMappingId
         * @return Response
         */


        public Response getKeyDetailsOfGivenType(String applicationId, String keyMappingId) {
            Response getKeyDetailsOfGivenTypeResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + applicationId + "/oauth-keys/" + keyMappingId);

            return getKeyDetailsOfGivenTypeResponse;
        }

        /**
         * .
         * Update grant type and call back URL of an application
         *
         * @param applicationId
         * @param keyMappingId
         * @return Response
         */

        public Response updateGrantTypesAndCallbackUrlOfApplication(String applicationId, String keyMappingId) {
            Response updateGrantTypesAndCallbackUrlOfApplicationResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/" + applicationId + "/oauth-keys/" + keyMappingId);

            return updateGrantTypesAndCallbackUrlOfApplicationResponse;
        }

        /**
         * .
         * Regenerate Consumer secret
         *
         * @param applicationId
         * @param keyMappingId
         * @return Response
         */
        public Response regenerateConsumerSecret(String applicationId, String keyMappingId) {
            Response regenerateConsumerSecretResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/" + applicationId + "/oauth-keys/" + keyMappingId + "/regenerate-secret");

            return regenerateConsumerSecretResponse;
        }

        /**
         * .
         * Clean up application keys
         *
         * @param applicationId
         * @param keyMappingId
         * @return Response
         */

        public Response cleanUpApplicationKeys(String applicationId, String keyMappingId) {
            Response cleanUpApplicationKeysResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/" + applicationId + "/oauth-keys/" + keyMappingId + "/clean-up");

            return cleanUpApplicationKeysResponse;
        }
    }

    /**
     * .
     * This class implemented the functionalities related to application tokens
     */

    public static class ApplicationTokens {

        String accessToken;
        String endPoint;

        String publisherApisString = "/applications";
        String resourceParenPath = "./src/test/payloads/";

        public ApplicationTokens(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Generate application token
         *
         * @param appclicationId
         * @param keyMappingId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response generateApplicationTokens(String appclicationId, String keyMappingId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while generating application token", e);
            }

            Response generateApplicationTokensResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/" + appclicationId + "/oauth-keys/" + keyMappingId + "/generate-token");

            return generateApplicationTokensResponse;
        }
    }

    /**
     * .
     * This class implemented the functionalities related to API keys
     */

    public static class ApiKeys {

        String accessToken;
        String endPoint;

        String publisherApisString = "/applications";
        String resourceParenPath = "./src/test/payloads/";

        public ApiKeys(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Generate API keys
         *
         * @param appclicationId
         * @param keyType
         * @param jsonPayloadPath
         * @return Response
         */

        public Response generateApiKeys(String appclicationId, String keyType, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while generating API keys", e);
            }

            Response generateApiKeysResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/" + appclicationId + "/api-keys/" + keyType + "/generate");

            return generateApiKeysResponse;
        }

        /**
         * .
         * Revoke API keys
         *
         * @param appclicationId
         * @param keyType
         * @param jsonPayloadPath
         * @return Response
         */

        public Response revokeApiKeys(String appclicationId, String keyType, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while revoking API keys", e);
            }

            Response revokeApiKeysResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/" + appclicationId + "/api-keys/" + keyType + "/revoke");

            return revokeApiKeysResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to subscriptions
     */

    public static class Subscriptions {

        String accessToken;
        String endPoint;

        String publisherApisString = "/subscriptions";
        String resourceParenPath = "./src/test/payloads/";

        public Subscriptions(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve all subscriptions
         *
         * @param apiId
         * @return Response
         */

        public Response getAllSubscriptons(String apiId) {

            Response generateApiKeysResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "?apiId=" + apiId);

            return generateApiKeysResponse;
        }

        /**
         * .
         * Add new subscription
         *
         * @param jsonPayloadPath
         * @param apiId
         * @param appId
         * @return
         */

        public Response addNewSubscription(String jsonPayloadPath, String apiId, String appId) throws RestAssuredMigrationException {

            JSONObject jsonObject = new JSONObject();

            try {

                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new FileReader(resourceParenPath + jsonPayloadPath));
                jsonObject = (JSONObject) obj;
                jsonObject.put("apiId", apiId);
                jsonObject.put("applicationId", appId);
                try (FileWriter file = new FileWriter(resourceParenPath + jsonPayloadPath)) {
                    file.write(jsonObject.toJSONString());
                    file.flush();

                } catch (IOException e) {
                    throw new RestAssuredMigrationException("Error occurred while writing the file  ", e);
                }
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while adding a new subscription", e);

            }

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while reading the file content", e);
            }

            Response addNesSubscriptionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString);

            return addNesSubscriptionResponse;
        }

        /**
         * Add multiple subscription
         *
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */

        public Response addNesSubscriptions(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while adding multiple subscriptions subscription", e);
            }

            Response addNesSubscriptionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/multiple");

            return addNesSubscriptionResponse;
        }

        /**
         * .
         * Retrieve the details of a subscription
         *
         * @param subscriptionId
         * @return
         */

        public Response getDetailsOfSubscription(String subscriptionId) {

            Response addNesSubscriptionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + subscriptionId);

            return addNesSubscriptionResponse;
        }

        /**
         * Update existing subscriptions
         *
         * @param subscriptionId
         * @param jsonPayloadPath
         * @return Response
         */
        public Response updateExisitingSubscription(String subscriptionId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while updating existing subscription", e);
            }

            Response updateExisitingSubscriptionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .put(endPoint + publisherApisString + "/" + subscriptionId);

            return updateExisitingSubscriptionResponse;
        }

        /**
         * .
         * Remove subscription
         *
         * @param subscriptionId
         * @return
         */

        public Response removeSubscription(String subscriptionId) {

            Response removeSubscriptionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + subscriptionId);

            return removeSubscriptionResponse;
        }

    }

    /**
     * .
     * This class implemented API monetization related functionalities
     */

    public static class ApiMonetization {

        String accessToken;
        String endPoint;

        String publisherApisString = "/subscriptions";
        String resourceParenPath = "./src/test/payloads/";

        public ApiMonetization(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve details of a pending invoice
         *
         * @param subscriptionId
         * @return
         */
        public Response getDetailsOfPendingInvoice(String subscriptionId) {

            Response getDetailsOfPendingInvoiceResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + subscriptionId + "/usage");

            return getDetailsOfPendingInvoiceResponse;
        }

    }

    /**
     * .
     * This class implemeted the functionalities related to throttling policies
     */

    public static class ThrottlingPolicies {

        String accessToken;
        String endPoint;

        String publisherApisString = "/throttling-policies";
        String resourceParenPath = "./src/test/payloads/";

        public ThrottlingPolicies(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        public Response getAllAvailableThrottlingPolicies(String policyLevel) {

            Response getAllAvailableThrottlingPoliciesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + policyLevel);

            return getAllAvailableThrottlingPoliciesResponse;
        }

        public Response getDetailsOfThrottlingPolicies(String policyLevel, String policyId) {

            Response getDetailsOfThrottlingPoliciesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + policyLevel + "/" + policyId);

            return getDetailsOfThrottlingPoliciesResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to tags
     */
    public static class Tags {

        String accessToken;
        String endPoint;

        String publisherApisString = "/tags";
        String resourceParenPath = "./src/test/payloads/";

        public Tags(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve all tags
         *
         * @return Response
         */

        public Response getAllTags() {

            Response getAllTagsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getAllTagsResponse;
        }

    }

    /**
     * .
     * This class implemented the unified search related functionalities
     */
    public static class UnfiedSearch {

        String accessToken;
        String endPoint;

        String publisherApisString = "/search";
        String resourceParenPath = "./src/test/payloads/";

        public UnfiedSearch(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve api document by content
         *
         * @param query
         * @return Response
         */

        public Response getApiAndApiDocumentByContent(String query) {

            Response getApiAndApiDocumentByContentResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "?query=" + query);

            return getApiAndApiDocumentByContentResponse;
        }

    }

    /**
     * .
     * This class implemented the settings related functionalities
     */

    public static class Settings {

        String accessToken;
        String endPoint;

        String publisherApisString = "/settings";
        String resourceParenPath = "./src/test/payloads/";

        public Settings(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve store settings
         *
         * @return Response
         */

        public Response getStoreSetting() {

            Response getStoreSettingResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getStoreSettingResponse;
        }

        /**
         * .
         * Retrieve application attributes from configuration
         *
         * @return
         */
        public Response getAllApplicationAttributesFromConfiguration() {

            Response getAllApplicationAttributesFromConfigurationResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/application-attributes");

            return getAllApplicationAttributesFromConfigurationResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to tenants
     */

    public static class Tenants {

        String accessToken;
        String endPoint;

        String publisherApisString = "/tenants";
        String resourceParenPath = "./src/test/payloads/";

        public Tenants(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve tenants by state
         *
         * @return Response
         */

        public Response getTenantByState() {

            Response getTenantByStateResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getTenantByStateResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to recommendations
     */
    public static class Recommendations {

        String accessToken;
        String endPoint;

        String publisherApisString = "/recommendations";
        String resourceParenPath = "./src/test/payloads/";

        public Recommendations(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * API recoomendations for user
         *
         * @return
         */
        public Response giveApiRecommendationsForUser() {

            Response giveApiRecommendationsForUserResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return giveApiRecommendationsForUserResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to alerts
     */

    public static class Alerts {

        String accessToken;
        String endPoint;

        String publisherApisString = "/alert-types";
        String resourceParenPath = "./src/test/payloads/";

        public Alerts(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve list of API portal developer alert types
         *
         * @return
         */

        public Response getListOfApiDeveloperPortalAlertTypes() {

            Response getListOfApiDeveloperPortalAlertTypesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getListOfApiDeveloperPortalAlertTypesResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to alert subscriptions
     */

    public static class AlertSubscriptions {

        String accessToken;
        String endPoint;

        String publisherApisString = "/alert-subscriptions";
        String resourceParenPath = "./src/test/payloads/";

        public AlertSubscriptions(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve the list of developer portal alert types subscribed by user
         *
         * @return Response
         */

        public Response getListOfApiDeveloperPortalAlertTypesSubscribedByUser() {

            Response getListOfApiDeveloperPortalAlertTypesSubscribedByUserResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getListOfApiDeveloperPortalAlertTypesSubscribedByUserResponse;
        }

        /**
         * .
         * Subsribe to selected alert types by user
         *
         * @param jsonPayloadPath
         * @return Response
         */

        public Response subscribeToSelectedAlertTypesByUser(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while reading the file content", e);
            }

            Response subscribeToSelectedAlertTypesByUserResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .put(endPoint + publisherApisString);

            return subscribeToSelectedAlertTypesByUserResponse;
        }

        public Response unsubscribeUserFromAllAlertTypes() {

            Response unsubscribeUserFromAllAlertTypesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString);

            return unsubscribeUserFromAllAlertTypesResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to api configurations
     */

    public static class ApiConfigurations {

        String accessToken;
        String endPoint;

        String publisherApisString = "/alerts";
        String resourceParenPath = "./src/test/payloads/";

        public ApiConfigurations(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve the abnormal requests per minute alert configurations
         *
         * @param alertType
         * @return Response
         */

        public Response getAllAbnormalRequestsPerMinAlertConfigurations(String alertType) {

            Response getAllAbnormalRequestsPerMinAlertConfigurationsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + alertType + "/configurations");

            return getAllAbnormalRequestsPerMinAlertConfigurationsResponse;
        }

        /**
         * .
         * Add abnormal requests per minute alert configurations
         *
         * @param alertType
         * @param configurationId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response addAbnormalRequestsPerMinAlertConfigurations(String alertType, String configurationId, String jsonPayloadPath) {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
            }

            Response addAbnormalRequestsPerMinAlertConfigurationsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .put(endPoint + publisherApisString + "/" + alertType + "/configurations/" + configurationId);

            return addAbnormalRequestsPerMinAlertConfigurationsResponse;
        }

        /**
         * Delete the selected configurations from abnormal requests per minute type
         *
         * @param alertType
         * @param configurationId
         * @return
         */
        public Response deleteSelectedConfigurationFromAbnormalRequestsPerMinAlertType(String alertType, String configurationId) {

            Response deleteSelectedConfigurationFromAbnormalRequestsPerMinAlertTypeResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + alertType + "/configurations/" + configurationId);

            return deleteSelectedConfigurationFromAbnormalRequestsPerMinAlertTypeResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to API categories
     */
    public static class ApiCategoryCollections {

        String accessToken;
        String endPoint;

        String publisherApisString = "/api-categories";
        String resourceParenPath = "./src/test/payloads/";

        public ApiCategoryCollections(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }
        }

        /**
         * .
         * Retrieve all API categories
         *
         * @return Response
         */
        public Response getAllApiCategories() {

            Response getAllApiCategoriesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getAllApiCategoriesResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to key manager
     */

    public static class KeyManagerCollections {

        String accessToken;
        String endPoint;

        String publisherApisString = "/key-managers";
        String resourceParenPath = "./src/test/payloads/";

        public KeyManagerCollections(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }
        }

        /**
         * .
         * Retrieve all key managers
         *
         * @return
         */
        public Response getAllKeyManagers() {

            Response getAllKeyManagersResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getAllKeyManagersResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to GraphQL policies
     */
    public static class GraphQlPolicies {

        String accessToken;
        String endPoint;

        String publisherApisString = "/apis";
        String resourceParenPath = "./src/test/payloads/";

        public GraphQlPolicies(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }

        }

        /**
         * .
         * Retrieve complexity related details of API
         *
         * @param apiId
         * @return Response
         */

        public Response getComplexityRelatedDetailsOfAPI(String apiId) {

            Response getComplexityRelatedDetailsOfAPIResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + apiId + "/graphql-policies/complexity");

            return getComplexityRelatedDetailsOfAPIResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to Users
     */

    public static class Users {

        String accessToken;
        String endPoint;

        String publisherApisString = "/me";
        String resourceParenPath = "./src/test/payloads/";

        public Users(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("devportal_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);

            }
        }

        /**
         * .
         * Change password of user
         *
         * @param jsonPayloadPath
         * @return Response
         */
        public Response changePasswordOfUser(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);
            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when changing the password ", e);
            }

            Response changePasswordOfUserResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/change-password");

            return changePasswordOfUserResponse;
        }

    }


}
