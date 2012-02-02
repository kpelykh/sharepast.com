package com.sharepast.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;


/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/28/12
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@EnableAspectJAutoProxy
public class BaseConfig {

    @Bean
    public static PropertiesFactoryBean properties() {
        PropertiesFactoryBean pfb = new PropertiesFactoryBean();
        final Resource[] resources;

        Environment env = new StandardEnvironment();
        resources = new Resource[ ] {
                new ClassPathResource("configuration.properties"),
                new ClassPathResource( env.resolveRequiredPlaceholders("${app.env}/environment.properties") ),
                new FileSystemResource( env.resolveRequiredPlaceholders("${user.home}/.m2/environment-${app.env}.properties") )

        };

        pfb.setLocations(resources);

        pfb.setSingleton(true);
        pfb.setIgnoreResourceNotFound(true);
        return  pfb;
    }

    @Bean
    @Autowired
    public static Properties pathPostProcess(Environment env, @Qualifier("properties") Properties properties) {
        properties.put("jetty.resources", convertRelPathToAbsolute(env.resolvePlaceholders(properties.getProperty("jetty.resources"))));
        properties.put("log.dir", convertRelPathToAbsolute(env.resolvePlaceholders(properties.getProperty("log.dir"))));
        properties.put("config.path", convertRelPathToAbsolute(env.resolvePlaceholders(properties.getProperty("config.path"))));
        properties.put("activemq.persist", convertRelPathToAbsolute(env.resolvePlaceholders(properties.getProperty("activemq.persist"))));
        properties.put("activemq.base", convertRelPathToAbsolute(env.resolvePlaceholders(properties.getProperty("activemq.base"))));
        properties.put("activemq.home", convertRelPathToAbsolute(env.resolvePlaceholders(properties.getProperty("activemq.home"))));
        properties.put("web.resources", convertRelPathToAbsolute(env.resolvePlaceholders(properties.getProperty("web.resources"))));

        return properties;
    }

    @Bean
    @DependsOn("pathPostProcess")
    public static PropertySourcesPlaceholderConfigurer ppc(@Qualifier("properties") Properties properties){
        PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
        ppc.setProperties(properties);
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc;
    }


    @Bean
    public ReloadableResourceBundleMessageSource messageSource()
    {
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

    private static String convertRelPathToAbsolute(String relPath)  {
        FileSystemResource resource = new FileSystemResource(relPath);
        try {
            return resource.getURL().getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
