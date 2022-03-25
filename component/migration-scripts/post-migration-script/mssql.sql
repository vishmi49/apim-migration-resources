---------------------------------Modifying foreign key constraint of AM_GW_API_DEPLOYMENTS table------------------------
DECLARE @am_gw_api  VARCHAR(500);
SET @am_gw_api = (SELECT name FROM sys.foreign_keys WHERE parent_object_id = object_id('AM_GW_API_DEPLOYMENTS') AND referenced_object_id = object_id('AM_GW_PUBLISHED_API_DETAILS'));
EXEC ('ALTER TABLE AM_GW_API_DEPLOYMENTS DROP CONSTRAINT ' + @am_gw_api);
ALTER TABLE AM_GW_API_DEPLOYMENTS ADD FOREIGN KEY (API_ID) REFERENCES AM_GW_PUBLISHED_API_DETAILS(API_ID) ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------------Modifying foreign key constraint of AM_GW_API_ARTIFACTS table--------------------------
DECLARE @am_gw_arti  VARCHAR(500);
SET @am_gw_arti = (SELECT name FROM sys.foreign_keys WHERE parent_object_id = object_id('AM_GW_API_ARTIFACTS') AND referenced_object_id = object_id('AM_GW_PUBLISHED_API_DETAILS'));
EXEC ('ALTER TABLE AM_GW_API_ARTIFACTS DROP CONSTRAINT ' + @am_gw_arti);
ALTER TABLE AM_GW_API_ARTIFACTS ADD FOREIGN KEY (API_ID) REFERENCES AM_GW_PUBLISHED_API_DETAILS(API_ID) ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------------Modifying foreign key constraint of AM_API_RATINGS table-------------------------------
DECLARE @am_api_ratings  VARCHAR(500);
SET @am_api_ratings = (SELECT name FROM sys.foreign_keys WHERE parent_object_id = object_id('AM_API_RATINGS') AND referenced_object_id = object_id('AM_API'));
EXEC ('ALTER TABLE AM_API_RATINGS DROP CONSTRAINT ' + @am_api_ratings);
ALTER TABLE AM_API_RATINGS ADD FOREIGN KEY(API_ID) REFERENCES AM_API(API_ID) ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------------Modifying foreign key constraint of AM_API_COMMENTS table------------------------------
DECLARE @am_api_comments  VARCHAR(500);
SET @am_api_comments = (SELECT name FROM sys.foreign_keys WHERE parent_object_id = object_id('AM_API_COMMENTS') AND referenced_object_id = object_id('AM_API'));
EXEC ('ALTER TABLE AM_API_COMMENTS DROP CONSTRAINT ' + @am_api_comments);
ALTER TABLE AM_API_COMMENTS ADD FOREIGN KEY(API_ID) REFERENCES AM_API(API_ID) ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------------Modifying foreign key constraint of AM_API_LC_EVENT table------------------------------
DECLARE @am_api_lc  VARCHAR(500);
SET @am_api_lc = (SELECT name FROM sys.foreign_keys WHERE parent_object_id = object_id('AM_API_LC_EVENT') AND referenced_object_id = object_id('AM_API'));
EXEC ('ALTER TABLE AM_API_LC_EVENT DROP CONSTRAINT ' + @am_api_lc);
ALTER TABLE AM_API_LC_EVENT ADD FOREIGN KEY(API_ID) REFERENCES AM_API(API_ID) ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------------Modifying foreign key constraint of AM_SUBSCRIPTION table------------------------------
DECLARE @am_sub_1  VARCHAR(500);
SET @am_sub_1 = (SELECT name FROM sys.foreign_keys WHERE parent_object_id = object_id('AM_SUBSCRIPTION') AND referenced_object_id = object_id('AM_API'));
EXEC ('ALTER TABLE AM_SUBSCRIPTION DROP CONSTRAINT ' + @am_sub_1);
ALTER TABLE AM_SUBSCRIPTION ADD FOREIGN KEY(API_ID) REFERENCES AM_API(API_ID) ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------------Modifying foreign key constraint of AM_SUBSCRIPTION table------------------------------
DECLARE @am_sec_audit  VARCHAR(500);
SET @am_sec_audit = (SELECT name FROM sys.foreign_keys WHERE parent_object_id = object_id('AM_SECURITY_AUDIT_UUID_MAPPING') AND referenced_object_id = object_id('AM_API'));
EXEC ('ALTER TABLE AM_SECURITY_AUDIT_UUID_MAPPING DROP CONSTRAINT ' + @am_sec_audit);
ALTER TABLE AM_SECURITY_AUDIT_UUID_MAPPING ADD FOREIGN KEY (API_ID) REFERENCES AM_API(API_ID) ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------------Column schema change scripts of IDN_OAUTH2_ACCESS_TOKEN_SCOPE table--------------------
DECLARE @am_token_scope_keys VARCHAR(500);
SET @am_token_scope_keys = (SELECT name FROM sys.key_constraints WHERE parent_object_id = object_id('IDN_OAUTH2_ACCESS_TOKEN_SCOPE'));
EXEC ('ALTER TABLE IDN_OAUTH2_ACCESS_TOKEN_SCOPE DROP CONSTRAINT ' + @am_token_scope_keys);
ALTER TABLE IDN_OAUTH2_ACCESS_TOKEN_SCOPE ALTER COLUMN TOKEN_SCOPE VARCHAR (100) NOT NULL;
ALTER TABLE IDN_OAUTH2_ACCESS_TOKEN_SCOPE ADD PRIMARY KEY (TOKEN_ID, TOKEN_SCOPE);

---------------------------------Modifying unique key constraint script of AM_API_CATEGORIES table----------------------
DECLARE @api_categories_keys VARCHAR(MAX);
SELECT @api_categories_keys = COALESCE (@api_categories_keys + ',' , '' ) + name FROM sys.key_constraints WHERE parent_object_id = object_id('AM_API_CATEGORIES') AND name LIKE 'UQ_%'
SELECT @api_categories_keys = COALESCE (@api_categories_keys + ',' , '' ) + name FROM sys.default_constraints WHERE parent_object_id = object_id('AM_API_CATEGORIES')
EXEC ('ALTER TABLE AM_API_CATEGORIES DROP CONSTRAINT ' + @api_categories_keys);
ALTER TABLE AM_API_CATEGORIES DROP COLUMN TENANT_ID;
ALTER TABLE AM_API_CATEGORIES ADD UNIQUE(NAME,ORGANIZATION);

---------------------------------Modifying unique key constraint script of AM_APPLICATION table-------------------------
DECLARE @am_application_keys VARCHAR(500);
SELECT @am_application_keys = COALESCE (@am_application_keys + ',' , '' ) + name FROM sys.key_constraints WHERE parent_object_id = object_id('AM_APPLICATION') AND name LIKE 'UQ_%'
EXEC ('ALTER TABLE AM_APPLICATION DROP CONSTRAINT ' + @am_application_keys);
ALTER TABLE AM_APPLICATION ADD UNIQUE(UUID);
ALTER TABLE AM_APPLICATION ADD UNIQUE(NAME,SUBSCRIBER_ID,ORGANIZATION);

---------------------------------Modifying unique key constraint script of AM_API table---------------------------------
DECLARE @am_env_keys  VARCHAR(500);
SET @am_env_keys = (SELECT name FROM sys.foreign_keys WHERE parent_object_id = object_id('AM_API_ENVIRONMENT_KEYS') AND referenced_object_id = object_id('AM_API'));
EXEC ('ALTER TABLE AM_API_ENVIRONMENT_KEYS DROP CONSTRAINT ' + @am_env_keys);

DECLARE @am_api_keys VARCHAR(500);
SELECT @am_api_keys = COALESCE (@am_api_keys + ',' , '' ) + name FROM sys.key_constraints WHERE parent_object_id = object_id('AM_API') AND name LIKE 'UQ_%'
EXEC ('ALTER TABLE AM_API DROP CONSTRAINT ' + @am_api_keys);
ALTER TABLE AM_API ADD UNIQUE (API_UUID)
ALTER TABLE AM_API ADD UNIQUE(API_PROVIDER,API_NAME,API_VERSION,ORGANIZATION);

ALTER TABLE AM_API_ENVIRONMENT_KEYS ADD FOREIGN KEY(API_UUID) REFERENCES AM_API(API_UUID) ON UPDATE CASCADE ON DELETE CASCADE;

---------------------------------Modifying unique key constraint script of AM_KEY_MANAGER table-------------------------
DECLARE @am_km_keys VARCHAR(500);
SELECT @am_km_keys = COALESCE (@am_km_keys + ',' , '' ) + name FROM sys.key_constraints WHERE parent_object_id = object_id('AM_KEY_MANAGER') AND name LIKE 'UQ_%'
EXEC ('ALTER TABLE AM_KEY_MANAGER DROP CONSTRAINT ' + @am_km_keys);
ALTER TABLE AM_KEY_MANAGER ADD UNIQUE(NAME,ORGANIZATION);

---------------------------------Modifying unique key constraint script of AM_GATEWAY_ENVIRONMENT table-----------------
DECLARE @am_gw_env_keys VARCHAR(500);
SELECT @am_gw_env_keys = COALESCE (@am_gw_env_keys + ',' , '' ) + name FROM sys.key_constraints WHERE parent_object_id = object_id('AM_GATEWAY_ENVIRONMENT') AND name LIKE 'UQ_%'
EXEC ('ALTER TABLE AM_GATEWAY_ENVIRONMENT DROP CONSTRAINT ' + @am_gw_env_keys);
ALTER TABLE AM_GATEWAY_ENVIRONMENT ADD UNIQUE(NAME,ORGANIZATION);

create index IDX_AAI_ORG on AM_API (ORGANIZATION);

---------------------------------Adding foreign key Constraints for AM_API_REVISION_METADATA,AM_REVISION table----------
-- you may encounter issues while adding foreign keys for AM_API_REVISION_METADATA,AM_REVISION due to data inconsistencies
ALTER TABLE AM_API_REVISION_METADATA ADD FOREIGN KEY(REVISION_UUID) REFERENCES AM_REVISION(REVISION_UUID) ON DELETE CASCADE;
ALTER TABLE AM_REVISION ADD FOREIGN KEY (API_UUID) REFERENCES AM_API(API_UUID) ON DELETE CASCADE;