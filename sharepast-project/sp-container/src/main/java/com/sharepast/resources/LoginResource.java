package com.sharepast.resources;

import com.sharepast.restlet.freemarker.AbstractConfigurableResource;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/10/11
 * Time: 1:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoginResource extends AbstractConfigurableResource {

    @Get("html")
    protected Representation showLoginForm(Variant variant) throws ResourceException {
        Map<String, Object> dataModel = new HashMap<String, Object>();
		return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }

}


