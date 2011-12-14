package kp.app.its.app00105;

import kp.app.its.common.MyJsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/22/11
 * Time: 12:48 AM
 * To change this template use File | Settings | File Templates.
 */
@Component(value = "/restResource")
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class RestResource extends ServerResource
{
  public static final String value = "called me 105";

    @Override
    protected Representation get() throws ResourceException {
        return new MyJsonRepresentation( value );
    }

}
