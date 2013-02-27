package com.sharepast.commons.spring.config;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.sharepast.commons.util.Util;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/16/12
 * Time: 12:57 AM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class PropertiesConfig {

    private static final Log LOG = LogFactory.getLog(PropertiesConfig.class);

    @Bean(name = "properties")
    public static PropertiesFactoryBean properties(Environment env) {
        PropertiesFactoryBean pfb = new PropertiesFactoryBean();
        List<Resource> resources = new ArrayList<Resource>();

        resources.add(new ClassPathResource("configuration.properties"));

        if (env.acceptsProfiles("development", "test")) {
            resources.add(new ClassPathResource(env.resolvePlaceholders("development/environment.properties")));
            if (env.acceptsProfiles("test")) {
                resources.add(new ClassPathResource(env.resolvePlaceholders("development/test/environment.properties")));
            }
            addResourceIfExists(resources, env.resolvePlaceholders("${user.home}/.m2/environment-development.properties"));
        } else if (env.acceptsProfiles("production")) {
            resources.add(new ClassPathResource(env.resolvePlaceholders("production/environment.properties")));
            addResourceIfExists(resources, env.resolvePlaceholders("${user.home}/.m2/environment-production.properties"));
        }

        pfb.setLocations(resources.toArray(new Resource[resources.size()]));
        pfb.setSingleton(true);
        pfb.setIgnoreResourceNotFound(true);
        return pfb;
    }

    private static void addResourceIfExists(List<Resource> resources, String path) {
        File propertisFile = new File(path);
        if (propertisFile.exists()) {
            resources.add(new FileSystemResource(propertisFile));
        } else {
            LOG.warn(String.format("Skipping loading of %s because it doesn't exist", path));
        }
    }

    @Bean
    public Properties pathPostProcess(ApplicationContext ctx, ConfigurableEnvironment env, @Qualifier("properties") Properties properties) {

        convertToResource(ctx, env, properties, "jetty.resource.base");
        convertToResource(ctx, env, properties, "grails.descriptor");
        convertToResource(ctx, env, properties, "grails.base");
        convertToResource(ctx, env, properties, "log.dir");
        convertToResource(ctx, env, properties, "config.path");
        convertToResource(ctx, env, properties, "activemq.persist");
        convertToResource(ctx, env, properties, "activemq.home");
        convertToResource(ctx, env, properties, "activemq.base");
        convertToResource(ctx, env, properties, "geoip.database.file.name");
        convertToResource(ctx, env, properties, "hsqldb.location");

        //adding zts properties to env in the first line, so that it will be used to resolve placeholders
        env.getPropertySources().addLast(new PropertiesPropertySource("applicationProperties", properties));

        replacePlaceholders(env, properties);

        printProperties(env, properties);

        // Deobfuscate passwords
        Util.setIfPropertyExists(properties, "jdbc.password", Util.getDeobfuscatedPassword(properties.getProperty("jdbc.password")));
        Util.setIfPropertyExists(properties, "keystore.manager.pass", Util.getDeobfuscatedPassword(properties.getProperty("keystore.manager.pass")));
        Util.setIfPropertyExists(properties, "keystore.pass", Util.getDeobfuscatedPassword(properties.getProperty("keystore.pass")));
        Util.setIfPropertyExists(properties, "truststore.pass", Util.getDeobfuscatedPassword(properties.getProperty("truststore.pass")));

        return properties;

    }

    private void replacePlaceholders(ConfigurableEnvironment env, Properties properties) {
        for (Map.Entry<Object, Object> prop : properties.entrySet()) {
            String newValue = env.resolvePlaceholders((String) prop.getValue());
            if (!newValue.equals(prop.getValue())) {
                properties.setProperty((String) prop.getKey(), newValue);
            }
        }
    }


    private String printProperties(ConfigurableEnvironment env, Properties properties) {
        StringBuilder taglineBuilder = new StringBuilder();
        char[] endLine;
        endLine = new char[34 + PropertiesConfig.class.getSimpleName().length()];
        Arrays.fill(endLine, '=');

        String lineSeparator = env.getProperty("line.separator");


        Map<String, Object> systemProperties = Maps.filterKeys(env.getSystemProperties(), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input != null && (input.contains("sharepast") || input.contains("sp"));
            }
        });

        Map<String, String> sortedProperties = Maps.newTreeMap();
        sortedProperties.putAll(Maps.fromProperties(properties));

        //Hide values for proprieties from "do.not.print" list
        String doNotPrintList = (String) properties.get("do.not.print");
        if (!StringUtils.isEmpty(doNotPrintList)) {
            String[] doNotPrintValues = StringUtils.split(doNotPrintList, ',');
            for (String val : doNotPrintValues) {
                sortedProperties.put( val, "************" );
            }
        }
        sortedProperties.remove("do.not.print");

        ByteArrayOutputStream propertiesOutStream = new ByteArrayOutputStream();
        PrintStream pw = new PrintStream(propertiesOutStream);
        MapUtils.verbosePrint(pw, null, sortedProperties);

        ByteArrayOutputStream systemPropertiesOutStream = new ByteArrayOutputStream();
        PrintStream pw2 = new PrintStream(systemPropertiesOutStream);
        MapUtils.verbosePrint(pw2, null, systemProperties);

        taglineBuilder
                .append("================ ")
                .append(PropertiesConfig.class.getSimpleName())
                .append(" ================")
                .append(lineSeparator).append(lineSeparator)
                .append("Properties:")
                .append(propertiesOutStream.toString())
                .append(lineSeparator)
                .append("System properties:")
                .append(systemPropertiesOutStream.toString())
                .append(lineSeparator)
        ;

        taglineBuilder
                .append(lineSeparator)
                .append(endLine)
                .append(lineSeparator)
        ;

        String res = taglineBuilder.toString();

        LOG.debug("\n" + res);
        return res;
    }

    private void convertToResource(ApplicationContext ctx, Environment env, Properties properties, String propertyName) {

        boolean systemPropertyOverride = false;
        if (env.getProperty(propertyName) != null) {
            systemPropertyOverride = true;
        }

        String propValue = systemPropertyOverride ? env.getProperty(propertyName) : properties.getProperty(propertyName);

        if (StringUtils.isEmpty(propValue)) {
            LOG.info(String.format("Property %s was not found in property files", propertyName));
        } else  {
        try {
            Resource res = ctx.getResource(propValue);
            if (res instanceof UrlResource) {
                String newValue = res.getFile().getCanonicalPath();
                    LOG.info(String.format("Expanded (%s) property ['%s=%s]' to [%s='%s]", (systemPropertyOverride ? "system" : ""), propertyName, propValue, propertyName, newValue));
                if (!res.exists()) {
                    LOG.warn(String.format("Resource %s doesn't exist", newValue));
                }
                    if (env.getProperty(propertyName) != null && systemPropertyOverride) {
                        System.setProperty(propertyName, "file:" + newValue);
            }
                    properties.setProperty(propertyName, "file:" + newValue);
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error occurred when trying to convert to [%s=%s] to resource", propertyName, propValue), e);
            }
        }
    }
}
