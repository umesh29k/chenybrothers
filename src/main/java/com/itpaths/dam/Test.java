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

public class Test {
    Set<String> ids = new HashSet<>();

    public void createFolder(String source, String dest) {
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

    public void mapFolders(){
        Retrival retrival = new Retrival();
        String fId = "1001N";
        JsonArray allFolders = new Gson().fromJson(retrival.getFolders(fId), JsonArray.class);

        String rpth = "E:\\ncert books";
        List<File> files = new ArrayList<>();

        listFiles(rpth, files);
        List<Node> nodes = new ArrayList<>();
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

        HashMap<Integer, List<Node>> map = new HashMap<>();
        int index = 0;
        for (List<String> list : dlist) {
            if (list.size() > index)
                index = list.size();
        }
        for (int i = 1; i <= index; i++) {
            summarize(dlist, map, i);
        }

        for (int i : map.keySet()) {
            System.out.println("\n\n");
            for (Node n : map.get(i)) {
                System.out.println(n.getName());
            }
        }

        HttpEntity he = retrival.initializeSession("tsupre", "Otmm@123");
        ResponseEntity<String> entity = null;
        for (int i : map.keySet()) {
            for (Node n : map.get(1)) {
                if (1 == 1)
                    retrival.getFolders(fId, entity, he, allFolders);
                else
                    retrival.getFolders(n.getParent().getKey(), entity, he, allFolders);
                for (JsonElement je : allFolders) {
                    JsonObject jo = je.getAsJsonObject();
                    if (jo.get("name").getAsString().equalsIgnoreCase(n.getName())) {
                        n.setKey(jo.get("id").getAsString());
                    }
                }
            }
        }
    }

    public static void main(String[] a) {
        Test test = new Test();
        test.createFolder("E:\\ncert books", "E:\\test");
    }

    public static void summarize(List<List<String>> dlist, HashMap<Integer, List<Node>> nodes, int index) {
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

class Node {
    private Node parent;
    private String name;
    private int index;
    private String key;

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}