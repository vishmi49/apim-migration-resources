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

import exceptions.RestAssuredMigrationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * .
 * This class is implemented to add populated data to a file
 */

public class JsonReadWrite {

    public static String runtimeJsonPath = "./src/test/runtimeData/runtime.json";

    /**
     * Add API data to file
     *
     * @param apiId
     */
    public static void addApiToJson(String apiId) throws RestAssuredMigrationException {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject apiIdToJson = new JSONObject();
            apiIdToJson.put("apiId", apiId);

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;
            JSONArray apisList = (JSONArray) jsonObject.get("apis");
            apisList.add(apiIdToJson);
            jsonObject.put("apis", apisList);
            try (FileWriter file = new FileWriter(runtimeJsonPath)) {
                file.write(jsonObject.toJSONString());
                file.flush();

            } catch (IOException e) {
                throw new RestAssuredMigrationException("Error occurred while writing file", e);
            }
        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred while adding API to runtime.json file", e);

        }

    }

    /**
     * Retrieve API Id from file
     *
     * @param indexOfApiInList
     * @return getApiId
     */

    public static String readApiId(int indexOfApiInList) {

        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray apis = (JSONArray) jsonObject.get("apis");
        JSONObject getApi = (JSONObject) apis.get(indexOfApiInList);
        String getApiId = (String) getApi.get("apiId");

        return getApiId;
    }

    /**
     * .
     * Add application data to file
     *
     * @param appId
     */

    public static void addAppToJson(String appId) throws RestAssuredMigrationException {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject apiIdToJson = new JSONObject();
            apiIdToJson.put("appId", appId);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;
            JSONArray appsList = (JSONArray) jsonObject.get("apps");
            appsList.add(apiIdToJson);
            jsonObject.put("apps", appsList);

            try (FileWriter file = new FileWriter(runtimeJsonPath)) {
                file.write(jsonObject.toJSONString());
                file.flush();

            } catch (IOException e) {
                throw new RestAssuredMigrationException("Error occurred while writing to the file", e);
            }
        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred while adding application data to runtime.json file ", e);
        }

    }

    /**
     * .
     * Retrieve Application Id from file
     *
     * @param indexOfApiInList
     * @return getAppId
     */

    public static String readAppId(int indexOfApiInList) throws RestAssuredMigrationException {

        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred when reading file content", e);
        }

        JSONArray apps = (JSONArray) jsonObject.get("apps");
        JSONObject getApp = (JSONObject) apps.get(indexOfApiInList);
        String getAppId = (String) getApp.get("appId");

        return getAppId;
    }

    /**
     * .
     * Add keys to file
     *
     * @param appId
     * @param keyType
     * @param keyObject
     */

    public static void addKeys(String appId, String keyType, String keyObject) throws RestAssuredMigrationException {

        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;

            int i = 0;
            JSONArray apps = (JSONArray) jsonObject.get("apps");
            JSONObject getApp = (JSONObject) apps.get(i);
            String getAppId = "";

            while (!getAppId.trim().equals(appId.trim())) {

                getApp = (JSONObject) apps.get(i);
                getAppId = (String) getApp.get("appId");

                if (getAppId.trim().equals(appId.trim())) {
                    JSONParser parser2 = new JSONParser();
                    JSONObject jsonKeyObject = (JSONObject) parser2.parse(keyObject);
                    getApp.put(keyType, jsonKeyObject);
                    break;
                }
                i += 1;
            }

            try (FileWriter file = new FileWriter(runtimeJsonPath)) {
                file.write(jsonObject.toJSONString());
                file.flush();

            } catch (IOException e) {
                throw new RestAssuredMigrationException("Error when writing file content to a file.", e);
            }
        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred while adding keys to runtime.json file", e);
        }

    }

    /**
     * .
     * Retrieve access token of an api from application data in file
     *
     * @param appId
     * @return
     * @throws RestAssuredMigrationException
     */

    public static String getAccessTokenOfApiFromApp(String appId) throws RestAssuredMigrationException {

        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();
        String getAccessToken = "";
        try {
            Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;

            int i = 0;
            JSONArray apps = (JSONArray) jsonObject.get("apps");
            JSONObject getApp = (JSONObject) apps.get(i);
            String getAppId = "";
            int appsArraySize = apps.size();

            while (!getAppId.trim().equals(appId.trim()) && i < appsArraySize) {

                getApp = (JSONObject) apps.get(i);
                getAppId = (String) getApp.get("appId");
                JSONObject getSandbox = (JSONObject) getApp.get("sandbox");
                JSONObject getToken = (JSONObject) getSandbox.get("token");
                getAccessToken = (String) getToken.get("accessToken");

                i += 1;
                return getAccessToken;
            }

        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred while retrieving the access token of application from file", e);
        }
        return getAccessToken;
    }

    /**
     * Add subscription data to file
     *
     * @param appId
     * @param subscriptionData
     * @throws RestAssuredMigrationException
     */

    public static void addSubscriptionData(String appId, String subscriptionData) throws RestAssuredMigrationException {

        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;

            int i = 0;
            JSONArray apps = (JSONArray) jsonObject.get("apps");
            JSONObject getApp = (JSONObject) apps.get(i);
            String getAppId = "";

            while (!getAppId.trim().equals(appId.trim())) {

                getApp = (JSONObject) apps.get(i);
                getAppId = (String) getApp.get("appId");

                JSONParser parser2 = new JSONParser();
                JSONObject jsonKeyObject = (JSONObject) parser2.parse(subscriptionData);

                if (getAppId.trim().equals(appId.trim())) {

                    if (getApp.get("subscription") == null) {
                        JSONArray jsonKeyObjects = new JSONArray();
                        jsonKeyObjects.add(jsonKeyObject);
                        getApp.put("subscription", jsonKeyObjects);

                    } else {
                        JSONArray subscripionListArray = (JSONArray) getApp.get("subscription");
                        subscripionListArray.add(jsonKeyObject);
                        getApp.remove("subscription");
                        getApp.put("subscription", subscripionListArray);

                    }

                    apps.remove(i);
                    apps.add(getApp);
                    jsonObject.remove("apps");
                    jsonObject.put("apps", apps);


                    break;
                }
                i += 1;
            }

            try (FileWriter file = new FileWriter(runtimeJsonPath)) {
                file.write(jsonObject.toJSONString());
                file.flush();

            } catch (IOException e) {
                throw new RestAssuredMigrationException("Error occurred while writing to the file", e);
            }
        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred while adding subscription data to runtime.json file.", e);
        }

    }

    /**
     * .
     * Retrieve subscription Id from file
     *
     * @param appId
     * @param subscriptionIndex
     * @return
     * @throws RestAssuredMigrationException
     */

    public static String getSubscriptionId(String appId, int subscriptionIndex) throws RestAssuredMigrationException {

        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;

            int i = 0;
            JSONArray apps = (JSONArray) jsonObject.get("apps");
            JSONObject getApp = (JSONObject) apps.get(i);
            String getAppId = "";


            while (!getAppId.trim().equals(appId.trim())) {

                getApp = (JSONObject) apps.get(i);
                getAppId = (String) getApp.get("appId");

                if (getAppId.trim().equals(appId.trim())) {


                    JSONArray subscriptionData = (JSONArray) getApp.get("subscription");
                    JSONObject subscription = (JSONObject) subscriptionData.get(subscriptionIndex);
                    String subscriptionId = (String) subscription.get("subscriptionId");

                    return subscriptionId;
                }
                i += 1;
            }
        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred while retrieving subscription ID", e);
        }
        return "";
    }

    /**
     * .
     * Remove subscription data
     *
     * @param appId
     * @param subscriptionId
     * @return
     */

    public static String removeSubscriptionData(String appId, String subscriptionId) throws RestAssuredMigrationException {

        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;

            int i = 0;
            JSONArray apps = (JSONArray) jsonObject.get("apps");
            JSONObject getApp = (JSONObject) apps.get(i);
            String getAppId = "";

            while (!getAppId.trim().equals(appId.trim())) {

                getApp = (JSONObject) apps.get(i);
                getAppId = (String) getApp.get("appId");

                if (getAppId.trim().equals(appId.trim())) {

                    int k = 0;

                    JSONArray subscriptionData = (JSONArray) getApp.get("subscription");
                    JSONObject subscription = (JSONObject) subscriptionData.get(k);
                    String getSubscriptionId = "";


                    while (!getSubscriptionId.trim().equals(subscriptionId.trim())) {

                        subscription = (JSONObject) subscriptionData.get(k);
                        getSubscriptionId = (String) subscription.get("subscriptionId");

                        if (getSubscriptionId.trim().equals(subscriptionId.trim())) {
                            subscriptionData.remove(k);
                            getApp.remove("subscription");
                            getApp.put("subscription", subscriptionData);
                            apps.remove(i);
                            apps.add(getApp);
                            jsonObject.remove("apps");
                            jsonObject.put("apps", apps);

                            try (FileWriter file = new FileWriter(runtimeJsonPath)) {
                                file.write(jsonObject.toJSONString());
                                file.flush();

                            } catch (IOException e) {
                                throw new RestAssuredMigrationException("Error occurred while writing to the file", e);
                            }
                            break;
                        }
                        k += 1;
                    }
                    break;
                }
                i += 1;
            }
        } catch (Exception e) {
            throw new RestAssuredMigrationException("Error occurred while removing subscription data", e);
        }
        return "";
    }

}
