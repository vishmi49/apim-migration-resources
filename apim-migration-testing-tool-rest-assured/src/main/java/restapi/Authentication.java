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

package restapi;

import io.restassured.response.Response;

import java.io.FileInputStream;

import exceptions.RestAssuredMigrationException;
import io.restassured.RestAssured;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;


/**
 * .
 * This class implemented methods to authenticate and get access token
 */
public class Authentication {
    Response getClientIdResponse, getAccessTokenResponse;

    FileInputStream input;
    Properties p;
    byte[] authPlayloadJson;
    String authPlayloadString;
    String accessToken;

    String username = "";
    String userpassword = "";
    String endpoint = "";
    String payloadPath = "";
    String tokenUrl = "";
    String scope = "";
    String grantType = "";
    String contentType = "";

    public Authentication(AuthenticationObject authenticationObject) {
        this.username = authenticationObject.getUsername();
        this.userpassword = authenticationObject.getUserpassword();
        this.endpoint = authenticationObject.getEndpoint();
        this.payloadPath = authenticationObject.getPayloadPath();
        this.tokenUrl = authenticationObject.getTokenUrl();
        this.scope = authenticationObject.getScopes();
        this.grantType = authenticationObject.getGrantType();
        this.contentType = authenticationObject.getContentType();
    }

    /**
     * .
     * Generate access token
     */

    public String getAccessToken() throws RestAssuredMigrationException {
        try {
            authPlayloadJson = Files.readAllBytes(Paths.get(payloadPath));
            authPlayloadString = new String(authPlayloadJson);
            getClientIdResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .preemptive()
                    .basic("admin", "admin")
                    .body(authPlayloadString)
                    .contentType(contentType)
                    .post(endpoint);

            String test = getClientIdResponse.jsonPath().prettify();
            System.out.println(test);
            getAccessTokenResponse = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .basic(getClientIdResponse.jsonPath().get("clientId").toString(), getClientIdResponse.jsonPath().get("clientSecret").toString())
                    .queryParam("grant_type", grantType)
                    .queryParam("username", username)
                    .queryParam("password", userpassword)
                    .queryParam("scope", scope)
                    .post(tokenUrl);
            System.out.println(getAccessTokenResponse.jsonPath());

            accessToken = getAccessTokenResponse.jsonPath().get("access_token").toString();
            System.out.println(accessToken);

        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred while generating access token", e);
        }
        return accessToken;

    }

}
