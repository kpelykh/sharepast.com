package com.sharepast.its.app00200;

import org.apache.shiro.authz.annotation.RequiresUser;
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
 * Time: 12:38 AM
 * To change this template use File | Settings | File Templates.
 */
@Component(value = "/protectedResource")
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class ProtectedResource extends ServerResource
{
  public static final String myMessage = "called me";

    @Override
    @RequiresUser
    protected Representation get() throws ResourceException {
        return new JsonRepresentation( myMessage );
    }

}

