package com.sharepast.restlet.ext.servlet;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.engine.Engine;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.engine.adapter.HttpResponse;
import org.restlet.ext.servlet.ServletAdapter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 4/21/11
 * Time: 1:53 AM
 * To change this template use File | Settings | File Templates.
 */

/*
* There're two reasons to extend default ServletAdapter:
* 1) In default implementation getBaseRef incorrectly calculates basePath and as a result correct restlet
* can't be located
* 2) Since we're using Shiro Security, DefaultWebSessionManager.getReferencedSessionId()  tries to get find
* JSESSIONID in all possible places, included cookies abd request params. And when it makes a call to
* Request.getParameter() it invokes extractParameters, which reads InputStream if content type == MimeTypes.FORM_ENCODED
* Therefore when request arrives into restlet, inputstream is empty, so we need to recreate it from parsed parameters
* 3) in service method cookies explicitly copied from jetty HttpServletRequest into Restlet request (httpResponse.setCookieSettings)
* and duplicate cookies removed from httpResponse.getCookieSettings() after handle method.
* Shiro will add security cookies directly to HttpServletResponse during authorization process,
* so we need to make sure that old cookies don't overwrite new ones, in case if they happen to be in HttpServletResponse and httpResponse.
*
* */
public class AppServletAdapter extends ServletAdapter {

    public AppServletAdapter(ServletContext context) {
        super(context);
    }

    @Override
    public Reference getBaseRef(HttpServletRequest request) {
        Reference result = null;
        final String basePath = request.getContextPath();
        final String baseUri = request.getRequestURL().toString();
        // Path starts at first slash after scheme://
        final int pathStart = baseUri.indexOf("/", request.getScheme().length() + 3);
        if (basePath.length() == 0) {
            // basePath is empty in case the webapp is mounted on root context
            if (pathStart != -1) {
                result = new Reference(baseUri.substring(0, pathStart));
            } else {
                result = new Reference(baseUri);
            }
        } else {
            if (pathStart != -1) {
                final int baseIndex = baseUri.indexOf(basePath, pathStart);
                if (baseIndex != -1) {
                    result = new Reference(baseUri.substring(0, baseIndex
                            + basePath.length()));
                }
            }
        }

        return result;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        if (getNext() != null) {
            try {
                // Set the current context
                Context.setCurrent(getContext());

                // Convert the Servlet call to a Restlet call
                AppServletCall servletCall = new AppServletCall(request
                        .getLocalAddr(), request.getLocalPort(), request, response);
                HttpRequest httpRequest = toRequest(servletCall);
                HttpResponse httpResponse = new HttpResponse(servletCall,
                        httpRequest);

                // Adjust the relative reference
                httpRequest.getResourceRef().setBaseRef(getBaseRef(request));

                // Adjust the root reference
                httpRequest.setRootRef(getRootRef(request));

                // Handle the request and commit the response
                getNext().handle(httpRequest, httpResponse);
                commit(httpResponse);
            } finally {
                Engine.clearThreadLocalVariables();
            }
        } else {
            getLogger().warning("Unable to find the Restlet target");
        }
    }

}
