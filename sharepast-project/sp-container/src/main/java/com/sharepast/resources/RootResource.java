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
 * Date: 7/17/11
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class RootResource extends AbstractConfigurableResource {

    @Get("html")
    public Representation showRootPage(Variant variant) throws ResourceException {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        return createTemplateRepresentation(variant.getMediaType(), dataModel);
    }

}
