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

package tests;

import java.util.ArrayList;
import java.util.Arrays;

import exceptions.RestAssuredMigrationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ataf.actions.BaseTest;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.Authentication;
import restapi.JsonReadWrite;
import restapi.SimpleRequests;
import restapi.devportal.DevPortal;

public class DemoDataValidation extends BaseTest {

    String accessToken;
    private static Logger logger = LogManager.getLogger(DemoDataValidation.class);

    @Test
    @Parameters({"tenantAdminUser", "tenantAdminUserPassword",
            "apiSearchingKeyWord", "restApiEndpoint"})
    public void testDataValidation(String tenantAdminUser, String tenantAdminUserPassword,
                                   String apiSearchingKeyWord, String restApiEndpoint) throws RestAssuredMigrationException {

        authenticationObject.setUsername(tenantAdminUser);
        authenticationObject.setUserpassword(tenantAdminUserPassword);

        Authentication authentication = new Authentication(authenticationObject);
        accessToken = authentication.getAccessToken();

        DevPortal.Applications applications = new DevPortal.Applications(accessToken, ApimVersions.APIM_3_2);

        DevPortal.UnfiedSearch dSearch = new DevPortal.UnfiedSearch(accessToken, ApimVersions.APIM_3_2);
        Response searchApiByName = dSearch.getApiAndApiDocumentByContent(apiSearchingKeyWord);
        logger.info("Status Code [SEARCHED API BY NAME]: " + searchApiByName.statusCode());

        Response getApplicationRes = applications.getDetailsOfApplication(JsonReadWrite.readAppId(0));
        logger.info("Status Code [AVAILABILITY OF APPLICATION 1]: " + getApplicationRes.statusCode());

        Response getResponse = SimpleRequests.get(JsonReadWrite.getAccessTokenOfApiFromApp(JsonReadWrite.readAppId(0)), restApiEndpoint);
        logger.info("Status Code [INVOKE REST API]: " + getResponse.statusCode());
//        System.out.println(JsonReadWrite.getAccessTokenOfApiFromApp(JsonReadWrite.readAppId(0)));


    }

}
