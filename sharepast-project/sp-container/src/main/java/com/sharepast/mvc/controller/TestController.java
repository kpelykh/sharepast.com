package com.sharepast.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/2/12
 * Time: 12:40 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class TestController {
    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/login");
    }
}
