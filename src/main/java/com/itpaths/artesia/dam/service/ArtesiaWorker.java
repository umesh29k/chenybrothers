package com.itpaths.artesia.dam.service;

import com.itpaths.artesia.dam.model.Node;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory;

@Service
public class ArtesiaWorker {
    Set<String> ids = new HashSet<>();
    public void parepare(String source, String dest) {
        List<File> files = new ArrayList<>();
        listFiles(source, files);
        for (File f : files) {
            String npth = f.getAbsolutePath();
            new File(npth.replace(source, dest)).mkdir();
        }
    }

    public void cleanup(String dest) {
        File[] allContents = new File(dest).listFiles();
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

    public HashMap<Integer, List<Node>> mapFolders(String fId, String rpth, ArtesiaRetrival retrival) {
        HashMap<Integer, List<Node>> map = new HashMap<>();
        JsonArray allFolders = new JsonArray();
        List<File> files = new ArrayList<>();
        listFiles(rpth, files);
        List<List<String>> dlist = new ArrayList<>();
        for (File f : files) {
            String[] pth = null;
            String opth = rpth.replace('\\', '/');
            String npth = f.getAbsolutePath().replace('\\', '/');
            if (npth != null)
                pth = npth.replace(opth, "").split("/");
            List<String> list = Collections.synchronizedList(Arrays.asList(pth));
            List<String> dlst = new ArrayList<>();
            dlst.addAll(list);
            synchronized (list) {
                if (dlst.get(0).isEmpty())
                    dlst.remove(0);
            }
            dlist.add(dlst);
        }
        int index = 0;
        for (List<String> list : dlist) {
            if (list.size() > index)
                index = list.size();
        }
        for (int i = 1; i <= index; i++) {
            summarize(dlist, map, i, rpth);
        }
        for (int i : map.keySet()) {
            System.out.println("\n\n");
            for (Node n : map.get(i)) {
                System.out.println(n.getName());
            }
        }

        HttpEntity he = retrival.initializeSession("tsuper", "Otmm@123");
        ResponseEntity<String> entity = null;
        for (int i : map.keySet()) {
            doFolderKeyMap(retrival, fId, allFolders, map, he, entity, i);
        }

        return map;
    }

    /**
     * This method iterate over the system-folder map, where each node hold parent-name and their child details,
     * this method will update folder key as per OTMM system so that we can iterate over this map to operate otmm folders
     * in the same way as we have folder hierarchy in our local
     *
     * @param retrival
     * @param fId
     * @param allFolders
     * @param map
     * @param he
     * @param entity
     * @param indx
     */
    private void doFolderKeyMap(ArtesiaRetrival retrival, String fId, JsonArray allFolders, HashMap<Integer, List<Node>> map, HttpEntity he, ResponseEntity<String> entity, int indx) {
        if (indx == 1) {
            for (Node n : map.get(indx)) {
                retrival.getFolders(fId, entity, he, allFolders);
                for (JsonElement je : allFolders) {
                    JsonObject jo = je.getAsJsonObject();
                    if (jo.get("name").getAsString().equalsIgnoreCase(n.getName())) {
                        n.setKey(jo.get("id").getAsString());
                    }
                }
            }
        } else {
            for (Node p : map.get(indx - 1)) {
                retrival.getFolders(p.getKey(), entity, he, allFolders);
                for (Node n : map.get(indx)) {
                    for (JsonElement je : allFolders) {
                        JsonObject jo = je.getAsJsonObject();
                        System.out.println(jo.get("pId").getAsString().equalsIgnoreCase(p.getKey()) + " || " + jo.get("name").getAsString().equalsIgnoreCase(n.getName()));
                        if (jo.get("pId").getAsString().equalsIgnoreCase(p.getKey()) && jo.get("name").getAsString().equalsIgnoreCase(n.getName())) {
                            n.setKey(jo.get("id").getAsString());
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] a) {
        ArtesiaWorker test = new ArtesiaWorker();
        String fId = "eb455368d104f30c4785f2c864cbf04ca0449473";
        String rpth = "E:\\ncert books";
        test.mapFolders(fId, rpth, new ArtesiaRetrival());
    }

    /**
     * setup map from list of folders in node format
     * @param dlist
     * @param nodes
     * @param index
     */
    public static void summarize(List<List<String>> dlist, HashMap<Integer, List<Node>> nodes, int index, String root) {
        List<Node> nl = new ArrayList<>();
        for (List<String> list : dlist) {
            if (list.size() == index) {
                Node node = new Node();
                node.setName(list.get(index - 1));
                node.setIndex(index);
                if (nodes.size() > 0) {
                    for (Node n : nodes.get(index - 1)) {
                        if (n.getName().equalsIgnoreCase(list.get(index - 2)))
                            node.setParent(n);
                    }
                } else {
                    node.setParent(new Node());
                }
                String path = "";
                for(int j=0; j<index; j++){
                    path += root + "\\" + list.get(j);
                }
                node.setPath(path);
                nl.add(node);
            }
        }
        nodes.put(index, nl);
    }

    public static void listFiles(String directoryName, List<File> files) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        if (fList != null)
            for (File file : fList) {
                if (file.isDirectory()) {
                    files.add(file);
                    listFiles(file.getAbsolutePath(), files);
                }
            }
    }

}