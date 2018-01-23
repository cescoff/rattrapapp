package com.edraw.config;

import java.util.Collection;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import com.google.common.collect.Lists;
import com.rattrap.utils.JAXBUtils;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectConfig {

	@XmlElement
	private String name;

	@XmlElement(name = "thumbnail-url")
	private String thumbnailURL;

	@XmlElement
	private String description;

	@XmlElement(name = "presentation")
	private String presentationText;
	
	@XmlElement(name = "variables-url")
	private String variablesURL;
	
	@XmlElement(name = "template-url")
	private String templateURL;
	
	@XmlElement(name = "output-config-url")
	private String outputConfigURL;

	@XmlElement(name = "dynamic-preview-url")
	private String dynamicPreviewURL;

	@XmlElement(name = "dynamic-preview-output-url")
	private String dynamicPreviewOutputURL;

	@XmlElementWrapper(name = "default") @XmlElement(name = "variable") 
	private Collection<DefaultVariableConfig> defaultVariablesValues = Lists.newArrayList();
	
	public ProjectConfig() {
		super();
	}

	public ProjectConfig(String thumbnailURL, String presentationText,
			String variablesURL, String templateURL, String outputConfigURL) {
		super();
		this.thumbnailURL = thumbnailURL;
		this.presentationText = presentationText;
		this.variablesURL = variablesURL;
		this.templateURL = templateURL;
		this.outputConfigURL = outputConfigURL;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public String getPresentationText() {
		return presentationText;
	}

	public String getVariablesURL() {
		return variablesURL;
	}

	public String getTemplateURL() {
		return templateURL;
	}

	public String getOutputConfigURL() {
		return outputConfigURL;
	}

	public String getDynamicPreviewURL() {
		return dynamicPreviewURL;
	}

	public String getDynamicPreviewOutputURL() {
		return dynamicPreviewOutputURL;
	}

	public Collection<DefaultVariableConfig> getDefaultVariablesValues() {
		return defaultVariablesValues;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class DefaultVariableConfig {
		
		@XmlAttribute
		private String name;

		@XmlAttribute(name =  "display-name")
		private String displayName;

		@XmlAttribute
		private String description;

		@XmlValue
		private String value;

		public DefaultVariableConfig() {
			super();
		}

		public DefaultVariableConfig(String name, String displayName, String description, String value) {
			super();
			this.name = name;
			this.displayName = displayName;
			this.description = description;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getDescription() {
			return description;
		}
	}

	public static void main(String[] args) throws JAXBException {
		final ProjectConfig projectConfig = new ProjectConfig();
		projectConfig.templateURL = "https://raw.githubusercontent.com/cescoff/rattrapchair/master/Chair.xml";
		projectConfig.variablesURL = "https://raw.githubusercontent.com/cescoff/rattrapchair/master/variables.xml";
		projectConfig.outputConfigURL = "https://raw.githubusercontent.com/cescoff/rattrapchair/master/chairOutputConfig.xml";
		projectConfig.dynamicPreviewURL = "https://raw.githubusercontent.com/cescoff/rattrapchair/master/chair_ShapeOutputConfig.xml";
		projectConfig.thumbnailURL = "https://www.amazon.com/photos/share/KrAbRlo7ONp7tepYRq1wSZaWv2HNM3j4lp5JJHNR0ob";

		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("fullSeatHeight", "Seat height", "The height of the seat. In millimeters", "460"));
		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("fullSeatLength", "Seat length", "The length of the seat. In millimeters", "310"));
		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("fullSeatwidth", "Seat width", "The width of the seat. In millimeters", "320"));
		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("fullUpright", "Chair upright", "The upright of the chair. In millimeters", "50"));
		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("alphaDeg", "Back angle", "The angle of the back of the chair (regarding vertical line). In degrees", "17"));

		System.out.println(JAXBUtils.marshal(projectConfig, true));
	}

}
