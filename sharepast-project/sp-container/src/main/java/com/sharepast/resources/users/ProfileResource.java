package com.sharepast.resources.users;

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
 * Date: 8/27/11
 * Time: 12:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileResource extends AbstractConfigurableResource {

    @Get("html")
    public Representation showProfile(Variant variant) throws ResourceException {
        int userId = Integer.parseInt((String) getRequestAttributes().get("userId"));
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("userId", userId);
		return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }

}

