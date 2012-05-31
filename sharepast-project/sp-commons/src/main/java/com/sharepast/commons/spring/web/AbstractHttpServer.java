package com.sharepast.commons.spring.web;

import com.sharepast.commons.spring.ContextListener;
import org.eclipse.jetty.server.Server;
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

}