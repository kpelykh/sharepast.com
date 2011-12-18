package com.sharepast.its.app00200;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 4/16/11
 * Time: 12:44 AM
 * To change this template use File | Settings | File Templates.
 */

@Component(value = "/protectedAnnotatedAuthenticatedResource")
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class ProtectedAnnotatedAuthenticatedResource extends ServerResource
{
  public static final String myMessage = "called me";

    @Override
    @RequiresAuthentication
    protected Representation get() throws ResourceException {
        return new JsonRepresentation( myMessage );
    }

}

