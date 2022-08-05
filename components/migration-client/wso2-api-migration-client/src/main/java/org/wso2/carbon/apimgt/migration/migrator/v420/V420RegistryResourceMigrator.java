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
                    JsonObject currentConfigJsonObject = (JsonObject) new JsonParser().parse(currentConfig);
                    if (currentConfigJsonObject.has("SelfSignUp")) {
                        JsonObject currentSelfSignUp = (JsonObject) currentConfigJsonObject.get("SelfSignUp");
                        JsonArray currentSignUpRoles = (JsonArray) currentSelfSignUp.get("SignUpRoles");
                        Iterator<JsonElement> currentSignUpRolesIterator = currentSignUpRoles.iterator();
                        while (currentSignUpRolesIterator.hasNext()) {
                            signUpRoles.add(currentSignUpRolesIterator.next().getAsString());
                        }
                        currentConfigJsonObject.remove("SelfSignUp");
                    }

                    Resource resource = registry.get("/apimgt/applicationdata/sign-up-config.xml");
                    OMElement element = AXIOMUtil.stringToOM(
                            new String((byte[]) resource.getContent(), Charset.defaultCharset()));
                    JsonObject selfSignUpJsonObject = new JsonObject();
                    String signUpDomain = element.getFirstChildWithName(new QName("SignUpDomain")).getText();
                    OMElement rolesElement = element.getFirstChildWithName(new QName("SignUpRoles"));
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

                    selfSignUpJsonObject.add("SignUpRoles", new Gson().toJsonTree(signUpRoles).getAsJsonArray());
                    currentConfigJsonObject.add("SelfSignUp", selfSignUpJsonObject);

                    // Prettify the tenant-conf
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String formattedTenantConf = gson.toJson(currentConfigJsonObject);
                    ServiceReferenceHolder.getInstance().getApimConfigService()
                            .updateTenantConfig(tenantDomain, formattedTenantConf);

                    registry.delete("/apimgt/applicationdata/sign-up-config.xml");
                }
                log.info("WSO2 API-M Migration Task : Completed data migration of Self Signup Configuration for tenant "
                        + tenantId + '(' + tenantDomain + ')');
            } catch (APIManagementException e) {
                throw new APIMigrationException(
                        "WSO2 API-M Migration Task : Error occurred while migrating Self Signup Configuration for tenant "
                                + tenantId + '(' + tenantDomain + ')', e);
            } catch (RegistryException e) {
                throw new APIMigrationException(
                        "WSO2 API-M Migration Task : Error occurred while accessing the registry ", e);
            } catch (XMLStreamException e) {
                throw new APIMigrationException(
                        "WSO2 API-M Migration Task : Error occurred while converting the XML string to OMElement", e);
            } finally {
                if (isTenantFlowStarted) {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            }
            log.info("WSO2 API-M Migration Task : Completed registry data migration for tenant " + tenantId + '('
                    + tenantDomain + ')');
        }
        log.info("WSO2 API-M Migration Task : Registry data migration is done for API Manager "
                + Constants.VERSION_4_2_0);
    }
}
