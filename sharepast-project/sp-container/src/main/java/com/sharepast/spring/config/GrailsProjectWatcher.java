package com.sharepast.spring.config;

import com.sharepast.commons.spring.ContextListener;
import grails.util.BuildSettingsHolder;
import grails.util.PluginBuildSettings;
import org.codehaus.groovy.grails.compiler.GrailsProjectCompiler;
import org.codehaus.groovy.grails.plugins.GrailsPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/13/12
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Profile("development")
public class GrailsProjectWatcher extends ContextListener {

    private @Autowired GrailsPluginManager pluginManager;

    @Override
    public void afterStartup(ApplicationContext context) {
        GrailsProjectCompiler compiler = new GrailsProjectCompiler(new PluginBuildSettings(BuildSettingsHolder.getSettings(), pluginManager));
        compiler.getAnt();
        compiler.configureClasspath();
        org.codehaus.groovy.grails.compiler.GrailsProjectWatcher watcher = new org.codehaus.groovy.grails.compiler.GrailsProjectWatcher(compiler, pluginManager);

        watcher.start();
    }
}
