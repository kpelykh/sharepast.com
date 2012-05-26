package com.sharepast.spring;

import com.sharepast.spring.components.WebHttpServer;
import com.sharepast.spring.config.*;
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
public class SPConfigurator {

    public static final String PLATFORM = "platform";
    public static final String JMS = "jms";
    public static final String PROPERTIES = "properties";

    public static final Map<String, Class[]> configurations = new HashMap<String, Class[]>() {
        {
            put(PLATFORM, new Class[]{PlatformRunner.class});
            put(JMS, new Class[]{JMSRunner.class});
            put(PROPERTIES, new Class[] { PropertiesConfig.class });
        }
    };

    @Configuration
    @Import({BaseConfig.class, HibernateConfiguration.class, WebHttpServer.class, SecurityConfig.class})
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

    private Class[] resources;

    public SPConfigurator(String configurationId) {
        this.resources = configurations.get(configurationId);

        if (resources == null)
            throw new IllegalArgumentException(String.format("cannot find configuration with id %s", configurationId));
    }

    public Class[] getResources() {
        return resources;
    }

    public void configure() {
        SpringConfiguration.getInstance().configure(this.getResources());
    }

}
