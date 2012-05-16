package com.sharepast.spring.config;

import com.sharepast.spring.ContextListener;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerConstants;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/8/12
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
@Component("hsqldb")
public class HSQLDServerConfig extends ContextListener implements InitializingBean, DisposableBean {

    private @Value("${hsqldb.location:/tmp/hsqldb}") FileSystemResource baseDBLocation;

    private @Value("${sp.db.schema:}") String spDbSchema;
    private @Value("${jdbc.driver}") String jdbcDriver;

    private org.hsqldb.Server server;

    @Autowired private Environment env;


    @Override
    public void afterPropertiesSet() throws Exception {
        if (!env.acceptsProfiles("test", "development")) return;
        if (!jdbcDriver.contains("hsqldb")) return;

        HsqlProperties props = new HsqlProperties();

        if (env.acceptsProfiles("test")) {
            props.setProperty("server.database.0", String.format("mem:%s", spDbSchema));
            props.setProperty("server.dbname.0", spDbSchema);
        } else {
            props.setProperty("server.database.0", String.format("%s/%s", baseDBLocation.getFile().getCanonicalPath(), spDbSchema));
            props.setProperty("server.dbname.0", spDbSchema);
        }

        server = new org.hsqldb.Server();
        server.setLogWriter(new PrintWriter(System.out));
        server.setAddress("127.0.0.1");

        try {
            server.setDaemon(true);
            server.setProperties(props);
        } catch (Exception e) {
            return;
        }

        server.start();

        int count = 0;
        while (server.getState() != ServerConstants.SERVER_STATE_ONLINE) {
            Thread.sleep(100);
            count++;
            if (count >= 20) {
                throw new IllegalStateException(String.format("Timed out when starting hsqldb in server mode, current server state = %s", server.getState()));
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        if (!env.acceptsProfiles("test", "development")) return;

        if (server != null) {
            server.shutdown();
            int count = 0;
            while (server.getState() != ServerConstants.SERVER_STATE_SHUTDOWN) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(String.format("Timed out when shutting down hsqldb, current server state = %s", server.getState()), e);
                }
                count++;
                if (count >= 20) {
                    throw new IllegalStateException(String.format("Timed out when shutting down hsqldb, current server state = %s", server.getState()));
                }
            }
        }
    }
}
