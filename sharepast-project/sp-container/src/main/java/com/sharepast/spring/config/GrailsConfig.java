package com.sharepast.spring.config;

import grails.web.UrlConverter;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsApplicationFactoryBean;
import org.codehaus.groovy.grails.commons.GrailsResourceLoaderFactoryBean;
import org.codehaus.groovy.grails.commons.spring.GrailsResourceHolder;
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator;
import org.codehaus.groovy.grails.compiler.support.GrailsResourceLoader;
import org.codehaus.groovy.grails.plugins.GrailsPluginManager;
import org.codehaus.groovy.grails.plugins.GrailsPluginManagerFactoryBean;
import org.codehaus.groovy.grails.web.pages.DefaultGroovyPagesUriService;
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine;
import org.codehaus.groovy.grails.web.pages.GroovyPagesUriService;
import org.codehaus.groovy.grails.web.pages.discovery.GrailsConventionGroovyPageLocator;
import org.codehaus.groovy.grails.web.pages.discovery.GroovyPageLocator;
import org.codehaus.groovy.grails.web.servlet.view.GrailsViewResolver;
import org.codehaus.groovy.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/9/12
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class GrailsConfig {

    @Bean
    public GrailsApplicationFactoryBean grailsApplication(GrailsResourceLoader resourceLoader,
                                                          @Value("${grails.descriptor}") FileSystemResource grailsDescriptor) {
        GrailsApplicationFactoryBean grailsApp = new GrailsApplicationFactoryBean();
        grailsApp.setGrailsResourceLoader(resourceLoader);
        grailsApp.setGrailsDescriptor(grailsDescriptor);
        return grailsApp;
    }

    /*@Bean(name = "groovyPageLayoutFinder")
    public GroovyPageLayoutFinder groovyPageLayoutFinder(GrailsViewResolver viewResolver) {
        GroovyPageLayoutFinder layoutFinder = new GroovyPageLayoutFinder();
        layoutFinder.setGspReloadEnabled(true);
        layoutFinder.setCacheEnabled(false);
        layoutFinder.setEnableNonGspViews(false);
        layoutFinder.setViewResolver(viewResolver);
        return layoutFinder;
    }*/


    /*@Bean(name = "groovyPagesUriService")
    public GroovyPagesUriService groovyPagesUriService() {
        return new DefaultGroovyPagesUriService();
    }*/


   /* @Bean(name = "groovyPageLocator")
    public GrailsConventionGroovyPageLocator groovyPageLocator(GrailsPluginManager pluginManager, ApplicationContext ctx) {
        GrailsConventionGroovyPageLocator locator = new GrailsConventionGroovyPageLocator();
        locator.setPluginManager(pluginManager);
        locator.setReloadEnabled(true);
        return locator;
    }*/

    /*@Bean(name = "jspViewResolver")
    public GrailsViewResolver grailsViewResolver(GroovyPagesTemplateEngine templateEngine, GrailsConventionGroovyPageLocator pageLocator) {
        GrailsViewResolver viewResolver = new GrailsViewResolver();
        viewResolver.setGroovyPageLocator(pageLocator);
        viewResolver.setTemplateEngine(templateEngine);
        return viewResolver;
    }*/

    /*@Bean(name = "groovyPagesTemplateEngine")
    public GroovyPagesTemplateEngine groovyPagesTemplateEngine() {
        return new GroovyPagesTemplateEngine();
    }*/

    @Bean(name = "characterEncodingFilter")
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("utf-8");
        return encodingFilter;
    }

    @Bean(name = "grailsConfigurator")
    public GrailsRuntimeConfigurator grailsConfigurator(GrailsApplication grailsApplication, GrailsPluginManager pluginManager, ApplicationContext ctx) {
        GrailsRuntimeConfigurator grailsRuntimeConfigurator = new GrailsRuntimeConfigurator(grailsApplication, ctx);
        grailsRuntimeConfigurator.setPluginManager(pluginManager);
        return grailsRuntimeConfigurator;
    }

    @Bean
    public GrailsResourceLoaderFactoryBean grailsResourceLoader() {
        return new GrailsResourceLoaderFactoryBean();
    }

    /*@Bean
    public GrailsResourceHolder grailsResourceHolder(@Value("${grails.resources}") FileSystemResource grailsResources) {
        GrailsResourceHolder resourceHolder = new GrailsResourceHolder();
        resourceHolder.setResources(new Resource[] {grailsResources});
        return resourceHolder;
    }*/

    @Bean
    public GrailsPluginManagerFactoryBean pluginManager(ApplicationContext ctx, GrailsApplication grailsApplication,
                                                        @Value("${grails.descriptor}") FileSystemResource grailsDescriptor) {
        GrailsPluginManagerFactoryBean grailsPluginManagerFactoryBean = new GrailsPluginManagerFactoryBean();
        grailsPluginManagerFactoryBean.setApplication(grailsApplication);
        grailsPluginManagerFactoryBean.setApplicationContext(ctx);
        grailsPluginManagerFactoryBean.setGrailsDescriptor(grailsDescriptor);
        return grailsPluginManagerFactoryBean;
    }

}
