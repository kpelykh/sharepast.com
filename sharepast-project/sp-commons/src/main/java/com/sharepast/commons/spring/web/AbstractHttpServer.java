package com.sharepast.commons.spring.web;

import com.sharepast.commons.spring.ContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/4/12
 * Time: 8:47 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractHttpServer extends ContextListener implements DisposableBean {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected Server server;

    @Override
    public void shutdown(ApplicationContext context) {
        Assert.notNull(server);

        try {
            server.stop();
        } catch (Exception e) {
            LOG.error("Error occurred while stopping the Jetty server", e);
        }
    }

    public void destroy() throws Exception {
        if (server != null)
            server.stop();
    }


    public Server getJettyServer() {
        return server;
    }


    public void start() throws Exception {
        Assert.notNull(server);

        server.start();
    }


    protected void initConnnector(Server server, HttpConfigs config) {

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

    protected void initSSL(Server server, HttpConfigs config) {

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
        factory.setKeyStorePassword(config.getKeyStorePassword());
        factory.setKeyManagerPassword(config.getKeyStoreManagerPassword());
        factory.setTrustStorePassword(config.getTrustStorePassword());

        SslSelectChannelConnector httpsConnector = new SslSelectChannelConnector(factory);
        httpsConnector.setPort(config.getSslport());
        httpsConnector.setHost(config.getHttpHost());

        server.addConnector(httpsConnector);
    }


}