package io.swagger.api;

import com.edraw.ProjectConfigBuilder;
import com.edraw.ValidationError;
import com.edraw.utils.Predicates;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.rattrap.spring.ProjectGeneratorFactory;
import com.rattrap.spring.SwaggerUtils;
import io.swagger.model.*;
import io.swagger.model.Error;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import io.swagger.annotations.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.validation.constraints.*;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-02T16:06:11.495Z")

@Controller
public class ApiApiController implements ApiApi {



    private static Logger logger = LoggerFactory.getLogger(ApiApiController.class);

    public ResponseEntity<Project> apiNewprojectGet( @NotNull@ApiParam(value = "The url of the project description file", required = true) @RequestParam(value = "url", required = true) String url) {
        // do some magic!
        return new ResponseEntity<Project>(HttpStatus.OK);
    }

    public ResponseEntity<Resource> apiPreviewPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody PreviewRenderingRef body) {
        logger.info("Rendering a preview on project '" + body.getProjectid() + "'");
        if (StringUtils.isEmpty(body.getProjectid())) {
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }
        final com.edraw.ProjectGenerator projectGenerator;
        try {
            projectGenerator = ProjectGeneratorFactory.getInstance().getProjectById(body.getProjectid());
        } catch (Exception e) {
            logger.error("Failed to load project with id '" + body.getProjectid() + "'", e);
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (projectGenerator == null) {
            logger.error("No project with id '" + body.getProjectid() + "'");
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }
        if (!projectGenerator.hasDynamicPreview()) {
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }
        final Builder<String, String> parameters = ImmutableMap.builder();
        if (body.getParameters() != null) {
            for (final ProjectParameter projectParameter : body.getParameters()) {
                parameters.put(projectParameter.getName(), projectParameter.getValue());
            }
        }
        try {
            projectGenerator.generateProject(parameters.build(), false);
            return new ResponseEntity<Resource>(SwaggerUtils.SpringResource(projectGenerator.generateThumbnail(parameters.build(), body.getWidth(), body.getHeight())), HttpStatus.OK);
        } catch (ValidationError v) {
            logger.error("Failed to render dynamic preview on project with id '" + body.getProjectid() + "'", v);
            final MultiValueMap<String, String> headers = new LinkedMultiValueMap();
            headers.add("RenderingError", Joiner.on(", ").join(v.getDisplayMessages()));
            return new ResponseEntity<Resource>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Failed to render dynamic preview on project with id '" + body.getProjectid() + "'", e);
            final MultiValueMap<String, String> headers = new LinkedMultiValueMap();
            headers.add("RenderingError", e.getMessage());
            return new ResponseEntity<Resource>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Project> apiProjectGet( @NotNull@ApiParam(value = "The id of the project", required = true) @RequestParam(value = "id", required = true) String id) {
        final com.edraw.ProjectGenerator projectGenerator;
        try {
            projectGenerator = ProjectGeneratorFactory.getInstance().getProjectById(id);
        } catch (Exception e) {
            logger.error("Cannot find project with id '" + id + "'", e);
            return new ResponseEntity<Project>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (projectGenerator == null) {
            return new ResponseEntity<Project>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Project>(SwaggerUtils.GetProject().apply(projectGenerator), HttpStatus.OK);
    }

    public ResponseEntity<Boolean> apiRegisterPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody User body) {
        // do some magic!
        return new ResponseEntity<Boolean>(HttpStatus.OK);
    }

    public ResponseEntity<Resource> apiRenderPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody PreviewRenderingRef body) {
        logger.info("Rendering project '" + body.getProjectid() + "'");
        if (StringUtils.isEmpty(body.getProjectid())) {
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }
        final com.edraw.ProjectGenerator projectGenerator;
        try {
            projectGenerator = ProjectGeneratorFactory.getInstance().getProjectById(body.getProjectid());
        } catch (Exception e) {
            logger.error("Failed to load project with id '" + body.getProjectid() + "'", e);
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (projectGenerator == null) {
            logger.error("No project with id '" + body.getProjectid() + "'");
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }
        if (!projectGenerator.hasDynamicPreview()) {
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }
        final Builder<String, String> parameters = ImmutableMap.builder();
        if (body.getParameters() != null) {
            for (final ProjectParameter projectParameter : body.getParameters()) {
                parameters.put(projectParameter.getName(), projectParameter.getValue());
            }
        }

        final Iterable<com.edraw.Resource> renderedFiles;
        try {
            renderedFiles = projectGenerator.generateProject(parameters.build(), body.getEnableSplitters() != null && !body.getEnableSplitters());
        } catch (ValidationError v) {
            logger.error("Failed to zip dynamic preview on project with id '" + body.getProjectid() + "'", v);
            final MultiValueMap<String, String> headers = new LinkedMultiValueMap();
            headers.add("RenderingError", Joiner.on(", ").join(v.getDisplayMessages()));
            return new ResponseEntity<Resource>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Failed to zip dynamic preview on project with id '" + body.getProjectid() + "'", e);
            final MultiValueMap<String, String> headers = new LinkedMultiValueMap();
            headers.add("RenderingError", e.getMessage());
            return new ResponseEntity<Resource>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ZipOutputStream zipOutputStream = new ZipOutputStream(bos);

        try {
            for (final com.edraw.Resource resource : renderedFiles) {
                final ZipEntry file = new ZipEntry(resource.getName());
                zipOutputStream.putNextEntry(file);
                IOUtils.copy(resource.open(), zipOutputStream);
                zipOutputStream.closeEntry();
            }
        } catch (ValidationError v) {
            logger.error("Failed to zip dynamic preview on project with id '" + body.getProjectid() + "'", v);
            final MultiValueMap<String, String> headers = new LinkedMultiValueMap();
            headers.add("RenderingError", Joiner.on(", ").join(v.getDisplayMessages()));
            return new ResponseEntity<Resource>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Failed to zip dynamic preview on project with id '" + body.getProjectid() + "'", e);
            final MultiValueMap<String, String> headers = new LinkedMultiValueMap();
            headers.add("RenderingError", e.getMessage());
            return new ResponseEntity<Resource>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                zipOutputStream.close();
                bos.close();
            } catch (IOException e) {
                logger.error("Failed to close zip outputstream on project with id '" + body.getProjectid() + "'", e);
                return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        final String dateTimeSuffix = new StringBuilder("_").
                append(DateTime.now().getYear()).append("-").
                append(DateTime.now().getMonthOfYear()).append("-").
                append(DateTime.now().getDayOfMonth()).append("_").
                append(DateTime.now().getHourOfDay()).append("h-").
                append(DateTime.now().getMinuteOfHour()).append("m").toString();

        return new ResponseEntity<Resource>(new ByteArrayResource(bos.toByteArray(), projectGenerator.getProjectName() + dateTimeSuffix + ".zip"), HttpStatus.OK);
    }

    public ResponseEntity<SearchResult> apiSearchGet( @NotNull@ApiParam(value = "The search query", required = true) @RequestParam(value = "fulltextquery", required = true) String fulltextquery) {
        final Iterable<ProjectGeneratorFactory.ProjectHolder> projectHolders;
        if ("#all".equals(fulltextquery)) {
            try {
                projectHolders = ProjectGeneratorFactory.getInstance().getProjects();
            } catch (Exception e) {
                logger.error("Failed to load project list", e);
                return new ResponseEntity<SearchResult>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            try {
                projectHolders = Iterables.filter(ProjectGeneratorFactory.getInstance().getProjects(), Predicates.FullTextFilter(fulltextquery, new Function<ProjectGeneratorFactory.ProjectHolder, String>() {

                    @Override
                    public String apply(ProjectGeneratorFactory.ProjectHolder projectHolder) {
                        return new StringBuilder(projectHolder.getGenerator().getProjectName()).append(" ").append(projectHolder.getGenerator().getDescription()).append(" ").append(projectHolder.getGenerator().getPresentation()).toString();
                    }

                }));
            } catch (Exception e) {
                logger.error("Failed to load project list", e);
                return new ResponseEntity<SearchResult>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        final SearchResult searchResult = new SearchResult();
        searchResult.setProjects(ImmutableList.copyOf(Iterables.transform(projectHolders, new Function<ProjectGeneratorFactory.ProjectHolder, ProjectSummary>() {

            @Override
            public ProjectSummary apply(ProjectGeneratorFactory.ProjectHolder f) {
                return new ProjectSummary().
                        id(f.getId()).
                        title(f.getGenerator().getProjectName()).
                        description(f.getGenerator().getDescription()).
                        thumbnailurl(f.getGenerator().getThumbnailURL());

            }
        })));
        return new ResponseEntity<SearchResult>(searchResult, HttpStatus.OK);
    }

    public ResponseEntity<Resource> apiGenerateprojectPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody ProjectGenerator body) {
        final ProjectConfigBuilder projectConfigBuilder = new ProjectConfigBuilder();
        projectConfigBuilder.withName(body.getName()).
                withDescription(body.getDescription()).
                withPresentation(body.getPresentation()).
                withUrlName(body.getUrlname()).
                withThumbnailURL(body.getPreviewurl()).
                withGgitHubRepoName(body.getReponame()).
                withVariableNames(body.getVariablenames());

        final com.edraw.Resource zippedProject = projectConfigBuilder.buildArchive();

        try {
            return new ResponseEntity<Resource>(new ByteArrayResource(IOUtils.toByteArray(zippedProject.open()), body.getName() + ".zip"), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to generate new project", e);
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
