package commons.testdata;

import java.io.IOException;

public class SOAPData {
	
	/* for more than one permissions add with coma seperated
	 * @permissions : "/permission/admin/manage/api/create,/permission/admin/login"
	 */
	public static String getAddRoleXML(String roleName, String permissions) {
		String soapEnvelop = """
			<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.ws.um.carbon.wso2.org" xmlns:xsd="http://dao.service.ws.um.carbon.wso2.org/xsd">
			   <soapenv:Header/>
			   <soapenv:Body>
			   	<ser:addRole>
			         <!--Optional:-->
			         <ser:roleName>%s</ser:roleName>
			         <!--Zero or more repetitions:-->
						%s
			      </ser:addRole>
			   </soapenv:Body>
			</soapenv:Envelope>
			     """;
		String permissionTag = """
					<ser:permissions>
			            <!--Optional:-->
			            <xsd:action>ui.execute</xsd:action>
			            <!--Optional:-->
			             <xsd:resourceId>%s</xsd:resourceId>
			         </ser:permissions>
				""";
		String permissionsStr = "";
		
		for(String permission : permissions.split(",")) {
			permissionsStr = permissionsStr + permissionTag.formatted(permission);
		}
		return soapEnvelop.formatted(roleName,permissionsStr);
	}
	
	public static String getAaddUserSOAPXML(String userName, String password, String roles) {
		String soapEnvelop = """
			<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.ws.um.carbon.wso2.org" xmlns:xsd="http://common.mgt.user.carbon.wso2.org/xsd">
			   <soapenv:Header/>
			   <soapenv:Body>
			      <ser:addUser>
			         <!--Optional:-->
			         <ser:userName>%s</ser:userName>
			         <!--Optional:-->
			         <ser:credential>%s</ser:credential>
			         <!--Zero or more repetitions:-->
			         %s
			         <!--Zero or more repetitions:-->
			         <ser:claims>
			            <!--Optional:-->
			            <xsd:claimURI>http://wso2.org/claims/country</xsd:claimURI>
			            <!--Optional:-->
			            <xsd:value>England</xsd:value>
			         </ser:claims>
			         <ser:claims>
			            <!--Optional:-->
			            <xsd:claimURI>http://wso2.org/claims/organization</xsd:claimURI>
			            <!--Optional:-->
			            <xsd:value>Intuit</xsd:value>
			         </ser:claims>
			         <!--Optional:-->
			         <ser:profileName>creator2_Test_ProfileName</ser:profileName>
			         <!--Optional:-->
			         <ser:requirePasswordChange>false</ser:requirePasswordChange>
			      </ser:addUser>
			   </soapenv:Body>
			</soapenv:Envelope>
				""";
		
		String rolesStr = "";
		for(String role : roles.split(",")) {
			rolesStr = rolesStr + "<ser:roleList>%s</ser:roleList>".formatted(role);
		}
		return soapEnvelop.formatted(userName,password,rolesStr);
	}
	
	public static void main(String arg[]) throws IOException {
		//System.out.print(getAddRoleXML("Bathiya","/permission/admin/login,/permission/admin/manage/api/create"));
		System.out.print(getAaddUserSOAPXML("adp_crt_user","adp_crt_user","ADP_CREATOR,ADP_COMMON,Internal/creator"));
	}

}
