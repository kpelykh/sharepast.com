package com.sharepast.config;

import com.sharepast.util.spring.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 1:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfiguration.class);

    public static final String PLATFORM = "platform";
    public static final String JMS = "job";


    public static final Map<String, String[]> configurations = new HashMap<String, String[]>() {
        {
            put(PLATFORM, new String[]{
                    // base
                    "com/sharepast/config/base.xml",
                    "com/sharepast/config/cache.xml",
                    "com/sharepast/config/security.xml",
                    "com/sharepast/config/jmx.xml",
                    "com/sharepast/config/persistence.xml",
                    "com/sharepast/config/services.xml",
                    "com/sharepast/config/geoip-location.xml",
                    "com/sharepast/config/jms_producer.xml",
                    "com/sharepast/config/container.xml",
                    "com/sharepast/config/restlets.xml"
            });
            put(JMS, new String[]{
                    // base
                    "com/sharepast/config/base.xml",
                    "com/sharepast/config/jmx.xml",
                    "com/sharepast/config/persistence.xml",
                    "com/sharepast/config/geoip-location.xml",
                    "com/sharepast/config/activemq.xml"
            });
        }
    };

    private String[] resources;

    public AppConfiguration(String configurationId) {
        this.resources = configurations.get(configurationId);

        if (resources == null)
            throw new IllegalArgumentException(String.format("cannot find configuration with id %s", configurationId));
    }

    public String[] getResources() {
        return resources;
    }

    public void configure() {
        Configurator.getInstance().configure(this.getResources());
    }

    public static void shutdown() {

        if (!Configurator.getInstance().isStopped()) {

            Thread shutter = new Thread() {
                public void run() {
                    Configurator.getInstance().shutdown();
                }
            };

            shutter.start();

            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {
                }

                if (!shutter.isAlive())
                    break;
            }

            if (shutter.isAlive()) {
                System.out.println("Configurator did not shut down in 10 seconds, forcing it to quit");
                System.out.println("Configurator shutdown dump follows\n========================");
                StackTraceElement[] st = shutter.getStackTrace();
                if (st != null)
                    for (StackTraceElement ste : st)
                        System.out.println(ste.toString());
                System.out.println("========================\n");

                shutter.interrupt();
            }
        }
    }
}
