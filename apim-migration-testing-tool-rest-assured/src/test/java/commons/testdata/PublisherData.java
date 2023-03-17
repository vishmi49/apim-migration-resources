package commons.testdata;

public class PublisherData {

	public static String getAPIADPRestAPIJsonPayload() {
		return """
			{
			   "name":"ADPRestAPI",
			   "version":"1.0.0",
			   "context":"adp-rest",
			   "policies":[
			      "ADPBrass"
			   ],
			   "endpointConfig":{
			      "endpoint_type":"http",
			      "sandbox_endpoints":{
			         "url":"http://localhost:8090/"
			      },
			      "production_endpoints":{
			         "url":"http://localhost:8090/"
			      }
			   },
			   "gatewayEnvironments":[
			      "Production and Sandbox"
			   ]
			}
				""";
	}
	
	public static String getADPInlineDocPayload() {
		return """
			{
				"name": "adp-inline-doc",
				"type": "HOWTO",
				"summary": "ADP inline doc summary",
				"sourceType": "INLINE",
				"visibility": "API_LEVEL",
				"sourceUrl": "",
				"otherTypeName": null,
				"inlineContent": ""
			}
				""";
	}
	
	public static String getADPMarkdownDocPayload() {
		return """
		 {
			    "name": "adp-markdown-doc",
			    "type": "HOWTO",
			    "summary": "ADP markdown doc summary",
			    "sourceType": "MARKDOWN",
			    "visibility": "API_LEVEL",
			    "sourceUrl": "",
			    "otherTypeName": null,
			    "inlineContent": ""
			}
				""";
	}
	public static String getADPForumURLDocPayload() {
		return """
			{
			    "name": "adp-url-doc",
			    "type": "PUBLIC_FORUM",
			    "summary": "ADP url doc summary",
			    "sourceType": "URL",
			    "visibility": "API_LEVEL",
			    "sourceUrl": "https://apim.docs.wso2.com/en/latest/",
			    "otherTypeName": null,
			    "inlineContent": ""
			}
				""";
	}
	
	public static String getADPOtherFileDocPayload() {
		return """
		{
		    "name": "adp-file-doc",
		    "type": "OTHER",
		    "summary": "ADP file doc summary",
		    "sourceType": "FILE",
		    "visibility": "API_LEVEL",
		    "sourceUrl": "",
		    "otherTypeName": "TXT",
		    "inlineContent": ""
		}
				""";
	}
	
	public static String getADPUpdateAPIPayload(String inMediationPolicy, String outMediationPolicy, String faultMediationPolicy ) {
		String payload = """
			{
			   "name":"ADPRestAPI",
			   "description":null,
			   "context":"/adp-rest",
			   "version":"1.0.0",
			   "provider":"adp_crt_user",
			   "lifeCycleStatus":"CREATED",
			   "wsdlInfo":null,
			   "wsdlUrl":null,
			   "testKey":null,
			   "responseCachingEnabled":false,
			   "cacheTimeout":300,
			   "destinationStatsEnabled":null,
			   "hasThumbnail":null,
			   "isDefaultVersion":false,
			   "enableSchemaValidation":true,
			   "enableStore":true,
			   "type":"HTTP",
			   "transport":[
			      "http",
			      "https"
			   ],
			   "tags":[
			      "adp-tag"
			   ],
			   "policies":[
			      "ADPBrass"
			   ],
			   "apiThrottlingPolicy":null,
			   "authorizationHeader":null,
			   "securityScheme":[
			      "oauth2",
			      "oauth_basic_auth_api_key_mandatory"
			   ],
			   "maxTps":null,
			   "visibility":"PUBLIC",
			   "visibleRoles":[
			      
			   ],
			   "visibleTenants":[
			      
			   ],
			   "endpointSecurity":null,
			   "gatewayEnvironments":[
			      "Production and Sandbox"
			   ],
			   "deploymentEnvironments":[
			      
			   ],
			   "labels":[
			      
			   ],
			   "mediationPolicies":[%s,%s,%s],
			   "subscriptionAvailability":"CURRENT_TENANT",
			   "subscriptionAvailableTenants":[
			      
			   ],
			   "additionalProperties":{
			      
			   },
			   "monetization":null,
			   "accessControl":"NONE",
			   "accessControlRoles":[
			      
			   ],
			   "businessInformation":{
			      "businessOwner":"Business Owner",
			      "businessOwnerEmail":"adp.bo@gmail.com",
			      "technicalOwner":"Technical Owner",
			      "technicalOwnerEmail":"adp.to@gmail.com"
			   },
			   "corsConfiguration":{
			      "corsConfigurationEnabled":false,
			      "accessControlAllowOrigins":[
			         "*"
			      ],
			      "accessControlAllowCredentials":false,
			      "accessControlAllowHeaders":[
			         "authorization",
			         "Access-Control-Allow-Origin",
			         "Content-Type",
			         "SOAPAction",
			         "apikey",
			         "testKey"
			      ],
			      "accessControlAllowMethods":[
			         "GET",
			         "PUT",
			         "POST",
			         "DELETE",
			         "PATCH",
			         "OPTIONS"
			      ]
			   },
			   "workflowStatus":null,
			   "createdTime":"2022-06-10T07:12:57.426Z",
			   "lastUpdatedTime":"2022-06-10T07:16:19.728Z",
			   "endpointConfig":{
			      "endpoint_type":"http",
			      "sandbox_endpoints":{
			         "url":"http://localhost:8090/"
			      },
			      "production_endpoints":{
			         "url":"http://localhost:8090/"
			      }
			   },
			   "endpointImplementationType":"ENDPOINT",
			   "scopes":[
			      {
			         "scope":{
			            "id":null,
			            "name":"adp-local-scope-without-roles",
			            "displayName":"adp-local-scope-without-roles",
			            "description":"ADP local scope without roles",
			            "bindings":[
			               
			            ],
			            "usageCount":null
			         },
			         "shared":false
			      },
			      {
			         "scope":{
			            "id":null,
			            "name":"adp-shared-scope-with-roles",
			            "displayName":"adp-shared-scope-with-roles",
			            "description":"Shared scope with role mapping",
			            "bindings":[
			               "ADP_CREATOR",
			               "ADP_PUBLISHER"
			            ],
			            "usageCount":null
			         },
			         "shared":true
			      },
			      {
			         "scope":{
			            "id":null,
			            "name":"adp-shared-scope-without-roles",
			            "displayName":"adp-shared-scope-without-roles",
			            "description":"Shared scope without role mapping",
			            "bindings":[
			               
			            ],
			            "usageCount":null
			         },
			         "shared":true
			      }
			   ],
			   "operations":[
			      {
			         "id":"",
			         "target":"/users/{id}",
			         "verb":"GET",
			         "authType":"Application & Application User",
			         "throttlingPolicy":"Unlimited",
			         "scopes":[
			            "adp-local-scope-without-roles",
			            "adp-shared-scope-without-roles"
			         ],
			         "usedProductIds":[
			            
			         ],
			         "amznResourceName":null,
			         "amznResourceTimeout":null
			      },
			      {
			         "id":"",
			         "target":"/users/{id}",
			         "verb":"DELETE",
			         "authType":"Application & Application User",
			         "throttlingPolicy":"ADP10PerMin",
			         "scopes":[
			            
			         ],
			         "usedProductIds":[
			            
			         ],
			         "amznResourceName":null,
			         "amznResourceTimeout":null
			      },
			      {
			         "id":"",
			         "target":"/users",
			         "verb":"GET",
			         "authType":"Application & Application User",
			         "throttlingPolicy":"Unlimited",
			         "scopes":[
			            "adp-local-scope-without-roles"
			         ],
			         "usedProductIds":[
			            
			         ],
			         "amznResourceName":null,
			         "amznResourceTimeout":null
			      },
			      {
			         "id":"",
			         "target":"/users",
			         "verb":"POST",
			         "authType":"Application & Application User",
			         "throttlingPolicy":"Unlimited",
			         "scopes":[
			            "adp-shared-scope-with-roles"
			         ],
			         "usedProductIds":[
			            
			         ],
			         "amznResourceName":null,
			         "amznResourceTimeout":null
			      },
			      {
			         "id":"",
			         "target":"/hello",
			         "verb":"GET",
			         "authType":"None",
			         "throttlingPolicy":"Unlimited",
			         "scopes":[
			            
			         ],
			         "usedProductIds":[
			            
			         ],
			         "amznResourceName":null,
			         "amznResourceTimeout":null
			      }
			   ],
			   "threatProtectionPolicies":null,
			   "categories":[
			      "adp-rest"
			   ],
			   "keyManagers":[
			      "all"
			   ]
			}
				""";
		return payload.formatted(inMediationPolicy,outMediationPolicy,faultMediationPolicy);
	}
	
	public static class TestDataV420{
		public static String getAPIADPRestAPIJsonPayload() {
			
			
			String s = """
					{
				  "name": "ADPRestAPI",
				  "version": "1.0.0",
				  "context": "adp-rest",
				  "policies": [
				    "ADPBrass"
				  ],
				  "endpointConfig": {
				    "endpoint_type": "http",
				    "sandbox_endpoints": {
				      "url": "http://localhost:8090/"
				    },
				    "production_endpoints": {
				      "url": "http://localhost:8090/"
				    }
				  },
				  "gatewayEnvironments": [
				    "Production and Sandbox"
				  ]
				}
					""";
			
			return """
				{
				   "name":"ADPRestAPI",
				   "version":"1.0.0",
				   "context":"adp-rest",
				   "policies":[
				      "ADPBrass"
				   ],
				   "endpointConfig":{
				      "endpoint_type":"http",
				      "sandbox_endpoints":{
				         "url":"http://localhost:8090/"
				      },
				      "production_endpoints":{
				         "url":"http://localhost:8090/"
				      }
				   }
				}
					""";

		}
	}
}
