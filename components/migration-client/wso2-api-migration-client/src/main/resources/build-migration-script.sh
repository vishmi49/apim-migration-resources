#!/bin/sh
sourcePath=../../../../../migration-scripts
targetPath=../../../target

function_create_script()
{

version400="migration-4.0.0_to_4.1.0"
version320="migration-3.2.0_to_4.1.0"
version320="migration-3.2.0_to_4.1.0"
version310="migration-3.1.0_to_4.1.0"
version300="migration-3.0.0_to_4.1.0"
version260="migration-2.6.0_to_4.1.0"

if [ ! -d "$targetPath/scripts/$1" ]; then
  mkdir -p $targetPath/scripts/"$1";
fi

if [ "$1" = "post-migration-script/reg_db" ]; then
  regDBFie="$sourcePath/post-migration-script/reg_db/reg-index.sql"
  cat $regDBFie > $targetPath/scripts/"$1"/reg-index.sql
else
  if [ "$1" = $version400 ]; then
     mssqlFileList="$sourcePath/migration-4.0.0_to_4.1.0/mssql.sql"
     mysqlFileList="$sourcePath/migration-4.0.0_to_4.1.0/mysql.sql"
     postgresqlFileList="$sourcePath/migration-4.0.0_to_4.1.0/postgresql.sql"
     oracleFileList="$sourcePath/migration-4.0.0_to_4.1.0/oracle.sql"
     else
       if [ "$1" = $version320 ]; then
          mssqlFileList="$sourcePath/migration-3.2.0_to_4.0.0/mssql.sql $sourcePath/migration-4.0.0_to_4.1.0/mssql.sql"
          mysqlFileList="$sourcePath/migration-3.2.0_to_4.0.0/mysql.sql $sourcePath/migration-4.0.0_to_4.1.0/mysql.sql"
          postgresqlFileList="$sourcePath/migration-3.2.0_to_4.0.0/postgresql.sql $sourcePath/migration-4.0.0_to_4.1.0/postgresql.sql"
          oracleFileList="$sourcePath/migration-3.2.0_to_4.0.0/oracle.sql $sourcePath/migration-4.0.0_to_4.1.0/oracle.sql"
         else
          if [ "$1" = $version310 ]; then
            mssqlFileList="$sourcePath/migration-3.1.0_to_3.2.0/mssql.sql $sourcePath/migration-3.2.0_to_4.0.0/mssql.sql $sourcePath/migration-4.0.0_to_4.1.0/mssql.sql"
            mysqlFileList="$sourcePath/migration-3.1.0_to_3.2.0/mysql.sql $sourcePath/migration-3.2.0_to_4.0.0/mysql.sql $sourcePath/migration-4.0.0_to_4.1.0/mysql.sql"
            postgresqlFileList="$sourcePath/migration-3.1.0_to_3.2.0/postgresql.sql $sourcePath/migration-3.2.0_to_4.0.0/postgresql.sql $sourcePath/migration-4.0.0_to_4.1.0/postgresql.sql"
            oracleFileList="$sourcePath/migration-3.1.0_to_3.2.0/oracle.sql $sourcePath/migration-3.2.0_to_4.0.0/oracle.sql $sourcePath/migration-4.0.0_to_4.1.0/oracle.sql"
          else
              if [ "$1" = $version300 ]; then
                 mssqlFileList="$sourcePath/migration-3.0.0_to_3.1.0/mssql.sql $sourcePath/migration-3.1.0_to_3.2.0/mssql.sql $sourcePath/migration-3.2.0_to_4.0.0/mssql.sql $sourcePath/migration-4.0.0_to_4.1.0/mssql.sql"
                 mysqlFileList="$sourcePath/migration-3.0.0_to_3.1.0/mysql.sql $sourcePath/migration-3.1.0_to_3.2.0/mysql.sql $sourcePath/migration-3.2.0_to_4.0.0/mysql.sql $sourcePath/migration-4.0.0_to_4.1.0/mysql.sql"
                 postgresqlFileList="$sourcePath/migration-3.0.0_to_3.1.0/postgresql.sql $sourcePath/migration-3.1.0_to_3.2.0/postgresql.sql $sourcePath/migration-3.2.0_to_4.0.0/postgresql.sql $sourcePath/migration-4.0.0_to_4.1.0/postgresql.sql"
                 oracleFileList="$sourcePath/migration-3.0.0_to_3.1.0/oracle.sql $sourcePath/migration-3.1.0_to_3.2.0/oracle.sql $sourcePath/migration-3.2.0_to_4.0.0/oracle.sql $sourcePath/migration-4.0.0_to_4.1.0/oracle.sql"
              else
                 if [ "$1" = $version260 ]; then
                   mssqlFileList="$sourcePath/migration-2.6.0_to_3.0.0/mssql.sql $sourcePath/migration-3.0.0_to_3.1.0/mssql.sql $sourcePath/migration-3.1.0_to_3.2.0/mssql.sql $sourcePath/migration-3.2.0_to_4.0.0/mssql.sql  $sourcePath/migration-4.0.0_to_4.1.0/mssql.sql"
                   mysqlFileList="$sourcePath/migration-2.6.0_to_3.0.0/mysql.sql $sourcePath/migration-3.0.0_to_3.1.0/mysql.sql $sourcePath/migration-3.1.0_to_3.2.0/mysql.sql $sourcePath/migration-3.2.0_to_4.0.0/mysql.sql  $sourcePath/migration-4.0.0_to_4.1.0/mysql.sql"
                   postgresqlFileList="$sourcePath/migration-2.6.0_to_3.0.0/postgresql.sql $sourcePath/migration-3.0.0_to_3.1.0/postgresql.sql $sourcePath/migration-3.1.0_to_3.2.0/postgresql.sql $sourcePath/migration-3.2.0_to_4.0.0/postgresql.sql $sourcePath/migration-4.0.0_to_4.1.0/postgresql.sql"
                   oracleFileList="$sourcePath/migration-2.6.0_to_3.0.0/oracle.sql $sourcePath/migration-3.0.0_to_3.1.0/oracle.sql $sourcePath/migration-3.1.0_to_3.2.0/oracle.sql $sourcePath/migration-3.2.0_to_4.0.0/oracle.sql  $sourcePath/migration-4.0.0_to_4.1.0/oracle.sql"
                 else
                   mssqlFileList="$sourcePath/post-migration-script/am_db/mssql.sql"
                   mysqlFileList="$sourcePath/post-migration-script/am_db/mysql.sql"
                   postgresqlFileList="$sourcePath/post-migration-script/am_db/postgresql.sql"
                   oracleFileList="$sourcePath/post-migration-script/am_db/oracle.sql"
                fi
              fi
          fi
       fi
  fi
  cat $mysqlFileList > $targetPath/scripts/"$1"/mysql.sql
  cat $mssqlFileList > $targetPath/scripts/"$1"/mssql.sql
  cat $postgresqlFileList > $targetPath/scripts/"$1"/postgresql.sql
  cat $oracleFileList > $targetPath/scripts/"$1"/oracle.sql
fi


}

if [ -e "$targetPath/scripts" ]; then
  rm -r $targetPath/scripts
fi

function_create_script migration-4.0.0_to_4.1.0
function_create_script migration-3.2.0_to_4.1.0
function_create_script migration-3.1.0_to_4.1.0
function_create_script migration-3.0.0_to_4.1.0
function_create_script migration-2.6.0_to_4.1.0
function_create_script post-migration-script/am_db
function_create_script post-migration-script/reg_db