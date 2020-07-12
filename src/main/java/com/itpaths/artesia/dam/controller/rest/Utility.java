package com.itpaths.artesia.dam.controller.rest;

import com.itpaths.artesia.dam.service.ArtesiaRetrival;
import com.itpaths.artesia.dam.service.ArtesiaUtil;
import com.itpaths.artesia.dam.service.ArtesiaWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/util")
public class Utility {
    @Autowired
    private ArtesiaUtil damUtil;
    @Autowired
    private ArtesiaWorker artesiaWorker;
    @Autowired
    private ArtesiaRetrival artesiaRetrival;
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public String index(@RequestParam(name="dfolder") String df, @RequestParam(name="sfolder") String sf) {
        String output = damUtil.status(df, sf);
        return output;
    }

    @RequestMapping(value = "/getFolders", method = RequestMethod.POST)
    @ResponseBody
    public String getFolders(@RequestParam(name="id") String id, @RequestParam(name="path") String path) {
        artesiaWorker.mapFolders(id, path, artesiaRetrival);
        return "output";
    }

    @RequestMapping(value = "/impex", method = RequestMethod.POST)
    @ResponseBody
    public String getImpexs(@RequestParam(name="dfolder") String df, @RequestParam(name="sfolder") String sf) {
        String output = damUtil.impex(df, sf);
        return output;
    }
}
