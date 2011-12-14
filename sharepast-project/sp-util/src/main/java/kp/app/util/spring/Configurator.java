package kp.app.util.spring;

import kp.app.util.Build;
import kp.app.util.Util;
import kp.app.util.config.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/19/11
 * Time: 12:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class Configurator {

    /**
     * The 'kp.override' property adds another environment file to the config properties
     * ~/.m2/environment-${kp.override}.properties
     */
    private static final String SYSTEM_PROPERTY_ENVIRONMENT_OVERRIDE = "kp.override";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /**
     * system properties that defines server pool
     */
    private static final String SYSTEM_PROPERTY_POOL = "kp.server.pool";

    private static final String DEFAULT_POOL = "app";


    /**
     * system properties that defines server inside pool
     */
    private static final String SYSTEM_PROPERTY_SERVER_ID = "kp.server.id";

    private static final String DEFAULT_SERVER_ID = "0001";

    private static final Logger LOG = LoggerFactory.getLogger(Configurator.class);

    private ConfigurableApplicationContext appContext;

    private final HashSet<String> configurationSet = new HashSet<String>();

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
                        .append(System.getProperty("kp.override"))
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
                .append(System.getProperty("kp.override"))
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

    public static Configurator getInstance() {
        return _instance;
    }

    public ConfigurableApplicationContext configure(String... configurations) {
        if (appContext != null)
            throw new ContextException("Attempted re-definition of the current application context: %s", format(stackTrace));

        addConfiguration(configurations);
        // prep pool environment
        System.setProperty(SYSTEM_PROPERTY_POOL, System.getProperty(SYSTEM_PROPERTY_POOL, DEFAULT_POOL));
        System.setProperty(SYSTEM_PROPERTY_SERVER_ID, System.getProperty(SYSTEM_PROPERTY_SERVER_ID, DEFAULT_SERVER_ID));
        appContext = new ClassPathXmlApplicationContext(getConfigurations());
        if (LOG.isDebugEnabled())
            LOG.debug("Processed configurations: {}", Arrays.toString(getConfigurations()));
        threadName = Thread.currentThread().getName();
        stackTrace = Thread.currentThread().getStackTrace();

        return appContext;
    }

    public void addConfiguration(String... configurations) {
        configurationSet.addAll(Arrays.asList(configurations));
    }

    public String[] getConfigurations() {
        synchronized (configurationSet) {
            String[] configurations;
            configurations = new String[configurationSet.size()];
            configurationSet.toArray(configurations);
            return configurations;
        }
    }

    public static <T> T squeeze(Class<T> clazz, String beanId) {
        return getInstance().getBean(clazz, beanId);
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

    public synchronized void shutdown() {

        if (appContext != null) {
            stopped = true;
            appContext.publishEvent( new ShutdownEvent( this ) );
            appContext.close();
        }
        appContext = null;
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

    public boolean isStopped() {
        return stopped;
    }
}
