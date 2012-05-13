package com.sharepast.spring.config;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.SessionScope;

import java.util.HashMap;
import java.util.Map;


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
public class BaseConfig {

    // explicitly register session and request scopes with AnnotationConfigApplicationContext
    @Bean
    public CustomScopeConfigurer configureSessionScope() {
        CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();
        Map<String, Object> scopes = new HashMap<String, Object>();
        scopes.put(WebApplicationContext.SCOPE_SESSION, new SessionScope());
        scopes.put(WebApplicationContext.SCOPE_REQUEST, new RequestScope());
        scopeConfigurer.setScopes(scopes);
        return scopeConfigurer;
    }

    @Bean
    @DependsOn("pathPostProcess")
    public static PropertySourcesPlaceholderConfigurer ppc(ConfigurableEnvironment env){
        PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
        ppc.setPropertySources(env.getPropertySources());
        ppc.setIgnoreUnresolvablePlaceholders(false);
        return ppc;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource()
    {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/com/sharepast/startup/messages");
        messageSource.setCacheSeconds( 0 );
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
