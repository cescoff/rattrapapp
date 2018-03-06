package com.rattrap.spring.impl;

import com.google.common.collect.Lists;
import com.rattrap.utils.JAXBUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "projects")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectListConfig {

    @XmlElementWrapper(name = "projects") @XmlElement(name = "project")
    private List<Project> projects = Lists.newArrayList();

    public List<Project> getProjects() {
        return projects;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Project {

        @XmlElement
        private String url;

        @XmlElement
        private String status;

        public Project() {
        }

        public Project(String url, String status) {
            this.url = url;
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public String getStatus() {
            return status;
        }

        public ProjectStatus getProjectStatus() {
            if (StringUtils.isEmpty(status)) {
                return ProjectStatus.DEV;
            }
            return ProjectStatus.valueOf(status);
        }

    }

    public enum ProjectStatus {
        DEV,
        PROD
    }

    public static void main(String[] args) throws JAXBException {
        final ProjectListConfig projectListConfig = new ProjectListConfig();
        projectListConfig.getProjects().add(new Project("https://raw.githubusercontent.com/cescoff/rattrapchair/master/project.xml", "PROD"));
        projectListConfig.getProjects().add(new Project("https://raw.githubusercontent.com/cescoff/towely/master/project.xml", "PROD"));

        System.out.println(JAXBUtils.marshal(projectListConfig, true));
    }

}
