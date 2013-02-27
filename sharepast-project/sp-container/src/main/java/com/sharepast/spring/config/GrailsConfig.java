package com.sharepast.spring.config;

import grails.util.BuildSettingsHolder;
import grails.util.PluginBuildSettings;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsApplicationFactoryBean;
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator;
import org.codehaus.groovy.grails.compiler.support.GrailsResourceLoader;
import org.codehaus.groovy.grails.compiler.support.GrailsResourceLoaderHolder;
import org.codehaus.groovy.grails.plugins.GrailsPluginManager;
import org.codehaus.groovy.grails.plugins.GrailsPluginManagerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
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
@Import(GrailsProjectWatcher.class)
public class GrailsConfig {

    @Autowired Environment env;

    @Bean
    public GrailsApplicationFactoryBean grailsApplication(GrailsResourceLoader resourceLoader,
                                                          @Value("${grails.descriptor}") Resource grailsDescriptor) {
        GrailsApplicationFactoryBean grailsApp = new GrailsApplicationFactoryBean();
        grailsApp.setGrailsResourceLoader(resourceLoader);
        //grailsApp.setGrailsDescriptor(grailsDescriptor);
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
    public GrailsResourceLoader grailsResourceLoader() {
        PluginBuildSettings pluginBuildSettings = new PluginBuildSettings(BuildSettingsHolder.getSettings());
        GrailsResourceLoaderHolder.setResourceLoader(new GrailsResourceLoader(pluginBuildSettings.getArtefactResourcesForCurrentEnvironment()));
        return GrailsResourceLoaderHolder.getResourceLoader();
    }

    @Bean
    public GrailsPluginManagerFactoryBean pluginManager(ApplicationContext ctx, GrailsApplication grailsApplication,
                                                        @Value("${grails.descriptor}") Resource grailsDescriptor) {
        GrailsPluginManagerFactoryBean grailsPluginManagerFactoryBean = new GrailsPluginManagerFactoryBean();
        grailsPluginManagerFactoryBean.setApplication(grailsApplication);
        grailsPluginManagerFactoryBean.setApplicationContext(ctx);
        grailsPluginManagerFactoryBean.setGrailsDescriptor(grailsDescriptor);
        return grailsPluginManagerFactoryBean;
    }

}
