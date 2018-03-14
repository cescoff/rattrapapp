package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * A dynamic project parameter
 */
@ApiModel(description = "A dynamic project parameter")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-14T08:37:40.478Z")

public class ProjectParameter   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("displayname")
  private String displayname = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("value")
  private String value = null;

  @JsonProperty("type")
  private String type = null;

  public ProjectParameter name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The technical name of the parameter
   * @return name
  **/
  @ApiModelProperty(value = "The technical name of the parameter")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProjectParameter displayname(String displayname) {
    this.displayname = displayname;
    return this;
  }

  /**
   * The display name of the parameter
   * @return displayname
  **/
  @ApiModelProperty(value = "The display name of the parameter")


  public String getDisplayname() {
    return displayname;
  }

  public void setDisplayname(String displayname) {
    this.displayname = displayname;
  }

  public ProjectParameter description(String description) {
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

  public ProjectParameter value(String value) {
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

  public ProjectParameter type(String type) {
    this.type = type;
    return this;
  }

  /**
   * The type of the variable (NUMERIC / BOOLEAN)
   * @return type
  **/
  @ApiModelProperty(value = "The type of the variable (NUMERIC / BOOLEAN)")


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectParameter projectParameter = (ProjectParameter) o;
    return Objects.equals(this.name, projectParameter.name) &&
        Objects.equals(this.displayname, projectParameter.displayname) &&
        Objects.equals(this.description, projectParameter.description) &&
        Objects.equals(this.value, projectParameter.value) &&
        Objects.equals(this.type, projectParameter.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, displayname, description, value, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProjectParameter {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    displayname: ").append(toIndentedString(displayname)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

