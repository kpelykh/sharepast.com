package com.sharepast.security;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/31/11
 * Time: 12:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppSecureRoute extends TemplateRoute {

    private static final Logger LOG = LoggerFactory.getLogger(AppSecureRoute.class);

    private String key;

    private AppResourceFinder appResourceFinder;

    /**
     * indicates that this route needs to perform security checks before handling the request
     */
    private boolean isSecured = false;

    public AppSecureRoute(Router router, Template template, Restlet next) {
        super(router, template, next);

        initSecure(next);

        key = template.getPattern();
    }

    public AppSecureRoute(Router router, String uriTemplate, Restlet next) {
        super(router, uriTemplate, next);

        initSecure(next);

        key = uriTemplate;
    }

    private void initSecure(Restlet next) {
        if (AppResourceFinder.class.isAssignableFrom(next.getClass())) {
            appResourceFinder = (AppResourceFinder) next;
            isSecured = true;
        }
    }

    /**
     * this is the main security checkpoint - called right before the engine passes control to the uri resource
     */
    @Override
    protected int beforeHandle(Request request, Response response) {
        int res = super.beforeHandle(request, response);

        if (!isSecured)
            return res;

         AppResourceFinder target = (AppResourceFinder) getNext();
         target.checkAuthz(request);

        return CONTINUE;
    }

}
