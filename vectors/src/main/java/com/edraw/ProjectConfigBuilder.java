package com.edraw;

import com.edraw.config.*;
import com.edraw.config.laser.LaserBluePrint;
import com.edraw.config.laser.LaserPoint;
import com.edraw.impl.StringResource;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.rattrap.utils.JAXBUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ProjectConfigBuilder {

    private static final String planConfigurationFileName = "Plan.xml";

    private static final String planOutputConfigFileName = "PlanOutputConfig.xml";

    private static final String thumbnailFileName = "Thumbnail.xml";

    private static final String thumbnailOutputConfigFileName = "ThumbnailOutputConfig.xml";

    private static final String variablesFileName = "Variables.xml";

    private static final String documentationFileName = "Documentation.html";

    private String name;

    private String gitHubRepoName;

    private String urlName;

    private String thumbnailURL;

    private String description;

    private String presentation;

    private List<String> variableNames = Lists.newArrayList();

    public ProjectConfigBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public ProjectConfigBuilder withGgitHubRepoName(final String repoName) {
        this.gitHubRepoName = repoName;
        return this;
    }

    public ProjectConfigBuilder withUrlName(final String urlName) {
        this.urlName = urlName;
        return this;
    }

    public ProjectConfigBuilder withThumbnailURL(final String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
        return this;
    }

    public ProjectConfigBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public ProjectConfigBuilder withPresentation(final String presentation) {
        this.presentation = presentation;
        return this;
    }

    public ProjectConfigBuilder withVariableName(final String varName) {
        this.variableNames.add(varName);
        return this;
    }

    public ProjectConfigBuilder withVariableNames(final Iterable<String> varNames) {
        Iterables.addAll(this.variableNames, varNames);
        return this;
    }

    public Iterable<Resource> build() {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name is required");
        }
        if (StringUtils.isEmpty(gitHubRepoName)) {
            throw new IllegalArgumentException("Repository name is required");
        }
        if (StringUtils.isEmpty(urlName)) {
            throw new IllegalArgumentException("Repository URL name is required");
        }

        final String url = StringUtils.replace(StringUtils.replace("https://raw.githubusercontent.com/${repo}/${project}/master", "${repo}", gitHubRepoName), "${project}", urlName);

        final List<ProjectConfig.DefaultVariableConfig> variableConfigs = Lists.newArrayList();

        for (final String variableName : variableNames) {
            variableConfigs.add(new ProjectConfig.DefaultVariableConfig(variableName, variableName, "mm", new StringBuilder(variableName).append(" description").toString(), "0.0"));
        }

        final ProjectConfig projectConfig = new ProjectConfig(
                name, thumbnailURL, description, presentation,
                getFileURL(url, variablesFileName),
                getFileURL(url, planConfigurationFileName),
                getFileURL(url, planOutputConfigFileName),
                getFileURL(url, thumbnailFileName),
                getFileURL(url, thumbnailOutputConfigFileName),
                getFileURL(url, planConfigurationFileName),
                variableConfigs,
                variableConfigs);

        final LaserBluePrint plan = new LaserBluePrint(DistanceUnit.MILLIMETERS.getSymbol());
        plan.getDrawings().add(new LaserPoint("APoint", "Main", "1.0", "1.0"));

        final OutputConfig outputConfig = new OutputConfig();
        final OutputConfig.Output output = new OutputConfig.Output("SampleOutput", OutputFormat.SVG.name());
        output.getLayers().add("Main");
        outputConfig.getOutputs().add(output);

        final Variables variables = new Variables();
        Iterables.addAll(variables.getVariables(), Iterables.transform(variableConfigs, new Function<ProjectConfig.DefaultVariableConfig, Variables.VariableDefinition>() {
            @Override
            public Variables.VariableDefinition apply(ProjectConfig.DefaultVariableConfig defaultVariableConfig) {
                return new Variables.VariableDefinition(defaultVariableConfig.getName(), "1.0");
            }
        }));

        final List<Resource> result = Lists.newArrayList();
        result.add(getXmlResource(projectConfig, "project.xml"));
        result.add(getXmlResource(variables, variablesFileName));
        result.add(getXmlResource(plan, planConfigurationFileName));
        result.add(getXmlResource(outputConfig, planOutputConfigFileName));
        result.add(getXmlResource(plan, thumbnailFileName));
        result.add(getXmlResource(outputConfig, thumbnailOutputConfigFileName));
        result.add(new StringResource(documentationFileName, generateEmptyDocumentation()));

        return ImmutableList.copyOf(result);
    }

    public Resource buildArchive() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ZipOutputStream zipOutputStream = new ZipOutputStream(bos);

        for (final Resource resource : build()) {
            final ZipEntry zipEntry = new ZipEntry(resource.getName());
            try {
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(IOUtils.toByteArray(resource.open()));
                zipOutputStream.closeEntry();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to write zip file", e);
            }
        }
        try {
            zipOutputStream.close();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to close zip file", e);
        }

        final byte[] data = bos.toByteArray();

        return new Resource() {

            @Override
            public String getName() {
                return new StringBuilder(name).append(".zip").toString();
            }

            @Override
            public InputStream open() throws Exception {
                return new ByteArrayInputStream(data);
            }

            @Override
            public URL getURL() throws Exception {
                return null;
            }
        };
    }

    private <T> Resource getXmlResource(final T object, final String fileName) {
        try {
            return new StringResource(fileName, JAXBUtils.marshal(object, true));
        } catch (JAXBException e) {
            throw new IllegalStateException("Failed to generate file '" + fileName + "'", e);
        }
    }

    private String getFileURL(final String prefix, final String fileName) {
        return new StringBuilder(prefix).append("/").append(fileName).toString();
    }

    private String generateEmptyDocumentation() {
        final StringBuilder result = new StringBuilder("<html>\n\t<head>\n\t\t<title>").
                append(this.name).
                append(" - Documentation</title>\n\t</head>\n\t<body>\n");

        result.append("\t\t<p>Sample variables usage\n\t\t\t<ul>\n");
        for (final String varName : variableNames) {
            result.append("\t\t\t\t<li>${").append(varName).append("} will be replaced by its value when project is generated</li>\n");
        }

        return result.append("\t\t\t</ul>\n\t\t</p>\n\t</body>\n</html>").toString();
    }

    public static void main(String[] args) throws Exception {
        final Resource zip = new ProjectConfigBuilder().withName("ProjectName").withThumbnailURL("https://github.com/cescoff/<PROJECT_URL_NAME>/raw/master/project_preview.jpeg").withDescription("Description").withPresentation("<p>Presentation</p>").withUrlName("project").withGgitHubRepoName("cescoff").withVariableName("var1").withVariableName("var2").buildArchive();
        final FileOutputStream fileOutputStream = new FileOutputStream(new File(args[0]));
        IOUtils.copy(zip.open(), fileOutputStream);
        fileOutputStream.close();
    }

}
