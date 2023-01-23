package commons.testdata;

public class AuthenticationData {
	
    public static String defaultPayload() {
    	return """
    		{
			  "callbackUrl": "www.google.lk",
			  "clientName": "rest_api_publisher",
			  "owner": "admin@carbon.super",
			  "grantType": "client_credentials password refresh_token",
			  "saasApp": true
			}
    			""";
    }
    public static String getPayloadOfOwner(String owner) {
    	return """
    		{
			  "callbackUrl": "www.google.lk",
			  "clientName": "rest_api_publisher",
			  "owner": "%s",
			  "grantType": "client_credentials password refresh_token",
			  "saasApp": true
			}
    			""".formatted(owner);
    }
}
