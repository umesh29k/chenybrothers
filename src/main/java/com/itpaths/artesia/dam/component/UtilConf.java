package com.itpaths.artesia.dam.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@PropertySource("classpath:application.yml")
public class UtilConf {
    @Value("${prpath}")
    private String prPath;
    @Value("${impath}")
    private String imPath;
    @Value("${prep}")
    private String prep;
    @Value("${imprep}")
    private String imprep;
    @Value(("${log}"))
    private String log;
    @Value(("${data}"))
    private String data;
    @Value(("${tempDir}"))
    private String tempDir;
    @Value("${aiprep")
    private String aiPrep;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public String getPrep() {
        return prep;
    }

    public void setPrep(String prep) {
        this.prep = prep;
    }

    public String getImprep() {
        return imprep;
    }

    public void setImprep(String imprep) {
        this.imprep = imprep;
    }

    public String getPrPath() {
        return prPath;
    }

    public void setPrPath(String prPath) {
        this.prPath = prPath;
    }

    public String getImPath() {
        return imPath;
    }

    public void setImPath(String imPath) {
        this.imPath = imPath;
    }

    public String getAiPrep() {
        return aiPrep;
    }

    public void setAiPrep(String aiPrep) {
        this.aiPrep = aiPrep;
    }
}
