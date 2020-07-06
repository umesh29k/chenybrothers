package com.itpaths.dam;


import com.fasterxml.jackson.core.format.InputAccessor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itpaths.dam.service.Retrival;
import com.itpaths.dam.util.Constants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;

public class Test {
    Set<String> ids = new HashSet<>();

    public static void main(String[] a) {
        String rpth = "E:\\ncert books";
        List<File> files = new ArrayList<>();
        listf(rpth, files);
        Map<String, List<String>> map = new HashMap<>();
        for (File f : files) {
            String[] pth = null;
            String opth = rpth.replaceAll("[^a-zA-Z0-9-&]", "/");
            String npth = f.getAbsolutePath().replaceAll("[^a-zA-Z0-9&]", "/");
            System.out.println(npth.replace(opth, ""));
            if (npth != null)
                pth = npth.replace(opth, "").split("/");
            List<String> list = Collections.synchronizedList(Arrays.asList(pth));
            List<String> dlst = new ArrayList<>();
            dlst.addAll(list);
            synchronized (list) {
                if (dlst.get(0).isEmpty())
                    dlst.remove(0);
            }
            m(dlst, map);
            JsonObject folder = new JsonObject();
            //create folder
            //get folder id

            //find all children of folder-id
            //if [0..n] index name exist in folder-children
            //take folder-id, get all children
            //else
            //create folder-id, get folder-id, add folder-id

            //trigger job for each folder to retrieve impex

            /*Retrival retrival = new Retrival();
            String fId = "";
            ResponseEntity<String> entity = null;
            HttpEntity requestEntity = null;
            RestTemplate restTemplate = new RestTemplate();
            JsonArray folders = new JsonArray();
            String fname = "";
            for (String nm : dlst) {
                entity = restTemplate.exchange(Constants.URL + "folders/" + fId + "/folders", HttpMethod.GET, requestEntity, String.class);
                JsonObject jo = new Gson().fromJson(entity.getBody(), JsonObject.class);
                for (JsonElement obj : jo.getAsJsonObject("folders_resource").getAsJsonArray("folder_list")) {
                    if (obj.getAsJsonObject().get("name").getAsString().equals(fname)) {
                        //map folder id
                    } else
                        folder.addProperty("pId", fId);
                    folder.addProperty("id", obj.getAsJsonObject().get("container_id").getAsString());
                    folder.addProperty("name", obj.getAsJsonObject().get("name").getAsString());
                    folders.add(folder);
                }
            }*/
        }
        System.out.println(map);
    }

    public static Map<String, List<String>> m(List<String> list, Map<String, List<String>> map) {
        if (list.size() == 1) {
            map.put(list.get(0), new ArrayList<>());
        } else {
            StringBuilder prnt = new StringBuilder();
            for (String nme : list) {
                if (prnt.toString().isEmpty()) {
                    if (!map.containsKey(nme)) {
                        /**
                         * add only if, if doesn't existing in map
                         */
                        map.put(nme, new ArrayList<>());
                    } else {
                        /**
                         * do nothing, becase parent already exists
                         */
                    }
                } else {
                    if (map.containsKey(prnt)) {
                        if (!map.get(prnt).contains(nme))
                            map.get(prnt).add(nme);
                    } else {

                    }
                }
                prnt.append(nme + "/");
            }
        }
        return map;
    }

    public static void listf(String directoryName, List<File> files) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        if (fList != null)
            for (File file : fList) {
                /*if (file.isFile()) {
                    files.add(file);
                } else*/
                if (file.isDirectory()) {
                    files.add(file);
                    listf(file.getAbsolutePath(), files);
                }
            }
    }

}

/***
 * split:
 * source-folder || folder dir
 * create-map
 * location- complete folder path for import
 * folder-dir-name, which need to create
 * - check, if folder name exists to location
 * if no
 * - create a folder under
 * - map folder-id and folder-absolute-path to new map
 * else
 * - map folder-id of found-folder and folder-absolute-path to new map
 * - iterate over new-map create for import utility
 */
