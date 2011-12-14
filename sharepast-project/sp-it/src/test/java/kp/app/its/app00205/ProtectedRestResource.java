package kp.app.its.app00205;

import kp.app.restlet.freemarker.AbstractConfigurableResource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.testng.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 8/15/11
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProtectedRestResource extends AbstractConfigurableResource {

    public static final String value = "called me 216";


    @Override
    @RequiresAuthentication
    protected Representation get() throws ResourceException {
        Subject subject = SecurityUtils.getSubject();

        Assert.assertNotNull(subject, "subject is null in the resource");

        System.out.println("secure resource succesfully accessed by " + subject.getPrincipal());

        return new JsonRepresentation( value );
    }
}


