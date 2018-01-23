package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.ChildrenLevel2;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ChildrenLevel1
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-15T11:07:36.062Z")

public class ChildrenLevel1   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("count")
  private Integer count = null;

  @JsonProperty("children")
  private List<ChildrenLevel2> children = null;

  public ChildrenLevel1 name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Classification name level 1
   * @return name
  **/
  @ApiModelProperty(value = "Classification name level 1")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChildrenLevel1 count(Integer count) {
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

  public ChildrenLevel1 children(List<ChildrenLevel2> children) {
    this.children = children;
    return this;
  }

  public ChildrenLevel1 addChildrenItem(ChildrenLevel2 childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<ChildrenLevel2>();
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

  public List<ChildrenLevel2> getChildren() {
    return children;
  }

  public void setChildren(List<ChildrenLevel2> children) {
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
    ChildrenLevel1 childrenLevel1 = (ChildrenLevel1) o;
    return Objects.equals(this.name, childrenLevel1.name) &&
        Objects.equals(this.count, childrenLevel1.count) &&
        Objects.equals(this.children, childrenLevel1.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, count, children);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChildrenLevel1 {\n");
    
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

