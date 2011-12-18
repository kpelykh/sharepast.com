package com.sharepast.restlet.freemarker;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

import java.util.Map;

/**
 * Uniform interface for factories that create concrete representations based on
 * specific template mechanisms.
 * 
 * @author Konstantin Laufer (laufer@cs.luc.edu)
 */
public interface RepresentationFactory {

	/**
	 * Creates a concrete representation for the given media type and data
	 * model.
	 * 
	 * @param defaultMediaType
	 *            the desired media type
	 * @param dataModel
	 *            the data model
	 * @return the representation
	 */
	Representation createRepresentation(MediaType defaultMediaType,
			Map<String, Object> dataModel, AbstractConfigurableResource resource);
}
