package com.sharepast.util.db;

import com.sharepast.util.lang.DebuggingWriter;
import liquibase.Liquibase;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/16/12
 * Time: 12:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomSpringLiquibase extends SpringLiquibase {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSpringLiquibase.class);

    private boolean preview = true;

    private boolean execute = false;

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    /**
     * Executed automatically when the bean is initialized.
     */
    @Override
    public void afterPropertiesSet() throws LiquibaseException {
        if (!execute) {
            return;
        }
        //liquibase.database.DatabaseFactory.getInstance().register(new CustomHsqlDatabase());
        //liquibase.database.DatabaseFactory.getInstance().register(new AddAutoIncrementGeneratorMySql());

        String shouldRunProperty = System.getProperty(Liquibase.SHOULD_RUN_SYSTEM_PROPERTY);
        if (shouldRunProperty != null && !Boolean.valueOf(shouldRunProperty)) {
            LOG.info("Liquibase did not run because '" + Liquibase.SHOULD_RUN_SYSTEM_PROPERTY + "' system property was set to false");
            return;
        }

        Connection c = null;
        Liquibase liquibase = null;
        try {
            c = getDataSource().getConnection();
            liquibase = createLiquibase(c);
            if ( preview ) {
                DebuggingWriter dw = new DebuggingWriter("::Liquidbase preview ", LOG);
                liquibase.update(getContexts(), dw);
            } else {
                liquibase.update( getContexts() );
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            if (c != null) {
                try {
                    c.rollback();
                    c.close();
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }


    }

}

