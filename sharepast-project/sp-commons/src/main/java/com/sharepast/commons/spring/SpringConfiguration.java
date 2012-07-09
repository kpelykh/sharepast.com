package com.sharepast.commons.spring;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.sharepast.commons.util.Build;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/19/11
 * Time: 12:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class SpringConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SpringConfiguration.class);

    private static final StandardEnvironment environment = new StandardEnvironment();

    private static final String LINE_SEPARATOR = environment.getProperty("line.separator");

    public static final String ENVIRONMENT_SYSTEM_PROPERTY = "com.sharepast.env";

    private AbstractApplicationContext appContext;

    private final HashSet<Class> configurationSet = new HashSet<Class>();

    private StackTraceElement[] stackTrace;

    private String threadName;

    private static SpringConfiguration _instance = new SpringConfiguration();

    private volatile CountDownLatch latch = new CountDownLatch(0);

    private volatile boolean contextInitialized = false;

    static {
        String env = System.getProperty(ENVIRONMENT_SYSTEM_PROPERTY, "development");
        System.setProperty(ENVIRONMENT_SYSTEM_PROPERTY, env);
        String[] envs = env.split(",");
        environment.setActiveProfiles(envs);

        getInfo();
    }

    public AbstractApplicationContext getAppContext() {
        return appContext;
    }

    public void setAppContext(AbstractApplicationContext newAppContext) {
        if (this.appContext != null) {
            boolean foundParentContext = false;
            while (newAppContext.getParent()!=null) {
                if (this.appContext == newAppContext.getParent()) {
                    foundParentContext = true;
                    break;
                }
            }
            if (!foundParentContext) {
                throw new IllegalArgumentException("Can't set a new application context, because previous context is not a parent of a new one.");
            }

        }
        this.appContext = newAppContext;
    }

    public boolean isContextInitialized() {
        return contextInitialized;
    }

    public static SpringConfiguration getInstance() {
        return _instance;
    }

    public static StandardEnvironment getEnvironment() {
        return environment;
    }

    public  ConfigurableApplicationContext configure(Class... configurations) {

        //Await till shutdown method cleans up SpringConfiguration.
        try {
            latch.await();
        } catch (InterruptedException intr) {
            LOG.error("====================================================");
            LOG.error("InterruptedException runnting spring configuration", intr);
            LOG.error("====================================================");
            throw new RuntimeException(intr);
        }

        if (appContext != null)
            throw new ContextException("Attempted re-definition of the current application context: %s", format(stackTrace));

        addConfiguration(configurations);

        LOG.info("Processing configurations: {}", Arrays.toString(getConfigurations()));

        try {
            appContext = new AnnotationConfigApplicationContext();
            appContext.setEnvironment(environment);
            ((AnnotationConfigApplicationContext)appContext).register(getConfigurations());
            appContext.refresh();
        } catch (RuntimeException rte) {
            LOG.error("====================================================");
            LOG.error("Runtime exception runnting spring configuration", rte);
            LOG.error("====================================================");
            throw rte;
        }

        contextInitialized = true;

        LOG.info("Sucessfully processed configurations: {}", Arrays.toString(getConfigurations()));

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
            LOG.error(msg, e);
        }
        return null;
    }


    public Object getBean(String beanId) {
        Assert.notNull(appContext, "Spring context is not initialized");
        try {
            return appContext.getBean(beanId);
        } catch (Exception e) {
            String msg = "Unable to get bean " + beanId;
            LOG.error(msg, e);
        }
        return null;
    }

    public boolean checkBean(String beanId) {
        Assert.notNull(appContext, "Spring context is not initialized");
        try {
            return appContext.getBean(beanId) != null;
        } catch (Exception ignored) {
        }
        return false;
    }

    public <T> T getBean(Class<T> clazz, String beanId) {
        Assert.notNull(appContext, "Spring context is not initialized");
        try {
            return clazz.cast(appContext.getBean(beanId, clazz));
        } catch (Exception e) {
            String msg = "Unable to get bean " + beanId + " of class " + clazz.getSimpleName();
            LOG.error(msg, e);
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
        char[] endLine;
        endLine = new char[34 + SpringConfiguration.class.getSimpleName().length()];
        Arrays.fill(endLine, '=');

        Map<String, Object> envs = Maps.filterKeys(environment.getSystemEnvironment(), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input != null && input.startsWith("SP_");
            }
        });

        StringBuffer components = new StringBuffer();
        for (Build.ComponentInfo component : Build.getComponents()) {
            components.append("\t").append(component.toString()).append("\n");
        }

        taglineBuilder
                .append("================ ")
                .append(SpringConfiguration.class.getSimpleName())
                .append(" ================")
                .append(LINE_SEPARATOR).append(LINE_SEPARATOR)
                .append("SP Components: ").append(LINE_SEPARATOR)
                .append(components.toString())
                .append(LINE_SEPARATOR)
                .append("current profile: ")
                .append(environment.getActiveProfiles()[0])
                .append(LINE_SEPARATOR)
                .append("Envs: ")
                .append(envs.toString())
                .append(LINE_SEPARATOR);

        //if ((new File(localPropertyPath.toString())).exists())
        //    taglineBuilder.append(String.format("configuration override: %s\n", localPropertyPath.toString()));

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
        latch = new CountDownLatch(1);

        Thread shutter = new Thread() {
            public void run() {
                if (appContext != null && contextInitialized) {
                    appContext.publishEvent(new ShutdownEvent(this));
                    appContext.close();
                }
                appContext = null;
                threadName = null;
                stackTrace = null;
                configurationSet.clear();
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
            LOG.error("SpringConfiguration did not shut down in 10 seconds, forcing it to quit");
            LOG.error("SpringConfiguration shutdown dump follows\n========================");
            StackTraceElement[] st = shutter.getStackTrace();
            if (st != null)
                for (StackTraceElement ste : st)
                    LOG.error(ste.toString());
            LOG.error("========================\n");

            shutter.interrupt();
        }

        latch.countDown();

    }

    public static void runConfiguration(String confId) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        LOG.info(String.format("Searching for profile configurators in 'com.sharepast.configurator' package, which support '%s' profile", confId));
        Reflections reflections = new Reflections("com.sharepast.configurator");
        Set<Class<? extends ProfileConfigurator>> subTypes = reflections.getSubTypesOf(ProfileConfigurator.class);

        if (subTypes.size()>0) {
            LOG.info(String.format("Found profile configurators: %s", Arrays.toString(subTypes.toArray())));
        } else {
            throw new IllegalStateException("No profile configurators found in 'com.sharepast.configurator'");
        }

        for (Class<? extends ProfileConfigurator> conf : subTypes) {
            ProfileConfigurator configuration = conf.newInstance();
            if (configuration.supportsProfile(confId)) {
                LOG.info(String.format("%s supports %s. Using it to configure application...", configuration.getClass().getName(), confId));
                conf.newInstance().configure(confId);
                break;
            }
        }
    }
}
