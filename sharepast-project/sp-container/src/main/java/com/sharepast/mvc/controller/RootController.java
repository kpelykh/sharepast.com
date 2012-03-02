package com.sharepast.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/22/12
 * Time: 12:40 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class RootController {

    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/login");
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public ModelAndView home(Locale locale, Model model) {
        logger.info("Welcome home! the client locale is "+ locale.toString());

        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

        String formattedDate = dateFormat.format(date);

        model.addAttribute("serverTime", formattedDate );

        return new ModelAndView("root");
    }



}
