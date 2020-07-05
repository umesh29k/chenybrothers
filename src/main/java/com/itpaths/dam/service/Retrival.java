package com.itpaths.dam.service;

import com.google.gson.*;
import com.itpaths.dam.component.UtilConf;
import com.itpaths.dam.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class Retrival {
    @Autowired
    private UtilConf utilConf;
    private boolean isIte;
    private String id = "";
    private RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders requestHeaders = new HttpHeaders();
    private JsonObject jsonObject = new JsonObject();
    private Set<String> ids = new CopyOnWriteArraySet<>();

    public String getFolders(String fId) {
        ResponseEntity<String> entity = null;
        HttpEntity requestEntity = initializeSession();
        Map<String, Object> data = new HashMap<>();
        JsonArray folders = new JsonArray();
        isIte = true;
        if (!id.isEmpty()) {
            synchronized (this) {
                getAllFolders(fId, entity, requestEntity, folders);
                getFolder(fId, entity, requestEntity, folders);
            }
            if (data.size() == 1) {
                boolean status = (boolean) data.get("status");
                if (!status)
                    jsonObject.add("folders", new Gson().fromJson("[\"no valid folder found for the given folder id\"]", JsonArray.class));
            }
            try {
                restTemplate.exchange(Constants.URL + "sessions", HttpMethod.DELETE, requestEntity, String.class);
            } catch (Exception e) {
            }
            if (folders.size() > 0)
                return folders.toString();
            else
                return jsonObject.toString();
        } else {
            return jsonObject.toString();
        }
    }

    private void getFolder(String fId, ResponseEntity<String> entity, HttpEntity requestEntity, JsonArray folders) {
        try {
            entity = restTemplate.exchange(Constants.URL + "folders/" + id, HttpMethod.GET, requestEntity, String.class);
            JsonObject jo = new Gson().fromJson(entity.getBody(), JsonObject.class);
            for (JsonElement obj : jo.getAsJsonObject("folders_resource").getAsJsonArray("folder")) {
                JsonObject folder = new JsonObject();
                folder.addProperty("open", true);
                folder.addProperty("id", obj.getAsJsonObject().get("container_id").getAsString());
                folder.addProperty("name", obj.getAsJsonObject().get("name").getAsString());
                folders.add(folder);
                ids.add(obj.getAsJsonObject().get("container_id").getAsString());
            }
        } catch (Exception e) {
        }
    }

    private void getAllFolders(String fId, ResponseEntity<String> entity, HttpEntity requestEntity, JsonArray folders) {
        if (isIte && ids.size() == 0) {
            List<String> nlist = getFolders(fId, entity, requestEntity, folders);
            ids.addAll(nlist);
            isIte = false;
        }
        if (ids.size() > 0) {
            for (String id : ids) {
                ids.remove(id);
                List<String> nlist = getFolders(id, entity, requestEntity, folders);
                ids.addAll(nlist);
            }
            getAllFolders(fId, entity, requestEntity, folders);
        }
    }

    private List<String> getFolders(String fId, ResponseEntity<String> entity, HttpEntity requestEntity, JsonArray folders) {
        List<String> ids = new ArrayList<>();
        try {
            entity = restTemplate.exchange(Constants.URL + "folders/" + fId + "/folders", HttpMethod.GET, requestEntity, String.class);
            JsonObject jo = new Gson().fromJson(entity.getBody(), JsonObject.class);
            for (JsonElement obj : jo.getAsJsonObject("folders_resource").getAsJsonArray("folder_list")) {
                JsonObject folder = new JsonObject();
                folder.addProperty("pId", fId);
                folder.addProperty("id", obj.getAsJsonObject().get("container_id").getAsString());
                folder.addProperty("name", obj.getAsJsonObject().get("name").getAsString());
                folders.add(folder);
                ids.add(obj.getAsJsonObject().get("container_id").getAsString());
            }
        } catch (Exception e) {
        }
        return ids;
    }

    public HttpEntity initializeSession() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        byte[] bytes = Base64.getDecoder().decode(utilConf.getData());
        String[] cred = null;
        try {
            cred = new String(bytes, "utf-8").split(";");
            requestBody.add("username", cred[0]);
            requestBody.add("password", cred[1]);
        } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            jsonObject.add("status", new Gson().fromJson("[\"Unable to retrieve credentials\"]", JsonArray.class));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
        try {
            ResponseEntity<String> entity = restTemplate.exchange(Constants.URL + "sessions", HttpMethod.POST, request, String.class);
            jsonObject = new JsonParser().parse(entity.getBody()).getAsJsonObject();
            if (jsonObject.getAsJsonObject("session_resource") != null)
                if (jsonObject.getAsJsonObject("session_resource").getAsJsonObject("session") != null)
                    if (jsonObject.getAsJsonObject("session_resource").getAsJsonObject("session").get("message_digest") != null)
                        id = jsonObject.getAsJsonObject("session_resource").getAsJsonObject("session").get("message_digest").getAsString();
            requestHeaders.add("otmmauthtoken", id);
            //Remove session entry form json object
            jsonObject.remove("session_resource");
            request = new HttpEntity(null, requestHeaders);
        } catch (Exception e) {
            jsonObject.add("status", new Gson().fromJson("[\"invalid credentials\"]", JsonArray.class));
        }
        return request;
    }
}
