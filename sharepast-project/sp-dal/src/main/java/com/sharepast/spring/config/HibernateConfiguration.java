package com.sharepast.spring.config;

import com.sharepast.genericdao.hibernate.HibernateBaseDAO;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate3.HibernateExceptionTranslator;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 4/5/12
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
//the reason why I prefer to use ASPECTJ instead of Spring AOP is because Spring sometimes would not apply transaction advisors to beans.
//for example when Spring instantiates customPermissionEvaluator, it is not advising autowired IGroupManager with Transaction aspect
//secondly, Spring JDK proxies has some limitation on advisable methods, which latter down the road we will need to overcome anyway.
@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@Import({DatabaseConfig.class})
public class HibernateConfiguration implements TransactionManagementConfigurer {

    @Autowired Environment env;

    @Autowired DataSource dataSource;

    @Bean
    public HibernateBaseDAO hibernateBaseDao() {
        return new HibernateBaseDAO();
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        HibernateTransactionManager txManager = new HibernateTransactionManager(sessionFactory());

        // configure the AnnotationTransactionAspect to use it; this must be done before executing any transactional methods
        AnnotationTransactionAspect.aspectOf().setTransactionManager(txManager);

        return txManager;
    }

    @Bean
    public HibernateExceptionTranslator getHibernateexceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        PersistenceExceptionTranslationPostProcessor b = new PersistenceExceptionTranslationPostProcessor();
        // b.setRepositoryAnnotationType(Service.class);
        // do this to make the persistence bean post processor pick up our @Service class. Normally
        // it only picks up @Repository
        return b;

    }

    @Lazy
    @Bean
    @DependsOn("dbMigrator")
    public SessionFactory sessionFactory() {
        Properties hp = new Properties();

        hp.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        hp.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        hp.setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        hp.setProperty("hibernate.use_sql_comments", "false");
        hp.setProperty("hibernate.max_fetch_depth", "1");

        hp.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        hp.setProperty("hibernate.connection.autocommit", env.getProperty("hibernate.connection.autocommit"));
        hp.setProperty("hibernate.jdbc.batch_size", env.getProperty("hibernate.jdbc.batch_size"));
        hp.setProperty("hibernate.connection.isolation", env.getProperty("hibernate.connection.isolation"));

        hp.setProperty("hibernate.generate_statistics", "false");
        hp.setProperty("hibernate.cache.use_second_level_cache", "false");
        hp.setProperty("hibernate.cache.use_query_cache", "false");
        hp.setProperty("hibernate.cache.use_minimal_puts", "true");
        hp.setProperty("hibernate.cache.use_structured_entries", "false");
        hp.setProperty("hibernate.cache.max_fetch_depth", "3");

        hp.setProperty("hibernate.c3p0.min_size", "10");
        hp.setProperty("hibernate.c3p0.max_size", "100");
        hp.setProperty("hibernate.c3p0.timeout", "7200");
        hp.setProperty("hibernate.c3p0.acquireRetryAttempts", "30");
        hp.setProperty("hibernate.c3p0.acquireIncrement", "5");
        hp.setProperty("hibernate.c3p0.idleConnectionTestPeriod", "60");
        hp.setProperty("hibernate.c3p0.initialPoolSize", "20");
        hp.setProperty("hibernate.c3p0.maxPoolSize", "100");
        hp.setProperty("hibernate.c3p0.maxIdleTime", "300");
        hp.setProperty("hibernate.c3p0.maxStatements", "50");
        hp.setProperty("hibernate.c3p0.minPoolSize", "10");
        hp.setProperty("hibernate.c3p0.preferredTestQuery", "SELECT 1");
        hp.setProperty("hibernate.c3p0.testConnectionOnCheckout", "true");

        //Hibernate 4
        /*LocalSessionFactoryBuilder sfb = new LocalSessionFactoryBuilder(dataSource);
        sfb.scanPackages("com.sharepast.domain", "com.sharepast.domain.user");
        sfb.addProperties(hp);
        return sfb.buildSessionFactory();*/

        AnnotationSessionFactoryBean sessionFactoryBean = new AnnotationSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan(new String[] {"com.sharepast.domain", "com.sharepast.domain.user"});
        sessionFactoryBean.setHibernateProperties(hp);
        try {
            sessionFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sessionFactoryBean.getObject();


    }

}
