package com.sharepast.jms.spring;

import com.sharepast.commons.spring.web.AbstractHttpServer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/4/12
 * Time: 9:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Component(value = "activemq-server")
public class JmsHttpServer extends AbstractHttpServer {

    private @Value("${activemq.base}/webapps") String res;

    @Autowired ApplicationContext ctx;
    @Autowired Environment env;

    @Override
    public void afterStartup(ApplicationContext context) {

        // Create an instance of Jetty Web server
        server = new Server();
        server.setGracefulShutdown(5000);
        server.setStopAtShutdown(true);
        server.setThreadPool(new QueuedThreadPool(50));

        initConnnector(server);

        try {
            initWebappContext(server);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        System.setProperty("activemq.home", env.getProperty("activemq.home") );
        System.setProperty("activemq.base", env.getProperty("activemq.base") );


    }

    protected void initWebappContext(Server server) throws IOException {

        HandlerCollection handlerCollection = new HandlerCollection();

        // Context
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        WebAppContext adminApp = new WebAppContext();
        adminApp.setResourceBase(res + "/admin");
        adminApp.setLogUrlOnStart(true);
        adminApp.setContextPath("/admin");
        adminApp.setThrowUnavailableOnStartupException(true);

        WebAppContext camelApp = new WebAppContext();
        camelApp.setResourceBase(res + "/camel");
        camelApp.setLogUrlOnStart(true);
        camelApp.setContextPath("/camel");
        camelApp.setThrowUnavailableOnStartupException(true);

        WebAppContext fileserverApp = new WebAppContext();
        fileserverApp.setResourceBase(res + "/fileserver");
        fileserverApp.setLogUrlOnStart(true);
        fileserverApp.setContextPath("/fileserver");
        fileserverApp.setThrowUnavailableOnStartupException(true);

        WebAppContext rootApp = new WebAppContext();
        rootApp.setResourceBase(res);
        rootApp.setLogUrlOnStart(true);
        rootApp.setContextPath("/");
        rootApp.setThrowUnavailableOnStartupException(true);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase(res);

        contexts.setHandlers(new Handler[]{adminApp, camelApp, fileserverApp, rootApp, resourceHandler});

        DefaultHandler defaultHandler = new DefaultHandler();
        defaultHandler.setServeIcon(false);

        handlerCollection.setHandlers(new Handler[]{contexts, defaultHandler});

        server.setHandler(handlerCollection);
    }

    private void initConnnector(Server server) {

        // Channel connectors
        SelectChannelConnector httpConnector = new SelectChannelConnector();
        httpConnector.setHost("0.0.0.0");
        httpConnector.setPort(8161);
        httpConnector.setMaxIdleTime(30000);
        httpConnector.setAcceptors(2);
        server.setConnectors(new Connector[] {httpConnector});

    }

}
