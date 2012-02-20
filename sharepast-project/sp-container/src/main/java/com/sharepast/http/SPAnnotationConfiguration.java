package com.sharepast.http;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/17/12
 * Time: 10:38 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.AnnotationParser;
import org.eclipse.jetty.annotations.ClassNameResolver;
import org.eclipse.jetty.util.PatternMatcher;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.DiscoveredAnnotation;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Since the original design for AnnotationConfiguration only scan WEB-INF/classes , WEB-INF/libs.
 * We create a specific implemenation to have a better support.
 *
 */


public class SPAnnotationConfiguration extends AnnotationConfiguration {

    private static final Logger LOG = Log.getLogger(SPAnnotationConfiguration.class);


    public void parseWebInfClasses(final WebAppContext context,
                                   final AnnotationParser parser) throws Exception {
        super.parseWebInfClasses(context, parser);

        Pattern containerPattern = Pattern.compile(".*classes/");
        
        PatternMatcher containerJarNameMatcher = new PatternMatcher ()
        {
            public void matched(URI uri) throws Exception
            {
                File file = new File(uri);
                if (file.isDirectory()) {

                    if (LOG.isDebugEnabled()) LOG.debug("scanning RJR classes for annotation:" + file.getAbsolutePath());
                    handleClasses(context, parser, uri);
                }
            }
        };

        ClassLoader loader = context.getClassLoader();
        while (loader != null && (loader instanceof URLClassLoader))
        {
            URL[] urls = ((URLClassLoader)loader).getURLs();
            if (urls != null)
            {
                URI[] containerUris = new URI[urls.length];
                int i=0;
                for (URL u : urls)
                {
                    try
                    {
                        containerUris[i] = u.toURI();
                    }
                    catch (URISyntaxException e)
                    {
                        containerUris[i] = new URI(u.toString().replaceAll(" ", "%20"));
                    }
                    i++;
                }
                containerJarNameMatcher.match(containerPattern, containerUris, false);
            }
            loader = loader.getParent();
        }
    }

    /* private helper */

    private void handleClasses(final WebAppContext context,
                               final AnnotationParser parser, URI dir)
            throws Exception {
        Resource classesDir = new FileResource(dir.toURL());

        if (classesDir.exists()) {
            clearAnnotationList(parser.getAnnotationHandlers());
            parser.parse(classesDir, new ClassNameResolver() {
                public boolean isExcluded(String name) {
                    if (context.isSystemClass(name))
                        return true;
                    if (context.isServerClass(name))
                        return false;
                    return false;
                }

                public boolean shouldOverride(String name) {
                    // looking at webapp classpath, found already-parsed
                    // class of same name - did it come from system or
                    // duplicate in webapp?
                    if (context.isParentLoaderPriority())
                        return false;
                    return true;
                }
            });

            // TODO - where to set the annotations discovered from
            // WEB-INF/classes?
            List<DiscoveredAnnotation> annotations = new ArrayList<DiscoveredAnnotation>();
            gatherAnnotations(annotations, parser.getAnnotationHandlers());
            context.getMetaData().addDiscoveredAnnotations(annotations);
        }
    }

}
