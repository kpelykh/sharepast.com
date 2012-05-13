package com.sharepast.grails.web.context;

import grails.util.Environment;
import grails.util.Metadata;
import groovy.grape.Grape;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClassRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.exceptions.DefaultStackTraceFilterer;
import org.codehaus.groovy.grails.lifecycle.ShutdownOperations;
import org.codehaus.groovy.grails.plugins.GrailsPluginManager;
import org.codehaus.groovy.grails.web.context.GrailsConfigUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.security.AccessControlException;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/10/12
 * Time: 1:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class GrailsContextLoader extends ContextLoader {

    public static final Log LOG = LogFactory.getLog(GrailsContextLoader.class);
    GrailsApplication application;

    @Override
    public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
        // disable annoying ehcache up-to-date check
        System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
        ExpandoMetaClass.enableGlobally();
        Metadata metadata = Metadata.getCurrent();
        if (metadata != null && metadata.isWarDeployed()) {
            Grape.setEnableAutoDownload(false);
            Grape.setEnableGrapes(false);
            Environment.cacheCurrentEnvironment();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("[GrailsContextLoader] Loading context. Creating parent application context");
        }

        WebApplicationContext  ctx;
        try {
            ctx = (WebApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

            if (LOG.isDebugEnabled()) {
                LOG.debug("[GrailsContextLoader] Created parent application context");
            }

            application = ctx.getBean(GrailsApplication.APPLICATION_ID, GrailsApplication.class);

            final WebApplicationContext finalCtx = ctx;
            ShutdownOperations.addOperation(new Runnable() {
                public void run() {
                    if (application != null) {
                        ClassLoader classLoader = application.getClassLoader();
                        if (classLoader instanceof GroovyClassLoader) {
                            MetaClassRegistry metaClassRegistry = GroovySystem.getMetaClassRegistry();
                            Class<?>[] loadedClasses = ((GroovyClassLoader) classLoader).getLoadedClasses();
                            for (Class<?> loadedClass : loadedClasses) {
                                metaClassRegistry.removeMetaClass(loadedClass);
                            }
                        }
                    }

                    GrailsPluginManager pluginManager = null;
                    if (finalCtx.containsBean(GrailsPluginManager.BEAN_NAME)) {
                        pluginManager = finalCtx.getBean(GrailsPluginManager.BEAN_NAME, GrailsPluginManager.class);
                    }

                    if (pluginManager != null) {
                        try {
                            pluginManager.shutdown();
                        } catch (Exception e) {
                            new DefaultStackTraceFilterer().filter(e);
                            LOG.error("Error occurred shutting down plug-in manager: " + e.getMessage(), e);
                        }
                    }
                }
            });
            ctx =  GrailsConfigUtils.configureWebApplicationContext(servletContext, ctx);
            GrailsConfigUtils.executeGrailsBootstraps(application, ctx, servletContext);
        }
        catch (Throwable e) {
            if (Environment.isDevelopmentMode() && !Environment.isWarDeployed()) {
                LOG.error("Error executing bootstraps: " + e.getMessage(), e);
                // bail out early in order to show appropriate error
                if(System.getProperty("grails.disable.exit") == null) {
                    System.exit(1);
                }
            }

            if (e instanceof BeansException) throw (BeansException)e;

            throw new BootstrapException("Error executing bootstraps", e);
        }
        return ctx;
    }


    @Override
    public void closeWebApplicationContext(ServletContext servletContext) {
        // clean up in war mode, in run-app these references may be needed again
        if (application == null || !application.isWarDeployed()) {
            return;
        }

        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        ConfigurableApplicationContext parent = ctx != null ? (ConfigurableApplicationContext) ctx.getParent() : null;

        try {
            ShutdownOperations.runOperations();
        } finally {
            super.closeWebApplicationContext(servletContext);
        }

        if (parent != null) {
            LOG.info("Destroying Spring parent WebApplicationContext " + parent.getDisplayName());
            parent.close();
        }
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        }
        catch (AccessControlException e) {
            // container doesn't allow, probably related to WAR deployment on AppEngine. proceed.
        }

        application = null;
    }
}
