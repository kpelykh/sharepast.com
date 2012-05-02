package com.sharepast.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/16/12
 * Time: 12:45 AM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@ComponentScan({"com.sharepast.service.", "com.sharepast.dal", "com.sharepast.persistence."})
@ImportResource({"classpath*:com/sharepast/spring/config/spring-data.xml"})
public class DatabaseConfig {

}
