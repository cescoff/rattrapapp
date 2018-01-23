package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.ChildrenLevel1;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The project classification
 */
@ApiModel(description = "The project classification")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-15T11:07:36.062Z")

public class Classification   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("count")
  private Integer count = null;

  @JsonProperty("children")
  private List<ChildrenLevel1> children = null;

  public Classification name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Classification name
   * @return name
  **/
  @ApiModelProperty(value = "Classification name")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Classification count(Integer count) {
    this.count = count;
    return this;
  }

   /**
   * The classification hit count
   * @return count
  **/
  @ApiModelProperty(value = "The classification hit count")


  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Classification children(List<ChildrenLevel1> children) {
    this.children = children;
    return this;
  }

  public Classification addChildrenItem(ChildrenLevel1 childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<ChildrenLevel1>();
    }
    this.children.add(childrenItem);
    return this;
  }

   /**
   * All children classification items. Recursive model
   * @return children
  **/
  @ApiModelProperty(value = "All children classification items. Recursive model")

  @Valid

  public List<ChildrenLevel1> getChildren() {
    return children;
  }

  public void setChildren(List<ChildrenLevel1> children) {
    this.children = children;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Classification classification = (Classification) o;
    return Objects.equals(this.name, classification.name) &&
        Objects.equals(this.count, classification.count) &&
        Objects.equals(this.children, classification.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, count, children);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Classification {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
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

