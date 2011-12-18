package com.sharepast.its.common;

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
 * Date: 4/11/11
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class TestServerConfig {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    @Qualifier("testJettyServer")
    private Server webServer;

    @Autowired
    @Qualifier("testWebAppContext")
    private WebAppContext webAppContext;

    /*
       The idea is that DispatcherServlet's context is child context of  GenericWebApplicationContext
       which in turn is a child of ClassPathXmlApplicationContext.
       Explicitly invoking setParent() gives you the access to all beans from the web application context.
       see details: http://techieboycdo.blogspot.com/2010/05/embedded-jetty-in-spring-mvc-iocdi.html
    */
    @Bean
    public Server testHttpServer() {

        GenericWebApplicationContext webApplicationContext = new GenericWebApplicationContext();
        webApplicationContext.setServletContext(webAppContext.getServletContext());
        webApplicationContext.setParent(appContext);

        webAppContext.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
        webApplicationContext.refresh();

        return webServer;
    }
}
