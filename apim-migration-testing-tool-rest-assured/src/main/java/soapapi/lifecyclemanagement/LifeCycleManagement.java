package soapapi.lifecyclemanagement;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import ataf.actions.BaseTest;
import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import restapi.TenantAdmin;
import soapapi.remoteuserstore.RemoteUserStore;

public class LifeCycleManagement {
    
    String endPointUrl="testing";
    private static Logger logger = LogManager.getLogger(LifeCycleManagement.class);
    
    public LifeCycleManagement(String accessToken, URI baseURL) {
        this.endPointUrl = baseURL.toString() + "services/LifeCycleManagementService";
    }
   
  public void createLifecycle(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
           
          Response response=RestAssured.given()
                  .relaxedHTTPSValidation()
                  .auth()
                  .basic("admin", "admin")
                  .header("SOAPAction","urn:createLifecycle")
                  .contentType("text/xml; charset=UTF-8;")
                  .body(getXMLPayload(tenantXmlFileName))
                  .when()
                  .post(endPointUrl);
           
          XmlPath jsXpath= new XmlPath(response.asString());
          String rate=jsXpath.getString("GetConversionRateResult");
          logger.info("[CREATE LIFECYCLE]: "+rate);
  }
  
  public void changeLifecycle(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
           
          Response response=RestAssured.given()
                  .relaxedHTTPSValidation()
                  .auth()
                  .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
                  .header("SOAPAction","urn:updateLifecycle")
                  .contentType("text/xml; charset=UTF-8;")
                  .body(getXMLPayload(tenantXmlFileName))
                  .when()
                  .post(endPointUrl);
           
          XmlPath jsXpath= new XmlPath(response.asString());
          String rate=jsXpath.getString("GetConversionRateResult");
          logger.info("[CHANGE LIFECYCLE]: "+rate);
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
