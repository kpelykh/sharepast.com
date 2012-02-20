package com.sharepast.config;

import com.sharepast.util.db.HsqldbSchemaCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/16/12
 * Time: 12:45 AM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@ComponentScan({"com.sharepast.service.", "com.sharepast.dal", "com.sharepast.persistence."})
@ImportResource({"classpath*:com/sharepast/config/spring-data.xml"})
public class DatabaseConfig {

    @Bean
    public HsqldbSchemaCreator hsqldbSchemaCreator(@Qualifier("properties") Properties properties) {
        return new HsqldbSchemaCreator(properties);
    }
}
