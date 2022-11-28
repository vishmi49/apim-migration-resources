package restapi;

public class ErrorHandling {

    public static String  getErrorMessage(int errorCode){
        System.out.println(errorCode);

        String errorMessage = "";

        if(errorCode==400){
            errorMessage = "Bad Request. Invalid request or validation error";
        }
        else if(errorCode==404){
            errorMessage = "Not Found. The specified resource does not exist";
        }
        else if(errorCode==406){
            errorMessage = "Not Acceptable. The requested media type is not supported";
        }
        else if(errorCode==409){
            errorMessage = "Conflict. Specified resource already exists";
        }
        else if(errorCode==412){
            errorMessage = "Precondition Failed. The request has not been performed because one of the preconditions is not met";
        }
        else{
            errorMessage = "There is an error occured while perfoming searching query";
        }
        return errorMessage;
    }
    
}
