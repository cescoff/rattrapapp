package io.swagger.api;

import com.edraw.ProjectGenerator;
import com.google.common.collect.ImmutableMap;
import com.rattrap.spring.ProjectGeneratorFactory;
import com.rattrap.spring.SwaggerUtils;
import io.swagger.model.PreviewRenderingRef;
import io.swagger.model.ProjectParameter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import io.swagger.annotations.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.validation.constraints.*;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-12T10:02:49.068Z")

@Controller
public class RenderApiController implements RenderApi {

    private static Logger logger = Logger.getLogger(RenderApiController.class);

    public ResponseEntity<Resource> renderPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody PreviewRenderingRef body) {
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
            renderedFiles = projectGenerator.generateProject(parameters.build());
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
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                zipOutputStream.close();
                bos.close();
            } catch (IOException e) {
                logger.error("Failed to close zip outputstream on project with id '" + body.getProjectid() + "'", e);
                return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<Resource>(new ByteArrayResource(bos.toByteArray(), projectGenerator.getProjectName() + ".zip"), HttpStatus.OK);
    }

}
