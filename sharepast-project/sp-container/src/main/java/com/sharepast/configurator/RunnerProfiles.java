package com.sharepast.configurator;

import com.sharepast.Bootstrap;
import com.sharepast.commons.spring.ProfileConfigurator;
import com.sharepast.commons.spring.config.BaseConfig;
import com.sharepast.commons.spring.config.PropertiesConfig;
import com.sharepast.commons.spring.config.SecurityConfig;
import com.sharepast.spring.components.Servlet3HttpServer;
import com.sharepast.spring.config.HibernateConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/4/12
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunnerProfiles extends ProfileConfigurator {

    public static final Map<String, Class[]> profiles = new HashMap<String, Class[]>() {
        {
            put(Bootstrap.START_PLATFORM, new Class[]{PlatformRunner.class});
            put(Bootstrap.START_JMS, new Class[]{JMSRunner.class});
            put(Bootstrap.GET_PROPERTY, new Class[] { PropertiesConfig.class });
        }
    };

    @Override
    public Map<String, Class[]> getProfiles() {
        return profiles;
    }

    @Configuration
    @Import({BaseConfig.class, HibernateConfiguration.class, Servlet3HttpServer.class, SecurityConfig.class})
    @ComponentScan({"com.sharepast.service", "com.sharepast.dao"})
    @ImportResource({
            "classpath:com/sharepast/config/cache.xml",
            "classpath:com/sharepast/config/security.xml",
            "classpath:com/sharepast/config/jmx.xml",
            "classpath:com/sharepast/config/geoip-location.xml",
            "classpath:com/sharepast/config/jms_producer.xml"})
    static class PlatformRunner {
    }

    @Configuration
    @Import(BaseConfig.class)
    @ImportResource({
            "classpath:com/sharepast/config/jmx.xml",
            "classpath:com/sharepast/config/spring-data.xml",
            "classpath:com/sharepast/config/geoip-location.xml",
            "classpath:com/sharepast/config/activemq.xml"})
    static class JMSRunner { }
}
