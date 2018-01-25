package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Classification;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * A quick project summary
 */
@ApiModel(description = "A quick project summary")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-25T10:38:37.158Z")

public class ProjectSummary   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("title")
  private String title = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("thumbnailurl")
  private String thumbnailurl = null;

  @JsonProperty("classification")
  private List<Classification> classification = null;

  public ProjectSummary id(String id) {
    this.id = id;
    return this;
  }

   /**
   * The id of the Project
   * @return id
  **/
  @ApiModelProperty(value = "The id of the Project")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ProjectSummary title(String title) {
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

  public ProjectSummary description(String description) {
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

  public ProjectSummary thumbnailurl(String thumbnailurl) {
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

  public ProjectSummary classification(List<Classification> classification) {
    this.classification = classification;
    return this;
  }

  public ProjectSummary addClassificationItem(Classification classificationItem) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectSummary projectSummary = (ProjectSummary) o;
    return Objects.equals(this.id, projectSummary.id) &&
        Objects.equals(this.title, projectSummary.title) &&
        Objects.equals(this.description, projectSummary.description) &&
        Objects.equals(this.thumbnailurl, projectSummary.thumbnailurl) &&
        Objects.equals(this.classification, projectSummary.classification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, description, thumbnailurl, classification);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProjectSummary {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    thumbnailurl: ").append(toIndentedString(thumbnailurl)).append("\n");
    sb.append("    classification: ").append(toIndentedString(classification)).append("\n");
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

