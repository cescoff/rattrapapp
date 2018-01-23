package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.ProjectParameter;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Reference to a given preview
 */
@ApiModel(description = "Reference to a given preview")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-15T11:07:36.062Z")

public class PreviewRenderingRef   {
  @JsonProperty("projectid")
  private String projectid = null;

  @JsonProperty("url")
  private String url = null;

  /**
   * Format type of preview e.g. SVG, IMG, YOUTUBE VIDEO...
   */
  public enum OutputformatEnum {
    SVG("SVG"),
    
    IMG("IMG"),
    
    ZIP("ZIP");

    private String value;

    OutputformatEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static OutputformatEnum fromValue(String text) {
      for (OutputformatEnum b : OutputformatEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("outputformat")
  private OutputformatEnum outputformat = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("format")
  private String format = null;

  @JsonProperty("width")
  private Integer width = null;

  @JsonProperty("height")
  private Integer height = null;

  @JsonProperty("parameters")
  private List<ProjectParameter> parameters = null;

  public PreviewRenderingRef projectid(String projectid) {
    this.projectid = projectid;
    return this;
  }

   /**
   * The Id of the project
   * @return projectid
  **/
  @ApiModelProperty(value = "The Id of the project")


  public String getProjectid() {
    return projectid;
  }

  public void setProjectid(String projectid) {
    this.projectid = projectid;
  }

  public PreviewRenderingRef url(String url) {
    this.url = url;
    return this;
  }

   /**
   * An image full URL for ex.
   * @return url
  **/
  @ApiModelProperty(value = "An image full URL for ex.")


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public PreviewRenderingRef outputformat(OutputformatEnum outputformat) {
    this.outputformat = outputformat;
    return this;
  }

   /**
   * Format type of preview e.g. SVG, IMG, YOUTUBE VIDEO...
   * @return outputformat
  **/
  @ApiModelProperty(value = "Format type of preview e.g. SVG, IMG, YOUTUBE VIDEO...")


  public OutputformatEnum getOutputformat() {
    return outputformat;
  }

  public void setOutputformat(OutputformatEnum outputformat) {
    this.outputformat = outputformat;
  }

  public PreviewRenderingRef name(String name) {
    this.name = name;
    return this;
  }

   /**
   * The name of the output in the outputConfig.xml file
   * @return name
  **/
  @ApiModelProperty(value = "The name of the output in the outputConfig.xml file")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PreviewRenderingRef format(String format) {
    this.format = format;
    return this;
  }

   /**
   * The output format of the preview e.g. SVG
   * @return format
  **/
  @ApiModelProperty(value = "The output format of the preview e.g. SVG")


  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public PreviewRenderingRef width(Integer width) {
    this.width = width;
    return this;
  }

   /**
   * Width of the output preview
   * @return width
  **/
  @ApiModelProperty(value = "Width of the output preview")


  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public PreviewRenderingRef height(Integer height) {
    this.height = height;
    return this;
  }

   /**
   * Height of the output preview
   * @return height
  **/
  @ApiModelProperty(value = "Height of the output preview")


  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public PreviewRenderingRef parameters(List<ProjectParameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public PreviewRenderingRef addParametersItem(ProjectParameter parametersItem) {
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
    PreviewRenderingRef previewRenderingRef = (PreviewRenderingRef) o;
    return Objects.equals(this.projectid, previewRenderingRef.projectid) &&
        Objects.equals(this.url, previewRenderingRef.url) &&
        Objects.equals(this.outputformat, previewRenderingRef.outputformat) &&
        Objects.equals(this.name, previewRenderingRef.name) &&
        Objects.equals(this.format, previewRenderingRef.format) &&
        Objects.equals(this.width, previewRenderingRef.width) &&
        Objects.equals(this.height, previewRenderingRef.height) &&
        Objects.equals(this.parameters, previewRenderingRef.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectid, url, outputformat, name, format, width, height, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PreviewRenderingRef {\n");
    
    sb.append("    projectid: ").append(toIndentedString(projectid)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    outputformat: ").append(toIndentedString(outputformat)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    format: ").append(toIndentedString(format)).append("\n");
    sb.append("    width: ").append(toIndentedString(width)).append("\n");
    sb.append("    height: ").append(toIndentedString(height)).append("\n");
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

