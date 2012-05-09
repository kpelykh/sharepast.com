package com.sharepast.spring.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sharepast.util.db.CustomSpringLiquibase;
import com.sharepast.spring.SpringConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/1/12
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@Import(HSQLDatabase.class)
public class DatabaseConfig {

    @Autowired
    Environment env;

    private @Value("${jdbc.pool.size.min:1}") Integer minPoolSize;
    private @Value("${jdbc.pool.size.max:5}") Integer maxPoolSize;
    private @Value("${jdbc.username}") String jdbcUsername;
    private @Value("${jdbc.password}") String jdbcPassword;
    private @Value("${jdbc.driver}") String jdbcDriver;

    @Bean(name = "dataSource")
    @DependsOn("hsqldb") //we need this annotation here, so that DataSource destroy method is called first followed by hsqldb's shutdown
    public DataSource dataSource() {

        String dbUrl;

        if (env.acceptsProfiles("test")) {
            dbUrl = env.getProperty("jdbc.sp.test.url");
        } else {
            dbUrl = env.getProperty("jdbc.sp.db.url");
        }

        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(jdbcDriver);
        } catch (PropertyVetoException e) {
            throw new IllegalStateException(e);
        }
        dataSource.setJdbcUrl(dbUrl);
        dataSource.setUser(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        dataSource.setAcquireIncrement(1);
        dataSource.setInitialPoolSize(1);
        dataSource.setAcquireRetryAttempts(0);
        dataSource.setAutoCommitOnClose(true);
        dataSource.setBreakAfterAcquireFailure(true);
        dataSource.setMaxStatements(50);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setMinPoolSize(minPoolSize);
        return dataSource;

    }

    @Bean(name = "dbMigrator")
    @DependsOn("hsqldb")
    public CustomSpringLiquibase dbMigrator(@Value("${liquibase.script}") String script,
                                            @Value("${liquibase.dropFirst}") Boolean  dropFirst,
                                            @Value("${liquibase.forceReleaseLocks}") Boolean  forceReleaseLocks,
                                            @Value("${liquibase.execute}") Boolean execute,
                                            @Value("${liquibase.preview}") Boolean preview) {
        CustomSpringLiquibase dbMigrator = new CustomSpringLiquibase();
        dbMigrator.setDataSource(dataSource());
        dbMigrator.setChangeLog(script);
        dbMigrator.setDropFirst(dropFirst);
        dbMigrator.setExecute(execute);
        dbMigrator.setPreview(preview);
        dbMigrator.setForceReleaseLocks(forceReleaseLocks);
        return dbMigrator;
    }


}
