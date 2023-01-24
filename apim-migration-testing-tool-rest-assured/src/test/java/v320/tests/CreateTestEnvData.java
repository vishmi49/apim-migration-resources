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
import commons.testdata.AdminData;
import commons.testdata.AuthenticationData;
import commons.testdata.PublisherData;
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
	    String restAPI_ADPRestAPIID;
	    String restAPI_inlineDoc_documentId;
	    String[] mediationsPoliciesToUpdate = new String[3];
	    
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
	        authenticationObject.setPayload(AuthenticationData.getPayloadOfOwner(publisherUserassword)); // get admin user from excel
	        authenticationObject.setUsername(publisherUsername);
	        authenticationObject.setUserpassword(publisherUserassword);
	        authenticationObject.setScopes(publisherAuthScope);
	        publisherAccesstoken = new Authentication(authenticationObject).getAccessToken();
	        
	        
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
	    
	    @Test(priority=9)
		public void createRestAPI() throws Exception {  
	    	Response response = publisherApis.createApi(PublisherData.getAPIADPRestAPIJsonPayload(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			restAPI_ADPRestAPIID = response.jsonPath().get("id");
			log("Created Rest API");
		}
	    
	    @Test(priority=10)
		public void addNewInlineDocument() throws Exception {  
	    	Response response = publisherApis.addNewDocToApi(restAPI_ADPRestAPIID,PublisherData.getADPInlineDocPayload(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			//log(response.body().asPrettyString());
			restAPI_inlineDoc_documentId = response.jsonPath().get("documentId");
			log("Added new inline doc type");
			
			String content = "<p>ADP inline doc content</p>";
			Response response2 = publisherApis.addContentOfDocOfApi(restAPI_ADPRestAPIID,restAPI_inlineDoc_documentId,content);
			if(response2.statusCode() != 201) {
				log(response2.body().asPrettyString());
			}
			log("Added inline content");
		}
	    @Test(priority=11)
		public void addMarkdownDocument() throws Exception {  
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
		}
	    @Test(priority=12)
		public void addPublicForumURLDocument() throws Exception {  
	    	Response response = publisherApis.addNewDocToApi(restAPI_ADPRestAPIID,PublisherData.getADPForumURLDocPayload(), false);

			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			log("Added public forum URL Document");
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
			
			publisherApis.uploadContentOfDocOfApi(restAPI_ADPRestAPIID,restAPI_fileDoc_documentId,"./src/test/resources/testdata/adp-file-doc-content.txt");
	    }
		
	    @Test(priority=14)
		public void getMediationPolicies() throws Exception {  
	    	
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

			log("Get Mediation Policies");	
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
	    }
	    
	    @Test(priority=16)
		public void createNewRestAPIVersion() throws Exception {  
	    	Response response = publisherApis.createNewApiVersion(restAPI_ADPRestAPIID, "2.0.0", false);
	    	
			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
			log("Created new version");
	    }
	    
	    @Test(priority=17)
		public void publishRestAPI() throws Exception { 
	    	Publisher.Apis pubApis = new Publisher.Apis(baseURL.toString(), publisherAccesstoken, ApimVersions.APIM_3_2);
	    	Response response = pubApis.changeApiStatus(restAPI_ADPRestAPIID,"Publish");
	    	
			if(response.statusCode() != 201) {
				log(response.body().asPrettyString());
			}
			Assert.assertEquals("Invalid status code",201, response.statusCode());
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
