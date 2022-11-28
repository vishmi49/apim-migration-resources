package soapapi.remoteuserstore;

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

public class RemoteUserStore{
    
    String endPointUrl="testing";
    private static Logger logger = LogManager.getLogger(RemoteUserStore.class);
    
    public RemoteUserStore(String accessToken, URI baseURL) {
        this.endPointUrl = baseURL.toString() + "services/RemoteUserStoreManagerService";
    }
    
    public void authenticate(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
             
            Response response=RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
                    .header("SOAPAction","urn:authenticate")
                    .contentType("text/xml; charset=UTF-8;")
                    .body(getXMLPayload(tenantXmlFileName))
                    .when()
                    .post(endPointUrl);
             
            XmlPath jsXpath= new XmlPath(response.asString());
            String rate=jsXpath.getString("GetConversionRateResult");
            logger.info("[AUTHENTICATION]: "+rate);
            
    }
    
    public void addRole(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
             
            Response response=RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
                    .header("SOAPAction","urn:addRole")
                    .contentType("text/xml; charset=UTF-8;")
                    .body(getXMLPayload(tenantXmlFileName))
                    .when()
                    .post(endPointUrl);
             
//            XmlPath jsXpath= new XmlPath(response.asString());
//            String rate=jsXpath.getString("GetConversionRateResult");
//            logger.info("[ADD ROLE]: "+rate);
    }
    
    public void addUser(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
             
            Response response=RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
                    .header("SOAPAction","urn:addUser")
                    .contentType("text/xml; charset=UTF-8;")
                    .body(getXMLPayload(tenantXmlFileName))
                    .when()
                    .post(endPointUrl);
             
//            XmlPath jsXpath= new XmlPath(response.asString());
//            String rate=jsXpath.getString("GetConversionRateResult");
//            logger.info("[ADD USER]: "+rate);
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
