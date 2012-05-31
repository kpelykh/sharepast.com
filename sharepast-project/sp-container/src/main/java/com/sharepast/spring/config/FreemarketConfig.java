package com.sharepast.spring.config;

import com.sharepast.freemarker.GetUserMethod;
import com.sharepast.freemarker.NlsMethod;
import com.sharepast.freemarker.UrlMethod;
import com.sharepast.commons.util.Build;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.HashMap;
import java.util.List;
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
    FreeMarkerConfigurer freemarkerConfig(Environment env) throws TemplateModelException {
        FreeMarkerConfigurer config = new FreeMarkerConfigurer();
        config.setTemplateLoaderPath("/templates/");
        config.setDefaultEncoding("UTF-8");
        Map<String,Object> variables = new HashMap<String,Object>();
        variables.put("config", freemarkerConfigConstants(env));
        variables.put("methods", freemarkerMethods());
        variables.put("statics", BeansWrapper.getDefaultInstance().getStaticModels());
        variables.put("build", build());
        variables.put("enums", freemarkerEnums());
        config.setFreemarkerVariables(variables);


        Properties props = new Properties();
        props.setProperty("auto_import", "layouts/layout.ftl as layout,libs/util.ftl as util");
        props.setProperty("output_encoding", "UTF-8");

        config.setFreemarkerSettings(props);
        return config;
    }

    @Bean
    List<Build.ComponentInfo> build() {
        return Build.getComponents();
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
    Map freemarkerConfigConstants(Environment env) {
        Map<String, Object> constants = new HashMap<String, Object>();
        constants.put("debug", env.getProperty("web.debug", Boolean.class));
        constants.put("isProduction", env.getProperty("is.production.env", Boolean.class));
        constants.put("isDevelopment", env.getProperty("is.development.env", Boolean.class));
        constants.put("hostName", env.getProperty("com.sharepast.host.name"));
        return constants;
    }

    @Bean
    Map freemarkerEnums() throws TemplateModelException {
      Map<String, Object> constants = new HashMap<String, Object>();
      constants.put("ParamNameEnum", BeansWrapper.getDefaultInstance().getEnumModels().get("com.sharepast.constants.ParamNameEnum"));
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
        return new UrlMethod();
    }

}
