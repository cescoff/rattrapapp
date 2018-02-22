package com.rattrap.spring.impl;

import com.edraw.ProjectGenerator;
import com.edraw.impl.URLResource;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.rattrap.spring.ProjectGeneratorFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StaticProjectGeneratorFactory extends ProjectGeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(StaticProjectGeneratorFactory.class);

    private static final String[] URLS = new String[] {
            "https://raw.githubusercontent.com/cescoff/rattrapchair/master/project.xml",
            "https://raw.githubusercontent.com/cescoff/towely/master/project.xml"
    };

    private static long CACHE_TTL = TimeUnit.MINUTES.toMillis(3);

    private Map<String, LocalDateTime> CACHE_TTLS = Maps.newHashMap();

    private Map<String, ProjectGenerator> CACHE = Maps.newHashMap();

    private synchronized void manageCache() throws Exception {
        for (final String url : URLS) {
            if (!CACHE_TTLS.containsKey(url) || LocalDateTime.now().minus(CACHE_TTL, ChronoUnit.MILLIS).isAfter(CACHE_TTLS.get(url))) {
                logger.info("Refreshing project cache for URL '" + url + "'");
                final ProjectGenerator projectGenerator = new ProjectGenerator(new URLResource(url));
                final StringBuilder idBuilder = new StringBuilder(url).append("-");
                String name = "";
                try {
                    name = projectGenerator.getProjectName();
                } catch (Throwable t) {
                    logger.error("Failed to load project with URL '" + url + "'", t);
                }
                if (StringUtils.isNotEmpty(name)) {
                    idBuilder.append(name);
                }
                final String id = DigestUtils.md5DigestAsHex(idBuilder.toString().getBytes());
                logger.info("Project '" + url + "' has id '" + id + "'");
                this.CACHE.put(id, projectGenerator);
                this.CACHE_TTLS.put(url, LocalDateTime.now());
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
