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

package ataf.actions;

import exceptions.RestAssuredMigrationException;
import restapi.AuthenticationObject;

import java.io.FileInputStream;
import java.net.URI;
import java.util.Properties;

import org.testng.annotations.BeforeSuite;

/**
 * .
 * The BaseTest class is the base class of rest assured migration testing
 */
public class BaseTest {

    protected URI baseURL;
    protected AuthenticationObject authenticationObject;

    @BeforeSuite
    public void initiaization() throws RestAssuredMigrationException {
        authenticationObject = new AuthenticationObject();
        FileInputStream input;
        Properties properties;

        try {
            String path = "./src/test/resources/config.properties";
            properties = new Properties();
            input = new FileInputStream(path);
            properties.load(input);
            this.baseURL = new URI(properties.getProperty("base_url") + "/");

        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred while retrieving the base URL", e);
        }
    }
}
