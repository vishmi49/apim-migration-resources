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

package soapapi.remoteuserstore;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exceptions.RestAssuredMigrationException;
import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import restapi.TenantAdmin;

public class RemoteUserStore {

    String endPointUrl = "testing";
    private static Logger logger = LogManager.getLogger(RemoteUserStore.class);

    public RemoteUserStore(String accessToken, URI baseURL) {
        this.endPointUrl = baseURL.toString() + "services/RemoteUserStoreManagerService";
    }
    public RemoteUserStore(URI baseURL) {
        this.endPointUrl = baseURL.toString() + "services/RemoteUserStoreManagerService";
    }
    public void authenticate(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .basic(tenantAdmin.getUserName(), tenantAdmin.getPassword())
                .header("SOAPAction", "urn:authenticate")
                .contentType("text/xml; charset=UTF-8;")
                .body(getXMLPayload(tenantXmlFileName))
                .when()
                .post(endPointUrl);

        XmlPath jsXpath = new XmlPath(response.asString());
        String rate = jsXpath.getString("GetConversionRateResult");
        logger.info("[AUTHENTICATION]: " + rate);

    }

    public Response addRole(String roleSoapPayload, TenantAdmin tenantAdmin, boolean isXMlFile) throws Exception {
    	
    	String rolePayload = roleSoapPayload;
    	if(isXMlFile) {
    		rolePayload = getXMLPayload(roleSoapPayload);
    	}
    	
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .basic(tenantAdmin.getUserName(), tenantAdmin.getPassword())
                .header("SOAPAction", "urn:addRole")
                .contentType("text/xml; charset=UTF-8;")
                .body(rolePayload)
                .when()
                .post(endPointUrl);
        return response;

//            XmlPath jsXpath= new XmlPath(response.asString());
//            String rate=jsXpath.getString("GetConversionRateResult");
//            logger.info("[ADD ROLE]: "+rate);
    }

    public Response addUser(String userSoapPayload, TenantAdmin tenantAdmin, boolean isXMlFile) throws Exception {

    	String userPayload = userSoapPayload;
    	if(isXMlFile) {
    		userPayload = getXMLPayload(userSoapPayload);
    	}
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .basic(tenantAdmin.getUserName(), tenantAdmin.getPassword())
                .header("SOAPAction", "urn:addUser")
                .contentType("text/xml; charset=UTF-8;")
                .body(userPayload)
                .when()
                .post(endPointUrl);
        return response;

//            XmlPath jsXpath= new XmlPath(response.asString());
//            String rate=jsXpath.getString("GetConversionRateResult");
//            logger.info("[ADD USER]: "+rate);
    }

    private String getXMLPayload(String tenantXmlFileName) throws RestAssuredMigrationException {

        byte[] payloadplj1;
        String payloadpls1 = "";

        try {
            payloadplj1 = Files.readAllBytes(Paths.get("./src/test/payloads/" + tenantXmlFileName));
            payloadpls1 = new String(payloadplj1);

        } catch (Exception e) {
            logger.error("Error occurred reading the file content", e);
            throw new RestAssuredMigrationException("Error occurred when reading file content", e);
        }

        return payloadpls1;
         
    }

}
