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
            Thread setupFoldersJob = new Thread(){
              @Override
              public void run(){
                  //prepare folders hierarchy
                  artesiaWorker.parepare(sf, utilConf.getTempDir());
              }
            };
            setupFoldersJob.setName("setup-folders");
            setupFoldersJob.start();

            //creating folder heierarchy
            String createFolders = MessageFormat.format(utilConf.getPrep(), sfolder, dfolder);
            Task createFolderJob = new Task(createFolders);
            //folder hierarchy created

            Thread initiateImport = new Thread() {
                @Override
                public void run() {
                    Thread getNodesJob = new Thread(){
                        @Override
                        public void run(){
                            nodes = artesiaWorker.mapFolders(dfolder, sfolder, artesiaRetrival);
                        }
                    };
                    getNodesJob.start();

                    try {
                        setupFoldersJob.join();
                        createFolderJob.start();
                        createFolderJob.join();
                        getNodesJob.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ImportJob importJob = new ImportJob();
                    importJob.start();
                }
            };

            initiateImport.start();
            output.append("Job placed successfully. ");

        } else {
            error.append("Invalid input.");
        }
        String response = "{\"output\": \"" + output.toString().replaceAll("\b", "/") + "\", \"error\": \"" + error + "\" }";
        return response;
    }

    private void intiateTask(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd", "/c", command);
        System.out.println(MessageFormat.format(utilConf.getPrep(), utilConf.getTempDir(), dfolder) + "\n");
        processBuilder.directory(new File(utilConf.getPrPath()));
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getProcessDetails(process);
    }

    private void getProcessDetails(Process process) {
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
    }

    public class Task extends Thread {
        private String command;

        public Task(String command) {
            this.command = command;
        }

        public void run() {
            intiateTask(command);
        }
    }

    public class ImportJob extends Thread {
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
                            String importAssets = MessageFormat.format(utilConf.getImprep(), node.getPath(), node.getKey());
                            final Task importAssetsJob = new Task(importAssets);
                            importAssetsJob.setName("ImportAssets-" + indx);
                            importAssetsJob.start();
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        importAssetsJob.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    String createImpexesCmd = MessageFormat.format(utilConf.getImprep(), node.getPath(), node.getKey());
                                    Task createImpexJob = new Task(createImpexesCmd);
                                    createImpexJob.setName("Import-Impex-" + indx);
                                    createImpexJob.start();
                                }
                            };
                        }
                    }
                } catch (IOException e) {
                    data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                data.append("\n" + sdf.format(new Timestamp(System.currentTimeMillis())) + " : " + e.getMessage());
                e.printStackTrace();
            } finally {

                Thread cleanupJob = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        artesiaWorker.cleanup(utilConf.getTempDir());
                    }
                });
                cleanupJob.setName("cleanup");
                cleanupJob.start();

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
