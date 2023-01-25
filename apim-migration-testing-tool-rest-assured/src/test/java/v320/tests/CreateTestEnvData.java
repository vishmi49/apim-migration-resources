package v320.tests;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import ataf.actions.BaseTest;
import ataf.utils.ExcelReader;
<<<<<<< HEAD
import commons.testdata.AuthenticationData;
import commons.testdata.PublisherData;
=======
import commons.testdata.AdminData;
import commons.testdata.AuthenticationData;
import commons.testdata.PublisherData;
import commons.testdata.SOAPData;
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
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
	    String creatorAccesstoken;
	    String publisherAccesstoken;
	    Admin.SystemScopes adminSystemScopesRestAPI;
	    Admin.ApiCategoryIndividual apiCategoryIndividual;
	    Admin.AdvancedPolicyCollection advancedPolicyCollection;
	    Admin.ApplicationPolicyCollection applicationPolicyCollection;
	    Admin.SubscriptionPolicyCollection subscriptionPolicyCollectionAPI;
	    Publisher.Scopes publisherScopesAPI;
	    Publisher.Apis publisherApis;
	    
	    
	    // response Data
<<<<<<< HEAD
	    JSONObject testOutputData = new JSONObject();
=======
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    String restAPI_ADPRestAPIID;
	    String restAPI_inlineDoc_documentId;
	    String[] mediationsPoliciesToUpdate = new String[3];
	    
<<<<<<< HEAD
	    
=======
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    @BeforeClass
	    @Parameters({"testTenant"})
	    public void getTenantInfo(String testTenant) throws Exception {
			migrationInputTestDataExcel = new ExcelReader("MigrationInputTestData.xlsx");
			Map<String,String> tenantData = migrationInputTestDataExcel.getDataRowMapOf("tenants", "domain", testTenant);
			
	    	tenantAdmin = new TenantAdmin(tenantData.get("admin_username"), tenantData.get("admin_password"), testTenant);	    	
	    	rUserStore = new RemoteUserStore(baseURL);
	    	
	    	// Admin authentication
	        String adminUserScope = "apim:api_view apim:api_publish apim:api_create apim:subscribe apim:subscription_view apim:document_create apim:comment_write apim:admin apim:tier_manage apim:scope_manage apim:admin_operations apim:shared_scope_manage apim:mediation_policy_create apim:api_import_export apim:mediation_policy_view apim:app_manage apim:sub_manage";
	        String tenantAdminUser = migrationInputTestDataExcel.getDataRowMapOf("tenants", "domain", testTenant).get("admin_username")+ "@" + testTenant;
	        String tenantAdminPassword = migrationInputTestDataExcel.getDataRowMapOf("tenants", "domain", testTenant).get("admin_password");
	        authenticationObject.setPayload(AuthenticationData.getPayloadOfOwner(tenantAdminUser)); // get admin suer from excel
	        authenticationObject.setUsername(tenantAdminUser);
	        authenticationObject.setUserpassword(tenantAdminPassword);
	        authenticationObject.setScopes(adminUserScope);
	        Authentication adminAuthentication = new Authentication(authenticationObject);
	        adminAccesstoken = adminAuthentication.getAccessToken();
	        
	        // Creator authentication
	        String creatorScope = "apim:api_view apim:api_create apim:comment_write apim:comment_view apim:shared_scope_manage apim:ep_certificates_view apim:ep_certificates_add apim:ep_certificates_update apim:client_certificates_view apim:client_certificates_add apim:client_certificates_update apim:mediation_policy_view apim:mediation_policy_create apim:mediation_policy_manage apim:document_create apim:document_manage";
	        String createUserCellValue = migrationInputTestDataExcel.getCellData("users", 1, 0);
	        Map<String,String> createUserData = migrationInputTestDataExcel.getDataRowMapOf("users", "domain", createUserCellValue);
	        String createUsername = createUserData.get("username")+ "@" + testTenant;
	        String createUserassword = createUserData.get("password");
	        authenticationObject.setPayload(AuthenticationData.getPayloadOfOwner(createUsername));
	        authenticationObject.setPayload(AuthenticationData.getPayloadOfOwner(createUserassword)); // get admin user from excel
	        authenticationObject.setUsername(createUsername);
	        authenticationObject.setUserpassword(createUserassword);
	        authenticationObject.setScopes(creatorScope);
	        creatorAccesstoken = new Authentication(authenticationObject).getAccessToken();
	        
	        // Publisher authentication
	        String publisherAuthScope = "apim:api_view apim:api_publish apim:subscription_view apim:mediation_policy_view apim:client_certificates_view apim:ep_certificates_view apim:comment_view apim:subscription_block";
	        String publisherCellValue = migrationInputTestDataExcel.getCellData("users", 2, 0);
	        Map<String,String> publisherUserData = migrationInputTestDataExcel.getDataRowMapOf("users", "domain", publisherCellValue);
	        String publisherUsername = publisherUserData.get("username")+ "@" + testTenant;
	        String publisherUserassword = publisherUserData.get("password");
	        authenticationObject.setPayload(AuthenticationData.getPayloadOfOwner(publisherUsername));
<<<<<<< HEAD
	        authenticationObject.setPayload(AuthenticationData.getPayloadOfOwner(publisherUserassword));
=======
	        authenticationObject.setPayload(AuthenticationData.getPayloadOfOwner(publisherUserassword)); // get admin user from excel
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	        authenticationObject.setUsername(publisherUsername);
	        authenticationObject.setUserpassword(publisherUserassword);
	        authenticationObject.setScopes(publisherAuthScope);
	        publisherAccesstoken = new Authentication(authenticationObject).getAccessToken();
<<<<<<< HEAD
	        log("======================================================= ::: " + createUsername);
	        log("======================================================= ::: " + publisherUsername);
=======
	        
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	        
	        adminSystemScopesRestAPI = new Admin.SystemScopes(baseURL.toString(),adminAccesstoken,ApimVersions.APIM_3_2);
	        apiCategoryIndividual = new Admin.ApiCategoryIndividual(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        advancedPolicyCollection = new Admin.AdvancedPolicyCollection(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        applicationPolicyCollection = new Admin.ApplicationPolicyCollection(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        subscriptionPolicyCollectionAPI = new Admin.SubscriptionPolicyCollection(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        
	        // Publisher apis
	        publisherScopesAPI = new Publisher.Scopes(baseURL.toString(), adminAccesstoken, ApimVersions.APIM_3_2);
	        publisherApis = new Publisher.Apis(baseURL.toString(), creatorAccesstoken, ApimVersions.APIM_3_2);
	        
	    	log("[CreateTestEnvData] Creating data for tenant " + testTenant);
	    }

<<<<<<< HEAD
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
//	    
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
//	    
//	    JSONArray addApiCategoryJsonArray = new JSONArray();
//	    @Test(dataProvider="getApiCategories", priority=4)
//		public void addApiCategory(String name, String description) throws Exception {  
//	    	Response response = apiCategoryIndividual.addApiCategories(AdminData.getAPICategoriesJsonPayload(name, description), false);
//
//			if(response.statusCode() != 201) {
//				log(response.body().asPrettyString());
//			}
//			Assert.assertEquals("Invalid status code",201, response.statusCode());
//			log("Added scope mappings");
//			
//			// add output data
//	    	addApiCategoryJsonArray.add(response.body().jsonPath().get("$"));
//	    	if(addApiCategoryJsonArray.size()==2) {
//	    		testOutputData.put("addApiCategory", addApiCategoryJsonArray);
//	    	}
//		}
//	    
//	    @Test(priority=5)
//		public void addAdvancedThrottlingPolicy() throws Exception {  
//	    	Response response = advancedPolicyCollection.addAdvancedThrottlingPolicy(AdminData.getAdvancedThrotellingPolicyADP10PerMin(), false);
//
//			if(response.statusCode() != 201) {
//				log(response.body().asPrettyString());
//			}
//			Assert.assertEquals("Invalid status code",201, response.statusCode());
//			log("Added Advanced Throttling Policy");
//			
//			// add output data
//			testOutputData.put("addAdvancedThrottlingPolicy", response.body().jsonPath().get("$"));
//		}
//	    
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
//			
//			// add output data
//			testOutputData.put("addApplicationThrottlingPolicy", response.body().jsonPath().get("$"));
//		}
//	    
//	    @Test(priority=7)
//		public void addSubscriptionThrottlingPolicy() throws Exception {  
//	    	Response response = subscriptionPolicyCollectionAPI.addSubscriptionThrottlingPolicy(AdminData.getSubscriptionThrottlingPolicyADPBrass(), false);
//
//			if(response.statusCode() != 201) {
//				log(response.body().asPrettyString());
//			}
//			Assert.assertEquals("Invalid status code",201, response.statusCode());
//			log("Added Application Throttling Policy");
//			
//			// add output data
//			testOutputData.put("addSubscriptionThrottlingPolicy", response.body().jsonPath().get("$"));
//		}
//	    
//	    JSONArray addSharedScopesJsonArray = new JSONArray();
//	    @Test(dataProvider="getScopesData", priority=8)
//		public void addSharedScopes(String name, String displayName, String description, String bindings) throws Exception {  
//	    	Response response = publisherScopesAPI.addNewSharedScopes(AdminData.getScopeJsonPayload(name, displayName, description, bindings), false);
//
//			if(response.statusCode() != 201) {
//				log(response.body().asPrettyString());
//			}
//			log(response.body().asPrettyString()); // get policyId from response and append to log file
//			Assert.assertEquals("Invalid status code",201, response.statusCode());
//			log("Added Application Throttling Policy");
//			// add output data
//			addSharedScopesJsonArray.add(response.body().jsonPath().get("$"));
//	    	if(addSharedScopesJsonArray.size()==2) {
//	    		testOutputData.put("addSharedScopes", addSharedScopesJsonArray);
//	    		log(testOutputData.toString());
//	    	}
//		}
//	    
=======
		@Test(dataProvider="getRolesData", priority=1)
		public void addRole(String roleName, String permissions) throws Exception {
			
			Response response = rUserStore.addRole(SOAPData.getAddRoleXML(roleName, permissions), tenantAdmin,false);
			
			String assertMessage = "Invalid status code";
			if(response.statusCode() != 202) {
				log(response.body().asPrettyString());
				assertMessage = response.body().xmlPath().getString("Envelope.Body.Fault.faultstring");
			}
			Assert.assertEquals(assertMessage,202, response.statusCode());
			log("Added Role : " + roleName);
		}
		
	    @Test(dataProvider="getUsersData", priority=2)
		public void addUser(String userName, String password, String roles) throws Exception {

			Response response = rUserStore.addUser(SOAPData.getAaddUserSOAPXML(userName,password,roles), tenantAdmin,false);
			String assertMessage = "Invalid status code";
			if(response.statusCode() != 202) {
				log(response.body().asPrettyString());
				assertMessage = response.body().xmlPath().getString("Envelope.Body.Fault.faultstring");
			}
			Assert.assertEquals(assertMessage,202, response.statusCode());
			log("Added user : " + userName);
		}
	    
	    @Test(priority=3)
		public void addRoleAliasMapping() throws Exception {    	
	    	Response response = adminSystemScopesRestAPI.addNewRoleAlias(AdminData.getADPRoleAliesesPayload(), false);

			if(response.statusCode() != 200) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",200, response.statusCode());
			log("Added scope mappings");
		}
	    
	    @Test(dataProvider="getApiCategories", priority=4)
		public void addApiCategory(String name, String description) throws Exception {  
	    	Response response = apiCategoryIndividual.addApiCategories(AdminData.getAPICategoriesJsonPayload(name, description), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			log("Added scope mappings");
	    	//log(response.body().asPrettyString()); // get responseID from response and append to log file
		}
	    
	    @Test(priority=5)
		public void addAdvancedThrottlingPolicy() throws Exception {  
	    	Response response = advancedPolicyCollection.addAdvancedThrottlingPolicy(AdminData.getAdvancedThrotellingPolicyADP10PerMin(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			//log(response.body().asPrettyString()); // get policyId from response and append to log file
			log("Added Advanced Throttling Policy");
		}
	    
	    @Test(priority=6)
		public void addApplicationThrottlingPolicy() throws Exception {  
	    	Response response = applicationPolicyCollection.addApplicationThrottlingPolicies(AdminData.getApplicationThrotellingPolicyADP5PerMin(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			//log(response.body().asPrettyString()); // get policyId from response and append to log file
			log("Added Application Throttling Policy");
		}
	    
	    @Test(priority=7)
		public void addSubscriptionThrottlingPolicy() throws Exception {  
	    	Response response = subscriptionPolicyCollectionAPI.addSubscriptionThrottlingPolicy(AdminData.getSubscriptionThrottlingPolicyADPBrass(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			log(response.body().asPrettyString()); // get policyId from response and append to log file
			log("Added Application Throttling Policy");
		}
	    
	    @Test(dataProvider="getScopesData", priority=8)
		public void addSharedScopes(String name, String displayName, String description, String bindings) throws Exception {  
	    	Response response = publisherScopesAPI.addNewSharedScopes(AdminData.getScopeJsonPayload(name, displayName, description, bindings), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			//log(response.body().asPrettyString()); // get policyId from response and append to log file
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			
			log("Added Application Throttling Policy");
		}
	    
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    @Test(priority=9)
		public void createRestAPI() throws Exception {  
	    	Response response = publisherApis.createApi(PublisherData.getAPIADPRestAPIJsonPayload(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
<<<<<<< HEAD
			//log(response.body().asPrettyString());
			restAPI_ADPRestAPIID = response.jsonPath().get("id");
			log("Created Rest API " + restAPI_ADPRestAPIID);
			
			// add output data
			testOutputData.put("createRestAPI", response.body().jsonPath().get("$"));

		}
	    
	    @Test(priority=10)
		public void addNewInlineDocument() throws Exception { 
=======
			restAPI_ADPRestAPIID = response.jsonPath().get("id");
			log("Created Rest API");
		}
	    
	    @Test(priority=10)
		public void addNewInlineDocument() throws Exception {  
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    	Response response = publisherApis.addNewDocToApi(restAPI_ADPRestAPIID,PublisherData.getADPInlineDocPayload(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
<<<<<<< HEAD
=======
			//log(response.body().asPrettyString());
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
			restAPI_inlineDoc_documentId = response.jsonPath().get("documentId");
			log("Added new inline doc type");
			
			String content = "<p>ADP inline doc content</p>";
			Response response2 = publisherApis.addContentOfDocOfApi(restAPI_ADPRestAPIID,restAPI_inlineDoc_documentId,content);
			if(response2.statusCode() != 201) {
				log(response2.body().asPrettyString());
			}
			log("Added inline content");
<<<<<<< HEAD
			
			// add output data
			testOutputData.put("addNewInlineDocument", response2.body().jsonPath().get("$"));
		}
	    @Test(priority=11)
		public void addMarkdownDocument() throws Exception {  
	    	
=======
		}
	    @Test(priority=11)
		public void addMarkdownDocument() throws Exception {  
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    	Response response = publisherApis.addNewDocToApi(restAPI_ADPRestAPIID,PublisherData.getADPMarkdownDocPayload(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			//log(response.body().asPrettyString());
			String restAPI_markdownDoc_documentId = response.jsonPath().get("documentId");
			log("Added markdown doc type");
			
			String content = "#ADP markdown doc content";
			Response response2 = publisherApis.addContentOfDocOfApi(restAPI_ADPRestAPIID,restAPI_markdownDoc_documentId,content);
			if(response2.statusCode() != 201) {
				log(response2.body().asPrettyString());
			}
			log("Added markdown content");
<<<<<<< HEAD
			
			// add output data
			testOutputData.put("addMarkdownDocument", response2.body().jsonPath().get("$"));
=======
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
		}
	    @Test(priority=12)
		public void addPublicForumURLDocument() throws Exception {  
	    	Response response = publisherApis.addNewDocToApi(restAPI_ADPRestAPIID,PublisherData.getADPForumURLDocPayload(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			log("Added public forum URL Document");
<<<<<<< HEAD
			
			// add output data
			testOutputData.put("addPublicForumURLDocument", response.body().jsonPath().get("$"));
=======
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    }
	    @Test(priority=13)
		public void addOtherFileDocument() throws Exception {  
	    	Response response = publisherApis.addNewDocToApi(restAPI_ADPRestAPIID,PublisherData.getADPOtherFileDocPayload(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			String restAPI_fileDoc_documentId = response.jsonPath().get("documentId");
			log("Added File Document");
			
<<<<<<< HEAD
			Response response2 = publisherApis.uploadContentOfDocOfApi(restAPI_ADPRestAPIID,restAPI_fileDoc_documentId,"./src/test/resources/testdata/adp-file-doc-content.txt");
			// add output data
			testOutputData.put("addOtherFileDocument", response2.body().jsonPath().get("$"));
=======
			publisherApis.uploadContentOfDocOfApi(restAPI_ADPRestAPIID,restAPI_fileDoc_documentId,"./src/test/resources/testdata/adp-file-doc-content.txt");
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    }
		
	    @Test(priority=14)
		public void getMediationPolicies() throws Exception {  
<<<<<<< HEAD

=======
	    	
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    	Response response = publisherApis.addApiSpecificMediationPolicy(restAPI_ADPRestAPIID,"./src/test/resources/testdata/log_in_message.xml");

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			mediationsPoliciesToUpdate[0] = new JSONObject(response.jsonPath().getJsonObject("$")).toString();
			
			Response response2 = publisherApis.getAllMediationPolicies();
			Assert.assertEquals("Invalid status code while getting all mediation policies",201, response.statusCode());
			mediationsPoliciesToUpdate[1] = new JSONObject(response2.jsonPath().getJsonObject("list[11]")).toString();
			mediationsPoliciesToUpdate[2] = new JSONObject(response2.jsonPath().getJsonObject("list[15]")).toString();

<<<<<<< HEAD
			log("Get Mediation Policies");
			
			// add output data
			testOutputData.put("getMediationPolicies", response2.body().jsonPath().get("$"));
=======
			log("Get Mediation Policies");	
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    }
	    
	    @Test(priority=15)
		public void updateRestAPI() throws Exception {  
	    	String payload = PublisherData.getADPUpdateAPIPayload(mediationsPoliciesToUpdate[0], mediationsPoliciesToUpdate[1], mediationsPoliciesToUpdate[2]);
	    	Response response = publisherApis.updateApi("application/json",restAPI_ADPRestAPIID,payload);
	    	
			if(response.statusCode() != 200) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",200, response.statusCode());
			log("Updated API with mediations policies");
<<<<<<< HEAD
			
			// add output data
			testOutputData.put("updateRestAPI", response.body().jsonPath().get("$"));
			//log(testOutputData.toString());
=======
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    }
	    
	    @Test(priority=16)
		public void createNewRestAPIVersion() throws Exception {  
	    	Response response = publisherApis.createNewApiVersion(restAPI_ADPRestAPIID, "2.0.0", false);
	    	
			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			log("Created new version");
<<<<<<< HEAD
			
			// add output data
			testOutputData.put("createNewRestAPIVersion", response.body().jsonPath().get("$"));
			log(testOutputData.toString());
=======
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
	    }
	    
	    @Test(priority=17)
		public void publishRestAPI() throws Exception { 
	    	Publisher.Apis pubApis = new Publisher.Apis(baseURL.toString(), publisherAccesstoken, ApimVersions.APIM_3_2);
	    	Response response = pubApis.changeApiStatus(restAPI_ADPRestAPIID,"Publish");
	    	
			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
<<<<<<< HEAD
			log(response.body().toString());
=======
>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
			log("Published API");
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
