package com.sharepast;

import com.sharepast.spring.SPConfigurator;
import com.sharepast.spring.components.WebHttpServer;
import com.sharepast.commons.spring.SpringConfiguration;
import com.sharepast.commons.spring.config.PropertiesConfig;
import com.sharepast.commons.spring.web.AbstractHttpServer;
import com.sharepast.commons.util.Util;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/13/11
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bootstrap {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Bootstrap.class);

    private static final String START_PLATFORM = "platform";
    private static final String STOP_PLATFORM = "stop";
    private static final String START_JMS = "jms";
    private static final String STOP_JMS = "stopjms";
    private static final String GET_PROPERTY = "property";

    /**
     * Platform HTTP server
     */
    private static AbstractHttpServer httpServer;
    private static int httpShutdownPort;

    public static void main(String[] args) throws Exception {
        try {

            OptionParser parser = new OptionParser();

            ResourceBundleMessageSource rb = new ResourceBundleMessageSource();
            rb.setBasename("com.sharepast.startup.messages");


            OptionSpec<Void> startPlatformCmd =
                    parser.accepts(START_PLATFORM, rb.getMessage("runner.cli.option.platform.start", null, Locale.getDefault()));

            OptionSpec<Void> stopPlatformCmd =
                    parser.accepts(STOP_PLATFORM, rb.getMessage("runner.cli.option.platform.stop", null, Locale.getDefault()));

            OptionSpec<Void> startJmsCmd =
                    parser.accepts(START_JMS, rb.getMessage("runner.cli.option.jms.start", null, Locale.getDefault()));

            OptionSpec<Void> stopJmsCmd =
                    parser.accepts(STOP_JMS, rb.getMessage("runner.cli.option.jms.stop", null, Locale.getDefault()));

            OptionSpec<String> propertyCmd =
                    parser.accepts(GET_PROPERTY, rb.getMessage("runner.cli.option.property", null, Locale.getDefault())).withRequiredArg().ofType(String.class).describedAs("http.port,zookeeper.address,...");


            OptionSet options = parser.parse(args);

            OptionSpec[] commands = { startPlatformCmd, startJmsCmd, stopJmsCmd, stopPlatformCmd, propertyCmd };

            int numCommands = 0;
            for (OptionSpec command : commands)
                if (options.has(command))
                    numCommands++;

            if (numCommands == 0) {
                System.err.println("Must specify a command");
                usage(parser);
            } else if (numCommands > 1) {
                System.err.println("Multiple commands detected, must specify a single command");
                usage(parser);
            }

            if (options.has(propertyCmd)) {
                printProperty(options.valueOf(propertyCmd));
            } else if (options.has(START_PLATFORM)) {
                startPlatform();
            } else if (options.has(START_JMS)) {
                startJms();
            } else if (options.has(STOP_PLATFORM)) {
                SpringConfiguration.getInstance().configure(PropertiesConfig.class);
                Environment env = SpringConfiguration.getInstance().getBean(Environment.class);
                int port = env.getProperty("platform.server.shutdown.port", Integer.class);
                sendShutdownCommand(port);
            } else if (options.has(STOP_JMS)) {
                SpringConfiguration.getInstance().configure(PropertiesConfig.class);
                Environment env = SpringConfiguration.getInstance().getBean(Environment.class);
                int port = env.getProperty("jms.server.shutdown.port", Integer.class, 11001);
                sendShutdownCommand(port);
            } else {
                usage(parser);
            }

        } catch (OptionException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    private static void startPlatform() throws Exception {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        try {
            new SPConfigurator(SPConfigurator.PLATFORM).configure();
            httpServer = SpringConfiguration.getInstance().getBean(WebHttpServer.class);
            Environment env = SpringConfiguration.getInstance().getBean(Environment.class);
            httpShutdownPort = env.getProperty("platform.server.shutdown.port", Integer.class);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }

        startHttpServer();
    }


    private static void startJms() throws Exception {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        try {
            new SPConfigurator(SPConfigurator.JMS).configure();
            httpServer = SpringConfiguration.getInstance().getBean(AbstractHttpServer.class, "activemq-server");
            Environment env = SpringConfiguration.getInstance().getBean(Environment.class);
            httpShutdownPort = env.getProperty("platform.server.shutdown.port", Integer.class);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }

        startHttpServer();
    }


    private static void printProperty(String propertyName) throws Exception {
        System.out.println();
        try {
            new SPConfigurator(SPConfigurator.PROPERTIES).configure();
            Environment env = SpringConfiguration.getInstance().getBean(Environment.class);
            System.out.println(String.format("============= PROPERTY=%s =============", propertyName));
            if (StringUtils.isEmpty(env.getProperty(propertyName))) {
                System.out.println(); //per Cramer's request will return CR if property doesn't exist
            } else {
                System.out.println(env.getProperty(propertyName));
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            System.exit(-1);
        }
    }


    private static void sendShutdownCommand(int port) throws IOException {
        if (!Util.isPortAvailable(port)) {
            Socket s = new Socket(InetAddress.getByName("127.0.0.1"), port);
            OutputStream out = s.getOutputStream();
            System.out.println(String.format("*** sending stop request to port %s", port));
            out.write(("\r\n").getBytes());
            out.flush();
            s.close();
        } else {
            LOG.warn(String.format("Port %s is taken by any process", port));
        }
    }

    private static void startHttpServer() {

        try {
            Thread monitor = new MonitorThread(httpShutdownPort);
            monitor.start();
            httpServer.start();
        } catch (Exception e) {
            LOG.error("Error starting Jetty server", e);
            System.exit(100);
        }

        LOG.info("HTTP server " + httpServer.getJettyServer().getState() + " on port " + httpServer.getJettyServer().getConnectors()[0].getPort());

    }

    static class ShutdownHook extends Thread {
        public void run() {
            SpringConfiguration.getInstance().shutdown();
        }
    }


    private static class MonitorThread extends Thread {

        private ServerSocket socket;

        public MonitorThread(int httpShutdownPort) {
            setDaemon(true);
            setName("ShutdownMonitor");
            try {
                Util.isPortAvailable(httpShutdownPort);
                socket = new ServerSocket(httpShutdownPort, 1);
                LOG.info(String.format("SHUTDOWN MONITOR started on port %s", httpShutdownPort));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            Socket accept;
            try {
                accept = socket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                reader.readLine();
                LOG.info("*** ShutdownMonitor: stopping server");
                accept.close();
                socket.close();

                SpringConfiguration.getInstance().shutdown();

            } catch (Throwable e) {
                LOG.error("Error occured in ShutdownMonitor thread: ", e);
                System.exit(-1);
            }
        }
    }

    private static void usage(OptionParser parser) throws IOException {
        System.err.println("Usage: Bootstrap <command> <options>");
        parser.printHelpOn(System.err);
        System.err.println();
        System.exit(-1);
    }

    public static void stopRunner() throws Exception {
        SpringConfiguration.getInstance().shutdown();
    }

}