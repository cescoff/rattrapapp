package com.edraw.config;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.edraw.config.laser.LaserTransformation;
import com.google.common.collect.Lists;

@XmlRootElement(name = "output-config")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputConfig {

	@XmlElementWrapper(name = "outputs") @XmlElement(name = "output")
	private Collection<Output> outputs = Lists.newArrayList();
	
	public OutputConfig() {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Output {
		
		@XmlAttribute
		private String name;
		
		@XmlElement(name = "layer")
		private Collection<String> layers = Lists.newArrayList();
		
		@XmlAttribute(name = "format")
		private String outputFormat = OutputFormat.SVG.name();

		@XmlElementRef
		private Collection<LaserTransformation> transformations = Lists.newArrayList();
		
		public Output() {
			super();
		}

		public Output(String name, String outputFormat) {
			super();
			this.name = name;
			this.outputFormat = outputFormat;
		}

		public String getName() {
			return name;
		}

		public Collection<String> getLayers() {
			return layers;
		}

		public String getOutputFormat() {
			return outputFormat;
		}

		public Collection<LaserTransformation> getTransformations() {
			return transformations;
		}
		
	}

	public Collection<Output> getOutputs() {
		return outputs;
	}

}
