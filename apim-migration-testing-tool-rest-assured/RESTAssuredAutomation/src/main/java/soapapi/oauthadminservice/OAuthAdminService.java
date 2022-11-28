package soapapi.oauthadminservice;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import restapi.TenantAdmin;
import soapapi.tenantmanagemant.TenantManagement;

public class OAuthAdminService {
    
    String endPointUrl="testing";
    private static Logger logger = LogManager.getLogger(TenantManagement.class);
      
    public OAuthAdminService(String accessToken, URI baseURL) {
        this.endPointUrl = baseURL.toString() + "/OAuthAdminService";
    }
    
    public void revokeApplicationData(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
             
            Response response=RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
                    .header("SOAPAction","urn:getAllOAuthApplicationData")
                    .contentType("text/xml; charset=UTF-8;")
                    .body(getXMLPayload(tenantXmlFileName))
                    .when()
                    .post("https://kavindudi:9443/services/OAuthAdminService.OAuthAdminServiceHttpsSoap11Endpoint?wsdl");
             
            XmlPath jsXpath= new XmlPath(response.asString());
            String rate=jsXpath.getString("GetConversionRateResult");
            logger.info("[REVOKE APPLICATION DATA]: "+rate);
    }
    
    private String getXMLPayload(String tenantXmlFileName){
        
        byte[] payloadplj1;
        String payloadpls1="";
   
        try {
           payloadplj1 = Files.readAllBytes(Paths.get("./src/test/payloads/"+tenantXmlFileName));
           payloadpls1 = new String(payloadplj1);

          } catch (Exception e) {
          }
        
        return payloadpls1;
         
   }

}
