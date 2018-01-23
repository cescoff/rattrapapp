package com.rattrap.spring;

import com.edraw.ProjectGenerator;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.Map;

public abstract class ProjectGeneratorFactory {

    private static final String DEFAULT_IMPL = "com.rattrap.spring.impl.StaticProjectGeneratorFactory";

    private static ProjectGeneratorFactory INSTANCE = null;

    public static synchronized ProjectGeneratorFactory getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        try {
            INSTANCE = (ProjectGeneratorFactory) Class.forName(DEFAULT_IMPL).newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Cannot find class '" + DEFAULT_IMPL + "'", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot find class '" + DEFAULT_IMPL + "'", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find class '" + DEFAULT_IMPL + "'", e);
        }
        return INSTANCE;
    }

    public abstract ProjectGenerator getProjectById(final String id) throws Exception;

    public abstract Iterable<ProjectHolder> getProjects() throws Exception;

    public static interface ProjectHolder {

        public String getId();

        public ProjectGenerator getGenerator();

    }

}
