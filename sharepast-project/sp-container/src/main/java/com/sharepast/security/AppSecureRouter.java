package com.sharepast.security;

import org.restlet.Restlet;
import org.restlet.ext.spring.SpringRouter;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/31/11
 * Time: 1:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppSecureRouter extends SpringRouter {
    private static final Logger LOG = LoggerFactory.getLogger(AppSecureRouter.class);

    protected TemplateRoute createRoute(String uriPattern, Restlet target, int matchingMode) {
        final AppSecureRoute result = new AppSecureRoute(this, uriPattern, target);

        result.getTemplate().setMatchingMode(matchingMode);

        result.setMatchingQuery(getDefaultMatchingQuery());

        if (LOG.isDebugEnabled() && target != null)
            LOG.debug(String.format("route %s => %s", uriPattern
                    , AppResourceFinder.class.isAssignableFrom(target.getClass())
                    ? ((AppResourceFinder) target).getTargetClass() == null ? "no target" : ((AppResourceFinder) target).getTargetClass().getName()
                    : target.getClass().getName()
            ));
        return result;
    }

    @Override
    protected TemplateRoute createRoute(String uriPattern, Restlet target) {
        return createRoute(uriPattern, target, Template.MODE_STARTS_WITH);
    }

}
