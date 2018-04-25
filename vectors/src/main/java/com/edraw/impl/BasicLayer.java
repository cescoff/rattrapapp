package com.edraw.impl;

import com.edraw.geom.Layer;

public class BasicLayer implements Layer {

    private final String name;

    private final boolean active;

    public BasicLayer(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "BasicLayer{" +
                "name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
