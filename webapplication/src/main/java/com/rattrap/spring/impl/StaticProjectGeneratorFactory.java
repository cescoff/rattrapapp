package com.rattrap.spring.impl;

import com.edraw.ProjectGenerator;
import com.edraw.impl.URLResource;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rattrap.spring.ProjectGeneratorFactory;
import com.rattrap.utils.JAXBUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StaticProjectGeneratorFactory extends ProjectGeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(StaticProjectGeneratorFactory.class);

    private static final String PROJECT_LIST_URL = "https://raw.githubusercontent.com/cescoff/rattrapapp/master/webapplication/src/main/resources/projects.xml";

    private static long CACHE_TTL = TimeUnit.MINUTES.toMillis(1);

    private Map<String, LocalDateTime> CACHE_TTLS = Maps.newHashMap();

    private Map<String, ProjectGenerator> CACHE = Maps.newHashMap();

    private List<ProjectListConfig.Project> projectUrls = Lists.newArrayList();

    private LocalDateTime URL_TTL = null;

    private synchronized void manageCache() throws Exception {
        manageURLs();
        final List<ProjectListConfig.Project> localProjects;

        synchronized (projectUrls) {
            localProjects = ImmutableList.copyOf(projectUrls);
        }

        for (final ProjectListConfig.Project project : localProjects) {
            if (!CACHE_TTLS.containsKey(project.getUrl()) || LocalDateTime.now().minus(CACHE_TTL, ChronoUnit.MILLIS).isAfter(CACHE_TTLS.get(project.getUrl()))) {
                logger.info("Refreshing project cache for URL '" + project.getUrl() + "'");
                final ProjectGenerator projectGenerator = new ProjectGenerator(new URLResource(project.getUrl()));
                final StringBuilder idBuilder = new StringBuilder(project.getUrl()).append("-");
                String name = "";
                try {
                    name = projectGenerator.getProjectName();
                } catch (Throwable t) {
                    logger.error("Failed to load project with URL '" + project.getUrl() + "'", t);
                }
                if (StringUtils.isNotEmpty(name)) {
                    idBuilder.append(name);
                }
                final String id = DigestUtils.md5DigestAsHex(idBuilder.toString().getBytes());
                logger.info("Project '" + project.getUrl() + "' has id '" + id + "'");
                this.CACHE.put(id, projectGenerator);
                this.CACHE_TTLS.put(project.getUrl(), LocalDateTime.now());
            }
        }
    }

    private void manageURLs() throws Exception {
        if (URL_TTL == null || LocalDateTime.now().minus(CACHE_TTL, ChronoUnit.MILLIS).isAfter(URL_TTL)) {
            logger.info("Refreshing project list cache");
            final ProjectListConfig projectListConfig = JAXBUtils.unmarshal(ProjectListConfig.class, new URLResource(PROJECT_LIST_URL).open());
            synchronized (projectUrls) {
                projectUrls.clear();
                projectUrls.addAll(projectListConfig.getProjects());
                URL_TTL = LocalDateTime.now();
            }
        }
    }

    @Override
    public ProjectGenerator getProjectById(String id) throws Exception {
        manageCache();
        if (!this.CACHE.containsKey(id)) {
            throw new Exception("No project with id '" + id + "'");
        }
        return this.CACHE.get(id);
    }

    @Override
    public Iterable<ProjectHolder> getProjects() throws Exception {
        manageCache();
        return ImmutableList.<ProjectHolder>builder().addAll(Iterables.transform(this.CACHE.entrySet(), new Function<Map.Entry<String,ProjectGenerator>, ProjectHolder>() {

            @Override
            public ProjectHolder apply(Map.Entry<String, ProjectGenerator> stringProjectGeneratorEntry) {
                return new ProjectHolder() {
                    @Override
                    public String getId() {
                        return stringProjectGeneratorEntry.getKey();
                    }

                    @Override
                    public ProjectGenerator getGenerator() {
                        return stringProjectGeneratorEntry.getValue();
                    }
                };
            }

        })).build();
    }
}
