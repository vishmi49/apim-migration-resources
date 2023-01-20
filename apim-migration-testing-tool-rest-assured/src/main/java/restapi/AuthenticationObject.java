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

/**
 * .
 * This class implemented method to create authentication object
 */
public class AuthenticationObject {

    private String username;
    private String userpassword;
    private String endpoint;
    private String payloadPath;
    private String tokenUrl;
    private String scopes;
    private String grantType;
    private String contentType;

    String[] defaultScopes = {Scopes.API_PUBLISH, Scopes.API_CREATE, Scopes.API_VIEW, Scopes.API_IMPORT_EXPORT, Scopes.API_MANAGE, Scopes.DOCUMENT_MANAGE, Scopes.APP_MANAGE, Scopes.APP_IMPORT_EXPORT, Scopes.SUBSCRIBE};

    public AuthenticationObject() {

        this.username = "admin";
        this.userpassword = "admin";
        this.endpoint = "https://localhost:9443/client-registration/v0.17/register";
        this.tokenUrl ="https://localhost:8243/token"; //"https://localhost:8243/token";https://localhost:9443/oauth2/token
        this.payloadPath = "./src/test/payloads/payload.json";
        this.grantType = GrantTypes.PASSSWORD;
        this.scopes = String.join(" ", defaultScopes);
        this.contentType = ContentTypes.APPLICATION_JSON;

    }

    /**
     * @return String return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return String return the userpassword
     */
    public String getUserpassword() {
        return userpassword;
    }

    /**
     * @param userpassword the userpassword to set
     */
    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    /**
     * @return String return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return String return the payloadPath
     */
    public String getPayloadPath() {
        return payloadPath;
    }

    /**
     * @param payloadPath the payloadPath to set
     */
    public void setPayloadPath(String payloadPath) {
        this.payloadPath = payloadPath;
    }

    /**
     * @return String return the tokenUrl
     */
    public String getTokenUrl() {
        return tokenUrl;
    }

    /**
     * @param tokenUrl the tokenUrl to set
     */
    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    /**
     * @return String return the scope
     */
    public String getScopes() {
        return scopes;
    }

    /**
     * @param scopes
     */
    public void setScopes(String... scopes) {
        this.scopes = String.join(" ", scopes);
    }

    /**
     * @return String return the grantType
     */
    public String getGrantType() {
        return grantType;
    }

    /**
     * @param grantType the grantType to set
     */
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    /**
     * @return String return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
