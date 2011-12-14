package kp.app.resources.users;

import kp.app.restlet.freemarker.AbstractConfigurableResource;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 7/22/11
 * Time: 11:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class HomeResource extends AbstractConfigurableResource {

    @Get("html")
    public Representation showUserHomePage(Variant variant) throws ResourceException {
        Map<String, Object> dataModel = new HashMap<String, Object>();
		return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }

}

