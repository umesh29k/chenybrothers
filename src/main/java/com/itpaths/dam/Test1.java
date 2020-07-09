package com.itpaths.dam;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itpaths.dam.service.Retrival;
import com.itpaths.dam.util.Constants;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory;

public class Test1 {
    Set<String> ids = new HashSet<>();

    public static void main(String[] a) {
        String fId = "1001N";
        String rpth = "E:\\ncert books";
        String np = "E:\\test";
        List<File> files = new ArrayList<>();

        listFiles(rpth, files);
        for (File f : files) {
            String npth = f.getAbsolutePath();
            new File(npth.replace(rpth, np)).delete();
        }
        File[] allContents = new File(np).listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                try {
                    deleteDirectory(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void listFiles(String directoryName, List<File> files) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        if (fList != null)
            for (File file : fList) {
                /*if (file.isFile()) {
                    files.add(file);
                } else*/
                if (file.isDirectory()) {
                    files.add(file);
                    listFiles(file.getAbsolutePath(), files);
                }
            }
    }
}
