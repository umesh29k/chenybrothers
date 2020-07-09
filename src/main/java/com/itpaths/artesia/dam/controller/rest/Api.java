package com.itpaths.artesia.dam.controller.rest;

import com.itpaths.artesia.dam.service.ArtesiaRetrival;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Api {
    @Autowired
    private ArtesiaRetrival retrival;

    @RequestMapping(path="/{fid}")
    public String index(@PathVariable("fid") String fId) {
        return retrival.getFolders(fId);
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam(name="dat") String dat) {
        return retrival.get(dat);
    }

}
