package com.itpaths.artesia.dam.controller.rest;

import com.itpaths.artesia.dam.service.ArtesiaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/util")
public class Utility {
    @Autowired
    private ArtesiaUtil damUtil;
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public String index(@RequestParam(name="dfolder") String df, @RequestParam(name="sfolder") String sf) {
        String output = damUtil.status(df, sf);
        return output;
    }
}
