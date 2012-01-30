package com.sharepast.startup;

import com.sharepast.util.Build;
import com.sharepast.util.Util;
import com.sharepast.util.config.Environment;
import com.sharepast.util.spring.ContextException;
import com.sharepast.util.spring.ShutdownEvent;
import com.sharepast.util.spring.StartupProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.StandardEnvironment;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/19/11
 * Time: 12:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class Configurator {

    /**
     * The 'app.override' property adds another environment file to the config properties
     * /usr/lib/zts/conf/environment-${app.override}.properties
     */
    public static final String SYSTEM_PROPERTY_ENVIRONMENT_OVERRIDE = "com.zettaset.env.override";

    /**
     * system properties that defines server pool
     */
    private static final String SYSTEM_PROPERTY_POOL = "com.sharepast.server.pool";

    private static final String DEFAULT_POOL = "app";

    private static final String DEFAULT_AREA = "development";

    /**
     * system properties that defines server inside pool
     */
    private static final String SYSTEM_PROPERTY_SERVER_ID = "com.sharepast.server.id";

    private static final String DEFAULT_SERVER_ID = "0001";


    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final Logger LOG = LoggerFactory.getLogger(Configurator.class);

    private AnnotationConfigApplicationContext appContext;

    private final HashSet<Class> configurationSet = new HashSet<Class>();

    private StackTraceElement[] stackTrace;

    private String threadName;

    private static Configurator _instance = new Configurator();

    private volatile boolean stopped = false;

    private static final String env;

     static {
        env = Environment.getCurrent();
        if (Util.isEmpty(System.getProperty(SYSTEM_PROPERTY_ENVIRONMENT_OVERRIDE))) {
            System.setProperty(SYSTEM_PROPERTY_ENVIRONMENT_OVERRIDE, env);
        }
        getInfo();
    }

    public static Configurator getInstance() {
        return _instance;
    }

    public ConfigurableApplicationContext configure(Class... configurations) {
        if (appContext != null)
            throw new ContextException("Attempted re-definition of the current application context: %s", format(stackTrace));

        addConfiguration(configurations);

        // prep pool environment
        System.setProperty(SYSTEM_PROPERTY_POOL, System.getProperty(SYSTEM_PROPERTY_POOL, DEFAULT_POOL));
        System.setProperty(SYSTEM_PROPERTY_SERVER_ID, System.getProperty(SYSTEM_PROPERTY_SERVER_ID, DEFAULT_SERVER_ID));

        appContext = new AnnotationConfigApplicationContext();
        appContext.setEnvironment(new StandardEnvironment());
        appContext.getEnvironment().setActiveProfiles(Environment.getCurrent());
        appContext.register(getConfigurations());
        appContext.refresh();

        if (LOG.isDebugEnabled())
            LOG.debug("Processed configurations: {}", Arrays.toString(getConfigurations()));
        threadName = Thread.currentThread().getName();
        stackTrace = Thread.currentThread().getStackTrace();

        return appContext;
    }

    public void addConfiguration(Class... configurations) {
        configurationSet.addAll(Arrays.asList(configurations));
    }

    public Class[] getConfigurations() {
        synchronized (configurationSet) {
            Class[] configurations;
            configurations = new Class[configurationSet.size()];
            configurationSet.toArray(configurations);
            return configurations;
        }
    }

    public <T> T getBean(Class<T> clazz) {
        try {
            return appContext.getBean(clazz);
        } catch (Exception e) {
            String msg = "Unable to get bean of class " + clazz.getName();
            LOG.error(msg);
            System.err.println(msg);
            throw (RuntimeException) e;
        }
    }


    public Object getBean(String beanId) {
        try {
            return appContext.getBean(beanId);
        } catch (Exception e) {
            String msg = "Unable to get bean " + beanId;
            LOG.error(msg);
            System.err.println(msg);
            throw (RuntimeException) e;
        }
    }

    public <T> T getBean(Class<T> clazz, String beanId) {
        try {
            return clazz.cast(appContext.getBean(beanId, clazz));
        } catch (Exception e) {
            String msg = "Unable to get bean " + beanId + " of class " + clazz.getSimpleName();
            LOG.error(msg);
            System.err.println(msg);
            throw (RuntimeException) e;
        }
    }

    private String format(StackTraceElement[] stackTrace) {
        StringBuilder traceBuilder = new StringBuilder();
        traceBuilder.append(System.getProperty("line.separator"));
        traceBuilder.append(System.getProperty("line.separator"));
        traceBuilder.append("\tOriginating Thread: ");
        traceBuilder.append(threadName);
        traceBuilder.append(System.getProperty("line.separator"));
        traceBuilder.append(System.getProperty("line.separator"));
        traceBuilder.append("\t============= Originating Stack =============");
        traceBuilder.append(System.getProperty("line.separator"));

        for (StackTraceElement element : stackTrace) {
            traceBuilder.append('\t');
            traceBuilder.append(element);
            traceBuilder.append(System.getProperty("line.separator"));
        }

        traceBuilder.append(System.getProperty("line.separator"));
        traceBuilder.append("\tException Thread: ");
        traceBuilder.append(Thread.currentThread().getName());
        traceBuilder.append(System.getProperty("line.separator"));
        traceBuilder.append(System.getProperty("line.separator"));
        traceBuilder.append("\t============== Exception Stack ==============");

        return traceBuilder.toString();
    }

    private static String getInfo() {
        StringBuilder taglineBuilder = new StringBuilder();
        StringBuilder localPropertyPath;
        char[] endLine;
        endLine = new char[34 + Configurator.class.getSimpleName().length()];
        Arrays.fill(endLine, '=');
        localPropertyPath =
                new StringBuilder(System.getProperty("user.home"))
                        .append(FILE_SEPARATOR)
                        .append(".m2")
                        .append(FILE_SEPARATOR)
                        .append("environment-")
                        .append(System.getProperty(SYSTEM_PROPERTY_ENVIRONMENT_OVERRIDE))
                        .append(".properties")
        ;

        taglineBuilder
                .append("================ ")
                .append(Configurator.class.getSimpleName())
                .append(" ================")
                .append(LINE_SEPARATOR).append(LINE_SEPARATOR)
                .append("code version: ")
                .append(Build.getVersion())
                .append(LINE_SEPARATOR)
                .append("built on: ")
                .append(Build.getTimestamp())
                .append(LINE_SEPARATOR)
                .append("current environment: ")
                .append(env)
                .append(LINE_SEPARATOR)
                .append("override environment: ")
                .append(System.getProperty(SYSTEM_PROPERTY_ENVIRONMENT_OVERRIDE))
                .append(LINE_SEPARATOR)
                .append("is production: ")
                .append(StartupProperties.SYSTEM_PROPERTY_IS_PRODUCTION.get())
                .append(LINE_SEPARATOR)
        ;

        if ((new File(localPropertyPath.toString())).exists())
            taglineBuilder.append(String.format("configuration override: %s\n", localPropertyPath.toString()));

        taglineBuilder
                .append(LINE_SEPARATOR)
                .append(endLine)
                .append(LINE_SEPARATOR)
        ;

        String res = taglineBuilder.toString();

        LOG.warn("\n" + res);
        return res;
    }

    public void shutdown() {

        if (!stopped) {

            Thread shutter = new Thread() {
                public void run() {
                    if (appContext != null) {
                        stopped = true;
                        appContext.publishEvent( new ShutdownEvent( this ) );
                        appContext.close();
                    }
                    appContext = null;
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
