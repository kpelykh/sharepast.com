package com.sharepast.servlet;

import com.sharepast.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/1/12
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebAppInitializer implements WebApplicationInitializer {

    @Autowired
    private ApplicationContext appContext;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.setParent(appContext);
        root.register(WebConfig.class);

        // Manages the lifecycle of the root application context
        servletContext.addListener(new ContextLoaderListener(root));

        ServletRegistration.Dynamic appServlet =
                servletContext.addServlet("spring", new DispatcherServlet(root));
        appServlet.setLoadOnStartup(1);
        Set<String> mappingConflicts = appServlet.addMapping("/*");
        if (!mappingConflicts.isEmpty()) {
            throw new IllegalStateException(mappingConflicts.toString());
        }
    }
}
