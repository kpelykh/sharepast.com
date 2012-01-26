package com.sharepast.startup;

import com.sharepast.config.AppConfiguration;
import com.sharepast.util.spring.Configurator;
import com.sharepast.util.spring.StartupProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/13/11
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppRunner {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AppRunner.class);

    private static final String CLI_START_PLATFORM = "p";
    private static final String CLI_START_JMS = "j";
    private static final String CLI_STOP = "t";

    /**
     * Platform HTTP server
     */
    private static Server httpServer;

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
            new AppConfiguration(AppConfiguration.PLATFORM).configure();
            httpServer = Configurator.squeeze(Server.class, StartupProperties.APP_SERVER_NAME.getKey());
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }

        httpServer.start();
        System.out.println("HTTP server " + httpServer.getState() + " on port " + httpServer.getConnectors()[0].getPort());
    }

    public static void startJms()
            throws Exception {
        try {
            new AppConfiguration(AppConfiguration.JMS).configure();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void stopRunner() throws Exception {
        int status = 0;

        try {
            if (httpServer != null && httpServer.isStarted()) {
                httpServer.stop();
                System.out.println("stopped HTTP server on " + httpServer.getConnectors()[0].getPort());
            }
        } catch (Throwable th) {
            LOG.error("cannot stop HTTP server", th);
            status += 1;
        }

        AppConfiguration.shutdown();

        System.exit(status);
    }


    static class ShutdownHook extends Thread {
        public void run() {
            AppConfiguration.shutdown();
        }
    }

}