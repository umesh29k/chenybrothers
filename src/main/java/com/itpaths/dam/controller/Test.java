package com.itpaths.dam.controller;

import java.io.*;

public class Test {
    public static void main(String[] a) {

        String command = "curl \"http://ot-dam-dev.cheneybrothers.com:11090/otmmapi/v5/folders/1001N?load_type=custom^&data_load_request=^%^7B^%^22data_load_request^%^22^%^3A^%^7B^%^22child_count_load_type^%^22^%^3A^%^22both^%^22^%^2C^%^22load_path^%^22^%^3Atrue^%^7D^%^7D\" ^\n" +
                "  -H \"Connection: keep-alive\" ^\n" +
                "  -H \"X-OTMM-Locale: en_US\" ^\n" +
                "  -H \"Accept: text/plain, */*; q=0.01\" ^\n" +
                "  -H \"X-Requested-With: XMLHttpRequest\" ^\n" +
                "  -H \"X-Requested-By: 1591020331\" ^\n" +
                "  -H \"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36\" ^\n" +
                "  -H \"Content-Type: application/json; charset=UTF-8\" ^\n" +
                "  -H \"Referer: http://ot-dam-dev.cheneybrothers.com:11090/\" ^\n" +
                "  -H \"Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\" ^\n" +
                "  -H \"Cookie: JSESSIONID=E9DCD05CAB29F32A92598C790D28A6C6\" ^\n" +
                "  --compressed ^\n" +
                "  --insecure";
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        try {
            //Process process = processBuilder.start();
            Process proc = Runtime.getRuntime().exec(command);
            proc.destroy();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

// Read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

// Read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
