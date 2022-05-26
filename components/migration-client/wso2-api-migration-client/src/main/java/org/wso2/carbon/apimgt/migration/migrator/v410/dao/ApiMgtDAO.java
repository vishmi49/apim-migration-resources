package org.wso2.carbon.apimgt.migration.migrator.v410.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.APIProductIdentifier;
import org.wso2.carbon.apimgt.api.model.Identifier;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.dao.constants.SQLConstants;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApiMgtDAO {
    private static final Log log = LogFactory.getLog(org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO.class);
    private static ApiMgtDAO INSTANCE = null;
    private final Object scopeMutex = new Object();
    private boolean forceCaseInsensitiveComparisons = false;
    private boolean multiGroupAppSharingEnabled = false;

    private ApiMgtDAO() {

        APIManagerConfiguration configuration = ServiceReferenceHolder.getInstance()
                .getAPIManagerConfigurationService().getAPIManagerConfiguration();

        String caseSensitiveComparison = ServiceReferenceHolder.getInstance().
                getAPIManagerConfigurationService().getAPIManagerConfiguration().getFirstProperty(APIConstants.API_STORE_FORCE_CI_COMPARISIONS);
        if (caseSensitiveComparison != null) {
            forceCaseInsensitiveComparisons = Boolean.parseBoolean(caseSensitiveComparison);
        }

        multiGroupAppSharingEnabled = APIUtil.isMultiGroupAppSharingEnabled();
    }

    /**
     * Method to get the instance of the ApiMgtDAO.
     *
     */
    public static ApiMgtDAO getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new ApiMgtDAO();
        }

        return INSTANCE;
    }


    public int getAPIID(Identifier apiId) throws APIManagementException {

        PreparedStatement prepStmt = null;
        ResultSet rs = null;

        int id = -1;
        String getAPIQuery = SQLConstants.GET_API_ID_SQL;

        if (apiId instanceof APIProductIdentifier) {
            getAPIQuery = SQLConstants.GET_API_PRODUCT_ID_SQL;
        }

        try {
            try (Connection connection = APIMgtDBUtil.getConnection()) {
                return getAPIID(apiId, connection);
            }
        } catch (SQLException e) {
            handleException("Error while locating API: " + apiId + " from the database", e);
        }
        return id;
    }

    public int getAPIID(Identifier apiId, Connection connection) throws APIManagementException, SQLException {

        int id = -1;
        String getAPIQuery = SQLConstants.GET_API_ID_SQL;

        if (apiId instanceof APIProductIdentifier) {
            getAPIQuery = SQLConstants.GET_API_PRODUCT_ID_SQL;
        }

        try (PreparedStatement prepStmt = connection.prepareStatement(getAPIQuery)) {
            prepStmt.setString(1, APIUtil.replaceEmailDomainBack(apiId.getProviderName()));
            prepStmt.setString(2, apiId.getName());
            prepStmt.setString(3, apiId.getVersion());
            try (ResultSet rs = prepStmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("API_ID");
                }
                if (id == -1) {
                    String msg = "Unable to find the API: " + apiId + " in the database";
                    log.error(msg);
                    throw new APIManagementException(msg);
                }
            }
        }
        return id;
    }

    private void handleException(String msg, Throwable t) throws APIManagementException {

        log.error(msg, t);
        throw new APIManagementException(msg, t);
    }
}
