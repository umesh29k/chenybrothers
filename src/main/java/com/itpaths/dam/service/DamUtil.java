package com.itpaths.dam.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.itpaths.dam.component.UtilConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

@Service
public class DamUtil {
    @Autowired
    private UtilConf utilConf;
    private String dfolder, sfolder;
    StringBuilder output = new StringBuilder();
    StringBuilder error = new StringBuilder();

    public String status(String df, String sf){
        boolean dir = false;
        this.dfolder = df;
        this.sfolder = sf;
        File sdir = null;
        try {
            sdir = new File(sf);
            if(sdir.isDirectory())
                dir = true;
            else
                error.append("Invalid source folder");
        }
        catch (Exception e){
            error.append("Invalid source folder");
        }

        if(dir) {
            output.append(">>>>>>>>> " + utilConf.getUtilPath() + "\n ");
            output.append(">>>>>>>>> " + MessageFormat.format(utilConf.getCommand(), sf, df) + "\n ");

        }
        else{
            error.append("<br>Invalid input");
        }
        String response = "{\"output\": \"" + output +"\", \"error\": \"" + error + "\" }";
        return response;
    }

    public class Ope extends Thread {
        /**
         * lets create .lck file in the bulk utility folder, however the log file will be crated into the cbutil webapp folder, where we kept folder.properties file too
         */
        public void run() {
            long startTime = System.currentTimeMillis();
            int i = 0;
            File lock = null;
            while (true) {
                try {
                    try {
                        lock = new File(utilConf.getUtilPath() + "\\.lck");
                        ProcessBuilder processBuilder = new ProcessBuilder();
                        processBuilder.command("cmd", "/c", MessageFormat.format(utilConf.getCommand(), sf, df));
                        processBuilder.directory(new File(utilConf.getUtilPath()));
                        Process process = processBuilder.start();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            output.append(line + "\n");
                        }
                        int exitVal = process.waitFor();
                        if (exitVal == 0) {
                            output.append("Job completed successfully");
                        } else {
                            output.append("Something went wrong, fore more details, please check logs!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    lock.delete();
                }
            }
        }
    }
}
