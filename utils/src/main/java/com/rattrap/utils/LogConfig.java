package com.rattrap.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class LogConfig {

    @XmlElement(name = "log-level")
    private String logLevel = "INFO";

    @XmlElement(name = "log-dir")
    private String logDir = "work/logs";

    @XmlElement(name = "log-file-name")
    private String logFileName = "log.log";

    @XmlElement(name = "log-to-console")
    private boolean logToConsole = true;

    public LogConfig() {
        super();
    }

    public LogConfig(String logLevel, String logDir, String logFileName,
                     boolean logToConsole) {
        super();
        this.logLevel = logLevel;
        this.logDir = logDir;
        this.logFileName = logFileName;
        this.logToConsole = logToConsole;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getLogDir() {
        return logDir;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public boolean isLogToConsole() {
        return logToConsole;
    }

}
