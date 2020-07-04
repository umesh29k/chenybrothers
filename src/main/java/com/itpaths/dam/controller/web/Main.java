package com.itpaths.dam.controller.web;

import com.itpaths.dam.service.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("name")
public class Main {
    @Autowired
    Page service;
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(ModelMap model) {
        return "login";
    }
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String home(ModelMap model, @RequestParam String name, @RequestParam String password) {
        boolean isValidUser = service.validateUser(name, password);
        if (!isValidUser) {
            model.put("errorMessage", "Invalid Credentials");
            return "login";
        }
        model.put("name", name);
        model.put("password", password);
        return "home";
    }
    @RequestMapping(value="/todo", method = RequestMethod.GET)
    public String todo(ModelMap model){
        String name = (String) model.get("name");
        model.put("todo", service.retrieveTodos(name));
        return "todo";
    }
}