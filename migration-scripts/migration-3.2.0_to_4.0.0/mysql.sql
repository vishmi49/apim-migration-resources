ALTER TABLE AM_API ADD API_UUID VARCHAR(255);

CREATE TABLE IF NOT EXISTS AM_REVISION (
  ID INTEGER NOT NULL,
  API_UUID VARCHAR(256) NOT NULL,
  REVISION_UUID VARCHAR(255) NOT NULL,
  DESCRIPTION VARCHAR(255),
  CREATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CREATED_BY VARCHAR(255),
  PRIMARY KEY (ID, API_UUID),
  UNIQUE(REVISION_UUID)
)ENGINE INNODB;

CREATE TABLE IF NOT EXISTS AM_DEPLOYMENT_REVISION_MAPPING (
  NAME VARCHAR(255) NOT NULL,
  REVISION_UUID VARCHAR(255) NOT NULL,
  DISPLAY_ON_DEVPORTAL BOOLEAN DEFAULT 0,
  DEPLOYED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (NAME, REVISION_UUID),
  FOREIGN KEY (REVISION_UUID) REFERENCES AM_REVISION(REVISION_UUID) ON UPDATE CASCADE ON DELETE CASCADE
)ENGINE INNODB;

ALTER TABLE AM_API_CLIENT_CERTIFICATE ADD REVISION_UUID VARCHAR(255) NOT NULL DEFAULT 'Current API';
ALTER TABLE AM_API_CLIENT_CERTIFICATE DROP PRIMARY KEY;
ALTER TABLE AM_API_CLIENT_CERTIFICATE ADD PRIMARY KEY(ALIAS,TENANT_ID, REMOVED, REVISION_UUID);

ALTER TABLE AM_API_URL_MAPPING ADD REVISION_UUID VARCHAR(256);

ALTER TABLE AM_GRAPHQL_COMPLEXITY ADD REVISION_UUID VARCHAR(256);

ALTER TABLE AM_API_PRODUCT_MAPPING ADD REVISION_UUID VARCHAR(256);

DROP TABLE IF EXISTS AM_GW_API_DEPLOYMENTS;
CREATE TABLE IF NOT EXISTS AM_GW_API_DEPLOYMENTS (
  API_ID VARCHAR(255) NOT NULL,
  REVISION_ID VARCHAR(255) NOT NULL,
  LABEL VARCHAR(255) NOT NULL,
  PRIMARY KEY (REVISION_ID, API_ID,LABEL),
  FOREIGN KEY (API_ID) REFERENCES AM_GW_PUBLISHED_API_DETAILS(API_ID) ON UPDATE CASCADE ON DELETE NO ACTION
)ENGINE INNODB;

ALTER TABLE AM_GW_PUBLISHED_API_DETAILS ADD API_TYPE VARCHAR(50);

ALTER TABLE AM_GW_API_ARTIFACTS ADD REVISION_ID VARCHAR(255) NOT NULL;
ALTER TABLE AM_GW_API_ARTIFACTS DROP PRIMARY KEY;
ALTER TABLE AM_GW_API_ARTIFACTS DROP GATEWAY_LABEL;
ALTER TABLE AM_GW_API_ARTIFACTS ADD PRIMARY KEY(REVISION_ID, API_ID);