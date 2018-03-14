package com.edraw.config;

import java.util.Collection;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;

import com.edraw.VariableType;
import com.google.common.collect.Lists;
import com.rattrap.utils.JAXBUtils;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectConfig {

	@XmlElement
	private String name;

	@XmlElement(name = "thumbnail-url")
	private String thumbnailURL;

	@XmlElementWrapper(name = "previews") @XmlElement(name = "url")
	private Collection<String> previewURLs = Lists.newArrayList();

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

	@XmlElement(name = "documentation-url")
	private String documentationURL;

	@XmlElementWrapper(name = "default") @XmlElement(name = "variable")
	private Collection<DefaultVariableConfig> defaultVariablesValues = Lists.newArrayList();

	@XmlElementWrapper(name = "printable") @XmlElement(name = "variable")
	private Collection<DefaultVariableConfig> printableVariablesValues = Lists.newArrayList();

	public ProjectConfig() {
		super();
	}

	public ProjectConfig(String name, String thumbnailURL, String description, String presentationText, String variablesURL, String templateURL, String outputConfigURL, String dynamicPreviewURL, String dynamicPreviewOutputURL, String documentationURL, Collection<DefaultVariableConfig> defaultVariablesValues, Collection<DefaultVariableConfig> printableVariablesValues) {
		this.name = name;
		this.thumbnailURL = thumbnailURL;
		this.description = description;
		this.presentationText = presentationText;
		this.variablesURL = variablesURL;
		this.templateURL = templateURL;
		this.outputConfigURL = outputConfigURL;
		this.dynamicPreviewURL = dynamicPreviewURL;
		this.dynamicPreviewOutputURL = dynamicPreviewOutputURL;
		this.documentationURL = documentationURL;
		this.defaultVariablesValues = defaultVariablesValues;
		this.printableVariablesValues = printableVariablesValues;
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

	public Collection<String> getPreviewURLs() { return previewURLs; }

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

	public Collection<DefaultVariableConfig> getPrintableVariablesValues() {
		return printableVariablesValues;
	}

    public String getDocumentationURL() { return documentationURL; }

    @XmlAccessorType(XmlAccessType.FIELD)
	public static class DefaultVariableConfig {
		
		@XmlAttribute
		private String name;

		@XmlAttribute(name =  "display-name")
		private String displayName;

		@XmlAttribute(name = "unit")
		private String unit;

		@XmlAttribute
		private String description;

		@XmlAttribute(name =  "type")
		private VariableType variableType;

		@XmlValue
		private String value;

		public DefaultVariableConfig() {
			super();
		}

		public DefaultVariableConfig(String name, String displayName, String unit, String description, String value) {
			super();
			this.name = name;
			this.displayName = displayName;
			this.unit = unit;
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

		public String getUnit() { return this.unit; }

		public String getDescription() {
			return description;
		}

		public VariableType getVariableType() { return variableType; }

	}

	public static void main(String[] args) throws JAXBException {
		final ProjectConfig projectConfig = new ProjectConfig();
		projectConfig.templateURL = "https://raw.githubusercontent.com/cescoff/rattrapchair/master/Chair.xml";
		projectConfig.variablesURL = "https://raw.githubusercontent.com/cescoff/rattrapchair/master/variables.xml";
		projectConfig.outputConfigURL = "https://raw.githubusercontent.com/cescoff/rattrapchair/master/chairOutputConfig.xml";
		projectConfig.dynamicPreviewURL = "https://raw.githubusercontent.com/cescoff/rattrapchair/master/chair_ShapeOutputConfig.xml";
		projectConfig.thumbnailURL = "https://www.amazon.com/photos/share/KrAbRlo7ONp7tepYRq1wSZaWv2HNM3j4lp5JJHNR0ob";

		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("fullSeatHeight", "Seat height", "mm", "The height of the seat. In millimeters", "460"));
		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("fullSeatLength", "Seat length", "mm", "The length of the seat. In millimeters", "310"));
		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("fullSeatwidth", "Seat width", "mm", "The width of the seat. In millimeters", "320"));
		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("fullUpright", "Chair upright", "mm", "The upright of the chair. In millimeters", "50"));
		projectConfig.defaultVariablesValues.add(new DefaultVariableConfig("alphaDeg", "Back angle", "degrees", "The angle of the back of the chair (regarding vertical line). In degrees", "17"));

		System.out.println(JAXBUtils.marshal(projectConfig, true));
	}

}
