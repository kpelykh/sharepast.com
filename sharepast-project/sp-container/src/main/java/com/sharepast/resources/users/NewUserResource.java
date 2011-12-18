package com.sharepast.resources.users;

import com.sharepast.dal.dao.UserDAO;
import com.sharepast.dal.domain.user.User;
import com.sharepast.restlet.freemarker.AbstractConfigurableResource;
import com.sharepast.security.LogonUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.restlet.data.Form;
import org.restlet.representation.EmptyRepresentation;
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
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            getResponse().redirectSeeOther( LogonUtils.getHomeUri() );
            return null;
        }
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
