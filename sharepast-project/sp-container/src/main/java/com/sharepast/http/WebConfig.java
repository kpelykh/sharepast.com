package com.sharepast.http;


import com.sharepast.freemarker.GetUserMethod;
import com.sharepast.freemarker.NlsMethod;
import com.sharepast.freemarker.UrlMethod;
import com.sharepast.util.Build;
import freemarker.ext.beans.BeansWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/13/11
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */

@EnableWebMvc
@Import(FreemarketConfig.class)
@ComponentScan(basePackages =  {"com.sharepast.mvc.controller", "com.sharepast.servlet"})
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    @Autowired
    public static PropertySourcesPlaceholderConfigurer ppc(@Qualifier("properties") Properties properties){
        PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
        ppc.setProperties(properties);
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/static/js/");
        registry.addResourceHandler("/css/**").addResourceLocations("/static/css/");
        registry.addResourceHandler("/img/**").addResourceLocations("/static/img/");
        registry.addResourceHandler("/less/**").addResourceLocations("/static/less/");
        registry.addResourceHandler("/*.txt").addResourceLocations("/static/*.txt");
        registry.addResourceHandler("/*.txt").addResourceLocations("/static/*.txt");
        registry.addResourceHandler("/*.ico").addResourceLocations("/static/*.ico");
    }
}
