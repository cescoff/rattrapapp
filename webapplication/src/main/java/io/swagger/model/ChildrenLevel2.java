package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ChildrenLevel2
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-02T16:06:11.495Z")

public class ChildrenLevel2   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("count")
  private Integer count = null;

  public ChildrenLevel2 name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Classification name level 2
   * @return name
  **/
  @ApiModelProperty(value = "Classification name level 2")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChildrenLevel2 count(Integer count) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChildrenLevel2 childrenLevel2 = (ChildrenLevel2) o;
    return Objects.equals(this.name, childrenLevel2.name) &&
        Objects.equals(this.count, childrenLevel2.count);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, count);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChildrenLevel2 {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
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

