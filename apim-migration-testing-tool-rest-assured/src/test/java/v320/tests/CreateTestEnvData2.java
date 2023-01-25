//package v320.tests;
//
//import java.util.Map;
//
//<<<<<<< HEAD
//import org.json.simple.JSONObject;
//=======
//>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
//import org.testng.annotations.Test;
//
//import ataf.actions.BaseTest;
//import ataf.utils.ExcelReader;
//<<<<<<< HEAD
//import restapi.Authentication;
//import restapi.TenantAdmin;
//=======
//import io.restassured.response.Response;
//import restapi.ApimVersions;
//import restapi.Authentication;
//import restapi.TenantAdmin;
//import restapi.admin.Admin;
//import restapi.admin.Admin.AdvancedPolicyCollection;
//>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
//
//public class CreateTestEnvData2 extends BaseTest{
//	   String accessToken;
//	    //private static Logger logger = LogManager.getLogger(DataPopulations.class);
//<<<<<<< HEAD
//	   JSONObject jsonObject = new JSONObject();
//=======
//>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
//
//	    TenantAdmin tenantAdmin;
//	    ExcelReader migrationInputTestDataExcel;
//	    Map<String,String> tenantData;
//	    
////	    @BeforeClass
////	    @Parameters({"testTenant"})
////	    public void getTenantInfo(String testTenant) throws Exception {
////			migrationInputTestDataExcel = new ExcelReader("MigrationInputTestData.xlsx");
////			Map<String,String> tenantData = migrationInputTestDataExcel.getDataRowMapOf("tenants", "domain", testTenant);
////			
////	    	tenantAdmin = new TenantAdmin(tenantData.get("admin_username"), tenantData.get("admin_password"), testTenant);
////	    	System.out.println("+++++++++++++++++=" + testTenant);
////	    }
//
//	    @Test
//	    public void addRole() throws Exception {
//<<<<<<< HEAD
//	    	JSONObject jsonObject2 = new JSONObject();
//	    	jsonObject2.put("addRole1", "value");
//	    	jsonObject2.put("addRole2", "value");
//	    	
//	    	jsonObject.put("parent", jsonObject2);
//	    	
//	    	System.out.print(jsonObject.toString());
//	    	
//=======
//	    	System.out.println("-------baseurl-------------" + baseURL);
//	    	
//	    	authenticationObject.setUsername("admin");
//	        authenticationObject.setUserpassword("admin");
//	        authenticationObject.setScopes("apim:admin apim:tier_view");
//	        Authentication authentication = new Authentication(authenticationObject);
//	        accessToken = authentication.getAccessToken();
//	        System.out.println("-------accessToken-------------" + accessToken);
//	        
//	    	//AdvancedPolicyCollection ap = new AdvancedPolicyCollection(accessToken,ApimVersions.APIM_3_2);
//	    	//Response response = ap.getAllAdvancedThrottlingPolicies();
//	        
//	       // System.out.println(response.getStatusCode());
//	       // System.out.println(response.getBody().asPrettyString());
////	        https://localhost:9443/api/am/admin/v1/policies/policies/advanced 
////	        https://localhost:9443/api/am/admin/v1/throttling/policies/advanced
////	        https://localhost:9443/api/am/admin/v1/throttling/policies/advanced
//	        
//	        
//	        //Admin.SystemScopes asc = new Admin.SystemScopes("",ApimVersions.APIM_3_2);
//>>>>>>> 1ab02b9d458a3b8421f5e4f4a2b2c35ca3568f88
//
//	    	
//	    }
//}
