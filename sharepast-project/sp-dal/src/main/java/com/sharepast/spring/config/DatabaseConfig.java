package com.sharepast.spring.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sharepast.util.db.CustomSpringLiquibase;
import com.sharepast.spring.SpringConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
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
public class DatabaseConfig {

    @Autowired
    Environment env;

    private @Value("${jdbc.pool.size.min:1}") Integer minPoolSize;
    private @Value("${jdbc.pool.size.max:5}") Integer maxPoolSize;
    private @Value("${jdbc.username}") String jdbcUsername;
    private @Value("${jdbc.password}") String jdbcPassword;
    private @Value("${jdbc.driver}") String jdbcDriver;

    @Bean(name = "dataSource")
    public DataSource dataSource() {

        String ztsDBUrl;

        if (SpringConfiguration.isTestActive) {
            ztsDBUrl = env.getProperty("jdbc.test.zts.url");
        } else {
            ztsDBUrl = env.getProperty("jdbc.zts.db.url");
        }

        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(jdbcDriver);
        } catch (PropertyVetoException e) {
            throw new IllegalStateException(e);
        }
        dataSource.setJdbcUrl(ztsDBUrl);
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
    @DependsOn("hsqldbSchemaCreator")
    public CustomSpringLiquibase dbMigrator(@Value("${liquibase.script}") String script,
                                            @Value("${liquibase.dropFirst}") Boolean  dropFirst,
                                            @Value("${liquibase.forceReleaseLocks}") Boolean  forceReleaseLocks,
                                            @Value("${liquibase.execute}") Boolean execute,
                                            @Value("${liquibase.preview}") Boolean preview) {
        CustomSpringLiquibase dbMigrator = new CustomSpringLiquibase();
        dbMigrator.setDataSource(dataSource());
        dbMigrator.setChangeLog(script);
        dbMigrator.setDropFirst(SpringConfiguration.isTestActive ? true : dropFirst);
        dbMigrator.setExecute(SpringConfiguration.isTestActive ? true : execute);
        dbMigrator.setPreview(SpringConfiguration.isTestActive ? false : preview);
        dbMigrator.setForceReleaseLocks(forceReleaseLocks);
        return dbMigrator;
    }


}
