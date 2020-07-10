package com.itpaths.artesia.dam.service;

import com.itpaths.artesia.dam.component.UtilConf;
import com.itpaths.artesia.dam.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
public class ArtesiaUtil {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    @Autowired
    private UtilConf utilConf;
    @Autowired
    private ArtesiaWorker artesiaWorker;
    @Autowired
    private ArtesiaRetrival artesiaRetrival;
    private String dfolder, sfolder;
    private StringBuilder output = new StringBuilder();
    private StringBuilder error = new StringBuilder();
    private StringBuilder data = new StringBuilder();
    private Map<Integer, List<Node>> nodes;

    public String status(String df, String sf) {
        boolean dir = false;
        this.dfolder = df;
        this.sfolder = sf;
        File sdir = null;
        try {
            sdir = new File(sf);
            if (sdir.isDirectory())
                dir = true;
            else
                error.append("Invalid source folder. ");
        } catch (Exception e) {
            error.append("Invalid source folder. ");
        }

        if (dir) {
            //prepare folders hierarchy
            artesiaWorker.parepare(sf, utilConf.getTempDir());
            nodes = artesiaWorker.mapFolders(dfolder, sfolder, artesiaRetrival);
            //creating folder heierarchy
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd", "/c", MessageFormat.format(utilConf.getPrep(), sfolder, dfolder));
            System.out.println(MessageFormat.format(utilConf.getPrep(), utilConf.getTempDir(), dfolder) + "\n");
            processBuilder.directory(new File(utilConf.getPrPath()));
            Process process = null;
            try {
                process = processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = "";
            data.append(sdf.format(new Timestamp(System.currentTimeMillis())) + " : Creating folders hierarchy\n");
            while (true) {
                try {
                    if (!((line = reader.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                data.append(sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + line + "\n");
            }
            output.append("Job placed successfully. ");
            Ope ope = new Ope();
            ope.start();
        } else {
            error.append("Invalid input.");
        }
        String response = "{\"output\": \"" + output.toString().replaceAll("\b", "/") + "\", \"error\": \"" + error + "\" }";
        return response;
    }

    public class Ope extends Thread {
        /**
         * lets create .lck file in the bulk utility folder, however the log file will be crated into the cbutil webapp folder, where we kept folder.properties file too
         */
        public void run() {
            int i = 0;
            File lock = null;
            File file = new File(sfolder + "\\job-status.log");
            FileWriter lockw = null;
            try {
                try {
                    lock = new File(sfolder + "\\.lck");
                    lock.createNewFile();
                    lockw = new FileWriter(lock.getAbsoluteFile(), true);
                    data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : Lock file is crated");
                    for (int indx : nodes.keySet()) {
                        for (Node node : nodes.get(indx)) {
                            ProcessBuilder processBuilder = new ProcessBuilder();
                            processBuilder.command("cmd", "/c", MessageFormat.format(utilConf.getImprep(), node.getPath(), node.getKey()));
                            System.out.println(MessageFormat.format(utilConf.getImprep(), node.getPath(), node.getKey()) + "\n");
                            processBuilder.directory(new File(utilConf.getImPath()));
                            Process process = processBuilder.start();
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(process.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                data.append(sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + line + "\n");
                            }
                            int exitVal = process.waitFor();
                            if (exitVal == 0) {
                                data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : Import job initiated for folder [ " + node.getParent() + "/" + node.getName() + " ]");
                            } else {
                                data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + "Import job failed to initiate for folder [ " + node.getParent() + "/" + node.getName() + " ], Something went wrong, fore more details, please check logs!");
                            }
                        }
                    }
                } catch (IOException e) {
                    data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + e.getMessage());
                e.printStackTrace();
            } finally {
                artesiaWorker.cleanup(utilConf.getTempDir());
                try {
                    lockw.close();
                } catch (Exception e) {
                    data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + "No lock found");
                }
                lock.delete();
                data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : Lock file is removed");
                BufferedWriter bw = null;
                FileWriter fw = null;

                try {
                    // if file doesnt exists, then create it
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    // true = append file
                    fw = new FileWriter(file.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    bw.write(data.toString());
                } catch (IOException e) {
                    data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }

        }
    }
}
