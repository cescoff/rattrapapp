/**
 * NOTE: This class is auto generated by the swagger code generator program (2.2.3).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.Error;
import io.swagger.model.Project;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-15T11:07:36.062Z")

@Api(value = "newproject", description = "the newproject API")
public interface NewprojectApi {

    @ApiOperation(value = "", notes = "Gets a project informations ", response = Project.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response", response = Project.class),
        @ApiResponse(code = 204, message = "The content of the project is not valid", response = Error.class),
        @ApiResponse(code = 404, message = "Project does not exist", response = String.class),
        @ApiResponse(code = 500, message = "Project rendering error", response = Error.class) })
    
    @RequestMapping(value = "/newproject",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<Project> newprojectGet( @NotNull@ApiParam(value = "The url of the project description file", required = true) @RequestParam(value = "url", required = true) String url);

}
