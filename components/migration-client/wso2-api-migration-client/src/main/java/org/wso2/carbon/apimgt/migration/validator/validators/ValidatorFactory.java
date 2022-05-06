package org.wso2.carbon.apimgt.migration.validator.validators;


import org.wso2.carbon.apimgt.migration.validator.utils.Utils;

public class ValidatorFactory {
    private final Utils utils;

    public ValidatorFactory(Utils utils) {
        this.utils = utils;
    }

    public Validator getVersionValidator(String migrateFromVersion) {
        if ("4.1.0".equals(migrateFromVersion)) {
            return new V410Validator(utils);
        }
        return null;
    }
}
