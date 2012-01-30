package com.sharepast.config;

import com.sharepast.spring.SpringPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.tuckey.web.filters.urlrewrite.Conf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/28/12
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class BaseConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties(){

        /*
        <bean id="propertyConfigurer" class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
            <property name="locations">
                <list>
                    <value>classpath:configuration.properties</value>
                    <value>classpath:${app.env}/environment.properties</value>
                    <value>file:${user.home}/.m2/environment-${app.env}.properties</value>
                </list>
            </property>
            <property name="ignoreResourceNotFound" value="true"/>
        </bean>
        */

        Environment env = new StandardEnvironment();
        PropertySourcesPlaceholderConfigurer ppc = new SpringPropertiesUtil();

        final Resource[] resources;

        resources = new Resource[ ] {
                new ClassPathResource( "configuration.properties" ),
                new ClassPathResource( env.resolveRequiredPlaceholders("${app.env}/environment.properties") ),
                new FileSystemResource( env.resolveRequiredPlaceholders("${user.home}/.m2/environment-${app.env}.properties") )

        };
        ppc.setLocations(resources);
        ppc.setIgnoreResourceNotFound(true);
        ppc.setIgnoreUnresolvablePlaceholders(true);

        return ppc;
    }

    @Bean
    public Properties path(SpringPropertiesUtil properties) {

        Properties localProps = new Properties();
        localProps.put("web.resources", convertRelPathToAbsolute(properties.getProperty("web.resources")));
        localProps.put("activemq.home", convertRelPathToAbsolute(properties.getProperty("activemq.home")));
        localProps.put("activemq.base", convertRelPathToAbsolute(properties.getProperty("activemq.base")));
        localProps.put("jetty.resources", convertRelPathToAbsolute(properties.getProperty("jetty.resources")));
        localProps.put("activemq.persist", convertRelPathToAbsolute(properties.getProperty("activemq.persist")));
        localProps.put("log.dir", convertRelPathToAbsolute(properties.getProperty("log.dir")));
        localProps.put("config.path", convertRelPathToAbsolute(properties.getProperty("config.path")));

        return localProps;

    }


    @Bean
    public ReloadableResourceBundleMessageSource messageSource()
    {
        /*
        <bean id="messageSource" class="org.springframework.context.support.ReloadableResourceBundleMessageSource">
          <property name="basenames">
            <list>
                <value>classpath:/com/sharepast/freemarker/messages</value>
                <value>classpath:/com/sharepast/resources/users/messages</value>
                <value>classpath:/com/sharepast/security/messages</value>
                <value>classpath:/com/sharepast/startup/messages</value>
            </list>
          </property>
          <property name="defaultEncoding" value="UTF-8"/>
        </bean>
        */

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames( new String[] {
                                    "classpath:/com/sharepast/freemarker/messages",
                                    "classpath:/com/sharepast/resources/users/messages",
                                    "classpath:/com/sharepast/security/messages",
                                    "classpath:/com/sharepast/startup/messages"} );
        messageSource.setCacheSeconds( 0 );
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    private String convertRelPathToAbsolute(String relPath)  {
        FileSystemResource resource = new FileSystemResource(relPath);
        try {
            return resource.getURL().getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
