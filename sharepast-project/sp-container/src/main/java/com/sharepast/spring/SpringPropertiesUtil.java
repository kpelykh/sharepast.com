package com.sharepast.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/30/12
 * Time: 1:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class SpringPropertiesUtil extends PropertySourcesPlaceholderConfigurer {

    private ConfigurablePropertyResolver propertyResolver;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                     final ConfigurablePropertyResolver propertyResolver) throws BeansException {
        super.processProperties(beanFactoryToProcess, propertyResolver);

        this.propertyResolver = propertyResolver;

    }

    public String getProperty(String name) {
        return propertyResolver.getProperty(name);
    }



}
