package com.edraw.impl;

import com.edraw.geom.Layer;
import com.google.common.base.Predicate;

public class DynamicLayer implements Layer {

    private final String name;

    private final Predicate<String> active;

    public DynamicLayer(String name, Predicate<String> active) {
        this.name = name;
        this.active = active;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isActive() {
        return active.apply(getName());
    }

    @Override
    public String toString() {
        return "DynamicLayer{" +
                "name='" + name + '\'' +
                ", active=" + active.apply(getName()) +
                '}';
    }
}
