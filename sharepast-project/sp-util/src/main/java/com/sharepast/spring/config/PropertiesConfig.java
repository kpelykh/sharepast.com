package com.sharepast.spring.config;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
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
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.ByteArrayOutputStream;
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
    public static PropertiesFactoryBean properties() {
        PropertiesFactoryBean pfb = new PropertiesFactoryBean();
        final Resource[] resources;

        Environment env = new StandardEnvironment();
        resources = new Resource[]{
                new ClassPathResource("configuration.properties"),
                new ClassPathResource(env.resolvePlaceholders("${com.sharepast.env}/environment.properties")),
                new FileSystemResource(env.resolvePlaceholders("${user.home}/.m2/environment-${com.sharepast.env}.properties"))
        };

        pfb.setLocations(resources);
        pfb.setSingleton(true);
        pfb.setIgnoreResourceNotFound(true);
        return pfb;
    }

    @Bean
    public Properties pathPostProcess(ApplicationContext ctx, ConfigurableEnvironment env, @Qualifier("properties") Properties properties) {

        //adding sharepast properties to env in the first line, so that it will be used to resolve placeholders
        env.getPropertySources().addLast(new PropertiesPropertySource("applicationProperties", properties));

        replacePlaceholders(env, properties);

        properties.put("web.resource.base", convertToResource(ctx, properties, "web.resource.base"));
        properties.put("jetty.web.default", convertToResource(ctx, properties, "jetty.web.default"));
        properties.put("log.dir", convertToResource(ctx, properties, properties.getProperty("log.dir")));
        properties.put("config.path", convertToResource(ctx, properties, "config.path"));

        printProperties(env, properties);

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

        ByteArrayOutputStream ztsPropertiesOutStream = new ByteArrayOutputStream();
        PrintStream pw = new PrintStream(ztsPropertiesOutStream);
        MapUtils.verbosePrint(pw, null, sortedProperties);

        ByteArrayOutputStream systemPropertiesOutStream = new ByteArrayOutputStream();
        PrintStream pw2 = new PrintStream(systemPropertiesOutStream);
        MapUtils.verbosePrint(pw2, null, systemProperties);

        taglineBuilder
                .append("================ ")
                .append(PropertiesConfig.class.getSimpleName())
                .append(" ================")
                .append(lineSeparator).append(lineSeparator)
                .append("ZTS Properties:")
                .append(ztsPropertiesOutStream.toString())
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

    private String convertToResource(ApplicationContext ctx, Properties properties, String propertyName) {
        String propValue = properties.getProperty(propertyName);
        try {
            Resource res = ctx.getResource(propValue);
            if (res instanceof UrlResource) {
                String newValue = res.getFile().getCanonicalPath();
                LOG.info(String.format("Expanded resource property ['%s=%s]' to [%s='%s]", propertyName, propValue, propertyName, newValue));
                if (!res.exists()) {
                    LOG.warn(String.format("Resource %s doesn't exist", newValue));
                }
                return newValue;
            }
            return propValue;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error occurred when trying to convert to [%s=%s] to resource", propertyName, propValue), e);
        }
    }
}
