package com.sharepast.http;

import com.sharepast.freemarker.GetUserMethod;
import com.sharepast.freemarker.NlsMethod;
import com.sharepast.freemarker.UrlMethod;
import com.sharepast.util.Build;
import com.sharepast.util.spring.SpringConfigurator;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateMethodModelEx;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/19/12
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@ComponentScan(basePackages =  {"com.sharepast.freemarker"})
public class FreemarketConfig {

    private @Value("${web.resource.base}") String webResourcesBase;

    @Bean
    FreeMarkerViewResolver freemarkerViewResolver(){
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(true);
        resolver.setSuffix(".ftl");
        //if you want to use the Spring FreeMarker macros, set this property to true
        resolver.setExposeSpringMacroHelpers(true);
        resolver.setOrder(1);
        return resolver;
    }

    @Bean
    FreeMarkerConfigurer freemarkerConfig(@Qualifier("properties") Properties properties) {
        FreeMarkerConfigurer config = new FreeMarkerConfigurer();
        config.setTemplateLoaderPath("/templates/");
        config.setDefaultEncoding("UTF-8");
        Map<String,Object> variables = new HashMap<String,Object>();
        variables.put("config", freemarkerConfigConstants(properties));
        variables.put("methods", freemarkerMethods());
        variables.put("statics", BeansWrapper.getDefaultInstance().getStaticModels());
        variables.put("build", build());
        config.setFreemarkerVariables(variables);

        Properties props = new Properties();
        props.setProperty("auto_import", "layouts/layout.ftl as layout,libs/util.ftl as util");
        props.setProperty("output_encoding", "UTF-8");

        config.setFreemarkerSettings(props);
        return config;
    }

    @Bean
    Map build() {
        Map<String, String> buildMap = new HashMap<String, String>();
        buildMap.put("version", Build.getVersion());
        buildMap.put("timestamp", Build.getTimestamp());
        buildMap.put("buildId", Build.getUniqueBuildId());
        return buildMap;
    }


    @Bean
    Map freemarkerMethods() {
        Map<String, TemplateMethodModelEx> methods = new HashMap<String, TemplateMethodModelEx>();
        methods.put("nlsFor", getNlsMethod());
        methods.put("getUser", getUserMethod());
        methods.put("url", getUrlMethod());
        return methods;
    }

    @Bean
    Map freemarkerConfigConstants(Properties properties) {
        Map<String, Object> constants = new HashMap<String, Object>();
        constants.put("debug", Boolean.parseBoolean(properties.getProperty("web.debug")));
        constants.put("isProduction", Boolean.parseBoolean(properties.getProperty("is.production.env")));
        constants.put("isDevelopment", Boolean.parseBoolean(properties.getProperty("is.development.env")));
        constants.put("hostName", properties.getProperty("com.sharepast.host.name"));
        return constants;
    }


    @Bean
    TemplateMethodModelEx getNlsMethod() {
        return new NlsMethod();
    }

    @Bean
    TemplateMethodModelEx getUserMethod() {
        return new GetUserMethod();
    }

    @Bean
    TemplateMethodModelEx getUrlMethod() {
        return new GetUserMethod();
    }

}
