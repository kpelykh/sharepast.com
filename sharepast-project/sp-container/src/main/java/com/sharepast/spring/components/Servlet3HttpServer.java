package com.sharepast.spring.components;

import com.sharepast.commons.spring.web.AbstractHttpServer;
import com.sharepast.commons.spring.web.HttpConfigs;
import com.sharepast.http.SPAnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 7/9/12
 * Time: 12:23 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Import({HttpConfigs.class})
public class Servlet3HttpServer extends AbstractHttpServer {

    @Autowired
    protected HttpConfigs configs;

    @Autowired protected ApplicationContext appContext;

    @Override
    public void afterStartup(ApplicationContext context) {

        // Create an instance of Jetty Web server
        server = new Server();
        server.setGracefulShutdown(5000);
        server.setStopAtShutdown(true);
        server.setThreadPool(new QueuedThreadPool(50));

        initConnnector(server, configs);

        try {
            initWebappContext(server, configs);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    protected void initWebappContext(Server server, HttpConfigs configs) throws IOException {
        WebAppContext ctxUI = new WebAppContext();
        ctxUI.setThrowUnavailableOnStartupException(true);

        //see http://jira.codehaus.org/browse/JETTY-467
        ctxUI.setResourceBase(configs.getResourceBase());
        //ctxUI.setDefaultsDescriptor(configs.getWebDefault());

        ctxUI.setAttribute(WebInfConfiguration.CONTAINER_JAR_PATTERN, configs.getContainerJarPattern());

        ctxUI.setConfigurations(new Configuration[]{new WebInfConfiguration(), new WebXmlConfiguration(), new SPAnnotationConfiguration()});

        server.setHandler(ctxUI);
    }

}
