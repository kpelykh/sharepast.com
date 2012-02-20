package com.sharepast.util.spring;

import com.sharepast.util.Build;
import com.sharepast.util.Util;
import com.sharepast.util.config.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/19/11
 * Time: 12:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class SpringConfigurator {

    private static final StandardEnvironment environment = new StandardEnvironment();

    private static final String LINE_SEPARATOR = environment.getProperty("line.separator");

    private static final String FILE_SEPARATOR = environment.getProperty("file.separator");

    private static final Logger LOG = LoggerFactory.getLogger(SpringConfigurator.class);

    private AnnotationConfigApplicationContext appContext;

    private final HashSet<Class> configurationSet = new HashSet<Class>();

    private StackTraceElement[] stackTrace;

    private String threadName;

    private static SpringConfigurator _instance = new SpringConfigurator();

    private volatile boolean stopped = false;

    private volatile boolean contextInitialized = false;

    static {
        getInfo();
        environment.setActiveProfiles(Environment.getCurrent());
    }

    public ApplicationContext getApplicationContext() {
        return appContext;
    }

    public boolean isContextInitialized() {
        return contextInitialized;
    }

    public static SpringConfigurator getInstance() {
        return _instance;
    }

    public ConfigurableApplicationContext configure(Class... configurations) {
        if (appContext != null)
            throw new ContextException("Attempted re-definition of the current application context: %s", format(stackTrace));

        addConfiguration(configurations);

        appContext = new AnnotationConfigApplicationContext();
        appContext.setEnvironment(environment);
        appContext.register(getConfigurations());
        appContext.refresh();

        contextInitialized = true;


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
        Assert.notNull(appContext, "Spring context is not initialized");
        try {
            return appContext.getBean(clazz);
        } catch (Exception e) {
            String msg = "Unable to get bean of class " + clazz.getName();
            LOG.error(msg);
        }
        return null;
    }


    public Object getBean(String beanId) {
        Assert.notNull(appContext, "Spring context is not initialized");
        try {
            return appContext.getBean(beanId);
        } catch (Exception e) {
            String msg = "Unable to get bean " + beanId;
            LOG.error(msg);
        }
        return null;
    }

    public <T> T getBean(Class<T> clazz, String beanId) {
        Assert.notNull(appContext, "Spring context is not initialized");
        try {
            return clazz.cast(appContext.getBean(beanId, clazz));
        } catch (Exception e) {
            String msg = "Unable to get bean " + beanId + " of class " + clazz.getSimpleName();
            LOG.error(msg);
        }
        return null;
    }

    private String format(StackTraceElement[] stackTrace) {
        StringBuilder traceBuilder = new StringBuilder();
        traceBuilder.append(environment.getProperty("line.separator"));
        traceBuilder.append(environment.getProperty("line.separator"));
        traceBuilder.append("\tOriginating Thread: ");
        traceBuilder.append(threadName);
        traceBuilder.append(environment.getProperty("line.separator"));
        traceBuilder.append(environment.getProperty("line.separator"));
        traceBuilder.append("\t============= Originating Stack =============");
        traceBuilder.append(environment.getProperty("line.separator"));

        for (StackTraceElement element : stackTrace) {
            traceBuilder.append('\t');
            traceBuilder.append(element);
            traceBuilder.append(environment.getProperty("line.separator"));
        }

        traceBuilder.append(environment.getProperty("line.separator"));
        traceBuilder.append("\tException Thread: ");
        traceBuilder.append(Thread.currentThread().getName());
        traceBuilder.append(environment.getProperty("line.separator"));
        traceBuilder.append(environment.getProperty("line.separator"));
        traceBuilder.append("\t============== Exception Stack ==============");

        return traceBuilder.toString();
    }

    private static String getInfo() {
        StringBuilder taglineBuilder = new StringBuilder();
        StringBuilder localPropertyPath;
        char[] endLine;
        endLine = new char[34 + SpringConfigurator.class.getSimpleName().length()];
        Arrays.fill(endLine, '=');
        localPropertyPath =
                new StringBuilder(environment.getProperty("user.home"))
                        .append(FILE_SEPARATOR)
                        .append(".m2")
                        .append(FILE_SEPARATOR)
                        .append("environment-")
                        .append(environment.getProperty(Environment.getCurrent()))
                        .append(".properties")
        ;

        taglineBuilder
                .append("================ ")
                .append(SpringConfigurator.class.getSimpleName())
                .append(" ================")
                .append(LINE_SEPARATOR).append(LINE_SEPARATOR)
                .append("code version: ")
                .append(Build.getVersion())
                .append(LINE_SEPARATOR)
                .append("built on: ")
                .append(Build.getTimestamp())
                .append(LINE_SEPARATOR)
                .append("current environment: ")
                .append(Environment.getCurrent())
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
                    if (appContext != null && contextInitialized) {
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
                System.out.println("SpringConfigurator did not shut down in 10 seconds, forcing it to quit");
                System.out.println("SpringConfigurator shutdown dump follows\n========================");
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
