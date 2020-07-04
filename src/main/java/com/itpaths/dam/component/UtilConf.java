package com.itpaths.dam.component;

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
    @Value("${util-path}")
    private String utilPath;
    @Value("${baseFolder}")
    private String baseFolder;
    @Value("${command}")
    private String command;

    public String getUtilPath() {
        return utilPath;
    }

    public void setUtilPath(String utilPath) {
        this.utilPath = utilPath;
    }

    public String getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
