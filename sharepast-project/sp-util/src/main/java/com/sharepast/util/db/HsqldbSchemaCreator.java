package com.sharepast.util.db;

import com.sharepast.util.config.Environment;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/16/12
 * Time: 12:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class HsqldbSchemaCreator {
    private static final Logger LOG = LoggerFactory.getLogger(HsqldbSchemaCreator.class);

    private Properties properties;

    public HsqldbSchemaCreator(Properties properties) {

        this.properties = properties;
        /*
             Problem:
                Getting exception when running hive metastore:
                java.sql.SQLSyntaxErrorException: user lacks privilege or object not found: PUBLIC.COLUMNS
             Why?:
                When using H2 or HQSQL databases with hive metastore, internally datanucleus checks whether COLUMNS table exists and
                if not it creates it automatically. When HQSQLDB wants to check for table existence it runs the following query:
                SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TRUE AND TABLE_NAME = 'COLUMNS'
                this query returns true, since INFORMATION_SCHEMA has 'COLUMNS' table.
             How to fix:
                We need to pass schema name to datanucleus, so that HQSQLDB would build the following query:
                SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TRUE AND TABLE_SCHEM LIKE 'HIVE_TEST' AND TABLE_NAME = 'COLUMNS'
                this query will correctly return 'false' and COLUMNS table will be created.

             For details see: https://forum.hibernate.org/viewtopic.php?f=6&t=943781&start=0
        */

        String driverClass = properties.getProperty("jdbc.driver");
        if (StringUtils.contains(driverClass, "hsqldb")) {
            LOG.info("HSQLDB configuration detected. Creating schema...");

            String databaseName = properties.getProperty("jdbc.db.name");

            Assert.notNull(databaseName);

            String dbUrl;

            if (Environment.isTestActive) {
                dbUrl = properties.getProperty("jdbc.test.db.url");
            } else {
                dbUrl = properties.getProperty("jdbc.db.url");
            }

            createSchema(databaseName.toUpperCase(), dbUrl);
        }
    }

    private void createSchema(String schemaName, String dbUrl) {
        Connection conn = null;
        Statement st = null;
        try {
            Class.forName(properties.getProperty("jdbc.driver"));
            conn = DriverManager.getConnection(dbUrl,
                    properties.getProperty("jdbc.username"),
                    properties.getProperty("jdbc.password"));
            st = conn.createStatement();

            ResultSet rs = conn.getMetaData().getSchemas();
            while (rs.next()) {
                String schema = rs.getString(1);
                if (schema != null && schema.equals(schemaName)) {
                    LOG.info("Schema {} already exists.", schemaName);
                    return;
                }
            }

            String expression = String.format("CREATE SCHEMA %s AUTHORIZATION DBA;", schemaName);
            st.executeUpdate(expression);
        } catch (ClassNotFoundException e) {
            LOG.error(e.getLocalizedMessage(), e);
        } catch (SQLException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }  finally {
            if (st!=null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
            if (conn!=null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }

        }
    }

}

