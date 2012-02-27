package com.sharepast.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.MessageSource;
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
@Import({PropertiesConfig.class})
@ComponentScan(basePackages = {"com.sharepast.service", "com.sharepast.dao"})
public class BaseConfig {

    @Bean
    @Autowired
    @DependsOn("pathPostProcess")
    public static PropertySourcesPlaceholderConfigurer ppc(@Qualifier("properties") Properties properties){
        PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
        ppc.setProperties(properties);
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc;
    }

    @Bean
    public MessageSource messageSource()
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

}
