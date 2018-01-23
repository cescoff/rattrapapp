package io.swagger.api;

import io.swagger.model.Error;
import io.swagger.model.SearchResult;

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

import java.util.List;

import javax.validation.constraints.*;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-15T11:07:36.062Z")

@Controller
public class SearchApiController implements SearchApi {



    public ResponseEntity<SearchResult> searchGet( @NotNull@ApiParam(value = "The search query", required = true) @RequestParam(value = "fulltextquery", required = true) String fulltextquery) {
        // do some magic!
        return new ResponseEntity<SearchResult>(HttpStatus.OK);
    }

}
