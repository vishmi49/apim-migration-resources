package restapi;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Flow.Subscription;

import org.apache.logging.log4j.core.appender.mom.kafka.KafkaAppender;
import org.codehaus.groovy.transform.sc.StaticCompilationVisitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import groovy.json.JsonException;
import io.restassured.path.json.JsonPath;

public class JsonReadWrite {
    
    public static String runtimeJsonPath = "./src/test/runtimeData/runtime.json";
    
    public static void addApiToJson(String apiId) {
        
        JSONObject jsonObject = new JSONObject();
            
        try {
          JSONObject apiIdToJson = new JSONObject();
          apiIdToJson.put("apiId", apiId);  
            
          JSONParser parser = new JSONParser();
          Object obj = parser.parse(new FileReader(runtimeJsonPath));
            jsonObject = (JSONObject) obj;
            JSONArray apisList = (JSONArray)jsonObject.get("apis");
            apisList.add(apiIdToJson);
            jsonObject.put("apis", apisList);
            try (FileWriter file = new FileWriter(runtimeJsonPath)) {
              file.write(jsonObject.toJSONString()); 
              file.flush();
   
          } catch (IOException e) {
              e.printStackTrace();
          }
        } catch (Exception e) {
          
        }
          
   }
    
   public static String readApiId(int indexOfApiInList) {
       
       JSONObject jsonObject = new JSONObject();
       JSONParser parser = new JSONParser();
       try {
           Object obj = parser.parse(new FileReader(runtimeJsonPath));
           jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
    
       JSONArray apis = (JSONArray)jsonObject.get("apis");
       JSONObject getApi = (JSONObject)apis.get(indexOfApiInList);
       String getApiId = (String)getApi.get("apiId");
       
       return getApiId;
   }
    
   public static void addAppToJson(String appId) {
       
       JSONObject jsonObject = new JSONObject();
       
       try {
         JSONObject apiIdToJson = new JSONObject();
         apiIdToJson.put("appId", appId);  
         JSONParser parser = new JSONParser();
         Object obj = parser.parse(new FileReader(runtimeJsonPath));
           jsonObject = (JSONObject) obj;
           JSONArray appsList = (JSONArray)jsonObject.get("apps");
           appsList.add(apiIdToJson);
           jsonObject.put("apps", appsList);
           
           try (FileWriter file = new FileWriter(runtimeJsonPath)) {
             file.write(jsonObject.toJSONString()); 
             file.flush();
  
         } catch (IOException e) {
             e.printStackTrace();
         }
       } catch (Exception e) {
         
       }
       
   }
   
   public static String readAppId(int indexOfApiInList) {
       
       JSONObject jsonObject = new JSONObject();
       JSONParser parser = new JSONParser();
       try {
           Object obj = parser.parse(new FileReader(runtimeJsonPath));
           jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
    
       JSONArray apps = (JSONArray)jsonObject.get("apps");
       JSONObject getApp = (JSONObject)apps.get(indexOfApiInList);
       String getAppId = (String)getApp.get("appId");
       
       return getAppId;
   }
   
   public static void addKeys(String appId, String keyType, String keyObject) {
       
       JSONObject jsonObject = new JSONObject();
       JSONParser parser = new JSONParser();
       try {
           Object obj = parser.parse(new FileReader(runtimeJsonPath));
           jsonObject = (JSONObject) obj;
           
           int i = 0;
           JSONArray apps = (JSONArray) jsonObject.get("apps");
           JSONObject getApp = (JSONObject)apps.get(i);
           String getAppId = "";
           
           while(!getAppId.trim().equals(appId.trim())) {
               
               getApp = (JSONObject)apps.get(i);
               getAppId = (String)getApp.get("appId");
               
               if(getAppId.trim().equals(appId.trim())) {
                   JSONParser parser2 = new JSONParser();
                   JSONObject jsonKeyObject = (JSONObject) parser2.parse(keyObject);
                   getApp.put(keyType, jsonKeyObject);
                   break;
               }
               i += 1;
           }
           
           try (FileWriter file = new FileWriter(runtimeJsonPath)) {
               file.write(jsonObject.toJSONString()); 
               file.flush();

           } catch (IOException e) {
               e.printStackTrace();
           }
        } catch (Exception e) {
            // TODO: handle exception
        }
       
   }
   
   public static String getAccessTokenOfApiFromApp(String appId) {
       
       JSONObject jsonObject = new JSONObject();
       JSONParser parser = new JSONParser();
       String getAccessToken = "";
       try {
           Object obj = parser.parse(new FileReader(runtimeJsonPath));
           jsonObject = (JSONObject) obj;
           
           int i = 0;
           JSONArray apps = (JSONArray) jsonObject.get("apps");
           JSONObject getApp = (JSONObject)apps.get(i);
           String getAppId = "";
           int appsArraySize = apps.size();
           
           while(!getAppId.trim().equals(appId.trim()) && i < appsArraySize) {
               
               getApp = (JSONObject)apps.get(i);
               getAppId = (String)getApp.get("appId");
               JSONObject getSandbox = (JSONObject)getApp.get("sandbox");
               JSONObject getToken = (JSONObject)getSandbox.get("token");
               getAccessToken = (String)getToken.get("accessToken");
               
               i += 1;
               return getAccessToken;
           }
           
        } catch (Exception e) {
            // TODO: handle exception
        }
       return getAccessToken;
   }
   
   public static void addSubscriptionData(String appId, String subscriptionData) {
       
       JSONObject jsonObject = new JSONObject();
       JSONParser parser = new JSONParser();
       try {
           Object obj = parser.parse(new FileReader(runtimeJsonPath));
           jsonObject = (JSONObject) obj;
           
           int i = 0;
           JSONArray apps = (JSONArray) jsonObject.get("apps");
           JSONObject getApp = (JSONObject)apps.get(i);
           String getAppId = "";
           
           while(!getAppId.trim().equals(appId.trim())) {
               
               getApp = (JSONObject)apps.get(i);
               getAppId = (String)getApp.get("appId");
               
               JSONParser parser2 = new JSONParser();
               JSONObject jsonKeyObject = (JSONObject) parser2.parse(subscriptionData);
               
               if(getAppId.trim().equals(appId.trim())) {
                   
                   if(getApp.get("subscription")==null) {
                       JSONArray jsonKeyObjects = new JSONArray();
                       jsonKeyObjects.add(jsonKeyObject);
                       getApp.put("subscription", jsonKeyObjects);
                       
                   }
                   else {
                       JSONArray subscripionListArray = (JSONArray) getApp.get("subscription");
                       subscripionListArray.add(jsonKeyObject);
                       getApp.remove("subscription");
                       getApp.put("subscription", subscripionListArray);
                       
                   }
                   
                   apps.remove(i);
                   apps.add(getApp);
                   jsonObject.remove("apps");
                   jsonObject.put("apps", apps);
                   
                   
                   break;
               }
               i += 1;
           }
           
           try (FileWriter file = new FileWriter(runtimeJsonPath)) {
               file.write(jsonObject.toJSONString()); 
               file.flush();

           } catch (IOException e) {
               e.printStackTrace();
           }
        } catch (Exception e) {
            // TODO: handle exception
        }
       
   }
   
   public static String getSubscriptionId (String appId, int subscriptionIndex) {
       
       JSONObject jsonObject = new JSONObject();
       JSONParser parser = new JSONParser();
       
       try {
           Object obj = parser.parse(new FileReader(runtimeJsonPath));
           jsonObject = (JSONObject) obj;
           
           int i = 0;
           JSONArray apps = (JSONArray) jsonObject.get("apps");
           JSONObject getApp = (JSONObject)apps.get(i);
           String getAppId = "";
           
//           System.out.println(appId);
//           System.out.println(getAppId);
           while(!getAppId.trim().equals(appId.trim())) {
               
               getApp = (JSONObject)apps.get(i);
               getAppId = (String)getApp.get("appId");
               
               if(getAppId.trim().equals(appId.trim())) {
                   
                   
                   JSONArray subscriptionData = (JSONArray)getApp.get("subscription");
                   JSONObject subscription = (JSONObject)subscriptionData.get(subscriptionIndex);
                   String subscriptionId = (String)subscription.get("subscriptionId");
                   
                   return subscriptionId;
                   
               }
               i += 1;
           }
           
           
        } catch (Exception e) {
            // TODO: handle exception
        }
       return "";
   } 

   
   
   public static String removeSubscriptionData (String appId, String subscriptionId) {
       
       JSONObject jsonObject = new JSONObject();
       JSONParser parser = new JSONParser();
       
       try {
           Object obj = parser.parse(new FileReader(runtimeJsonPath));
           jsonObject = (JSONObject) obj;
           
           int i = 0;
           JSONArray apps = (JSONArray) jsonObject.get("apps");
           JSONObject getApp = (JSONObject)apps.get(i);
           String getAppId = "";
           
           while(!getAppId.trim().equals(appId.trim())) {
               
               getApp = (JSONObject)apps.get(i);
               getAppId = (String)getApp.get("appId");
               
               if(getAppId.trim().equals(appId.trim())) {
                   
                   int k = 0;
                   
                   JSONArray subscriptionData = (JSONArray)getApp.get("subscription");
                   JSONObject subscription = (JSONObject)subscriptionData.get(k);
                   String getSubscriptionId = "";
                   
                   
                   while(!getSubscriptionId.trim().equals(subscriptionId.trim())) {
                       
                       subscription = (JSONObject)subscriptionData.get(k);
                       getSubscriptionId = (String)subscription.get("subscriptionId");
                       
                       if(getSubscriptionId.trim().equals(subscriptionId.trim())) {
                           subscriptionData.remove(k);
                           getApp.remove("subscription");
                           getApp.put("subscription", subscriptionData);
                           apps.remove(i);
                           apps.add(getApp);
                           jsonObject.remove("apps");
                           jsonObject.put("apps", apps);
                           
                           try (FileWriter file = new FileWriter(runtimeJsonPath)) {
                               file.write(jsonObject.toJSONString()); 
                               file.flush();

                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                           
                           break;
                       }
                       
                       k += 1;
                   }
                   break;
                   
               }
               i += 1;
               
           }
        } catch (Exception e) {
            // TODO: handle exception
        }
       return "";
   } 
  
   
}
