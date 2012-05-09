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

        if (env.acceptsProfiles("de", "test")) {
            resources.add(new ClassPathResource(env.resolvePlaceholders("de/environment.properties")));
            addResourceIfExists(resources, env.resolvePlaceholders("${user.home}/.m2/environment-de.properties"));
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

        //adding sharepast properties to env in the first line, so that it will be used to resolve placeholders
        env.getPropertySources().addLast(new PropertiesPropertySource("applicationProperties", properties));

        replacePlaceholders(env, properties);

        properties.put("web.resource.base", convertToResource(ctx, properties, "web.resource.base"));
        properties.put("jetty.web.default", convertToResource(ctx, properties, "jetty.web.default"));
        properties.put("log.dir", convertToResource(ctx, properties, "log.dir"));
        properties.put("config.path", convertToResource(ctx, properties, "config.path"));
        properties.put("activemq.home", convertToResource(ctx, properties, "activemq.home"));
        properties.put("activemq.base", convertToResource(ctx, properties, "activemq.base"));

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
