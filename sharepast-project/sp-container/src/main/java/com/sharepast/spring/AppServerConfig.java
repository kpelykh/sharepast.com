package com.sharepast.spring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/13/11
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */

@Configuration
public class AppServerConfig {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    @Qualifier("jettyServer")
    private Server webServer;

    @Autowired
    private WebAppContext webAppContext;

    /*
       The idea is that DispatcherServlet's context is child context of  GenericWebApplicationContext
       which in turn is a child of ClassPathXmlApplicationContext.
       Explicitly invoking setParent() gives you the access to all beans from the web application context.
       see details: http://techieboycdo.blogspot.com/2010/05/embedded-jetty-in-spring-mvc-iocdi.html
    */

    //Should be same as in StartupProperties.APP_SERVER_NAME
    @Bean
    public Server appHttpServer() {

        GenericWebApplicationContext webApplicationContext = new GenericWebApplicationContext();
        webApplicationContext.setServletContext(webAppContext.getServletContext());
        webApplicationContext.setParent(appContext);

        webAppContext.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
        webApplicationContext.refresh();

        return webServer;
    }
}
