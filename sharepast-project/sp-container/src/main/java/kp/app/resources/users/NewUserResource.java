package kp.app.resources.users;

import kp.app.dal.dao.UserDAO;
import kp.app.dal.domain.user.User;
import kp.app.restlet.freemarker.AbstractConfigurableResource;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/10/11
 * Time: 12:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewUserResource extends AbstractConfigurableResource {

    @Autowired
    private UserDAO userDao;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @Get("html")
    public Representation showForm(Variant variant) throws ResourceException {
		return createTemplateRepresentation(variant.getMediaType(), Collections.EMPTY_MAP);
    }

    @Post("form")
    public Representation registerNewUser(Representation entity, Variant variant) throws ResourceException {
        Form newUserData = new Form(entity);
        Map<String, Object> dataModel = new HashMap<String, Object>();
        List<String> errors = new ArrayList<String>();

        String email = newUserData.getFirstValue("email");
        String username = newUserData.getFirstValue("username");
        User user = userDao.findByEmail(email);
        if (user != null) {
           errors.add(messageSource.getMessage("error.email.taken", null, Locale.getDefault()));
        }
        user = userDao.findUser(username);
        if (user != null) {
           errors.add(messageSource.getMessage("error.username.taken", null, Locale.getDefault()));
        }

        if (errors.size() > 0) {
            dataModel.put("error", errors);
        }


        return createTemplateRepresentation(dataModel);

    }

}
