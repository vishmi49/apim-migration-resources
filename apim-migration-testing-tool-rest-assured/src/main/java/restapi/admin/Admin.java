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

package restapi.admin;

import exceptions.RestAssuredMigrationException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.ContentTypes;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * .
 * <p>
 * The Admin class for admin portal functionality implementation
 */
public class Admin {

    public static class ApplicationPolicyCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/throttling";
        String resourceParenPath = "./src/test/payloads/";

        public ApplicationPolicyCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.endPoint = endPoint;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving url", e);
            }
        }

        /**
         * Retrieve application throttling policies
         *
         * @return Response
         */

        public Response getAllApplicationThrottlingPolicies() {
            Response getAllApplicationThrottlingPoliciesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/application");

            return getAllApplicationThrottlingPoliciesResponse;
        }

        /**
         * .
         * This method will add the Application Throttling Policies when testing in Admin portal
         *
         * @param jsonPayloadPath
         * @return Response
         */
        public Response addApplicationThrottlingPolicies(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }
            Response addApplicationThrottlingPoliciesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .body(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/policies/application");

            return addApplicationThrottlingPoliciesResponse;
        }

    }

    /**
     * .
     * <p>
     * Class implements application policy related functionalities
     */

    public static class ApplicationPolicyIndividual {
        String accessToken;
        String endPoint;

        String publisherApisString = "/throttling";
        String resourceParenPath = "./src/test/payloads/";

        public ApplicationPolicyIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.endPoint = endPoint;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve  Application throttling policy
         *
         * @param policyId
         * @return Response
         */

        public Response getApplicationThrottlingPolicy(String policyId) {
            Response getAllApplicationThrottlingPoliciesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/application/" + policyId);

            return getAllApplicationThrottlingPoliciesResponse;
        }

        /**
         * .
         * Delete application throttling policy
         *
         * @param policyId
         * @return Response
         */

        public Response deleteApplicationThrottlingPolicy(String policyId) {
            Response deleteAllApplicationThrottlingPoliciesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/policies/application/" + policyId);

            return deleteAllApplicationThrottlingPoliciesResponse;
        }

        /**
         * .
         * Update application throttling policies
         *
         * @param policyId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response updateApplicationThrottlingPolicy(String policyId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when updating application throttling policies", e);
            }

            Response updateAllApplicationThrottlingPoliciesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .body(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/policies/application/" + policyId);

            return updateAllApplicationThrottlingPoliciesResponse;
        }

    }

    /**
     * Class implements functionalities related to mediation policies.
     */

    public static class MediationPolicyCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public MediationPolicyCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.endPoint = endPoint;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve all global mediation policies
         *
         * @return Response
         */

        public Response getAllGlobalMediationPolicies() {

            Response getAllGlobalMediationPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/mediation");

            return getAllGlobalMediationPolicyResponse;
        }

        /**
         * .
         * Add global mediation policy
         *
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */

        public Response addGlobalMediationPolicy(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when adding global mediation policy ", e);
            }

            Response addGlobalMediationPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .body(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/mediation");

            return addGlobalMediationPolicyResponse;
        }

    }

    /**
     * Class implements the functionalities related to mediation policies.
     */

    public static class MediationPolicyIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public MediationPolicyIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve global mediation policies
         *
         * @param mediationPolicyId
         * @return
         */

        public Response getGlobalMediationPolicy(String mediationPolicyId) {

            Response getAllGlobalMediationPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/mediation/" + mediationPolicyId);

            return getAllGlobalMediationPolicyResponse;
        }

        /**
         * .
         * Delete global mediation policies
         *
         * @param mediationPolicyId
         * @return
         */

        public Response deleteGlobalMediationPolicy(String mediationPolicyId) {

            Response deleteAllGlobalMediationPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/mediation/" + mediationPolicyId);

            return deleteAllGlobalMediationPolicyResponse;
        }

        /**
         * .
         * Update global mediation policies
         *
         * @param mediationPolicyId
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */

        public Response updateGlobalMediationPolicy(String mediationPolicyId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while updating medation policies ", e);
            }

            Response updateGlobalMediationPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/mediation/" + mediationPolicyId);

            return updateGlobalMediationPolicyResponse;
        }

    }

    /**
     * .
     * This class implements subscription policy related functionalities.
     */

    public static class SubscriptionPolicyCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public SubscriptionPolicyCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;
            this.endPoint = endPoint;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve all subscription throttling policies
         *
         * @return
         */

        public Response getAllSubscriptionThrottlingPolicies() {

            Response getAllSubscriptionThrottlingPoliciesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/subscription");

            return getAllSubscriptionThrottlingPoliciesResponse;
        }

        /**
         * .
         * Add subscription throttling policy
         *
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */

        public Response addSubscriptionThrottlingPolicy(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when adding subscribing throttling policies", e);
            }

            Response addSubscriptionThrottlingPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/policies/subscription");

            return addSubscriptionThrottlingPolicyResponse;
        }

    }

    /**
     * .
     * This class implements API specific subscription policy related functionalities.
     */

    public static class SubscriptionPolicyIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public SubscriptionPolicyIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve subscription policy
         *
         * @param policyId
         * @return
         */

        public Response getSubscriptionPolicy(String policyId) {

            Response getSubscriptionPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/subscription/" + policyId);

            return getSubscriptionPolicyResponse;
        }

        /**
         * .
         * Delete subscription policy
         *
         * @param policyId
         * @return
         */

        public Response deleteSubscriptionPolicy(String policyId) {

            Response deleteSubscriptionPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/subscription/" + policyId);

            return deleteSubscriptionPolicyResponse;
        }

        /**
         * .
         * Update subscription policy
         *
         * @param policyId
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */

        public Response updateSubscriptionPolicy(String policyId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when updating subscription policies", e);
            }

            Response updateSubscriptionPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/policies/subscription/" + policyId);

            return updateSubscriptionPolicyResponse;
        }

    }

    /**
     * .
     * This class implements custom policy related functionalities.
     */

    public static class CustomRulesCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public CustomRulesCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {

                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve custom policies
         *
         * @return
         */

        public Response getAllCustomRules() {

            Response getAllCustomRulesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/custom");

            return getAllCustomRulesResponse;
        }

        /**
         * .
         * Add custom policy
         *
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */

        public Response addCustomRule(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when adding custom policies", e);
            }

            Response addCustomRuleResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/policies/custom");

            return addCustomRuleResponse;
        }
    }

    /**
     * .
     * This class implements functionalities related to API specific custom policies
     */

    public static class CustomRulesIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public CustomRulesIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve  a custom policy
         *
         * @param ruleId
         * @return
         */

        public Response getCustomRule(String ruleId) {

            Response getCustomRuleResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/custom/" + ruleId);

            return getCustomRuleResponse;
        }

        /**
         * .
         * Delete custom policy
         *
         * @param ruleId
         * @return
         */

        public Response deleteCustomRule(String ruleId) {

            Response deleteCustomRuleResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/custom/" + ruleId);

            return deleteCustomRuleResponse;
        }

        /**
         * .
         * Update custom policy
         *
         * @param policyId
         * @param jsonPayloadPath
         * @return
         */

        public Response updateCustomRule(String policyId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when updating custom policies", e);
            }

            Response updateCustomRuleResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/policies/custom/" + policyId);

            return updateCustomRuleResponse;
        }
    }

    /**
     * This class implemented the advanced policy related functionalities.
     */

    public static class AdvancedPolicyCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public AdvancedPolicyCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {

                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve all advanced throttling policies
         *
         * @return
         */

        public Response getAllAdvancedThrottlingPolicies() {

            Response getAllAdvancedThrottlingPoliciesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/advanced");

            return getAllAdvancedThrottlingPoliciesResponse;
        }

        /**
         * .
         * Add advanced throttling policy
         *
         * @param jsonPayloadPath
         * @return
         */

        public Response addAdvancedThrottlingPolicy(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when adding advanced throttling policies", e);
            }

            Response addAdvancedThrottlingPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/policies/advanced");

            return addAdvancedThrottlingPolicyResponse;
        }
    }

    /**
     * .
     * This class implements functionalities related to API specific advanced throttling policies
     */

    public static class AdvancedPolicyIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public AdvancedPolicyIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve Advanced throttling policies
         *
         * @param policyId
         * @return
         */

        public Response getAdvancedThrottlingPolicy(String policyId) {

            Response getAdvancedThrottlingPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/advanced/" + policyId);

            return getAdvancedThrottlingPolicyResponse;
        }

        /**
         * .
         * Delete advanced throttling policies
         *
         * @param policyId
         * @return
         */

        public Response deleteAdvancedThrottlingPolicy(String policyId) {

            Response deleteAdvancedThrottlingPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/advanced/" + policyId);

            return deleteAdvancedThrottlingPolicyResponse;
        }

        /**
         * .
         * Update custom policies
         *
         * @param policyId
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */

        public Response updateCustomRule(String policyId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when updating custom policies", e);
            }

            Response updateCustomRuleResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/policies/advanced/" + policyId);

            return updateCustomRuleResponse;
        }
    }

    /**
     * .
     * This class implements the functionalities related to deny policies
     */

    public static class BlacklistCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public BlacklistCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve all deny policies
         *
         * @return
         */

        public Response getAllBlockingConditions() {

            Response getAllBlockingConditionsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/blacklist");

            return getAllBlockingConditionsResponse;
        }

        /**
         * .
         * Add all blocking conditions
         *
         * @param jsonPayloadPath
         * @return
         */

        public Response addBlockingConditions(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when adding blocking conditions", e);
            }

            Response addBlockingConditionsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/policies/blacklist");

            return addBlockingConditionsResponse;
        }
    }

    /**
     * This class implemented the functionalities related to API specific deny policies
     */

    public static class BlacklistIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/policies";
        String resourceParenPath = "./src/test/payloads/";

        public BlacklistIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve deny policy
         *
         * @param conditionId
         * @return
         */

        public Response getBlacklistPolicy(String conditionId) {

            Response getBlacklistPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/blacklist/" + conditionId);

            return getBlacklistPolicyResponse;
        }

        /**
         * .
         * Delete deny policy
         *
         * @param conditionId
         * @return Response
         */

        public Response deleteBlacklistPolicy(String conditionId) {

            Response deleteBlacklistPolicyResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/blacklist/" + conditionId);

            return deleteBlacklistPolicyResponse;
        }

        /**
         * .
         * Update deny policy
         *
         * @param conditionId
         * @param jsonPayloadPath
         * @return Response
         */

        public Response updateBlacklistPolicy(String conditionId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when updating deny policies", e);
            }
            Response updateBlacklistPolicyResponse = RestAssured.given()

                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .patch(endPoint + publisherApisString + "/policies/blacklist/" + conditionId);

            return updateBlacklistPolicyResponse;
        }
    }

    /**
     * .
     * This class implemented the applications related functionalities.
     */

    public static class ApplicationCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/applications";
        String resourceParenPath = "./src/test/payloads/";

        public ApplicationCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
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
                    .get(endPoint + publisherApisString);

            return searchApplicationsResponse;
        }
    }

    /**
     * .
     * This class implemented the applications related functionalities.
     */
    public static class Applications {

        String accessToken;
        String endPoint;

        String publisherApisString = "/applications";
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
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when retrieving the URL", e);
            }
        }

        /**
         * Delete application
         *
         * @param applicationId
         * @param owner
         * @return Response
         */

        public Response deleteApplication(String applicationId, String owner) {

            Response deleteApplicationResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + applicationId + "/change-owner?owner=" + owner);

            return deleteApplicationResponse;
        }

    }

    public static class Application {

        String accessToken;
        String endPoint;

        String publisherApisString = "/applications";
        String resourceParenPath = "./src/test/payloads/";

        public Application(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Change application owner
         *
         * @param applicationId
         * @param owner
         * @return Response
         */

        public Response changeApplicationOwner(String applicationId, String owner) {

            Response changeApplicationOwnerResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/" + applicationId + "/change-owner?owner=" + owner);

            return changeApplicationOwnerResponse;
        }
    }

    public static class ApplicationIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/export";
        String resourceParenPath = "./src/test/payloads/";

        public ApplicationIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * Export application
         *
         * @param appName
         * @param appOwner
         * @param withKeys
         * @return Response
         */

        public Response exportApplication(String appName, String appOwner, boolean withKeys) {

            Response exportApplicationResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + "/export/applications?appNAme=" + appName + "&owner=" + appOwner + "&withKeys=" + withKeys);

            return exportApplicationResponse;
        }

        /**
         * Import application
         *
         * @param exportedApplicationZipPath
         * @param preserveOwner
         * @param appOwner
         * @param skipSubscription
         * @param skipApplicationKey
         * @param update
         * @return Response
         */
        public Response importApplication(String exportedApplicationZipPath, Boolean preserveOwner, String appOwner, boolean skipSubscription, boolean skipApplicationKey, boolean update) {

            Response importApplicationResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .multiPart("file", new File(resourceParenPath + exportedApplicationZipPath))
                    .post(endPoint + "/import/applications?preserveOwner=" + preserveOwner + "&skipSubscriptions=" + skipSubscription + "&appOwner=" + appOwner + "&skipApplicationKeys=" + skipApplicationKey + "&update=" + update);

            return importApplicationResponse;
        }
    }

    /**
     * .
     * This class implemented the API import/export functionalities
     */

    public static class ApiIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/export";
        String resourceParenPath = "./src/test/payloads/";

        public ApiIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * Import API
         *
         * @param api
         * @param preserveOwner
         * @param overwrite
         * @return Response
         */

        public Response importApi(String api, boolean preserveOwner, boolean overwrite) {

            Response importApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .multiPart("file", new File(resourceParenPath + api))
                    .post(endPoint + "/import/api?preserveOwner=" + preserveOwner + "&overwrite=" + overwrite);

            return importApiResponse;
        }

        /**
         * .
         * Export API
         *
         * @param name
         * @param version
         * @param providerName
         * @param format
         * @param preserveStatus
         * @return Response
         */

        public Response exportApi(String name, String version, String providerName, String format, boolean preserveStatus) {

            Response exportApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + "/export/api?name=" + name + "&version=" + version + "&providerName=" + providerName + "&format=" + format + "&preserveStatus=" + preserveStatus);

            return exportApiResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to API products
     */
    public static class ApiProductIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/export";
        String resourceParenPath = "./src/test/payloads/";

        public ApiProductIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * Import API product
         *
         * @param exportApiProductPath
         * @param preserveProvider
         * @param overwriteAPIProduct
         * @param overwriteAPIs
         * @param importAPIs
         * @return Response
         */

        public Response importApiProduct(String exportApiProductPath, boolean preserveProvider, boolean overwriteAPIProduct, boolean overwriteAPIs, boolean importAPIs) {

            Response importApiResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .multiPart("file", new File(resourceParenPath + exportApiProductPath))
                    .post(endPoint + "/import/api-product?preserveProvider=" + preserveProvider + "&overwriteAPIProduct=" + overwriteAPIProduct + "&overwriteAPIs=" + overwriteAPIs + "&importAPIs=" + importAPIs);

            return importApiResponse;
        }

        public Response exportApiProduct(String name, String version, String providerName, String format, boolean preserveStatus) {

            Response exportApiProductResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .get(endPoint + "/export/api-product?name=" + name + "&version=" + version + "&providerName=" + providerName + "&format=" + format + "&preserveStatus=" + preserveStatus);

            return exportApiProductResponse;
        }

    }

    public static class LabelCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/labels";
        String resourceParenPath = "./src/test/payloads/";

        public LabelCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * Retrieve all registered labels
         *
         * @return Response
         */

        public Response getAllRegisteredLabels() {

            Response getAllRegisteredLabelsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString);

            return getAllRegisteredLabelsResponse;
        }
    }


    public static class Label {

        String accessToken;
        String endPoint;

        String publisherApisString = "/labels";
        String resourceParenPath = "./src/test/payloads/";

        public Label(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * Add label
         *
         * @param jsonPayloadPath
         * @return
         */

        public Response addLabel(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while adding a label", e);
            }

            Response addLabelResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString);

            return addLabelResponse;
        }

        public Response updateLabel(String labelId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when updating label", e);
            }

            Response updateLabelResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .put(endPoint + publisherApisString + "/" + labelId);

            return updateLabelResponse;
        }

        /**
         * .
         * Delete label
         *
         * @param policyId
         * @return Response
         */

        public Response deleteLabel(String policyId) {

            Response deleteLabelResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/policies/advanced/" + policyId);

            return deleteLabelResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to bot detection
     */

    public static class BotDetectionData {

        String accessToken;
        String endPoint;

        String publisherApisString = "/bot-detection-data";
        String resourceParenPath = "./src/test/payloads/";

        public BotDetectionData(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve bot detection data
         *
         * @return
         */

        public Response getAllBotDetectedData() {

            Response getAllBotDetectedDataResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString);

            return getAllBotDetectedDataResponse;
        }


    }

    /**
     * This class implemented the monetization related functionalities
     */

    public static class MonetizationCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/monetization";
        String resourceParenPath = "./src/test/payloads/";

        public MonetizationCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Publish usage records
         *
         * @return
         */

        public Response publishUsageRecords() {

            Response publishUsageRecordsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString + "/publish-usage");

            return publishUsageRecordsResponse;
        }

        public Response getStatusOfMonetizationUsageRecords() {

            Response getStatusOfMonetizationUsageRecordsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/publish-usage/status");

            return getStatusOfMonetizationUsageRecordsResponse;
        }

    }

    /**
     * .
     * This class implemented the workflow related
     */
    public static class WorkflowCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/workflows";
        String resourceParenPath = "./src/test/payloads/";

        public WorkflowCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Rerieve all pending workflow processes
         *
         * @return Response
         */

        public Response getAllPendingWorkflowProcesses() {

            Response getAllPendingWorkflowProcessesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getAllPendingWorkflowProcessesResponse;
        }

    }

    /**
     * .
     * This class implemented the workflow related
     */

    public static class WorkflowIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/workflows";
        String resourceParenPath = "./src/test/payloads/";

        public WorkflowIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Rerieve all pending workflow details
         *
         * @return Response
         */

        public Response getPendingWorkflowDetails(String externalWorkflowRef) {

            Response getPendingWorkflowDetailsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + externalWorkflowRef);

            return getPendingWorkflowDetailsResponse;
        }

        /**
         * .
         * Update pending workflow status
         *
         * @return Response
         */

        public Response updateWorkflowStatus(String externalWorkflowRef, String workflowReferenceId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }

            Response updateWorkflowStatusResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .body(payloadpls1)
                    .post(endPoint + publisherApisString + "/update-workflow-status?workflowReferenceId=" + workflowReferenceId);

            return updateWorkflowStatusResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to tenants
     */

    public static class Tenants {

        String accessToken;
        String endPoint;

        String publisherApisString = "/tenant-info";
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
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Rerieve the tenant Id of users
         *
         * @return Response
         */

        public Response getTenantIdOfUser(String userName) {

            Response getTenantIdOfUserResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + userName);

            return getTenantIdOfUserResponse;
        }

        /**
         * .
         * Retrieve custom URL info of tenant domain
         *
         * @return Response
         */

        public Response getCustomUrlInfoOfTenantDomain(String tenantDomain) {

            Response getCustomUrlInfoOfTenantDomainResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + "/custom-urls/" + tenantDomain);

            return getCustomUrlInfoOfTenantDomainResponse;
        }

    }

    /**
     * .
     * This class implemented the functionalities related to API categories
     */

    public static class ApiCategoryCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/api-categories";
        String resourceParenPath = "./src/test/payloads/";

        public ApiCategoryCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
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
     * This class implemented the API category related
     */

    public static class ApiCategoryIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/api-categories";
        String resourceParenPath = "./src/test/payloads/";

        public ApiCategoryIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Add API category
         *
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */

        public Response addApiCategories(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }

            Response addApiCategoriesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .body(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString);

            return addApiCategoriesResponse;
        }

        /**
         * .
         * Update API category
         *
         * @param jsonPayloadPath
         * @param apiCategoryId
         * @return Response
         */

        public Response updateApiCategory(String jsonPayloadPath, String apiCategoryId) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }

            Response updateApiCategoryResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .body(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/" + apiCategoryId);

            return updateApiCategoryResponse;
        }

        /**
         * .
         * Delete API category
         *
         * @param apiCategoryId
         * @return
         */
        public Response deleteApiCategory(String apiCategoryId) {

            Response deleteApiCategoryResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + apiCategoryId);

            return deleteApiCategoryResponse;
        }

    }

    /**
     * This class implemented the API settings related functionalities
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
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * Retrieve admin settings
         */

        public Response getAdminSetting() {

            Response getAdminSettingResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getAdminSettingResponse;
        }

    }

    /**
     * .
     * This class implemented the alerts related functionalities
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
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve all admin alert types
         */

        public Response getAllAdminAlertTypes() {

            Response getAllAdminAlertTypesResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getAllAdminAlertTypesResponse;
        }

    }

    /**
     * .
     * This class implemented the alert subscription related functionalities
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
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * Retrieve alert subscription types
         */

        public Response getSubscribedAlertTypes() {

            Response getCustomRuleResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getCustomRuleResponse;
        }

        /**
         * .
         * Subscribe to admin alert
         */

        public Response subscribeToAdminAlert(String conditionId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }

            Response subscribeToAdminAlertResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString);

            return subscribeToAdminAlertResponse;
        }

        /**
         * .
         * Unsubscribe from all admin alerts
         */

        public Response unsubscribeUserFromAllAdminAlerts() {

            Response unsubscribeUserFromAllAdminAlertsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString);

            return unsubscribeUserFromAllAdminAlertsResponse;
        }

    }

    /**
     * .
     * This class implemented functionalities related to bot detection alert subscriptions
     */

    public static class BotDetectionAlertSubscriptions {

        String accessToken;
        String endPoint;

        String publisherApisString = "/alert-subscriptions/bot-detection";
        String resourceParenPath = "./src/test/payloads/";

        public BotDetectionAlertSubscriptions(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * bot Detection alert subscriptions
         *
         * @return
         */

        public Response getSubscriptionsForBotDetection() {

            Response getSubscriptionsForBotDetectionResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getSubscriptionsForBotDetectionResponse;
        }

        /**
         * .
         * Subscribe for bot detection alerts
         *
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */

        public Response subscribeForBotDetectionAlerts(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }

            Response subscribeForBotDetectionAlertsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString);

            return subscribeForBotDetectionAlertsResponse;
        }

        /**
         * .
         * Unsubscribe from bot detection alerts
         *
         * @param uuid
         * @return Response
         */

        public Response unsubscribeFromBotDetectionAlerts(String uuid) {

            Response unsubscribeFromBotDetectionAlertsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + uuid);

            return unsubscribeFromBotDetectionAlertsResponse;
        }

    }

    /**
     * .
     * This class implemented the system scopes related functionalities
     */
    public static class SystemScopes {

        String accessToken;
        String endPoint;

        String publisherApisString = "/system-scopes";
        String resourceParenPath = "./src/test/payloads/";

        public SystemScopes(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve scopes for particular user
         *
         * @param scopeName
         * @param userName
         * @return
         */

        public Response getScopesForParticularUser(String scopeName, String userName) {

            Response getScopesForParticularUserResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + scopeName + "?username=" + userName);

            return getScopesForParticularUserResponse;
        }

        /**
         * .
         * Retrieve role scope mapping
         *
         * @return
         */

        public Response getRoleScopeMappings() {

            Response getRoleScopeMappingsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return getRoleScopeMappingsResponse;
        }

        /**
         * .
         * Update roles for scope
         */

        public Response updateRolesForScope(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }

            Response updateRolesForScopeResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString);

            return updateRolesForScopeResponse;
        }

        /**
         * .
         * Retrieve roles alias mappings
         */


        public Response getRoleAliasMappings() {

            Response getRoleAliasMappingsResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/role-aliases");

            return getRoleAliasMappingsResponse;
        }

        /**
         * .
         * Add new role alias
         *
         * @param jsonPayloadPath
         * @return Response
         */

        public Response addNewRoleAlias(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while adding new role alias", e);

            }

            Response addNewRoleAliasResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/role-aliases");

            return addNewRoleAliasResponse;
        }
    }

    /**
     * This class implemented tenant theme related functionalities
     */

    public static class TenantTheme {

        String accessToken;
        String endPoint;

        String publisherApisString = "/tenant-theme";
        String resourceParenPath = "./src/test/payloads/";

        public TenantTheme(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Export dev portal tenant theme
         *
         * @return Response
         */

        public Response exportDevPortalTenantTheme() {

            Response exportDevPortalTenantThemeResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString);

            return exportDevPortalTenantThemeResponse;
        }

        /**
         * Import dev portal tenant themeI
         *
         * @param themeZipPath
         * @return Response
         */

        public Response importDevPortalTenantTheme(String themeZipPath) {

            Response importDevPortalTenantThemeResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .multiPart("file", new File(resourceParenPath + themeZipPath))
                    .put(endPoint + publisherApisString);

            return importDevPortalTenantThemeResponse;
        }
    }

    /**
     * .
     * This class implemented the key manager collection related functionalities
     */

    public static class KeyManagerCollection {

        String accessToken;
        String endPoint;

        String publisherApisString = "/key-managers";
        String resourceParenPath = "./src/test/payloads/";

        public KeyManagerCollection(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }
        }

        /**
         * .
         * Retrieve all key managers
         *
         * @return Response
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

        /**
         * .
         * Add new key manager
         *
         * @param jsonPayloadPath
         * @return Response
         */

        public Response addNewApiKeyManager(String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }

            Response addNewApiKeyManagerResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .post(endPoint + publisherApisString);

            return addNewApiKeyManagerResponse;
        }

    }

    /**
     * .
     * This class implemented the key manager related functionalities
     */

    public static class KeyManagerIndividual {

        String accessToken;
        String endPoint;

        String publisherApisString = "/key-managers";
        String resourceParenPath = "./src/test/payloads/";

        public KeyManagerIndividual(String accessToken, ApimVersions version) throws RestAssuredMigrationException {
            this.accessToken = accessToken;

            FileInputStream input;
            Properties properties;

            try {
                String path = "./src/test/resources/config.properties";
                properties = new Properties();
                input = new FileInputStream(path);
                properties.load(input);
                if (version == ApimVersions.APIM_3_2) {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_3_2");
                } else {
                    this.endPoint = properties.getProperty("base_url") + properties.getProperty("admin_url_4_1");
                }

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred while retrieving URL", e);
            }

        }

        /**
         * .
         * Retrieve key manager configurations
         *
         * @param keyManagerId
         * @return Response
         */

        public Response getKeyManagerConfiguration(String keyManagerId) {

            Response getKeyManagerConfigurationResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .get(endPoint + publisherApisString + "/" + keyManagerId);

            return getKeyManagerConfigurationResponse;
        }

        /**
         * Update key manager
         *
         * @param keyManagerId
         * @param jsonPayloadPath
         * @return
         * @throws RestAssuredMigrationException
         */
        public Response updateKeyManager(String keyManagerId, String jsonPayloadPath) throws RestAssuredMigrationException {

            byte[] payloadplj1;
            String payloadpls1 = "";

            try {
                payloadplj1 = Files.readAllBytes(Paths.get(resourceParenPath + jsonPayloadPath));
                payloadpls1 = new String(payloadplj1);

            } catch (Exception e) {
                throw new RestAssuredMigrationException("Error occurred when reading file content", e);
            }

            Response updateKeyManagerResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .basePath(payloadpls1)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .put(endPoint + publisherApisString + "/" + keyManagerId);

            return updateKeyManagerResponse;
        }

        /**
         * .
         * Delete key manager
         *
         * @param keyManagerId
         * @param jsonPayloadPath
         * @return
         */
        public Response deleteKeyManager(String keyManagerId, String jsonPayloadPath) {

            Response deleteKeyManagerResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .oauth2(accessToken)
                    .contentType(ContentTypes.APPLICATION_JSON)
                    .delete(endPoint + publisherApisString + "/" + keyManagerId);

            return deleteKeyManagerResponse;
        }
    }
}
