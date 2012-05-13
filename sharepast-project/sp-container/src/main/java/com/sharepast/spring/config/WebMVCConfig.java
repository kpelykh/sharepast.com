package com.sharepast.spring.config;


import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/13/11
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */

@EnableWebMvc
@Import(GrailsConfig.class)
@ComponentScan(basePackages =  {"com.sharepast.mvc.controller", "com.sharepast.servlet"})
public class WebMVCConfig extends WebMvcConfigurerAdapter {

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

   /* @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js*//**").addResourceLocations("/static/js/");
        registry.addResourceHandler("/css*//**").addResourceLocations("/static/css/");
        registry.addResourceHandler("/img*//**").addResourceLocations("/static/img/");
        registry.addResourceHandler("/less*//**").addResourceLocations("/static/less/");
        registry.addResourceHandler("*//*.txt").addResourceLocations("/static*//*.txt");
        registry.addResourceHandler("*//*.txt").addResourceLocations("/static*//*.txt");
        registry.addResourceHandler("*//*.ico").addResourceLocations("/static*//*.ico");
    }*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //Changes the locale when a 'locale' request parameter is sent; e.g. /?locale=de
        registry.addInterceptor(new LocaleChangeInterceptor());
    }
}
