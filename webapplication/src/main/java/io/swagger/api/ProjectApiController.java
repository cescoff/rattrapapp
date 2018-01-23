package io.swagger.api;

import com.edraw.ProjectGenerator;
import com.rattrap.spring.ProjectGeneratorFactory;
import com.rattrap.spring.SwaggerUtils;
import io.swagger.model.Error;
import io.swagger.model.Project;

import io.swagger.annotations.*;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.validation.constraints.*;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-11T14:43:21.749Z")

@Controller
public class ProjectApiController implements ProjectApi {

    private static final Logger logger = Logger.getLogger(ProjectApiController.class);

    public ResponseEntity<Project> projectGet( @NotNull@ApiParam(value = "The id of the project", required = true) @RequestParam(value = "id", required = true) String id) {
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

}
