package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Project details
 */
@ApiModel(description = "Coating Project details")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-14T08:37:40.478Z")

public class CoatingProject {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("family")
  private String family = null;

  @JsonProperty("state")
  private String state = null;

  @JsonProperty("image")
  private String image = null;


  public CoatingProject id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The title of the project
   * @return title
  **/
  @ApiModelProperty(value = "The title of the project")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CoatingProject name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The project description
   * @return description
  **/
  @ApiModelProperty(value = "The project description")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CoatingProject family(String family) {
    this.family = family;
    return this;
  }

  /**
   * The project presentation
   * @return presentation
  **/
  @ApiModelProperty(value = "The project presentation")


  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public CoatingProject state(String state) {
    this.state = state;
    return this;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getState() {
    return this.state;
  }

  public CoatingProject image(String image) {
    this.image = image;
    return this;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }
}

