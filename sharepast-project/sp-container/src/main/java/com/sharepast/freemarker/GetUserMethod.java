package com.sharepast.freemarker;

import com.sharepast.domain.user.User;
import com.sharepast.security.Subject;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/26/11
 * Time: 11:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetUserMethod implements TemplateMethodModelEx {

    public Object exec (List arguments) throws TemplateModelException {
        Map<String, Object> result = new HashMap<String, Object>();

        boolean isAuthenticated = Subject.isAuthenticated();
        boolean isRemembered = Subject.isRememberMe();

        User user = Subject.getCurrentUser();
        result.put("user", user);
        result.put("isAuthenticated", isAuthenticated);
        result.put("isRemembered", isRemembered);
        return result;

    }
}
