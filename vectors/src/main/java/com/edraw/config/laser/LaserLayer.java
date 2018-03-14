package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class LaserLayer {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String condition = "true";

    public LaserLayer() {
    }

    public LaserLayer(String name, String condition) {
        this.name = name;
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isActive() {
        return "true".equals(condition);
    }

}
