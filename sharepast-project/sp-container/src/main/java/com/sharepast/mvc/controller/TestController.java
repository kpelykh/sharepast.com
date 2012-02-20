package com.sharepast.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String test() {
        return "login";
    }
}
