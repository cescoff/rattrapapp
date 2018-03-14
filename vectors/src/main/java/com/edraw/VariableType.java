package com.edraw;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlEnum()
@XmlType(name = "variableType")
public enum VariableType {
    @XmlEnumValue("numeric")
    NUMERIC("text"),
    @XmlEnumValue("boolean")
    BOOLEAN("checkbox");

    private final String displayName;

    private VariableType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
