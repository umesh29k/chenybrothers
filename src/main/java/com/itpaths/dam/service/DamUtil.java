package com.itpaths.dam.service;

import com.itpaths.dam.component.UtilConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;

import static javax.script.ScriptEngine.FILENAME;

@Service
public class DamUtil {
    @Autowired
    private UtilConf utilConf;
    private String dfolder, sfolder;
    StringBuilder output = new StringBuilder();
    StringBuilder error = new StringBuilder();

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
                error.append("Invalid source folder");
        } catch (Exception e) {
            error.append("Invalid source folder");
        }

        if (dir) {
            output.append(">>>>>>>>> " + utilConf.getUtilPath() + "\n ");
            output.append(">>>>>>>>> " + MessageFormat.format(utilConf.getCommand(), sf, df) + "\n ");
            Ope ope = new Ope();
            ope.start();
        } else {
            error.append("<br>Invalid input");
        }
        String response = "{\"output\": \"" + output.toString().replaceAll("\b", "/") + "\", \"error\": \"" + error + "\" }";
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
            File file = new File(sfolder + "\\import.log");
            StringBuilder data = new StringBuilder();
            FileWriter lockw = null;
            try {
                try {
                    lock = new File(new File(sfolder).getParent() + "\\.lck");
                    lock.createNewFile();
                    lockw = new FileWriter(lock.getAbsoluteFile(), true);
                    data.append("Lock file is crated");
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("cmd", "/c", MessageFormat.format(utilConf.getCommand(), sfolder, dfolder));
                    processBuilder.directory(new File(utilConf.getUtilPath()));
                    Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        data.append(line + "\n");
                    }
                    int exitVal = process.waitFor();
                    if (exitVal == 0) {
                        data.append("Job completed successfully");
                    } else {
                        data.append("Something went wrong, fore more details, please check logs!");
                    }
                } catch (IOException e) {
                    data.append(e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    data.append(e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                data.append(e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    lockw.close();
                } catch (Exception e) {
                }
                lock.delete();
                data.append("Lock file is removed");
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
                    data.append(e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        data.append(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }

        }
    }
}
