package com.sharepast.http;

import com.google.common.collect.Lists;
import com.sharepast.spring.SpringConfiguration;
import com.sharepast.spring.config.WebMVCConfig;
import com.sun.tools.javac.util.List;
import grails.util.BuildSettings;
import grails.util.BuildSettingsHolder;
import org.codehaus.groovy.grails.commons.ApplicationAttributes;
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext;
import org.codehaus.groovy.grails.web.context.GrailsConfigUtils;
import org.codehaus.groovy.grails.web.filters.HiddenHttpMethodFilter;
import org.codehaus.groovy.grails.web.mapping.filter.UrlMappingsFilter;
import org.codehaus.groovy.grails.web.pages.GroovyPagesServlet;
import org.codehaus.groovy.grails.web.servlet.ErrorHandlingServlet;
import org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequestFilter;
import org.codehaus.groovy.grails.web.sitemesh.GrailsPageFilter;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.io.File;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/1/12
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates.
 */

// Implementations of this SPI will be detected automatically by SpringServletContainerInitializer,
// which itself is bootstrapped automatically by any Servlet 3.0 container.

public class WebAppConfigurator implements WebApplicationInitializer {

    public static final int HTTP_SESSION_TIMEOUT = 30; //in minutes

    @Override
    public void onStartup(ServletContext container) throws ServletException {

        Environment env = SpringConfiguration.getInstance().getBean(Environment.class);

        //Grails is using this property to resolve base dir of the project
        System.setProperty(BuildSettings.APP_BASE_DIR, env.getProperty("grails.base"));
        if (env.acceptsProfiles("test")) {
            System.setProperty(grails.util.Environment.KEY, "test");
        } else if (env.acceptsProfiles("development")) {
            System.setProperty(grails.util.Environment.KEY, "development");
        } else {
            System.setProperty(grails.util.Environment.KEY, "production");
        }


        BuildSettings buildSettings = new BuildSettings(null, new File(env.getProperty("grails.base")));
        buildSettings.loadConfig();
        BuildSettingsHolder.setSettings(buildSettings);

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.setServletContext(container);
        applicationContext.setParent(SpringConfiguration.getInstance().getAppContext());
        applicationContext.register(WebMVCConfig.class);
        applicationContext.refresh();

        container.setAttribute(ApplicationAttributes.APPLICATION_CONTEXT, applicationContext);
        GrailsWebApplicationContext grailsWebContext = (GrailsWebApplicationContext) GrailsConfigUtils.configureWebApplicationContext(container, applicationContext);

        WebAppContext webAppContext = (WebAppContext)((WebAppContext.Context) container).getContextHandler();

        // RequestContextListener is to add session scope in spring
        webAppContext.getSessionHandler().getSessionManager().setMaxInactiveInterval(HTTP_SESSION_TIMEOUT * 60);

        //see http://jira.codehaus.org/browse/JETTY-467
        webAppContext.setInitParameter(SessionManager.__SessionIdPathParameterNameProperty, "none");
        webAppContext.setContextPath("/");

        ErrorPageErrorHandler errorHandler = (ErrorPageErrorHandler)webAppContext.getErrorHandler();
        errorHandler.addErrorPage("500", "/grails-errorhandler");
        errorHandler.addErrorPage("404", "/grails-errorhandler");
        errorHandler.addErrorPage("403", "/grails-errorhandler");

        // RequestContextListener is to add session scope in spring
        container.addListener(RequestContextListener.class);
        container.addListener(HttpSessionEventPublisher.class);

        container.addFilter("urlMapping", new UrlMappingsFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, "/*");

        // sitemesh
        FilterRegistration sitemeshFilter = container.addFilter("sitemesh", new GrailsPageFilter());
        sitemeshFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), false, "/*");

        if ( env.acceptsProfiles("development")) {
            container.addFilter("'ResourcesDevModeFilter'", "org.grails.plugin.resource.DevModeSanityFilter")
                    .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
        }

        container.addFilter("grailsWebRequestFilter", new GrailsWebRequestFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), false, "/*");

        container.addFilter("hiddenHttpMethod", new HiddenHttpMethodFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, "/*");

        FilterRegistration adHocResourcesPluginFilter = container.addFilter("AdHocResourcesPluginFilter", "org.grails.plugin.resource.ProcessingFilter");
        adHocResourcesPluginFilter.setInitParameter("adhoc", "true");
        adHocResourcesPluginFilter.addMappingForUrlPatterns(null, false, "/images/*", "/css/*", "/js/*", "/plugins/*");

        container.addFilter("DeclaredResourcesPluginFilter", "org.grails.plugin.resource.ProcessingFilter")
                .addMappingForUrlPatterns(null, false, "/static/*");

        FilterRegistration characterEncodingFilter = container.addFilter("charEncodingFilter", new CharacterEncodingFilter());
        characterEncodingFilter.setInitParameters(getCharacterEncodingFilterParam());
        characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");

        // spring security
        container.addFilter("securityFilter", new DelegatingFilterProxy("springSecurityFilterChain", grailsWebContext))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.ERROR, DispatcherType.REQUEST, DispatcherType.FORWARD), false, "/*");

        //spring dispatcher
        ServletRegistration.Dynamic dispatcher = container.addServlet("spring", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("*.action");

        ServletRegistration.Dynamic grailsDispatcher = container.addServlet("grails", new GrailsDispatcherServlet());
        grailsDispatcher.setLoadOnStartup(1);
        grailsDispatcher.addMapping("*.dispatch");

        container.addServlet("grails-errorhandler", new ErrorHandlingServlet()).addMapping("/grails-errorhandler");

        ServletRegistration.Dynamic gspServlet = container.addServlet("gsp", new GroovyPagesServlet());
        // Allows developers to view the intermediate source code, when they pass a spillGroovy argument in the URL
        if (env.acceptsProfiles("development")) {
            gspServlet.setInitParameter("showSource", "1");
        }
        gspServlet.addMapping("*.gsp");


    }

    private Map<String, String> getCharacterEncodingFilterParam() {
        Map<String, String> filterParam = new HashMap<String, String>();
        filterParam.put("encoding", "UTF-8");
        filterParam.put("forceEncoding", "true");
        return filterParam;
    }

    private Map<String, String> getSiteMeshFilterParam(String siteMeshConfig) {
        Map<String, String> filterParam = new HashMap<String, String>();
        filterParam.put("configFile", siteMeshConfig);
        return filterParam;
    }


}
