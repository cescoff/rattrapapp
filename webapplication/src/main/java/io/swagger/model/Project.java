package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Classification;
import io.swagger.model.PreviewRenderingRef;
import io.swagger.model.ProjectParameter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Project details
 */
@ApiModel(description = "Project details")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-14T08:37:40.478Z")

public class Project   {
  @JsonProperty("title")
  private String title = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("presentation")
  private String presentation = null;

  @JsonProperty("thumbnailurl")
  private String thumbnailurl = null;

  @JsonProperty("previews")
  @Valid
  private List<PreviewRenderingRef> previews = null;

  @JsonProperty("classification")
  @Valid
  private List<Classification> classification = null;

  @JsonProperty("parameters")
  @Valid
  private List<ProjectParameter> parameters = null;

  public Project title(String title) {
    this.title = title;
    return this;
  }

  /**
   * The title of the project
   * @return title
  **/
  @ApiModelProperty(value = "The title of the project")


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Project description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The project description
   * @return description
  **/
  @ApiModelProperty(value = "The project description")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Project presentation(String presentation) {
    this.presentation = presentation;
    return this;
  }

  /**
   * The project presentation
   * @return presentation
  **/
  @ApiModelProperty(value = "The project presentation")


  public String getPresentation() {
    return presentation;
  }

  public void setPresentation(String presentation) {
    this.presentation = presentation;
  }

  public Project thumbnailurl(String thumbnailurl) {
    this.thumbnailurl = thumbnailurl;
    return this;
  }

  /**
   * The URL of the thumbnail
   * @return thumbnailurl
  **/
  @ApiModelProperty(value = "The URL of the thumbnail")


  public String getThumbnailurl() {
    return thumbnailurl;
  }

  public void setThumbnailurl(String thumbnailurl) {
    this.thumbnailurl = thumbnailurl;
  }

  public Project previews(List<PreviewRenderingRef> previews) {
    this.previews = previews;
    return this;
  }

  public Project addPreviewsItem(PreviewRenderingRef previewsItem) {
    if (this.previews == null) {
      this.previews = new ArrayList<PreviewRenderingRef>();
    }
    this.previews.add(previewsItem);
    return this;
  }

  /**
   * The information required to render a preview
   * @return previews
  **/
  @ApiModelProperty(value = "The information required to render a preview")

  @Valid

  public List<PreviewRenderingRef> getPreviews() {
    return previews;
  }

  public void setPreviews(List<PreviewRenderingRef> previews) {
    this.previews = previews;
  }

  public Project classification(List<Classification> classification) {
    this.classification = classification;
    return this;
  }

  public Project addClassificationItem(Classification classificationItem) {
    if (this.classification == null) {
      this.classification = new ArrayList<Classification>();
    }
    this.classification.add(classificationItem);
    return this;
  }

  /**
   * All classifications to which project belongs
   * @return classification
  **/
  @ApiModelProperty(value = "All classifications to which project belongs")

  @Valid

  public List<Classification> getClassification() {
    return classification;
  }

  public void setClassification(List<Classification> classification) {
    this.classification = classification;
  }

  public Project parameters(List<ProjectParameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public Project addParametersItem(ProjectParameter parametersItem) {
    if (this.parameters == null) {
      this.parameters = new ArrayList<ProjectParameter>();
    }
    this.parameters.add(parametersItem);
    return this;
  }

  /**
   * All dynamic parameters in the project
   * @return parameters
  **/
  @ApiModelProperty(value = "All dynamic parameters in the project")

  @Valid

  public List<ProjectParameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<ProjectParameter> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Project project = (Project) o;
    return Objects.equals(this.title, project.title) &&
        Objects.equals(this.description, project.description) &&
        Objects.equals(this.presentation, project.presentation) &&
        Objects.equals(this.thumbnailurl, project.thumbnailurl) &&
        Objects.equals(this.previews, project.previews) &&
        Objects.equals(this.classification, project.classification) &&
        Objects.equals(this.parameters, project.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, presentation, thumbnailurl, previews, classification, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Project {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    presentation: ").append(toIndentedString(presentation)).append("\n");
    sb.append("    thumbnailurl: ").append(toIndentedString(thumbnailurl)).append("\n");
    sb.append("    previews: ").append(toIndentedString(previews)).append("\n");
    sb.append("    classification: ").append(toIndentedString(classification)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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

