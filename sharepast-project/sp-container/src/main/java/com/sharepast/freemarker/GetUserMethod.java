package com.sharepast.freemarker;

import com.sharepast.dal.domain.user.User;
import com.sharepast.security.SecurityDataUtil;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private SecurityDataUtil securityDataUtil;

    public Object exec (List arguments) throws TemplateModelException {
        Map<String, Object> result = new HashMap<String, Object>();
        boolean isAuthenticated = false;
        boolean isRemembered = false;
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            isAuthenticated = subject.isAuthenticated();
            isRemembered = subject.isRemembered();
        }
        User user = securityDataUtil.getCurrentUser();
        result.put("user", user);
        result.put("isAuthenticated", isAuthenticated);
        result.put("isRemembered", isRemembered);
        return result;

    }
}
