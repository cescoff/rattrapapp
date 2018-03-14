package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The project basic parameters
 */
@ApiModel(description = "The project basic parameters")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-14T08:37:40.478Z")

public class ProjectGenerator   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("reponame")
  private String reponame = null;

  @JsonProperty("urlname")
  private String urlname = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("presentation")
  private String presentation = null;

  @JsonProperty("previewurl")
  private String previewurl = null;

  @JsonProperty("variablenames")
  @Valid
  private List<String> variablenames = null;

  @JsonProperty("value")
  private String value = null;

  public ProjectGenerator name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The display name of the project
   * @return name
  **/
  @ApiModelProperty(value = "The display name of the project")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProjectGenerator reponame(String reponame) {
    this.reponame = reponame;
    return this;
  }

  /**
   * The name of the repository
   * @return reponame
  **/
  @ApiModelProperty(value = "The name of the repository")


  public String getReponame() {
    return reponame;
  }

  public void setReponame(String reponame) {
    this.reponame = reponame;
  }

  public ProjectGenerator urlname(String urlname) {
    this.urlname = urlname;
    return this;
  }

  /**
   * The URL name of the project
   * @return urlname
  **/
  @ApiModelProperty(value = "The URL name of the project")


  public String getUrlname() {
    return urlname;
  }

  public void setUrlname(String urlname) {
    this.urlname = urlname;
  }

  public ProjectGenerator description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of the parameter
   * @return description
  **/
  @ApiModelProperty(value = "The description of the parameter")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProjectGenerator presentation(String presentation) {
    this.presentation = presentation;
    return this;
  }

  /**
   * The presentation of the project
   * @return presentation
  **/
  @ApiModelProperty(value = "The presentation of the project")


  public String getPresentation() {
    return presentation;
  }

  public void setPresentation(String presentation) {
    this.presentation = presentation;
  }

  public ProjectGenerator previewurl(String previewurl) {
    this.previewurl = previewurl;
    return this;
  }

  /**
   * The URL of the sample preview image
   * @return previewurl
  **/
  @ApiModelProperty(value = "The URL of the sample preview image")


  public String getPreviewurl() {
    return previewurl;
  }

  public void setPreviewurl(String previewurl) {
    this.previewurl = previewurl;
  }

  public ProjectGenerator variablenames(List<String> variablenames) {
    this.variablenames = variablenames;
    return this;
  }

  public ProjectGenerator addVariablenamesItem(String variablenamesItem) {
    if (this.variablenames == null) {
      this.variablenames = new ArrayList<String>();
    }
    this.variablenames.add(variablenamesItem);
    return this;
  }

  /**
   * The names of the project variables
   * @return variablenames
  **/
  @ApiModelProperty(value = "The names of the project variables")


  public List<String> getVariablenames() {
    return variablenames;
  }

  public void setVariablenames(List<String> variablenames) {
    this.variablenames = variablenames;
  }

  public ProjectGenerator value(String value) {
    this.value = value;
    return this;
  }

  /**
   * The value of the parameter
   * @return value
  **/
  @ApiModelProperty(value = "The value of the parameter")


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectGenerator projectGenerator = (ProjectGenerator) o;
    return Objects.equals(this.name, projectGenerator.name) &&
        Objects.equals(this.reponame, projectGenerator.reponame) &&
        Objects.equals(this.urlname, projectGenerator.urlname) &&
        Objects.equals(this.description, projectGenerator.description) &&
        Objects.equals(this.presentation, projectGenerator.presentation) &&
        Objects.equals(this.previewurl, projectGenerator.previewurl) &&
        Objects.equals(this.variablenames, projectGenerator.variablenames) &&
        Objects.equals(this.value, projectGenerator.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, reponame, urlname, description, presentation, previewurl, variablenames, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProjectGenerator {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    reponame: ").append(toIndentedString(reponame)).append("\n");
    sb.append("    urlname: ").append(toIndentedString(urlname)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    presentation: ").append(toIndentedString(presentation)).append("\n");
    sb.append("    previewurl: ").append(toIndentedString(previewurl)).append("\n");
    sb.append("    variablenames: ").append(toIndentedString(variablenames)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

