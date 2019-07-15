package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * SearchResult
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-14T08:37:40.478Z")

public class CoatingProjectList {
  @JsonProperty("projects")
  @Valid
  private List<CoatingProject> projects = null;

  public CoatingProjectList projects(List<CoatingProject> projects) {
    this.projects = projects;
    return this;
  }

  public CoatingProjectList addProjectsItem(CoatingProject projectsItem) {
    if (this.projects == null) {
      this.projects = new ArrayList<CoatingProject>();
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

  public List<CoatingProject> getProjects() {
    return projects;
  }

  public void setProjects(List<CoatingProject> projects) {
    this.projects = projects;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CoatingProjectList searchResult = (CoatingProjectList) o;
    return Objects.equals(this.projects, searchResult.projects);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projects);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchResult {\n");

    sb.append("    projects: ").append(toIndentedString(projects)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

