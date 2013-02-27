package com.sharepast.mvc.controller;

import com.sharepast.dao.UserDAO;
import com.sharepast.domain.user.User;
import com.sharepast.mvc.form.NewUserForm;
import com.sharepast.service.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/22/12
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
//@Controller
public class UserController {

    @Autowired
    private UserDAO userDao;

    @Autowired
    private MessageSource messageSource;


    @RequestMapping(value = "/users/new", method = RequestMethod.GET)
    public ModelAndView newUser() {

        if (Subject.isAuthenticated()) {
            return new ModelAndView(new RedirectView("/users/home"));
        }

        return new ModelAndView("/users/new");
    }

    @RequestMapping(value = "/users/new", method = RequestMethod.POST)
    public ModelAndView registerNewUser(@Valid NewUserForm userForm, BindingResult result,
                                  Map model) {

        if (result.hasErrors()) {
            return new ModelAndView("loginform");
        }

        userForm = (NewUserForm) model.get("userForm");

        List<String> errors = new ArrayList<String>();

        String email = userForm.getEmail();
        String username = userForm.getUserName();

        User user = userDao.findByEmail(email);
        if (user != null) {
            errors.add(messageSource.getMessage("error.email.taken", null, Locale.getDefault()));
        }
        user = userDao.findByUsername(username);
        if (user != null) {
            errors.add(messageSource.getMessage("error.username.taken", null, Locale.getDefault()));
        }

        if (errors.size() > 0) {
            model.put("error", errors);
        }

        return new ModelAndView("loginsuccess");
    }


    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ModelAndView profile(@PathVariable Integer userId) {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("userId", userId);

        return new ModelAndView("users/profile");
    }

}
