package org.wso2.carbon.apimgt.migration.validator.validators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.validator.utils.Utils;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.registry.core.session.UserRegistry;

/**
 * Validator specific implementation
 */
public abstract class Validator {
    private static final Log log = LogFactory.getLog(Validator.class);
    protected Utils utils;
    protected UserRegistry registry;
    protected String apiName;
    protected String apiVersion;
    protected String provider;
    protected String apiType;
    protected String apiId;

    public Validator(Utils utils) {
        this.utils = utils;
    }

    public void validate(UserRegistry registry, GenericArtifact artifact, String preMigrationStep)
            throws GovernanceException {
        this.registry = registry;
        this.apiName = artifact.getAttribute(APIConstants.API_OVERVIEW_NAME);
        this.apiVersion = artifact.getAttribute(APIConstants.API_OVERVIEW_VERSION);
        this.provider = artifact.getAttribute(APIConstants.API_OVERVIEW_PROVIDER);
        this.apiType = artifact.getAttribute(APIConstants.API_OVERVIEW_TYPE);
        this.apiId = artifact.getId();
        if (Constants.preValidationService.API_ENDPOINT_VALIDATION.equals(preMigrationStep)) {
            validateEndpoints();
        } else if (Constants.preValidationService.API_DEFINITION_VALIDATION.equals(preMigrationStep)) {
            validateAPIDefinition();
        }
    }

    public abstract void validateEndpoints();

    public abstract void validateAPIDefinition();
}
