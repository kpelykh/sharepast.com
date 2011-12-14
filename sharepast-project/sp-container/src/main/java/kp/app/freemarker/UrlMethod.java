package kp.app.freemarker;

import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import kp.app.restlet.freemarker.AbstractConfigurableResource;
import kp.app.restlet.freemarker.FreemarkerRepresentationFactory;
import kp.app.util.restlet.RequestContextUtil;
import org.restlet.resource.ResourceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/27/11
 * Time: 12:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class UrlMethod implements TemplateMethodModelEx {

    public Object exec(List arguments)
            throws TemplateModelException {
        if (arguments.size() != 2) {
            throw new TemplateModelException("Wrong arguments");
        }
        // first argument is url type
        String routeName = arguments.get( 0 ).toString();
        // second argument is hash of params e.g. {"offerId": 12}
        TemplateHashModelEx hash = (TemplateHashModelEx) arguments.get(1);
        Map<String, Object> params = convertToMap(hash);

        try
        {
          AbstractConfigurableResource resource = (AbstractConfigurableResource) RequestContextUtil.get(FreemarkerRepresentationFactory.TEMPLATE_PARAM_RESOURCE);
          return resource.prepareUri( routeName, params );
        }
        catch ( ResourceException ex )
        {
          throw new TemplateModelException( ex );
        }
    }

    private static Map<String, Object> convertToMap(TemplateHashModelEx hash)
            throws TemplateModelException {
        Map<String, Object> map = new HashMap<String, Object>();
        TemplateModelIterator iterator = hash.keys().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = hash.get(key).toString();
            map.put(key, value);
        }
        return map;
    }
}

