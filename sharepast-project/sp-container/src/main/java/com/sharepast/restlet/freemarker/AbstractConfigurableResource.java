package com.sharepast.restlet.freemarker;

import com.sharepast.util.spring.Configurator;
import com.sharepast.restlet.spring.AppResourceRouteAssembler;
import com.sharepast.security.LogonUtils;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.resource.AnnotationInfo;
import org.restlet.engine.resource.AnnotationUtils;
import org.restlet.engine.resource.VariantInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Template;
import org.restlet.util.Resolver;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanNameAware;

import java.util.*;

/**
 * A Spring-configurable Restlet Resource.
 *
 * @author Konstantin Laufer (laufer@cs.luc.edu)
 */
public abstract class AbstractConfigurableResource extends ServerResource implements BeanNameAware {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AbstractConfigurableResource.class);

    // TODO generation of representations of response entities methods other
    // than GET
    // TODO think about how to deal with dependencies on so many
    // concrete representations

    private String resourceName;

    /**
     * Modifiable list of variants.
     */
    private volatile List<Variant> variants;

    @Override
    public void init(final Context context, final Request request,
                     final Response response) {
        // workaround for overzealous init method
        //LOG.info("before init: variants = " + getVariants());
        final ResourcePropertyHolder backup = new ResourcePropertyHolder();
        BeanUtils.copyProperties(this, backup);
        super.init(context, request, response);
        BeanUtils.copyProperties(backup, this);
        //LOG.info("after init: variants = " + getVariants());
    }

    @Override
    protected List<Variant> getVariants(Method method) {
        List<Variant> variants = this.variants;

        if (variants == null && method != null) {
            variants = new ArrayList<Variant>();

            // Add annotation-based variants in priority
            if (isAnnotated() && hasAnnotations()) {
                List<Variant> annoVariants = null;
                method = (Method.HEAD.equals(method)) ? Method.GET : method;

                for (AnnotationInfo annotationInfo : getAnnotations()) {
                    if (annotationInfo.isCompatible(method, getQuery(),
                            getRequestEntity(), getMetadataService(),
                            getConverterService())) {
                        annoVariants = annotationInfo.getResponseVariants(
                                getMetadataService(), getConverterService());

                        if (annoVariants != null) {
                            for (Variant v : annoVariants) {
                                variants.add(new VariantInfo(v, annotationInfo));
                            }
                        }
                    }
                }
            }

            this.variants = variants;
        }

        return variants;
    }

    /**
     * Returns the annotation descriptors.
     *
     * @return The annotation descriptors.
     */
    private List<AnnotationInfo> getAnnotations() {
        return isAnnotated() ? AnnotationUtils.getAnnotations(getClass())
                : null;
    }

    @SuppressWarnings("unchecked")
    public Map<MediaType, RepresentationFactory> getRepresentationTemplates() {
        return representationTemplates != null ? representationTemplates
                : Collections.EMPTY_MAP;
    }

    public void setRepresentationTemplates(
            final Map<MediaType, RepresentationFactory> representationTemplates) {
        LOG.info("setRepresentationTemplates: " + representationTemplates);
        if (this.representationTemplates == null)
            this.representationTemplates = new LinkedHashMap<MediaType, RepresentationFactory>();
        else
            this.representationTemplates.clear();
        this.representationTemplates.putAll(representationTemplates);
        LOG.info("setRepresentationTemplates: " + representationTemplates);
    }

    private LinkedHashMap<MediaType, RepresentationFactory> representationTemplates;

    protected Representation createTemplateRepresentation(
            final MediaType mediaType, final Map<String, Object> dataModel) {
        final RepresentationFactory factory = representationTemplates
                .get(mediaType);
        return factory.createRepresentation(mediaType, dataModel, this);
    }

    protected Representation createTemplateRepresentation(final Map<String, Object> dataModel) {
        MediaType mediaType = getClientInfo().getPreferredMediaType(new ArrayList<MediaType>(representationTemplates.keySet()));
        final RepresentationFactory factory = representationTemplates
                .get(mediaType);
        return factory.createRepresentation(mediaType, dataModel, this);
    }

    public String prepareUri(String name, Map<String, ?> values) throws ResourceException {

        AppResourceRouteAssembler appAssembler = Configurator.getInstance().getBean(AppResourceRouteAssembler.class, "appAssembler");
        if (appAssembler.getUriMap() == null) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }

        Template template = appAssembler.getUriMap().get(name);

        if (template == null)
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);

        Map<String, ?> uriValues = new HashMap<String, Object>(values);
        DestructiveMapResolver resolver = new DestructiveMapResolver(uriValues);
        String uri = LogonUtils.getApiPrefix() + template.format(resolver);

        // unresolved variables?
        if (uriValues.size() == 0) {
            // no, then we're done
            return uri;
        }

        // add unresolved variables to query string
        Reference ref = new Reference(uri);
        for (Map.Entry<String, ?> entry : uriValues.entrySet()) {
            ref.addQueryParameter(entry.getKey(), entry.getValue().toString());
        }
        return ref.toString();
    }

    protected final static class ResourcePropertyHolder {

        private boolean modifiable;

        public boolean isModifiable() {
            return modifiable;
        }

        public void setModifiable(final boolean modifiable) {
            this.modifiable = modifiable;
        }

        private boolean available;

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        private boolean negotiateContent;

        public boolean isNegotiateContent() {
            return negotiateContent;
        }

        public void setNegotiateContent(boolean negotiateContent) {
            this.negotiateContent = negotiateContent;
        }

        private boolean readable;

        public boolean isReadable() {
            return readable;
        }

        public void setReadable(boolean readable) {
            this.readable = readable;
        }
    }

    @Override
    public void setBeanName(String name) {
        resourceName = name;
    }

    public String getName() {
        return resourceName;
    }

    // Clone of regular map resolver
    // removes variables from the passed-in map as they get resolved
    // this allows us to put unresolved variables in the query string
    private static class DestructiveMapResolver extends Resolver<String> {
        private final Map<String, ?> map;

        public DestructiveMapResolver(Map<String, ?> map) {
            this.map = map;
        }

        @Override
        public String resolve(String variableName) {
            final Object value = this.map.remove(variableName);
            return (value == null) ? null : value.toString();
        }
    }
}
