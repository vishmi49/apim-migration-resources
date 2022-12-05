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

package restapi.gateway;

import exceptions.RestAssuredMigrationException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.ContentTypes;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * .
 * Gateway class implemented functionalities related to gateway
 */

public class Gateway {
    public static class ReDeploy {

        String accessToken;
        String endPoint;

        String publisherApisString = "/redeploy-api";
        String resourceParenPath = "./src/test/payloads/";

        @SuppressWarnings({"checkstyle:WhitespaceAround", "checkstyle:RightCurly"})
        public ReDeploy(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving url", e);
            }

        }

        public Response reDeployAPIInGateway(String apiName, String version, String tenantDomain) {
            Response reDeployAPIInGatewayRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "?apiName=" + apiName + "&version=" + version + "&tenantDomain=" + tenantDomain);

            return reDeployAPIInGatewayRes;
        }

    }

    /**
     * .
     * Class implemented to verify undeploy functionality
     */
    public static class Undeploy {

        String accessToken;
        String endPoint;

        String publisherApisString = "/undeploy-api";
        String resourceParenPath = "./src/test/payloads/";

        public Undeploy(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving url", e);
            }

        }

        /**
         * .
         * Undeploy API from gateway
         *
         * @param apiName
         * @param version
         * @param tenantDomain
         * @return
         */

        public Response undeployAPIFromGateway(String apiName, String version, String tenantDomain) {
            Response undeployAPIFromGatewayRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "?apiName=" + apiName + "&version=" + version + "&tenantDomain=" + tenantDomain);

            return undeployAPIFromGatewayRes;
        }

    }

    /**
     * .
     * Retrieve API artifacts
     */

    public static class GetApiArtifact {

        String accessToken;
        String endPoint;

        String resourceParenPath = "./src/test/payloads/";

        public GetApiArtifact(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving url", e);
            }

        }

        /**
         * .
         * Retrieve synapse definition artifact from storage
         *
         * @param apiName
         * @param version
         * @param tenantDomain
         * @return Response
         */
        public Response getAPISynapseDefinitionArtifactFromStorage(String apiName, String version, String tenantDomain) {
            Response getAPISynapseDefinitionArtifactFromStorageRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + "/api-artifact?" + "?apiName=" + apiName + "&version=" + version + "&tenantDomain=" + tenantDomain);

            return getAPISynapseDefinitionArtifactFromStorageRes;
        }

        /**
         * .
         * Retrieve local entry from storage
         *
         * @param apiName
         * @param version
         * @param tenantDomain
         * @return Response
         */

        public Response getLocalEntryFromStorage(String apiName, String version, String tenantDomain) {
            Response getLocalEntryFromStorageRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + "/local-entry" + "?apiName=" + apiName + "&version=" + version + "&tenantDomain=" + tenantDomain);

            return getLocalEntryFromStorageRes;
        }

        /**
         * .
         * Retrieve sequences from storage
         *
         * @param apiName
         * @param version
         * @param tenantDomain
         * @return Response
         */
        public Response getSequencesFromStorage(String apiName, String version, String tenantDomain) {
            Response getSequencesFromStorageRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + "/sequence" + "?apiName=" + apiName + "&version=" + version + "&tenantDomain=" + tenantDomain);

            return getSequencesFromStorageRes;
        }

        /**
         * .
         * Retrieve endpoints
         *
         * @param apiName
         * @param version
         * @param tenantDomain
         * @return Response
         */

        public Response getEndPointsFromStorageForAPI(String apiName, String version, String tenantDomain) {
            Response getEndPointsFromStorageForAPIRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + "/end-points" + "?apiName=" + apiName + "&version=" + version + "&tenantDomain=" + tenantDomain);

            return getEndPointsFromStorageForAPIRes;
        }

    }

    /**
     * .
     * Class implemented to retrieve API information
     */
    public static class GetApiInfo {

        String accessToken;
        String endPoint;

        String publisherApisString = "apis";
        String resourceParenPath = "./src/test/payloads/";

        public GetApiInfo(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving url", e);
            }

        }

        /**
         * .
         * Retrieve list of APIs by providing context and version
         *
         * @param context
         * @param version
         * @param tenantDomain
         * @return Response
         */

        public Response getListAPISByProvidingContextAndVersion(String context, String version, String tenantDomain) {
            Response getListAPISByProvidingContextAndVersionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + "/apis?" + "?context=" + context + "&version=" + version + "&tenantDomain=" + tenantDomain);

            return getListAPISByProvidingContextAndVersionRes;
        }

        /**
         * .
         * Retrieve subscription information of API by providing API UUID
         *
         * @param apiId
         * @param tenantDomain
         * @return Response
         */
        public Response getSubscriptionInformationOfApiByProvidingApiUuid(String apiId, String tenantDomain) {
            Response getSubscriptionInformationOfApiByProvidingApiUuidRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + "/apis/" + apiId + "?tenantDomain=" + tenantDomain);

            return getSubscriptionInformationOfApiByProvidingApiUuidRes;
        }

    }

    /**
     * .
     * Retrieve API application Information
     */
    public static class GetApplicationInfo {

        String accessToken;
        String endPoint;

        String publisherApisString = "applications";
        String resourceParenPath = "./src/test/payloads/";

        public GetApplicationInfo(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving url", e);
            }

        }

        /**
         * .
         * Retrieve list of APIs by providing context and version
         *
         * @param applicationName
         * @param version
         * @param tenantDomain
         * @return Response
         */

        public Response getListAPISByProvidingContextAndVersion(String applicationName, String version, String tenantDomain) {
            Response getListAPISByProvidingContextAndVersionRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "?applicationName=" + applicationName + "&version=" + version + "&tenantDomain=" + tenantDomain);

            return getListAPISByProvidingContextAndVersionRes;
        }

    }

    /**
     * .
     * Retrieve subscription information
     */
    public static class GetSubscriptionInfo {

        String accessToken;
        String endPoint;

        String publisherApisString = "subscriptions";
        String resourceParenPath = "./src/test/payloads/";

        public GetSubscriptionInfo(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("gateway_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving url", e);
            }

        }

        /**
         * .
         * Retrieve the subscription meta information
         *
         * @param appUUID
         * @param apiUUID
         * @param version
         * @param tenantDomain
         * @return Response
         */

        public Response getSubscriptionsMetaInformation(String appUUID, String apiUUID, String version, String tenantDomain) {
            Response getSubscriptionsMetaInformationRes = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "?apiUUID=" + appUUID + "&apiUUID=" + apiUUID + "&tenantDomain=" + tenantDomain);

            return getSubscriptionsMetaInformationRes;
        }

    }

}
