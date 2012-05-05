package com.sharepast.http;

import com.sharepast.spring.SpringConfiguration;
import com.sharepast.spring.config.WebMVCConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;
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
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.setServletContext(servletContext);
        webApplicationContext.setParent(SpringConfiguration.getInstance().getAppContext());
        webApplicationContext.register(WebMVCConfig.class);

        // RequestContextListener is to add session scope in spring
        servletContext.addListener(RequestContextListener.class);

        ServletRegistration.Dynamic appServlet =
                servletContext.addServlet("spring", new DispatcherServlet(webApplicationContext));
        appServlet.setLoadOnStartup(1);
        Set<String> mappingConflicts = appServlet.addMapping("/*");
        if (!mappingConflicts.isEmpty()) {
            throw new IllegalStateException(mappingConflicts.toString());
        }

        // spring security
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy("springSecurityFilterChain", webApplicationContext);
        FilterRegistration fr = servletContext.addFilter("securityFilter", delegatingFilterProxy);
        fr.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/*");

    }
}
