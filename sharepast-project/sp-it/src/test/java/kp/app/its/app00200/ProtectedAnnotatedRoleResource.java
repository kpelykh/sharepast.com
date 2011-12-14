package kp.app.its.app00200;

import org.apache.shiro.authz.annotation.RequiresRoles;
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
 * Date: 3/23/11
 * Time: 10:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Component(value = "/protectedAnnotatedRoleResource")
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class ProtectedAnnotatedRoleResource extends ServerResource
{
  public static final String myMessage = "called me";

    @Override
    @RequiresRoles("role-that-does-not-exist")
    protected Representation get() throws ResourceException {
        return new JsonRepresentation( myMessage );
    }

}
