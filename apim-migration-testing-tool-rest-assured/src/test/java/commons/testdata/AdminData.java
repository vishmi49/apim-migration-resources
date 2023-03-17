package commons.testdata;

public class AdminData {

	public static String getADPRoleAliesesPayload() {
		return """
		{
		    "count": 3,
		    "list": [
		        {
		            "role": "Internal/creator",
		            "aliases": ["ADP_CREATOR"]
		        },
		        {
		            "role": "Internal/publisher",
		            "aliases": ["ADP_PUBLISHER"]
		        },
		        {
		            "role": "Internal/subscriber",
		            "aliases": ["ADP_SUBSCRIBER"]
		        }
		    ]
		}
				""";
	}
	
	public static String getAPICategoriesJsonPayload(String name, String description) {
		
		String jsonPayload = """
				{
				  "name": "%s",
				  "description": "%s"
				}
				""";
		return jsonPayload.formatted(name,description);
	}
	
	public static String getAdvancedThrotellingPolicyADP10PerMin() {
		return """
				{
				    "policyName": "ADP10PerMin",
				    "description": "10PerMinute",
				    "conditionalGroups": [],
				    "defaultLimit": {
				        "type": "REQUESTCOUNTLIMIT",
				        "requestCount": {
				            "timeUnit": "min",
				            "unitTime": "1",
				            "requestCount": "10"
				        },
				        "bandwidth": null
				    }
				}
				""";
	}
	public static String getApplicationThrotellingPolicyADP5PerMin() {
		return """
				{
				  "policyName": "ADP5PerMin",
				  "description": "5PerMinute",
				  "defaultLimit": {
				    "type": "REQUESTCOUNTLIMIT",
				    "requestCount": {
				      "requestCount": "5",
				      "timeUnit": "min",
				      "unitTime": "1"
				    }
				  }
				}
				""";
	}
	
	public static String getSubscriptionThrottlingPolicyADPBrass() {
		return """
			{
			  "policyName": "ADPBrass",
			  "description": "Allows 20 requests per minute",
			  "defaultLimit": {
			    "type": "REQUESTCOUNTLIMIT",
			    "requestCount": {
			      "requestCount": "20",
			      "timeUnit": "min",
			      "unitTime": "1"
			    }
			  },
			  "rateLimitCount": "5",
			  "rateLimitTimeUnit": "sec",
			  "billingPlan": "FREE",
			  "stopOnQuotaReach": true,
			  "customAttributes": [
			    
			  ],
			  "graphQLMaxComplexity": "10",
			  "graphQLMaxDepth": "5",
			  "monetization": {
			    "monetizationPlan": "FIXEDRATE",
			    "properties": {
			      "fixedPrice": "",
			      "pricePerRequest": "",
			      "currencyType": "",
			      "billingCycle": "week"
			    }
			  },
			  "permissions": {
			    "permissionType": "ALLOW",
			    "roles": [
			      "Internal/everyone"
			    ]
			  }
			}
				""";
		
		// 4.2.0 payload
//		return """
//				{
//				  "policyName": "ADPBrass",
//				  "description": "Allows 20 requests per minute",
//				  "defaultLimit": {
//				    "type": "REQUESTCOUNTLIMIT",
//				    "requestCount": {
//				      "requestCount": "20",
//				      "timeUnit": "min",
//				      "unitTime": "1"
//				    }
//				  },
//				  "subscriberCount": 0,
//				  "rateLimitCount": "5",
//				  "rateLimitTimeUnit": "min",
//				  "billingPlan": "FREE",
//				  "stopOnQuotaReach": true,
//				  "customAttributes": [
//				    
//				  ],
//				  "graphQLMaxComplexity": 0,
//				  "graphQLMaxDepth": 0,
//				  "monetization": {
//				    "monetizationPlan": "FIXEDRATE",
//				    "properties": {
//				      "fixedPrice": "",
//				      "pricePerRequest": "",
//				      "currencyType": "",
//				      "billingCycle": "week"
//				    }
//				  },
//				  "permissions": {
//				    "permissionType": "ALLOW",
//				    "roles": [
//				      "Internal/everyone"
//				    ]
//				  }
//				}
//				""";
		
//		// payload in documentation
//		// "subscriberCount": 0,(this is for webhooks) is mandatory in 4.2.0, but this is not mention in the documentation
//		return """
//				{
//			  "policyId": "78c3ebff-176d-40d8-9377-fb3276528291",
//			  "policyName": "Gold2",
//			  "displayName": "Gold",
//			  "description": "Allows 5000 requests per minute",
//			  "isDeployed": true,
//			  "graphQLMaxComplexity": 0,
//			  "graphQLMaxDepth": 0,
//			  "defaultLimit": {
//			    "type": "REQUESTCOUNTLIMIT",
//			    "requestCount": {
//			      "timeUnit": "min",
//			      "unitTime": 1,
//			      "requestCount": 5000
//			    }
//			  },
//			  "subscriberCount": 9,
//			  "rateLimitCount": 0,
//			  "customAttributes": [],
//			  "stopOnQuotaReach": true,
//			  "billingPlan": "FREE"
//			}
//				""";
	}
	
	public static String getScopeJsonPayload(String name, String displayName, String description, String bindings) {
		String jsonPayload= """
				{
				  "name": "%s",
				  "displayName": "%s",
				  "description": "%s",
				  "bindings": [%s]
				}
				""";
		return jsonPayload.formatted(name, displayName, description, bindings);
		
	}
	
	public static class TestData420 {
		public static String getSubscriptionThrottlingPolicyADPBrass() {
			return """
					{
					  "policyName": "ADPBrass",
					  "description": "Allows 20 requests per minute",
					  "defaultLimit": {
					    "type": "REQUESTCOUNTLIMIT",
					    "requestCount": {
					      "requestCount": "20",
					      "timeUnit": "min",
					      "unitTime": "1"
					    }
					  },
					  "subscriberCount": 0,
					  "rateLimitCount": "5",
					  "rateLimitTimeUnit": "sec",
					  "billingPlan": "FREE",
					  "stopOnQuotaReach": true,
					  "customAttributes": [

					  ],
					  "graphQLMaxComplexity": "10",
					  "graphQLMaxDepth": "5",
					  "monetization": {
					    "monetizationPlan": "FIXEDRATE",
					    "properties": {
					      "fixedPrice": "",
					      "pricePerRequest": "",
					      "currencyType": "",
					      "billingCycle": "week"
					    }
					  },
					  "permissions": {
					    "permissionType": "ALLOW",
					    "roles": [
					      "Internal/everyone"
					    ]
					  }
					}
						""";
		}
	}
}
