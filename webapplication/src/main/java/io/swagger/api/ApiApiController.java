package io.swagger.api;

import com.edraw.ProjectGenerator;
import com.google.common.collect.ImmutableMap;
import com.rattrap.spring.ProjectGeneratorFactory;
import com.rattrap.spring.SwaggerUtils;
import io.swagger.model.*;
import io.swagger.model.Error;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-25T10:38:37.158Z")

@Controller
public class ApiApiController implements ApiApi {

    private static Logger logger = Logger.getLogger(ApiApiController.class);

    public ResponseEntity<Project> apiNewprojectGet( @NotNull@ApiParam(value = "The url of the project description file", required = true) @RequestParam(value = "url", required = true) String url) {
        // do some magic!
        return new ResponseEntity<Project>(HttpStatus.OK);
    }

    public ResponseEntity<Resource> apiPreviewPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody PreviewRenderingRef body) {
        logger.info("Rendering a preview on project '" + body.getProjectid() + "'");
        if (StringUtils.isEmpty(body.getProjectid())) {
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }
        final ProjectGenerator projectGenerator;
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
        final ImmutableMap.Builder<String, String> parameters = ImmutableMap.builder();
        if (body.getParameters() != null) {
            for (final ProjectParameter projectParameter : body.getParameters()) {
                parameters.put(projectParameter.getName(), projectParameter.getValue());
            }
        }
        try {
            projectGenerator.generateProject(parameters.build(), false);
            return new ResponseEntity<Resource>(SwaggerUtils.SpringResource(projectGenerator.generateThumbnail(parameters.build(), body.getWidth(), body.getHeight())), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to render dynamic preview on project with id '" + body.getProjectid() + "'", e);
            final MultiValueMap<String, String> headers = new LinkedMultiValueMap();
            headers.add("RenderingError", e.getMessage());
            return new ResponseEntity<Resource>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Project> apiProjectGet( @NotNull@ApiParam(value = "The id of the project", required = true) @RequestParam(value = "id", required = true) String id) {
        final ProjectGenerator projectGenerator;
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
        final ProjectGenerator projectGenerator;
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
        final ImmutableMap.Builder<String, String> parameters = ImmutableMap.builder();
        if (body.getParameters() != null) {
            for (final ProjectParameter projectParameter : body.getParameters()) {
                parameters.put(projectParameter.getName(), projectParameter.getValue());
            }
        }

        final Iterable<com.edraw.Resource> renderedFiles;
        try {
            renderedFiles = projectGenerator.generateProject(parameters.build(), body.getEnableSplitters() != null && !body.getEnableSplitters());
        } catch (Exception e) {
            logger.error("Failed to render dynamic preview on project with id '" + body.getProjectid() + "'", e);
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
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
        // do some magic!
        return new ResponseEntity<SearchResult>(HttpStatus.OK);
    }

}