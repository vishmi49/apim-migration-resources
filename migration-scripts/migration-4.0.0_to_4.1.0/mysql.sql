ALTER TABLE AM_API ADD ORGANIZATION VARCHAR(100);
ALTER TABLE AM_APPLICATION ADD ORGANIZATION VARCHAR(100);
ALTER TABLE AM_API_CATEGORIES ADD ORGANIZATION VARCHAR(100);
ALTER TABLE AM_API_DEFAULT_VERSION ADD ORGANIZATION VARCHAR(100) NULL;
ALTER TABLE AM_GATEWAY_ENVIRONMENT ADD ORGANIZATION VARCHAR(100);
ALTER TABLE AM_GATEWAY_ENVIRONMENT ADD PROVIDER VARCHAR(255) DEFAULT 'wso2';
ALTER TABLE AM_API ADD LOG_LEVEL VARCHAR(255) DEFAULT 'OFF';
ALTER TABLE AM_API ADD VERSION_COMPARABLE VARCHAR(15);
ALTER TABLE AM_KEY_MANAGER ADD COLUMN TOKEN_TYPE VARCHAR(45) DEFAULT "DIRECT";
ALTER TABLE AM_KEY_MANAGER ADD EXTERNAL_REFERENCE_ID VARCHAR(100) DEFAULT NULL;
ALTER TABLE AM_KEY_MANAGER RENAME COLUMN TENANT_DOMAIN TO ORGANIZATION;
ALTER TABLE AM_API_REVISION_METADATA MODIFY COLUMN REVISION_UUID VARCHAR(255);

CREATE TABLE IF NOT EXISTS AM_SYSTEM_CONFIGS
(
  ORGANIZATION     VARCHAR(100)            NOT NULL,
  CONFIG_TYPE      VARCHAR(100)            NOT NULL,
  CONFIGURATION    BLOB                    NOT NULL,
  PRIMARY KEY (ORGANIZATION,CONFIG_TYPE)
)ENGINE INNODB;

CREATE TABLE IF NOT EXISTS AM_OPERATION_POLICY (
    POLICY_UUID VARCHAR(45) NOT NULL,
    POLICY_NAME VARCHAR(300) NOT NULL,
    POLICY_VERSION VARCHAR(45) DEFAULT 'v1',
    DISPLAY_NAME VARCHAR(300) NOT NULL,
    POLICY_DESCRIPTION VARCHAR(1024),
    APPLICABLE_FLOWS VARCHAR(45) NOT NULL,
    GATEWAY_TYPES VARCHAR(45) NOT NULL,
    API_TYPES VARCHAR(45) NOT NULL,
    POLICY_PARAMETERS blob,
    ORGANIZATION VARCHAR(100),
    POLICY_CATEGORY VARCHAR(45) NOT NULL,
    MULTIPLE_ALLOWED BOOLEAN DEFAULT 1,
    POLICY_MD5 VARCHAR(45) NOT NULL,
    PRIMARY KEY(POLICY_UUID)
)ENGINE INNODB;

CREATE TABLE IF NOT EXISTS AM_OPERATION_POLICY_DEFINITION (
   DEFINITION_ID INTEGER AUTO_INCREMENT,
   POLICY_UUID VARCHAR(45) NOT NULL,
   POLICY_DEFINITION blob NOT NULL,
   GATEWAY_TYPE VARCHAR(20) NOT NULL,
   DEFINITION_MD5 VARCHAR(45) NOT NULL,
   UNIQUE (POLICY_UUID, GATEWAY_TYPE),
   FOREIGN KEY (POLICY_UUID) REFERENCES AM_OPERATION_POLICY(POLICY_UUID) ON DELETE CASCADE,
   PRIMARY KEY(DEFINITION_ID)
)ENGINE INNODB;

CREATE TABLE IF NOT EXISTS AM_COMMON_OPERATION_POLICY (
   COMMON_POLICY_ID INTEGER AUTO_INCREMENT,
   POLICY_UUID VARCHAR(45) NOT NULL,
   FOREIGN KEY (POLICY_UUID) REFERENCES AM_OPERATION_POLICY(POLICY_UUID) ON DELETE CASCADE,
   PRIMARY KEY(COMMON_POLICY_ID)
)ENGINE INNODB;

CREATE TABLE IF NOT EXISTS AM_API_OPERATION_POLICY (
   API_SPECIFIC_POLICY_ID INTEGER AUTO_INCREMENT,
   POLICY_UUID VARCHAR(45) NOT NULL,
   API_UUID VARCHAR(45) NOT NULL,
   REVISION_UUID VARCHAR(45),
   CLONED_POLICY_UUID VARCHAR(45),
   FOREIGN KEY (POLICY_UUID) REFERENCES AM_OPERATION_POLICY(POLICY_UUID) ON DELETE CASCADE,
   PRIMARY KEY(API_SPECIFIC_POLICY_ID)
)ENGINE INNODB;

CREATE TABLE IF NOT EXISTS AM_API_OPERATION_POLICY_MAPPING (
   OPERATION_POLICY_MAPPING_ID INTEGER AUTO_INCREMENT,
   URL_MAPPING_ID INTEGER NOT NULL,
   POLICY_UUID VARCHAR(45) NOT NULL,
   POLICY_ORDER INTEGER NOT NULL,
   DIRECTION VARCHAR(10) NOT NULL,
   PARAMETERS VARCHAR(1024) NOT NULL,
   FOREIGN KEY (URL_MAPPING_ID) REFERENCES AM_API_URL_MAPPING(URL_MAPPING_ID) ON DELETE CASCADE,
   FOREIGN KEY (POLICY_UUID) REFERENCES AM_OPERATION_POLICY(POLICY_UUID) ON DELETE CASCADE,
   PRIMARY KEY(OPERATION_POLICY_MAPPING_ID)
)ENGINE INNODB;

CREATE TABLE IF NOT EXISTS AM_DEPLOYED_REVISION (
  NAME VARCHAR(255) NOT NULL,
  VHOST VARCHAR(255) NULL,
  REVISION_UUID VARCHAR(255) NOT NULL,
  DEPLOYED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (NAME, REVISION_UUID),
  FOREIGN KEY (REVISION_UUID) REFERENCES AM_REVISION(REVISION_UUID) ON UPDATE CASCADE ON DELETE CASCADE
)ENGINE INNODB;

CREATE TABLE IF NOT EXISTS AM_API_ENVIRONMENT_KEYS
(
    UUID            VARCHAR(45)  NOT NULL,
    ENVIRONMENT_ID  VARCHAR(45)  NOT NULL,
    API_UUID          VARCHAR(256) NOT NULL,
    PROPERTY_CONFIG BLOB DEFAULT NULL,
    UNIQUE (ENVIRONMENT_ID, API_UUID),
    FOREIGN KEY (API_UUID) REFERENCES AM_API(API_UUID) ON DELETE CASCADE,
    PRIMARY KEY (UUID)
)ENGINE INNODB;
