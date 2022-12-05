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
 * <p>
 * This class implemented to define the scopes
 */
public abstract class Scopes {

    public static final String API_PUBLISH = "apim:api_publish";
    public static final String API_IMPORT_EXPORT = "apim:api_import_export";
    public static final String API_VIEW = "apim:api_view";
    public static final String API_CREATE = "apim:api_create";
    public static final String API_PRODUCT_IMPORT_EXPORT = "apim:api_product_import_export";
    public static final String API_MANAGE = "apim:api_manage";
    public static final String SUBSCRIPTION_VIEW = "apim:subscription_view";
    public static final String SUBSCRIPTION_BLOCK = "apim:subscription_block";
    public static final String CLIENT_CERTIFICAE_VIEW = "apim:client_certificates_view";
    public static final String SHARED_SCOPE_MANAGE = "apim:shared_scope_manage";
    public static final String PUBLISHER_SETTINGS = "apim:publisher_settings";
    public static final String DOCUMENT_MANAGE = "apim:document_manage";
    public static final String SUBSCRIBE = "apim:subscribe";
    public static final String APP_MANAGE = "apim:app_manage";
    public static final String APP_IMPORT_EXPORT = "apim:app_import_export";


    //"apim:api_publish apim:api_admin apim:api_import_export apim:api_view apim:api_create apim:api_product_import_export apim:api_product_create"


}
