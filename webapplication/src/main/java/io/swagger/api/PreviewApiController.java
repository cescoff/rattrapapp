package io.swagger.api;

import com.edraw.ProjectGenerator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.validation.constraints.*;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-15T11:07:36.062Z")

@Controller
public class PreviewApiController implements PreviewApi {

    private static Logger logger = Logger.getLogger(PreviewApiController.class);

    public ResponseEntity<Resource> previewPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody PreviewRenderingRef body) {
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
            return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
