package com.sharepast;

import com.sharepast.http.HttpServer;
import com.sharepast.runners.JmsRunner;
import com.sharepast.spring.SpringConfiguration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import com.sharepast.runners.PlatformRunner;

import java.util.Enumeration;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/13/11
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bootstrapper {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Bootstrapper.class);

    private static final String CLI_START_PLATFORM = "p";
    private static final String CLI_START_JMS = "j";
    private static final String CLI_STOP = "t";

    /**
     * Platform HTTP server
     */
    private static HttpServer httpServer;

    /**
     * create CLI options
     *
     * @return Options
     */
    private static Options createOptions() {
        Options options = new Options();

        ResourceBundleMessageSource rb = new ResourceBundleMessageSource();
        rb.setBasename("com.sharepast.startup.Messages");

        options.addOption(CLI_START_PLATFORM, "platform", false, rb.getMessage("runner.cli.option.platform", null, Locale.getDefault()));
        options.addOption(CLI_START_JMS, "jms", false, rb.getMessage("runner.cli.option.jms", null, Locale.getDefault()));
        options.addOption(CLI_STOP, "stop", false, rb.getMessage("runner.cli.option.stop", null, Locale.getDefault()));

        return options;
    }

    /**
     * main entry point to the runner instance. It instantiates the server, configures all the routes and runs it
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
            throws Exception {
        Options options = createOptions();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args);

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        if (cmd.hasOption(CLI_STOP)) {
            stopRunner();
        } else if (cmd.hasOption(CLI_START_PLATFORM)) {
            startPlatform();
        } else if (cmd.hasOption(CLI_START_JMS)) {
            startJms();
        }
    }

    public static void startPlatform()
            throws Exception {
        try {
            SpringConfiguration.getInstance().configure(PlatformRunner.class);
            httpServer = SpringConfiguration.getInstance().getBean(HttpServer.class);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            httpServer.start();
        } catch (Exception e) {
            LOG.error("Error starting Jetty server", e);
            System.exit(100);
        }

        Enumeration e = httpServer.getJettyServer().getAttributeNames();
        LOG.info("HTTPServer attributes:");
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            LOG.info("Name: %s, Value: %s", name, httpServer.getJettyServer().getAttribute(name));
        }


        System.out.println("HTTP server " + httpServer.getJettyServer().getState() + " on port " + httpServer.getJettyServer().getConnectors()[0].getPort());
    }

    public static void startJms()
            throws Exception {
        try {
            SpringConfiguration.getInstance().configure(JmsRunner.class);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void stopRunner() throws Exception {
        SpringConfiguration.getInstance().shutdown();

        System.exit(0);
    }


    static class ShutdownHook extends Thread {
        public void run() {
            SpringConfiguration.getInstance().shutdown();
        }
    }

}