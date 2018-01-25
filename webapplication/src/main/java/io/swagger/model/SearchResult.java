package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Classification;
import io.swagger.model.ProjectSummary;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * SearchResult
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-25T10:38:37.158Z")

public class SearchResult   {
  @JsonProperty("projects")
  private List<ProjectSummary> projects = null;

  @JsonProperty("facecting")
  private List<Classification> facecting = null;

  public SearchResult projects(List<ProjectSummary> projects) {
    this.projects = projects;
    return this;
  }

  public SearchResult addProjectsItem(ProjectSummary projectsItem) {
    if (this.projects == null) {
      this.projects = new ArrayList<ProjectSummary>();
    }
    this.projects.add(projectsItem);
    return this;
  }

   /**
   * Get projects
   * @return projects
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<ProjectSummary> getProjects() {
    return projects;
  }

  public void setProjects(List<ProjectSummary> projects) {
    this.projects = projects;
  }

  public SearchResult facecting(List<Classification> facecting) {
    this.facecting = facecting;
    return this;
  }

  public SearchResult addFacectingItem(Classification facectingItem) {
    if (this.facecting == null) {
      this.facecting = new ArrayList<Classification>();
    }
    this.facecting.add(facectingItem);
    return this;
  }

   /**
   * Get facecting
   * @return facecting
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<Classification> getFacecting() {
    return facecting;
  }

  public void setFacecting(List<Classification> facecting) {
    this.facecting = facecting;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchResult searchResult = (SearchResult) o;
    return Objects.equals(this.projects, searchResult.projects) &&
        Objects.equals(this.facecting, searchResult.facecting);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projects, facecting);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchResult {\n");
    
    sb.append("    projects: ").append(toIndentedString(projects)).append("\n");
    sb.append("    facecting: ").append(toIndentedString(facecting)).append("\n");
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

