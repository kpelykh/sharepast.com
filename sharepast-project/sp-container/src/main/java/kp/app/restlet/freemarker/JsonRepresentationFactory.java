package kp.app.restlet.freemarker;

import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory for representations based on the Restlet JSON template extension.
 * 
 * @author Konstantin Laufer (laufer@cs.luc.edu)
 */
public class JsonRepresentationFactory implements RepresentationFactory {

	@Override
	public Representation createRepresentation(
			final MediaType defaultMediaType,
			final Map<String, Object> dataModel, AbstractConfigurableResource resource) {
		final Map<Object, Object> model = new HashMap<Object, Object>();
		model.putAll(dataModel);
		return new JsonRepresentation(model);
	}
}
