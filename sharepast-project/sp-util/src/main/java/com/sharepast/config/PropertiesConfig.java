package com.sharepast.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/16/12
 * Time: 12:57 AM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class PropertiesConfig {

  private static final Log logger = LogFactory.getLog(PropertiesConfig.class);

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
  public Properties pathPostProcess(ConfigurableEnvironment env, @Qualifier("properties") Properties properties) {

    properties.put("web.resource.base", convertRelPathToAbsolute(env.resolvePlaceholders(properties.getProperty("web.resource.base"))));
    properties.put("jetty.web.default", convertRelPathToAbsolute(env.resolvePlaceholders(properties.getProperty("jetty.web.default"))));
    properties.put("log.dir", convertRelPathToAbsolute(env.resolveRequiredPlaceholders(properties.getProperty("log.dir"))));
    properties.put("config.path", convertRelPathToAbsolute(env.resolveRequiredPlaceholders(properties.getProperty("config.path"))));

    for (Map.Entry<Object, Object> prop : properties.entrySet()) {
      if (prop.getValue() instanceof String && ((String) prop.getValue()).contains("${") && ((String) prop.getValue()).contains("}")) {
        PropertyPlaceholderHelper pph = new PropertyPlaceholderHelper("${", "}", null, false);
        String newValue = pph.replacePlaceholders((String) prop.getValue(), properties);
        properties.setProperty((String) prop.getKey(), newValue);
      }
    }

    env.getPropertySources().addLast(new PropertiesPropertySource("applicationProperties", properties));
    return properties;
  }

  private String convertRelPathToAbsolute(String relPath) {
    FileSystemResource resource = new FileSystemResource(relPath);
    try {
      return resource.getFile().getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
