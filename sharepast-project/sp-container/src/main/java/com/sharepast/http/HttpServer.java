package com.sharepast.http;

import com.sharepast.spring.ContextListener;
import org.eclipse.jetty.http.ssl.SslContextFactory;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/15/12
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Import({HttpConfigs.class})
public class HttpServer extends ContextListener implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);

    private Server jettyServer;

    @Autowired
    public HttpServer(HttpConfigs configs) {
        // Create an instance of Jetty Web server
        jettyServer = new Server();
        jettyServer.setGracefulShutdown(1000);
        jettyServer.setStopAtShutdown(true);
        jettyServer.setThreadPool(new QueuedThreadPool(50));

        initConnnector(jettyServer, configs);

        try {
            initWebappContext(jettyServer,configs);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void initWebappContext(Server server, HttpConfigs configs)
            throws IOException {

        WebAppContext context = new WebAppContext();
        context.setContextPath(configs.getContextPath());
        context.setDefaultsDescriptor(configs.getWebDefault());
        context.setResourceBase(configs.getResourceBase());
        context.setParentLoaderPriority(true);

        //see http://jira.codehaus.org/browse/JETTY-467
        context.setInitParameter(SessionManager.__SessionIdPathParameterNameProperty, "none");

        context.setConfigurations(new Configuration[]{
                new SPAnnotationConfiguration(), new WebXmlConfiguration(),
                new WebInfConfiguration(), new TagLibConfiguration(),
                new PlusConfiguration(), new MetaInfConfiguration(),
                new FragmentConfiguration(), new EnvConfiguration()});


        server.setHandler(context);

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


    public void start() throws Exception {
        Assert.notNull(jettyServer);

        jettyServer.start();

    }

    @Override
    public void afterStartup(ApplicationContext context) {
    }

    public void shutdown(ApplicationContext context) {
        Assert.notNull(jettyServer);

        try {
            jettyServer.stop();
        } catch (Exception e) {
            LOG.error("Error occurred while stopping the Jetty server", e);
        }

    }

    @Override
    public void afterShutdown() {
    }

    public Server getJettyServer() {
        return jettyServer;
    }

    @Override
    public void destroy() throws Exception {
        jettyServer.stop();
    }
}
