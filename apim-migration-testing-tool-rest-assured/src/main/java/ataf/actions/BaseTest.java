package ataf.actions;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import ataf.constants.ProgramConstants;
import restapi.ApimVersions;
import restapi.Authentication;
import restapi.AuthenticationObject;
import restapi.ContentTypes;
import restapi.GrantTypes;
import restapi.Scopes; 


public class BaseTest {

	protected URI baseURL;
	protected AuthenticationObject authenticationObject;
	
	
	@BeforeSuite
//	@Parameters({"baseURL"})	
	public void initiaization()  {
		authenticationObject = new AuthenticationObject();
		FileInputStream input;
        Properties properties;
        
        try {
            String path =  "./src/test/resources/config.properties";
            properties = new Properties();
            input = new FileInputStream(path);
            properties.load(input);
            this.baseURL = new URI(properties.getProperty("base_url")+"/");
            
        } catch (Exception e) {
        }
//        bindBaseURL(baseurlParm);
		
	}
	
//    protected void bindBaseURL(String baseurlParameter) throws URISyntaxException {  
//        
//        FileInputStream input;
//        Properties properties;
//        
//        try {
//            String path =  "./src/test/resources/config.properties";
//            properties = new Properties();
//            input = new FileInputStream(path);
//            properties.load(input);
//            this.baseURL = new URI(properties.getProperty("base_url")+"/");
//            
//        } catch (Exception e) {
//        }
//        
//    }
	
//	protected void bindBaseURL(String baseurlParameter) throws URISyntaxException {		
//		if(baseurlParameter==null || baseurlParameter.isEmpty() || baseurlParameter.contains(ProgramConstants.TESTNG_PARM_VALUE_NOT_FOUND_MSG)) {
//			baseURL = new URI(ProgramConstants.DEFAULT_BASE_URL);
//
//		} else {
//			baseURL = new URI(baseurlParameter);
//		}
//
//	}
}
