/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.apimgt.migration.migrator.v420;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.apimgt.migration.migrator.commonMigrators.RegistryResourceMigrator;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class V420RegistryResourceMigrator extends RegistryResourceMigrator {
    private static final Log log = LogFactory.getLog(V420RegistryResourceMigrator.class);
    List<Tenant> tenants;

    public V420RegistryResourceMigrator(String rxtDir) throws UserStoreException {
        super(rxtDir);
        tenants = loadTenants();
    }

    public void migrate() throws APIMigrationException {
        super.migrate();
        registryDataPopulation();
    }

    private void registryDataPopulation() throws APIMigrationException {
        log.info("WSO2 API-M Migration Task : Starting registry data migration for API Manager "
                + Constants.VERSION_4_2_0);

        boolean isError = false;
        boolean isTenantFlowStarted = false;
        for (Tenant tenant : tenants) {
            String tenantDomain = tenant.getDomain();
            int tenantId = tenant.getId();
            log.info("WSO2 API-M Migration Task : Starting registry data migration for tenant " + tenantId + '('
                    + tenantDomain + ')');

            try {
                PrivilegedCarbonContext.startTenantFlow();
                isTenantFlowStarted = true;
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenantId, true);
                ServiceHolder.getTenantRegLoader().loadTenantRegistry(tenantId);
                UserRegistry registry = ServiceHolder.getRegistryService().getGovernanceSystemRegistry(tenantId);

                log.info("WSO2 API-M Migration Task : Starting data migration of Self Signup Configuration for tenant "
                        + tenantId + '(' + tenantDomain + ')');
                if (registry.resourceExists("/apimgt/applicationdata/sign-up-config.xml")) {
                    HashSet<String> signUpRoles = new HashSet<String>();
                    String currentConfig = ServiceReferenceHolder.getInstance().getApimConfigService()
                            .getTenantConfig(tenantDomain);
                    log.info("Current Config of tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                    log.info(currentConfig);

                    JsonObject currentConfigJsonObject = (JsonObject) new JsonParser().parse(currentConfig);
                    if (currentConfigJsonObject.has(Constants.SELF_SIGNUP)) {
                        JsonObject currentSelfSignUp = (JsonObject) currentConfigJsonObject.get(Constants.SELF_SIGNUP);
                        JsonArray currentSignUpRoles = (JsonArray) currentSelfSignUp.get(Constants.SELF_SIGNUP_ROLES);
                        Iterator<JsonElement> currentSignUpRolesIterator = currentSignUpRoles.iterator();
                        while (currentSignUpRolesIterator.hasNext()) {
                            signUpRoles.add(currentSignUpRolesIterator.next().getAsString());
                        }
                        currentConfigJsonObject.remove(Constants.SELF_SIGNUP);
                    }

                    Resource resource = registry.get("/apimgt/applicationdata/sign-up-config.xml");
                    OMElement element = AXIOMUtil.stringToOM(
                            new String((byte[]) resource.getContent(), Charset.defaultCharset()));
                    JsonObject selfSignUpJsonObject = new JsonObject();
                    String signUpDomain = element.getFirstChildWithName(new QName("SignUpDomain")).getText();
                    OMElement rolesElement = element.getFirstChildWithName(new QName(Constants.SELF_SIGNUP_ROLES));
                    Iterator roleListIterator = rolesElement.getChildrenWithLocalName("SignUpRole");
                    while (roleListIterator.hasNext()) {
                        OMElement roleElement = (OMElement) roleListIterator.next();
                        boolean isExternalRole = Boolean.parseBoolean(
                                roleElement.getFirstChildWithName(new QName("IsExternalRole")).getText());
                        String roleName = roleElement.getFirstChildWithName(new QName("RoleName")).getText();
                        if (isExternalRole) {
                            signUpRoles.add(signUpDomain + "/" + roleName);
                        } else {
                            signUpRoles.add("Internal/" + roleName);
                        }
                    }

                    selfSignUpJsonObject.add(Constants.SELF_SIGNUP_ROLES,
                            new Gson().toJsonTree(signUpRoles).getAsJsonArray());
                    currentConfigJsonObject.add(Constants.SELF_SIGNUP, selfSignUpJsonObject);

                    // Prettify the tenant-conf
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String formattedTenantConf = gson.toJson(currentConfigJsonObject);
                    ServiceReferenceHolder.getInstance().getApimConfigService()
                            .updateTenantConfig(tenantDomain, formattedTenantConf);

                    // Log the updated tenant config
                    String updatedConfig = ServiceReferenceHolder.getInstance().getApimConfigService()
                            .getTenantConfig(tenantDomain);
                    log.info("Updated Config of tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                    log.info(updatedConfig);

                    registry.delete("/apimgt/applicationdata/sign-up-config.xml");
                }
                log.info("WSO2 API-M Migration Task : Completed data migration of Self Signup Configuration for tenant "
                        + tenantId + '(' + tenantDomain + ')');
            } catch (APIManagementException e) {
                log.error(
                        "WSO2 API-M Migration Task : Error occurred while migrating Self Signup Configuration for tenant "
                                + tenantId + '(' + tenantDomain + ')', e);
                isError = true;
                continue;
            } catch (RegistryException e) {
                log.error(
                        "WSO2 API-M Migration Task : Error occurred while accessing the registry for tenant " + tenantId
                                + '(' + tenantDomain + ')', e);
                isError = true;
                continue;
            } catch (XMLStreamException e) {
                log.error(
                        "WSO2 API-M Migration Task : Error occurred while converting the XML string to OMElement for tenant "
                                + tenantId + '(' + tenantDomain + ')', e);
                isError = true;
                continue;
            } finally {
                if (isTenantFlowStarted) {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            }
            log.info("WSO2 API-M Migration Task : Completed registry data migration for tenant " + tenantId + '('
                    + tenantDomain + ')');
        }
        if (isError) {
            throw new APIMigrationException(
                    "WSO2 API-M Migration Task : Error/s occurred during Registry data migration");
        } else {
            log.info("WSO2 API-M Migration Task : API registry data migration done for all the tenants");
        }
    }
}
