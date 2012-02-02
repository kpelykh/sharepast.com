package com.sharepast.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/13/11
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */

@Configuration
@EnableWebMvc
@ComponentScan(basePackages =  {"com.sharepast.mvc.controller", "com.sharepast.servlet"})
public class WebConfig {

    @Bean
    public ViewResolver viewResolver() {
        FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
        viewResolver.setCache(true);
        viewResolver.setSuffix(".ftl");
        return viewResolver;
    }

}
