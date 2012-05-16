package com.sharepast.grails.core.io;

import org.codehaus.groovy.grails.core.io.DefaultResourceLocator;
import org.codehaus.groovy.grails.core.io.ResourceLocator;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/15/12
 * Time: 1:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class PluginPathAwareFileSystemResourceLoader extends FileSystemResourceLoader {

    public static final String WEB_APP_DIRECTORY = "web-app";
    ResourceLocator resourceLocator = new DefaultResourceLocator();

    public void setSearchLocations(Collection<String> searchLocations) {
        resourceLocator.setSearchLocations(searchLocations);
    }

    @Override
    protected Resource getResourceByPath(String path) {
        Resource resource = super.getResourceByPath(path);
        if (resource != null && resource.exists()) {
            return resource;
        }

        String resourcePath = path;
        if (resourcePath.startsWith(WEB_APP_DIRECTORY)) {
            resourcePath = resourcePath.substring("web-app".length(),resourcePath.length());
        }
        Resource res = resourceLocator.findResourceForURI(resourcePath);
        if (res != null) {
            return res;
        }
        return new FileSystemContextResource(path);
    }

    /**
     * FileSystemResource that explicitly expresses a context-relative path
     * through implementing the ContextResource interface.
     */
    private static class FileSystemContextResource extends FileSystemResource implements ContextResource {

        public FileSystemContextResource(String path) {
            super(path);
        }

        public String getPathWithinContext() {
            return getPath();
        }
    }
}
