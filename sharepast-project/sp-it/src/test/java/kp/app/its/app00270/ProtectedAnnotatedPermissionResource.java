package kp.app.its.app00270;

import kp.app.restlet.freemarker.AbstractConfigurableResource;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/31/11
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProtectedAnnotatedPermissionResource extends AbstractConfigurableResource
{
  public static final String myMessage = ProtectedAnnotatedPermissionResource.class.getName();

    @Override
    @RequiresPermissions( "impossible:permission" )
    protected Representation get() throws ResourceException {
        return new JsonRepresentation( myMessage );
    }

}
