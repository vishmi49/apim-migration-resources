package v320.tests;

import java.util.Map;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import ataf.actions.BaseTest;
import ataf.utils.ExcelReader;
import restapi.Authentication;
import restapi.TenantAdmin;

public class CreateTestEnvData2 extends BaseTest{
	   String accessToken;
	    //private static Logger logger = LogManager.getLogger(DataPopulations.class);
	   JSONObject jsonObject = new JSONObject();

	    TenantAdmin tenantAdmin;
	    ExcelReader migrationInputTestDataExcel;
	    Map<String,String> tenantData;
	    
//	    @BeforeClass
//	    @Parameters({"testTenant"})
//	    public void getTenantInfo(String testTenant) throws Exception {
//			migrationInputTestDataExcel = new ExcelReader("MigrationInputTestData.xlsx");
//			Map<String,String> tenantData = migrationInputTestDataExcel.getDataRowMapOf("tenants", "domain", testTenant);
//			
//	    	tenantAdmin = new TenantAdmin(tenantData.get("admin_username"), tenantData.get("admin_password"), testTenant);
//	    	System.out.println("+++++++++++++++++=" + testTenant);
//	    }

	    @Test
	    public void addRole() throws Exception {
	    	JSONObject jsonObject2 = new JSONObject();
	    	jsonObject2.put("addRole1", "value");
	    	jsonObject2.put("addRole2", "value");
	    	
	    	jsonObject.put("parent", jsonObject2);
	    	
	    	System.out.print(jsonObject.toString());
	    	

	    	
	    }
}
