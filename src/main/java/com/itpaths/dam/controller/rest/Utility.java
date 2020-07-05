package com.itpaths.dam.controller.rest;

import com.itpaths.dam.service.DamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/util")
public class Utility {
    @Autowired
    private DamUtil damUtil;
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public String index(@RequestParam(name="dfolder") String df, @RequestParam(name="sfolder") String sf) {
        String output = damUtil.status(df, sf);
        return output;
    }
}
