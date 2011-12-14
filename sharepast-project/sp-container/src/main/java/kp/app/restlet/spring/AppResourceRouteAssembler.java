package kp.app.restlet.spring;

import kp.app.util.lang.SystemRuntimeException;
import org.restlet.Restlet;
import org.restlet.ext.spring.SpringFinder;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;
import org.slf4j.Logger;

import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/27/11
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppResourceRouteAssembler {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger( AppResourceRouteAssembler.class );

      TreeMap<String,Template> uriMap;
      TreeMap<String,Template> pubUriMap;
      TreeMap<String,String>   reversePubUriMap;

      private void processRouter(Router aRouter, TreeMap<String,Template> aUriMap )
      {
        for( Route route : aRouter.getRoutes() )
        {
          Restlet restlet = route.getNext();

          if( !SpringFinder.class.isAssignableFrom( restlet.getClass() ) )
            continue;

          SpringFinder finder = (SpringFinder) restlet;

          String resName = finder.getName();

          Template template = ((TemplateRoute)route).getTemplate();

          if( resName == null )
          {
            String error = String.format( "pattern %s does not have a name - please configure", template.getPattern() );
            LOG.error( error );
            throw new SystemRuntimeException( error );
          }

          aUriMap.put( resName, template );
        }
      }

      private void reverseRouter( Router aRouter, TreeMap<String,String> aUriMap )
      {
        for( Route route : aRouter.getRoutes() )
        {
          Restlet restlet = route.getNext();

          if( ! SpringFinder.class.isAssignableFrom( restlet.getClass() ) )
            continue;

          SpringFinder finder = (SpringFinder) restlet;

          String resName = finder.getName();

          Template template = ((TemplateRoute)route).getTemplate();

          if( resName == null )
          {
            String error = String.format( "pattern %s does not have a name - please configure", template.getPattern() );
            LOG.error( error );
            throw new SystemRuntimeException( error );
          }

          aUriMap.put( template.getPattern(), resName );
        }
      }

      public AppResourceRouteAssembler( Router router )
      {
        this( router, null );
      }

      public AppResourceRouteAssembler( Router router, Router pubRouter )
      {
        uriMap = new TreeMap<String,Template>();
        processRouter( router, uriMap );

        if( pubRouter != null )
        {
          pubUriMap = new TreeMap<String,Template>();
          processRouter( pubRouter, pubUriMap );
          reversePubUriMap = new TreeMap<String, String>();
          reverseRouter( pubRouter, reversePubUriMap );
        }

        LOG.info( String.format(  "Resource assmebler: %d application URLs, %d public URLs", uriMap.size(), pubUriMap == null ? 0 : pubUriMap.size() ) );
      }

      public TreeMap<String, Template> getUriMap()
      {
        return uriMap;
      }

    }


