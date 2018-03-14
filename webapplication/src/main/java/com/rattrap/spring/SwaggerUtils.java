package com.rattrap.spring;

import com.edraw.ProjectGenerator;
import com.edraw.Resource;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.swagger.model.PreviewRenderingRef;
import io.swagger.model.Project;
import io.swagger.model.ProjectParameter;
import io.swagger.model.ProjectSummary;
import org.apache.commons.io.IOUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class SwaggerUtils {

    public static Function<ProjectGenerator, Project> GetProject() {
        return new Function<ProjectGenerator, Project>() {
            @Override
            public Project apply(final ProjectGenerator projectGenerator) {
                final Project result = new Project().title(projectGenerator.getProjectName())
                        .description(projectGenerator.getDescription())
                        .presentation(projectGenerator.getPresentation())
                        .thumbnailurl(projectGenerator.getThumbnailURL());
                if (projectGenerator.hasDynamicPreview()) {
                        result.addPreviewsItem(new PreviewRenderingRef()
                            .outputformat(PreviewRenderingRef.OutputformatEnum.SVG)
                            .format("SVG")
                            .name("DynamicPreview")
                            .url("/preview"));
                }
                if (projectGenerator.getPreviewURLs() != null) {
                    for (final String url : projectGenerator.getPreviewURLs()) {
                        result.addPreviewsItem(new PreviewRenderingRef()
                                .outputformat(PreviewRenderingRef.OutputformatEnum.SVG)
                                .format("IMG")
                                .name("StaticPreview")
                                .url(url));
                    }
                }
                result.parameters(Lists.newArrayList(Iterables.transform(projectGenerator.getVariables().entrySet(), new Function<Map.Entry<String, String>, ProjectParameter>() {
                    @Override
                    public ProjectParameter apply(Map.Entry<String, String> stringStringEntry) {
                        return new ProjectParameter().
                                name(stringStringEntry.getKey()).
                                value(stringStringEntry.getValue()).
                                displayname(projectGenerator.getVariableDisplayName(stringStringEntry.getKey(), Locale.US)).
                                description(projectGenerator.getVariableDescription(stringStringEntry.getKey(), Locale.US)).
                                type(projectGenerator.getVariableType(stringStringEntry.getKey()));
                    }
                })));
                return result;
            }
        };
    }

    public static Function<ProjectGeneratorFactory.ProjectHolder, ProjectSummary> GetProjectSearchResult() {
        return new Function<ProjectGeneratorFactory.ProjectHolder, ProjectSummary>() {
            @Override
            public ProjectSummary apply(ProjectGeneratorFactory.ProjectHolder projectHolder) {
                return new ProjectSummary()
                        .title(projectHolder.getGenerator().getProjectName())
                        .id(projectHolder.getId())
                        .description(projectHolder.getGenerator().getDescription())
                        .thumbnailurl(projectHolder.getGenerator().getThumbnailURL());
            }
        };
    }

    public static MultiValueMap<String, String> Headers() {
        final MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();
        result.add("Access-Control-Allow-Origin", "*");
        result.add("Access-Control-Allow-Methods", "GET, POST");
        return result;
    }

    public static org.springframework.core.io.Resource SpringResource(final Resource resource) throws Exception {
        final byte[] data = IOUtils.toByteArray(resource.open());

        return new org.springframework.core.io.Resource() {
            @Override
            public boolean exists() {
                return data != null && data.length > 0;
            }

            @Override
            public boolean isReadable() {
                return exists();
            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public URL getURL() throws IOException {
                try {
                    return resource.getURL();
                } catch (Exception e) {
                    throw new IOException("Failed to load resource URL", e);
                }
            }

            @Override
            public URI getURI() throws IOException {
                try {
                    return getURL().toURI();
                } catch (URISyntaxException e) {
                    throw new IOException("Failed to load resource URI", e);
                }
            }

            @Override
            public File getFile() throws IOException {
                return null;
            }

            @Override
            public long contentLength() throws IOException {
                return data.length;
            }

            @Override
            public long lastModified() throws IOException {
                return 0;
            }

            @Override
            public org.springframework.core.io.Resource createRelative(String s) throws IOException {
                return null;
            }

            @Override
            public String getFilename() {
                return resource.getName();
            }

            @Override
            public String getDescription() {
                return getFilename();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(data);
            }
        };

    }

}
