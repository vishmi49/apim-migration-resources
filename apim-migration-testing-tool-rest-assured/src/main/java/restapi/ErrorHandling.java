/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package restapi;

/**
 * .
 * This class implemented define error messages
 */
public class ErrorHandling {

    public static String getErrorMessage(int errorCode) {
        System.out.println(errorCode);

        String errorMessage = "";

        if (errorCode == 400) {

            errorMessage = "Bad Request. Invalid request or validation error";

        } else if (errorCode == 404) {
            errorMessage = "Not Found. The specified resource does not exist";

        } else if (errorCode == 406) {

            errorMessage = "Not Acceptable. The requested media type is not supported";

        } else if (errorCode == 409) {

            errorMessage = "Conflict. Specified resource already exists";

        } else if (errorCode == 412) {

            errorMessage = "Precondition Failed. The request has not been performed because one of the preconditions is not met";
        } else {

            errorMessage = "There is an error occurred while performing searching query";
        }

        return errorMessage;
    }

}
