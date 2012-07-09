package com.sharepast.grails.web.context;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;


/**
 * Extends the Spring default ContextLoader to load GrailsApplicationContext.
 *
 * @author Graeme Rocher
 */
public class GrailsContextLoaderListener extends ContextLoaderListener {

    @Override
    protected ContextLoader createContextLoader() {
        return new GrailsContextLoader();
    }
}
