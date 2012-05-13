package com.sharepast.spring.components;

import com.sharepast.http.SPAnnotationConfiguration;
import com.sharepast.spring.web.HttpConfigs;
import com.sharepast.spring.web.AbstractHttpServer;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextListener;

import java.io.IOException;
import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/15/12
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Import({HttpConfigs.class})
public class WebHttpServer extends AbstractHttpServer {

    @Autowired protected HttpConfigs configs;

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

        //see http://jira.codehaus.org/browse/JETTY-467
        ctxUI.setResourceBase(configs.getResourceBase());
        ctxUI.setDefaultsDescriptor(configs.getWebDefault());

        ctxUI.setConfigurations(new Configuration[]{
                new SPAnnotationConfiguration(), new WebXmlConfiguration(),
                new WebInfConfiguration(), new TagLibConfiguration(),
                new MetaInfConfiguration(), new FragmentConfiguration()});

        server.setHandler(ctxUI);
    }

    private void initConnnector(Server server, HttpConfigs config) {

        // Channel connectors
        SelectChannelConnector httpConnector = new SelectChannelConnector();
        httpConnector.setHost(config.getHttpHost());
        httpConnector.setPort(config.getHttpPort());
        httpConnector.setMaxIdleTime(30000);
        httpConnector.setAcceptors(2);

        if (config.isSSLEnabled()) {
            httpConnector.setConfidentialPort(config.getSslport());
        }

        server.addConnector(httpConnector);

        if (config.isSSLEnabled()) {
            initSSL(server, config);
        }


    }

    private void initSSL(Server server, HttpConfigs config) {

        if (config.getKeystore() == null) {
            throw new IllegalStateException(
                    "you need to provide property 'keystore.path' and 'keystore.pass'");
        }

        if (config.getTrustStore() == null) {
            throw new IllegalStateException(
                    "you need to provide property 'truststore.path' and 'truststore.pass'");
        }

        SslContextFactory factory = new SslContextFactory();
        factory.setKeyStore(config.getKeystore());
        factory.setTrustStore(config.getTrustStore());
        factory.setKeyManagerPassword(config.getKeyPassword());

        SslSelectChannelConnector httpsConnector = new SslSelectChannelConnector(factory);
        httpsConnector.setPort(config.getSslport());
        httpsConnector.setHost(config.getHttpHost());

        server.addConnector(httpsConnector);
    }
}
