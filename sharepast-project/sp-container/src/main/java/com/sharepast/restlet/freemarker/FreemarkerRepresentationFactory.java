package com.sharepast.restlet.freemarker;

import com.sharepast.util.restlet.RequestContextUtil;
import freemarker.template.Configuration;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * A factory for representations based on the Restlet Freemarker template
 * extension.
 * 
 * @author Konstantin Laufer (laufer@cs.luc.edu)
 */
public class FreemarkerRepresentationFactory implements RepresentationFactory {

	private MediaType mediaType;
    public static final String TEMPLATE_PARAM_RESOURCE = "template_param_resource";

    public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(final MediaType mediaType) {
		this.mediaType = mediaType;
	}

	private String templateName;

	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

	/**
	 * The Freemarker configuration for this resource.
	 */
    @Autowired( required = true )
	private Configuration freemarkerConfig;

	@Override
	public Representation createRepresentation(
			final MediaType defaultMediaType,
			final Map<String, Object> dataModel,
            AbstractConfigurableResource resource ) {

        // put the resource in the context, so we can call getAccount from freemarker templates
        RequestContextUtil.put(TEMPLATE_PARAM_RESOURCE, resource);

		MediaType actualMediaType = getMediaType();
		if (actualMediaType == null)
			actualMediaType = defaultMediaType;
		try {
			return new TemplateRepresentation(freemarkerConfig
					.getTemplate(templateName), dataModel, actualMediaType);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
