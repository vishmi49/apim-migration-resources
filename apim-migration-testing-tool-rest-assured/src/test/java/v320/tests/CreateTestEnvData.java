package v320.tests;

import java.util.Map;

import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ataf.actions.BaseTest;
import ataf.utils.ExcelReader;
import commons.testdata.AdminData;
import commons.testdata.SOAPData;
import io.restassured.response.Response;
import restapi.ApimVersions;
import restapi.Authentication;
import restapi.TenantAdmin;
import restapi.admin.Admin;
import restapi.publisher.Publisher;
import soapapi.remoteuserstore.RemoteUserStore;

public class CreateTestEnvData extends BaseTest{

	    TenantAdmin tenantAdmin;
	    RemoteUserStore rUserStore;
	    ExcelReader migrationInputTestDataExcel;
	    Map<String,String> tenantData;
	    String adminAccesstoken;
	    Admin.SystemScopes adminSystemScopesRestAPI;
	    Admin.ApiCategoryIndividual apiCategoryIndividual;
	    Admin.AdvancedPolicyCollection advancedPolicyCollection;
	    Admin.ApplicationPolicyCollection applicationPolicyCollection;
	    Admin.SubscriptionPolicyCollection subscriptionPolicyCollection;
	    Publisher.Scopes publisherScopes;
	    
	    @BeforeClass
	    @Parameters({"testTenant"})
	    public void getTenantInfo(String testTenant) throws Exception {
			migrationInputTestDataExcel = new ExcelReader("MigrationInputTestData.xlsx");
			Map<String,String> tenantData = migrationInputTestDataExcel.getDataRowMapOf("tenants", "domain", testTenant);
			
	    	tenantAdmin = new TenantAdmin(tenantData.get("admin_username"), tenantData.get("admin_password"), testTenant);	    	
	    	rUserStore = new RemoteUserStore(baseURL);
	    	
	    	// Admin authentication
	        String scp = "apim:api_view apim:api_publish apim:api_create apim:subscribe apim:subscription_view apim:document_create apim:comment_write apim:admin apim:tier_manage apim:scope_manage apim:admin_operations apim:shared_scope_manage apim:mediation_policy_create apim:api_import_export apim:mediation_policy_view apim:app_manage apim:sub_manage";
	        authenticationObject.setScopes(scp);
	        Authentication authentication = new Authentication(authenticationObject);
	        adminAccesstoken = authentication.getAccessToken();
	        
	        adminSystemScopesRestAPI = new Admin.SystemScopes(baseURL.toString(),adminAccesstoken,ApimVersions.APIM_3_2);
	        apiCategoryIndividual = new Admin.ApiCategoryIndividual(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        advancedPolicyCollection = new Admin.AdvancedPolicyCollection(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        applicationPolicyCollection = new Admin.ApplicationPolicyCollection(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        subscriptionPolicyCollection = new Admin.SubscriptionPolicyCollection(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        
	        // Publisher apis
	        publisherScopes = new Publisher.Scopes(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        
	    	log("[CreateTestEnvData] Creating data for tenant " + testTenant);
	    }

//		@Test(dataProvider="getRolesData", priority=1)
//		public void addRole(String roleName, String permissions) throws Exception {
//			
//			Response response = rUserStore.addRole(SOAPData.getAddRoleXML(roleName, permissions), tenantAdmin,false);
//			
//			String assertMessage = "Invalid status code";
//			if(response.statusCode() != 202) {
//				log(response.body().asPrettyString());
//				assertMessage = response.body().xmlPath().getString("Envelope.Body.Fault.faultstring");
//			}
//			Assert.assertEquals(assertMessage,202, response.statusCode());
//			log("Added Role : " + roleName);
//		}
//		
//	    @Test(dataProvider="getUsersData", priority=2)
//		public void addUser(String userName, String password, String roles) throws Exception {
//
//			Response response = rUserStore.addUser(SOAPData.getAaddUserSOAPXML(userName,password,roles), tenantAdmin,false);
//			String assertMessage = "Invalid status code";
//			if(response.statusCode() != 202) {
//				log(response.body().asPrettyString());
//				assertMessage = response.body().xmlPath().getString("Envelope.Body.Fault.faultstring");
//			}
//			Assert.assertEquals(assertMessage,202, response.statusCode());
//			log("Added user : " + userName);
//		}
	    
//	    @Test(priority=3)
//		public void addRoleAliasMapping() throws Exception {    	
//	    	Response response = adminSystemScopesRestAPI.addNewRoleAlias(AdminData.getADPRoleAliesesPayload(), false);
//
//			if(response.statusCode() != 200) {
//				log(response.body().asPrettyString());
//			}
//			Assert.assertEquals("Invalid status code",200, response.statusCode());
//			log("Added scope mappings");
//		}
	    
//	    @Test(dataProvider="getApiCategories", priority=4)
//		public void addApiCategory(String name, String description) throws Exception {  
//	    	Response response = apiCategoryIndividual.addApiCategories(AdminData.getAPICategoriesJsonPayload(name, description), false);
//
//			if(response.statusCode() != 201) {
//				log(response.body().asPrettyString());
//			}
//			Assert.assertEquals("Invalid status code",201, response.statusCode());
//			log("Added scope mappings");
//	    	//log(response.body().asPrettyString()); // get responseID from response and append to log file
//		}
	    
//	    @Test(priority=5)
//		public void addAdvancedThrottlingPolicy() throws Exception {  
//	    	Response response = advancedPolicyCollection.addAdvancedThrottlingPolicy(AdminData.getAdvancedThrotellingPolicyADP10PerMin(), false);
//
//			if(response.statusCode() != 201) {
//				log(response.body().asPrettyString());
//			}
//			Assert.assertEquals("Invalid status code",201, response.statusCode());
//			//log(response.body().asPrettyString()); // get policyId from response and append to log file
//			log("Added Advanced Throttling Policy");
//		}
	    
//	    @Test(priority=6)
//		public void addApplicationThrottlingPolicy() throws Exception {  
//	    	Response response = applicationPolicyCollection.addApplicationThrottlingPolicies(AdminData.getApplicationThrotellingPolicyADP5PerMin(), false);
//
//			if(response.statusCode() != 201) {
//				log(response.body().asPrettyString());
//			}
//			Assert.assertEquals("Invalid status code",201, response.statusCode());
//			//log(response.body().asPrettyString()); // get policyId from response and append to log file
//			log("Added Application Throttling Policy");
//		}
	    
//	    @Test(priority=7)
//		public void addApplicationThrottlingPolicy() throws Exception {  
//	    	Response response = subscriptionPolicyCollection.addSubscriptionThrottlingPolicy(AdminData.getSubscriptionThrottlingPolicyADPBrass(), false);
//
//			if(response.statusCode() != 201) {
//				log(response.body().asPrettyString());
//			}
//			Assert.assertEquals("Invalid status code",201, response.statusCode());
//			log(response.body().asPrettyString()); // get policyId from response and append to log file
//			log("Added Application Throttling Policy");
//		}
	    
	    @Test(dataProvider="getScopesData", priority=8)
		public void addSharedScopes(String name, String displayName, String description, String bindings) throws Exception {  
	    	Response response = publisherScopes.addNewSharedScopes(AdminData.getScopeJsonPayload(name, displayName, description, bindings), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			log(response.body().asPrettyString()); // get policyId from response and append to log file
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			
			log("Added Application Throttling Policy");
		}
		
		@DataProvider
		public Object[][] getRolesData(){
			return migrationInputTestDataExcel.getDataTable("roles");
			
		}
		@DataProvider
		public Object[][] getUsersData(){
			return migrationInputTestDataExcel.getDataTable("users");
			
		}
		@DataProvider
		public Object[][] getApiCategories(){
			return migrationInputTestDataExcel.getDataTable("api-categories");
		}
		@DataProvider
		public Object[][] getScopesData(){
			return migrationInputTestDataExcel.getDataTable("scopes");
		}
}
