package com.sharepast.resources;

import com.sharepast.dal.domain.user.User;
import com.sharepast.security.LogonUtils;
import com.sharepast.security.SecurityDataUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
    
    @Autowired
    private SecurityDataUtil securityUtil;

    @Override
    public void handle(Request request, Response response) {
        System.out.println("Authenticated=" + SecurityUtils.getSubject().isAuthenticated());

        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            User user = securityUtil.getCurrentUser();
            //response.redirectTemporary(String.format("%s/users/%d", LogonUtils.getApiPrefix(), user.getId()));
            response.redirectTemporary(LogonUtils.getHomeUri());
        } else {
            response.redirectTemporary(LogonUtils.getRootUri());
        }
    }


    private void renderRootPage(Response response) {
        response.setEntity(rootResource.getRepresentationTemplates().get(MediaType.TEXT_HTML).createRepresentation(MediaType.TEXT_HTML, null, rootResource));
        response.setStatus(Status.SUCCESS_OK);
    }


}