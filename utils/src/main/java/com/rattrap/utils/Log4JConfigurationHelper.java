package com.rattrap.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.Properties;

public class Log4JConfigurationHelper {

    private LogConfig config;

    public Log4JConfigurationHelper(LogConfig config) {
        super();
        this.config = config;
    }

    public void configure() {
        PropertyConfigurator configurator = new PropertyConfigurator();
        Properties logProperties = new Properties();

        String rootLoggerConfigLine = config.getLogLevel() + ", file";
        if (config.isLogToConsole()) rootLoggerConfigLine = rootLoggerConfigLine + ", console";
        logProperties.put("log4j.rootLogger", rootLoggerConfigLine);
        logProperties.put("log4j.logger.com.exalead", config.getLogLevel() + "");
        logProperties.put("log4j.appender.file", "org.apache.log4j.RollingFileAppender");
        logProperties.put("log4j.appender.file.BufferedIO", true);
        logProperties.put("log4j.appender.file.BufferSize", 4096);
        logProperties.put("log4j.appender.file.File", config.getLogDir() + File.separator + config.getLogFileName());
        logProperties.put("log4j.appender.file.MaxFileSize", "10MB");
        logProperties.put("log4j.appender.file.MaxBackupIndex", "10");
        logProperties.put("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
        logProperties.put("log4j.appender.file.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");

        if (config.isLogToConsole()) {
            logProperties.put("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
            logProperties.put("log4j.appender.console.Target", "System.out");
            logProperties.put("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
            logProperties.put("log4j.appender.console.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        }
        configurator.doConfigure(logProperties, LogManager.getLoggerRepository());
    }
}