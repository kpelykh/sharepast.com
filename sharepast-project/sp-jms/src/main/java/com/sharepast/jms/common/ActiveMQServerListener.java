package com.sharepast.jms.common;

import com.sharepast.util.spring.ContextListener;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/8/11
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActiveMQServerListener extends ContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQServerListener.class);

    @Autowired
    @Qualifier("activemq-server")
    private Server activeMQServer;

    private @Value("#{path['activemq.home']}") String activemqHome;
    private @Value("#{path['activemq.base']}") String activemqBase;

    @Override
    public void afterStartup(ApplicationContext context) {
        try {
            if (!StringUtils.isEmpty(activemqHome)) {
                System.setProperty("activemq.home", activemqHome );
            }
            if (!StringUtils.isEmpty(activemqBase)) {
                System.setProperty("activemq.base", activemqBase );
            }
            activeMQServer.start();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void beforeShutdown(ApplicationContext context) {
    }

    @Override
    public void afterShutdown() {
    }

    @Override
    public void shutdown(ApplicationContext context) {
        try {
          activeMQServer.stop();
      } catch (Exception e) {
          LOG.error(e.getMessage(), e);
      }
    }

}
