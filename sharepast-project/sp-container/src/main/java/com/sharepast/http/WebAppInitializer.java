package com.sharepast.http;

import com.sharepast.grails.web.context.GrailsContextLoader;
import com.sharepast.grails.web.context.GrailsContextLoaderListener;
import com.sharepast.spring.SpringConfiguration;
import com.sharepast.spring.config.GrailsConfig;
import com.sharepast.spring.config.WebMVCConfig;
import grails.util.GrailsUtil;
import org.codehaus.groovy.grails.commons.ApplicationAttributes;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext;
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator;
import org.codehaus.groovy.grails.web.context.GrailsConfigUtils;
import org.codehaus.groovy.grails.web.filters.HiddenHttpMethodFilter;
import org.codehaus.groovy.grails.web.mapping.filter.UrlMappingsFilter;
import org.codehaus.groovy.grails.web.pages.GroovyPagesServlet;
import org.codehaus.groovy.grails.web.servlet.ErrorHandlingServlet;
import org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequestFilter;
import org.codehaus.groovy.grails.web.sitemesh.GrailsPageFilter;
import org.springframework.core.env.Environment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.IntrospectorCleanupListener;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/1/12
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates.
 */

// Implementations of this SPI will be detected automatically by SpringServletContainerInitializer,
// which itself is bootstrapped automatically by any Servlet 3.0 container.

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {

        Environment env = SpringConfiguration.getInstance().getBean(Environment.class);
        //String siteMeshConfig = env.getProperty("sitemesh.config");

        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.setServletContext(container);
        webApplicationContext.setParent(SpringConfiguration.getInstance().getAppContext());
        webApplicationContext.register(WebMVCConfig.class);
        webApplicationContext.refresh();

        //container.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
        container.setAttribute(ApplicationAttributes.APPLICATION_CONTEXT, webApplicationContext);
        WebApplicationContext wac = GrailsConfigUtils.configureWebApplicationContext(container, webApplicationContext);

        container.addListener(RequestContextListener.class);

        // sitemesh
        FilterRegistration sitemeshFilter = container.addFilter("sitemesh", new GrailsPageFilter());
        sitemeshFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), false, "/*");

        // spring security
        container.addFilter("securityFilter", new DelegatingFilterProxy("springSecurityFilterChain", wac))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.ERROR, DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/*");

        FilterRegistration characterEncodingFilter = container.addFilter("characterEncodingFilter", new CharacterEncodingFilter());
        characterEncodingFilter.setInitParameters(getCharacterEncodingFilterParam());
        characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");

        container.addFilter("urlMapping", new UrlMappingsFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, "/*");
        container.addFilter("hiddenHttpMethod", new HiddenHttpMethodFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, "/*");
        container.addFilter("grailsWebRequestFilter", new GrailsWebRequestFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ERROR), false, "/*");

        //spring dispatcher
        ServletRegistration.Dynamic dispatcher = container.addServlet("spring", new DispatcherServlet(webApplicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("*.action");

        ServletRegistration.Dynamic grailsDispatcher = container.addServlet("grails", new GrailsDispatcherServlet());
        grailsDispatcher.setLoadOnStartup(1);
        grailsDispatcher.addMapping("*.dispatch");

        container.addServlet("grails-errorhandler", new ErrorHandlingServlet()).addMapping("/grails-errorhandler");

        ServletRegistration.Dynamic gspServlet = container.addServlet("gsp", new GroovyPagesServlet());
        // Allows developers to view the intermediate source code, when they pass a spillGroovy argument in the URL
        gspServlet.setInitParameter("showSource", "1");
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
