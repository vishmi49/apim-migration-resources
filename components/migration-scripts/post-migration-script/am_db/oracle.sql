ALTER TABLE IDN_OAUTH2_ACCESS_TOKEN_SCOPE MODIFY TOKEN_SCOPE VARCHAR (100)
/
ALTER TABLE AM_API_CATEGORIES DROP UNIQUE(NAME,TENANT_ID)
/
ALTER TABLE AM_API_CATEGORIES DROP COLUMN TENANT_ID
/
ALTER TABLE AM_API_CATEGORIES ADD UNIQUE(NAME,ORGANIZATION)
/
ALTER TABLE AM_APPLICATION ADD UNIQUE(NAME,SUBSCRIBER_ID,ORGANIZATION)
/
ALTER TABLE AM_API ADD UNIQUE(API_PROVIDER,API_NAME,API_VERSION,ORGANIZATION)
/
ALTER TABLE AM_GATEWAY_ENVIRONMENT ADD UNIQUE(NAME,ORGANIZATION)
/

DECLARE
  con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT constraint_name
    INTO con_name
    FROM all_constraints a where a.table_name = 'AM_GW_API_DEPLOYMENTS'
                            and a.constraint_type = 'R' and UPPER(a.OWNER) = UPPER(databasename);

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_GW_API_DEPLOYMENTS DROP CONSTRAINT ' || con_name;
    EXECUTE IMMEDIATE command;
    END IF;
  END;

END;
/

ALTER TABLE AM_GW_API_DEPLOYMENTS ADD CONSTRAINT AM_GW_API_DEPLOYMENTS_ibfk_1 FOREIGN KEY (API_ID) REFERENCES AM_GW_PUBLISHED_API_DETAILS(API_ID) ON DELETE CASCADE
/

DECLARE
con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT constraint_name
    INTO con_name
    FROM all_constraints a where a.table_name = 'AM_GW_API_ARTIFACTS' and constraint_type = 'R'
                            and UPPER(a.OWNER) = UPPER(databasename);

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_GW_API_ARTIFACTS DROP CONSTRAINT ' || con_name;
    EXECUTE IMMEDIATE command;
    END IF;
  END;

END;
/

ALTER TABLE AM_GW_API_ARTIFACTS ADD CONSTRAINT AM_GW_API_ARTIFACTS_ibfk_1 FOREIGN KEY (API_ID) REFERENCES AM_GW_PUBLISHED_API_DETAILS(API_ID) ON DELETE CASCADE
/

DECLARE
  con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT a.constraint_name
    INTO con_name
    FROM all_cons_columns a
            JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
            JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
    WHERE
            c.constraint_type = 'R' AND a.table_name = 'AM_API_RATINGS' AND UPPER(a.OWNER) = UPPER(databasename)
      AND c_pk.table_name = 'AM_API' AND ROWNUM < 2;

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_API_RATINGS DROP CONSTRAINT ' || con_name;
    EXECUTE IMMEDIATE command;
    END IF;
  END;

END;
/
ALTER TABLE AM_API_RATINGS ADD CONSTRAINT AM_API_RATINGS_ibfk_1 FOREIGN KEY(API_ID) REFERENCES AM_API(API_ID) ON DELETE CASCADE
    /

DECLARE
  con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT a.constraint_name
    INTO con_name
    FROM all_cons_columns a
            JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
            JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
    WHERE
            c.constraint_type = 'R' AND a.table_name = 'AM_API_COMMENTS' AND UPPER(a.OWNER) = UPPER(databasename)
      AND c_pk.table_name = 'AM_API' AND ROWNUM < 2;

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_API_COMMENTS DROP CONSTRAINT ' || con_name;
    EXECUTE IMMEDIATE command;
    END IF;
  END;

END;
/

ALTER TABLE AM_API_COMMENTS ADD CONSTRAINT AM_API_COMMENTS_ibfk_1 FOREIGN KEY(API_ID) REFERENCES AM_API(API_ID) ON DELETE CASCADE
/

CREATE OR REPLACE TRIGGER AM_API_API_COMMENTS_TRGR
    AFTER UPDATE OF API_ID ON AM_API FOR EACH ROW
BEGIN
  UPDATE AM_API_COMMENTS
  SET API_ID = :new.API_ID
  WHERE API_ID = :old.API_ID;
END;
/

DECLARE
  con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT constraint_name
    INTO con_name
    FROM all_constraints a where a.table_name = 'AM_API_LC_EVENT' and constraint_type = 'R'
                            AND UPPER(a.OWNER) = UPPER(databasename);

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_API_LC_EVENT DROP CONSTRAINT ' || con_name;
    EXECUTE IMMEDIATE command;
    END IF;
  END;

END;
/

ALTER TABLE AM_API_LC_EVENT ADD CONSTRAINT AM_API_LC_EVENT_ibfk_1 FOREIGN KEY(API_ID) REFERENCES AM_API(API_ID) ON DELETE CASCADE
/

CREATE OR REPLACE TRIGGER AM_API_AM_API_LC_EVENT_TRGR
    AFTER UPDATE OF API_ID ON AM_API FOR EACH ROW
BEGIN
  UPDATE AM_API_LC_EVENT
  SET API_ID = :new.API_ID
  WHERE API_ID = :old.API_ID;
END;
/

DECLARE
  con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT a.constraint_name
    INTO con_name
    FROM all_cons_columns a
            JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
            JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
    WHERE
            c.constraint_type = 'R' AND a.table_name = 'AM_SUBSCRIPTION' AND UPPER(a.OWNER) = UPPER(databasename)
      AND c_pk.table_name = 'AM_API' AND ROWNUM < 2;

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_SUBSCRIPTION DROP CONSTRAINT ' || con_name;
          dbms_output.Put_line(command);
    EXECUTE IMMEDIATE command;
    END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND
        THEN
        dbms_output.Put_line('Foreign key not found');
  END;

END;
/

ALTER TABLE AM_SUBSCRIPTION ADD CONSTRAINT AM_SUBSCRIPTION_ibfk_2 FOREIGN KEY(API_ID) REFERENCES AM_API(API_ID) ON DELETE CASCADE
/

CREATE OR REPLACE TRIGGER AM_API_AM_SUBS_TRGR
    AFTER UPDATE OF API_ID ON AM_API FOR EACH ROW
  BEGIN
    UPDATE AM_SUBSCRIPTION
    SET API_ID = :new.API_ID
    WHERE API_ID = :old.API_ID;
  END;
/

DECLARE
  con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT a.constraint_name
    INTO con_name
    FROM all_cons_columns a
            JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
            JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
    WHERE
            c.constraint_type = 'R' AND a.table_name = 'AM_SECURITY_AUDIT_UUID_MAPPING' AND UPPER(a.OWNER) = UPPER(databasename)
      AND c_pk.table_name = 'AM_API' AND ROWNUM < 2;

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_SECURITY_AUDIT_UUID_MAPPING DROP CONSTRAINT ' || con_name;
          dbms_output.Put_line(command);
    EXECUTE IMMEDIATE command;
    END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND
        THEN
        dbms_output.Put_line('Foreign key not found');
  END;

END;
/

ALTER TABLE AM_SECURITY_AUDIT_UUID_MAPPING ADD CONSTRAINT AM_SECURITY_AUDIT_UUID_MAPPING_ibfk_1 FOREIGN KEY (API_ID) REFERENCES AM_API(API_ID)  ON DELETE CASCADE
    /
DECLARE
  con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT a.constraint_name
    INTO con_name
    FROM all_cons_columns a
            JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
            JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
    WHERE
            c.constraint_type = 'R' AND a.table_name = 'AM_APPLICATION_REGISTRATION' AND UPPER(a.OWNER) = UPPER(databasename)
      AND c_pk.table_name = 'AM_APPLICATION' AND ROWNUM < 2;

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_APPLICATION_REGISTRATION DROP CONSTRAINT ' || con_name;
          dbms_output.Put_line(command);
    EXECUTE IMMEDIATE command;
    END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND
        THEN
        dbms_output.Put_line('Foreign key not found');
  END;

END;
/

ALTER TABLE AM_APPLICATION_REGISTRATION ADD CONSTRAINT AM_APPLICATION_REGISTRATION_ibfk_2 FOREIGN KEY (APP_ID) REFERENCES AM_APPLICATION(APPLICATION_ID) ON DELETE CASCADE
/

DECLARE
  con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT a.constraint_name
    INTO con_name
    FROM all_cons_columns a
            JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
            JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
    WHERE
            c.constraint_type = 'R' AND a.table_name = 'AM_APPLICATION_KEY_MAPPING' AND UPPER(a.OWNER) = UPPER(databasename)
      AND c_pk.table_name = 'AM_APPLICATION' AND ROWNUM < 2;

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_APPLICATION_KEY_MAPPING DROP CONSTRAINT ' || con_name;
          dbms_output.Put_line(command);
    EXECUTE IMMEDIATE command;
    END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND
        THEN
        dbms_output.Put_line('Foreign key not found');
  END;

END;
/

ALTER TABLE AM_APPLICATION_KEY_MAPPING ADD CONSTRAINT AM_APPLICATION_KEY_MAPPING_ibfk_1 FOREIGN KEY (APPLICATION_ID) REFERENCES AM_APPLICATION(APPLICATION_ID) ON DELETE CASCADE
/

DECLARE
  con_name     VARCHAR2(100);
  command      VARCHAR2(200);
  databasename VARCHAR2(100);
BEGIN

  SELECT sys_context('userenv', 'current_schema')
  INTO databasename
  FROM dual;

  BEGIN
    SELECT a.constraint_name
    INTO con_name
    FROM all_cons_columns a
            JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name
            JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name
    WHERE
            c.constraint_type = 'R' AND a.table_name = 'AM_SUBSCRIPTION' AND UPPER(a.OWNER) = UPPER(databasename)
      AND c_pk.table_name = 'AM_APPLICATION' AND ROWNUM < 2;

    IF TRIM(con_name) IS NOT NULL
        THEN
          command := 'ALTER TABLE AM_SUBSCRIPTION DROP CONSTRAINT ' || con_name;
          dbms_output.Put_line(command);
    EXECUTE IMMEDIATE command;
    END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND
        THEN
        dbms_output.Put_line('Foreign key not found');
  END;

END;
/
ALTER TABLE AM_SUBSCRIPTION ADD CONSTRAINT AM_SUBSCRIPTION_ibfk_1 FOREIGN KEY (APPLICATION_ID) REFERENCES AM_APPLICATION(APPLICATION_ID) ON DELETE CASCADE
/
create index IDX_AAI_ORG on AM_API (ORGANIZATION)
/
ALTER TABLE AM_API_REVISION_METADATA ADD FOREIGN KEY(REVISION_UUID) REFERENCES AM_REVISION(REVISION_UUID) ON DELETE CASCADE
/
ALTER TABLE AM_REVISION ADD FOREIGN KEY (API_UUID) REFERENCES AM_API(API_UUID) ON DELETE CASCADE
/
