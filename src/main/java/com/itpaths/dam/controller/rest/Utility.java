package com.itpaths.dam.controller.rest;

import com.itpaths.dam.service.DamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/util")
public class Utility {
    @Autowired
    private DamUtil damUtil;
    @RequestMapping("")
    public String index() {
        StringBuilder output = damUtil.status();
        return "Greetings from Spring Boot!\n" + output;
    }
}
