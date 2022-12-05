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

package soapapi.tenantmanagemant;

import exceptions.RestAssuredMigrationException;
import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import restapi.TenantAdmin;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * .
 * This class implemented the functionalities related tenant management
 */
public class TenantManagement {

    String endPointUrl = "testing";
    private static Logger logger = LogManager.getLogger(TenantManagement.class);

    public TenantManagement(String accessToken, URI baseURL) {
        this.endPointUrl = baseURL.toString() + "services/TenantMgtAdminService";
    }

    public void retrieveTenants(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .basic(tenantAdmin.getUserName(), tenantAdmin.getPassword())
                .header("SOAPAction", "urn:retrieveTenants")
                .contentType("text/xml; charset=UTF-8;")
                .body(getXMLPayload(tenantXmlFileName))
                .when()
                .post(endPointUrl);

        XmlPath jsXpath = new XmlPath(response.asString());
        String rate = jsXpath.getString("GetConversionRateResult");
        logger.info("[RETRIVE TENENT RESPONSE]: " + rate);
    }

    public void checkDomainAvailability(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .basic(tenantAdmin.getUserName(), tenantAdmin.getPassword())
                .header("SOAPAction", "urn:checkDomainAvailabilityRequest.xml")
                .contentType("text/xml; charset=UTF-8;")
                .body(getXMLPayload(tenantXmlFileName))
                .when()
                .post(endPointUrl);

        XmlPath jsXpath = new XmlPath(response.asString());
        String rate = jsXpath.getString("GetConversionRateResult");
        logger.info("[CHECK DOMAIN AVAILABILITY]: " + rate);
    }


    public void deleteTenants(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .basic(tenantAdmin.getUserName(), tenantAdmin.getPassword())
                .header("SOAPAction", "urn:deleteTenant")
                .contentType("application/soap+xml; charset=UTF-8;")
                .body(getXMLPayload(tenantXmlFileName))
                .when()
                .post(endPointUrl);

        XmlPath jsXpath = new XmlPath(response.asString());
        String rate = jsXpath.getString("GetConversionRateResult");
        logger.info("[DELETE TENENT RESPONSE]: " + rate);
    }


    public void createTenants(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .basic(tenantAdmin.getUserName(), tenantAdmin.getPassword())
                .header("SOAPAction", "urn:addTenant")
                .contentType("text/xml; charset=UTF-8;")
                .body(getXMLPayload(tenantXmlFileName))
                .when()
                .post(endPointUrl);

        XmlPath jsXpath = new XmlPath(response.asString());
        String rate = jsXpath.getString("GetConversionRateResult");
        logger.info("[CREATE TENANTS]: " + rate);
    }

    public void deactivateTenants(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth()
                .basic(tenantAdmin.getUserName(), tenantAdmin.getPassword())
                .header("SOAPAction", "urn:deactivateTenant")
                .contentType("application/soap+xml; charset=UTF-8;")
                .body(getXMLPayload(tenantXmlFileName))
                .when()
                .post(endPointUrl);

        XmlPath jsXpath = new XmlPath(response.asString());
        String rate = jsXpath.getString("GetConversionRateResult");
        logger.info("[DEACTIVATE TENENT RESPONSE]: " + rate);
    }

    private String getXMLPayload(String tenantXmlFileName) throws RestAssuredMigrationException {

        byte[] payloadplj1;
        String payloadpls1 = "";

        try {
            payloadplj1 = Files.readAllBytes(Paths.get("./src/test/payloads/" + tenantXmlFileName));
            payloadpls1 = new String(payloadplj1);

        } catch (Exception e) {
            logger.error("Error occurred reading the file content", e);
            throw new RestAssuredMigrationException("Error occurred reading the file content", e);
        }

        return payloadpls1;

    }

}
