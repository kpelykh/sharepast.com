package com.sharepast.resources;

import org.apache.shiro.SecurityUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 4/25/11
 * Time: 1:10 AM
 * To change this template use File | Settings | File Templates.
 */

public class RootNamespaceRestlet extends Restlet {

    private static final String home = "/pub/home";

    @Autowired
    private RootResource rootResource;

    @Override
    public void handle(Request request, Response response) {
        System.out.println("Authenticated=" + SecurityUtils.getSubject().isAuthenticated());
        renderRootPage(response);
        /*Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            User user = SecurityDataUtil.getCurrentAccount();
            if (user != null)
                response.redirectTemporary(String.format("%s/home", LogonUtils.getApiPrefix(), user.getId()));
            else {
                response.redirectTemporary( ref404 );
                renderRootPage(response);
            }
        } else {
            renderRootPage(response);
        }*/

    }


    private void renderRootPage(Response response) {
        response.setEntity(rootResource.getRepresentationTemplates().get(MediaType.TEXT_HTML).createRepresentation(MediaType.TEXT_HTML, null, rootResource));
        response.setStatus(Status.SUCCESS_OK);
    }


}