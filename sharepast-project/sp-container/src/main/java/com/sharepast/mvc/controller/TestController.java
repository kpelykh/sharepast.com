package com.sharepast.mvc.controller;

import com.sharepast.domain.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/10/12
 * Time: 12:03 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/test")
public class TestController {

    @RequestMapping(method = RequestMethod.GET, value = "/1")
    public ModelAndView getCreateForm() {
        Map map = new HashMap();

        User user = new User("john", "password");
        user.setFirstName("John");
        user.setLastName("Doe");

        map.put("foo", "foo value");
        map.put("account", user);

        return new ModelAndView("/test", map);
    }
}