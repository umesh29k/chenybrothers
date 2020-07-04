package com.itpaths.dam.service;

import com.itpaths.dam.component.UtilConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class DamUtil {
    @Autowired
    private UtilConf utilConf;

    public StringBuilder status(){
        StringBuilder output = new StringBuilder();
        output.append(">>>>>>>>> " + utilConf.getUtilPath() + "\n ");
        output.append(">>>>>>>>> " + utilConf.getCommand() + "\n ");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd", "/c", "hello.bat \"This is test\"");
            File dir = new File("E:\\utility\\");
            processBuilder.directory(dir);
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
        return output;
    }

}
