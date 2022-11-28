package soapapi.tenantmanagemant;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import restapi.TenantAdmin;

public class TenantManagement {
    
  String endPointUrl="testing";
  private static Logger logger = LogManager.getLogger(TenantManagement.class);
    
  public TenantManagement(String accessToken, URI baseURL) {
      this.endPointUrl = baseURL.toString() + "services/TenantMgtAdminService";
  }
    
      public void retrieveTenants(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
           
          Response response=RestAssured.given()
                  .relaxedHTTPSValidation()
                  .auth()
                  .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
                  .header("SOAPAction","urn:retrieveTenants")
                  .contentType("text/xml; charset=UTF-8;")
                  .body(getXMLPayload(tenantXmlFileName))
                  .when()
                  .post(endPointUrl);
           
          XmlPath jsXpath= new XmlPath(response.asString());
          String rate=jsXpath.getString("GetConversionRateResult");
          logger.info("[RETRIVE TENENT RESPONSE]: "+rate);
       }
  
       public void checkDomainAvailability(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
                
               Response response=RestAssured.given()
                       .relaxedHTTPSValidation()
                       .auth()
                       .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
                       .header("SOAPAction","urn:checkDomainAvailabilityRequest.xml")
                       .contentType("text/xml; charset=UTF-8;")
                       .body(getXMLPayload(tenantXmlFileName))
                       .when()
                       .post(endPointUrl);
                
               XmlPath jsXpath= new XmlPath(response.asString());
               String rate=jsXpath.getString("GetConversionRateResult");
               logger.info("[CHECK DOMAIN AVAILABILITY]: "+rate);
       }
    
    
       public void deleteTenants(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
             
            Response response=RestAssured.given()
                    .relaxedHTTPSValidation()
                    .auth()
                    .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
                    .header("SOAPAction","urn:deleteTenant")
                    .contentType("application/soap+xml; charset=UTF-8;")
                    .body(getXMLPayload(tenantXmlFileName))
                    .when()
                    .post(endPointUrl);
             
            XmlPath jsXpath= new XmlPath(response.asString());
            String rate=jsXpath.getString("GetConversionRateResult");
            logger.info("[DELETE TENENT RESPONSE]: "+rate);
       }
       
       
     public void createTenants(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
              
             Response response=RestAssured.given()
                     .relaxedHTTPSValidation()
                     .auth()
                     .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
                     .header("SOAPAction","urn:addTenant")
                     .contentType("text/xml; charset=UTF-8;")
                     .body(getXMLPayload(tenantXmlFileName))
                     .when()
                     .post(endPointUrl);
              
             XmlPath jsXpath= new XmlPath(response.asString());
             String rate=jsXpath.getString("GetConversionRateResult");
             logger.info("[CREATE TENANTS]: "+rate);
     }
     
      public void deactivateTenants(String tenantXmlFileName, TenantAdmin tenantAdmin) throws Exception {
       
      Response response=RestAssured.given()
              .relaxedHTTPSValidation()
              .auth()
              .basic(tenantAdmin.getUserName(),tenantAdmin.getPassword())
              .header("SOAPAction","urn:deactivateTenant")
              .contentType("application/soap+xml; charset=UTF-8;")
              .body(getXMLPayload(tenantXmlFileName))
              .when()
              .post(endPointUrl);
       
      XmlPath jsXpath= new XmlPath(response.asString());
      String rate=jsXpath.getString("GetConversionRateResult");
      logger.info("[DEACTIVATE TENENT RESPONSE]: "+rate);
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
