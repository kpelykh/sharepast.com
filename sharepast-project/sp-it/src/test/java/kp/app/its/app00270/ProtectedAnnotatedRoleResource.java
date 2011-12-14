package kp.app.its.app00270;

import kp.app.restlet.freemarker.AbstractConfigurableResource;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 9/1/11
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProtectedAnnotatedRoleResource extends AbstractConfigurableResource
{
  public static final String myMessage = ProtectedAnnotatedRoleResource.class.getName();

    @Override
    protected Representation get() throws ResourceException {
        return new JsonRepresentation( myMessage );
    }

}

