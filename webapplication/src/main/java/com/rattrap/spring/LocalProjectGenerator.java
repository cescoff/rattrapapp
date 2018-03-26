package com.rattrap.spring;

import com.edraw.ProjectGenerator;
import com.google.common.collect.Maps;
import com.rattrap.utils.Log4JConfigurationHelper;
import com.rattrap.utils.LogConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class LocalProjectGenerator {

    public static void main(String[] args) throws Exception {
        new Log4JConfigurationHelper(new LogConfig("INFO", null, null,
        true)).configure();
        final String projectId;
        if (args.length > 0) {
            projectId = args[0];
        } else {
            projectId = null;
        }
        final Logger logger = Logger.getLogger(LocalProjectGenerator.class);
        for (final ProjectGeneratorFactory.ProjectHolder projectGenerator : ProjectGeneratorFactory.getInstance().getProjects()) {
            if (StringUtils.isEmpty(projectId)) {
                logger.info("Project '" + projectGenerator.getGenerator().getProjectName() + "' has id '" + projectGenerator.getId() + "'");
            } else {
                if (projectId.equals(projectGenerator.getId())) {
                    logger.info("Rendering project '" + projectGenerator.getGenerator().getProjectName() + "'");
                    projectGenerator.getGenerator().generateProject(Maps.newHashMap(), false);
                    projectGenerator.getGenerator().generateThumbnail(Maps.<String, String>newHashMap(), 320, 320);
                }
            }
        }
    }

}
