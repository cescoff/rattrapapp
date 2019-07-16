package com.rattrap.google.data;

/**
 * Created by corentinescoffier on 15/07/19.
 */
public class ProjectLaunch {

    private String id;

    private String name;

    private String family;

    private String state;

    public ProjectLaunch() {
    }

    public ProjectLaunch(String id, String name, String family, String state) {
        this.id = id;
        this.name = name;
        this.family = family;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFamily() {
        return family;
    }

    public String getState() {
        return state;
    }
}
