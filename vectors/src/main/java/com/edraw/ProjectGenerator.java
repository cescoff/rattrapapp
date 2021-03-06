package com.edraw;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;


import com.edraw.config.ProjectConfig;
import com.edraw.config.ProjectConfig.DefaultVariableConfig;
import com.edraw.config.Variables;
import com.edraw.config.Variables.VariableDefinition;
import com.edraw.impl.JAXBResource;
import com.edraw.impl.StringResource;
import com.edraw.impl.URLResource;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.rattrap.utils.JAXBUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

public class ProjectGenerator {

	private static final Pattern SVG_MATCHER = Pattern.compile("<svg width=\"([0-9\\.,]+)mm\" height=\"([0-9\\.,]+)mm\">");

	private final Resource projectConfig;
	
	private final AtomicBoolean configured = new AtomicBoolean(false);

	private String name = null;

	private String description = null;

	private String presentation = null;
	
	private String thumbnailURL = null;

	private Iterable<String> previewURLs = null;

	private Resource variablesResource = null;
	
	private Resource templateResource = null;
	
	private Resource outputConfigResource = null;

	private Resource dynamicPreviewResource = null;

	private Resource dynamicPreviewOutputResource = null;

	private Resource documentationResource = null;

	private final Map<String, String> customVariables = Maps.newHashMap();

	private ProjectConfig config;

	public ProjectGenerator(Resource projectConfig) {
		super();
		this.projectConfig = projectConfig;
	}

	public String getProjectName() {
		try {
			init();
		} catch (Exception e) {
			throw new IllegalStateException("Wrong project configuration", e);
		}
		return name;
	}

	public boolean hasDynamicPreview() {
		try {
			init();
		} catch (Exception e) {
			throw new IllegalStateException("Wrong project configuration", e);
		}
		return this.dynamicPreviewResource != null;
	}

	private synchronized void init() throws Exception {
		if (configured.get()) {
			return;
		}
		try {
			this.config = JAXBUtils.unmarshal(ProjectConfig.class, projectConfig.open());
		} catch (JAXBException e) {
			throw new IllegalArgumentException("Project config '" + projectConfig.getURL() + "' is not well formed", e);
		} catch (IOException e) {
			throw new IllegalArgumentException("Project config '" + projectConfig.getURL() + "' cannot be opened", e);
		} catch (Exception e) {
			throw new IllegalArgumentException("Project config '" + projectConfig.getURL() + "' cannot be opened", e);
		}
		
		for (final DefaultVariableConfig defaultVariableConfig : config.getDefaultVariablesValues()) {
			final String value;
			if (StringUtils.isEmpty(defaultVariableConfig.getValue())) {
				value = "";
			} else {
				value = defaultVariableConfig.getValue();
				if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
					try {
						Double.parseDouble(value);
					} catch (NumberFormatException nfe) {
						throw new IllegalArgumentException("Variable '" + defaultVariableConfig.getName() + "' has a value '" + value + "' that is not a well formed double");
					}
				}
			}
			this.customVariables.put(defaultVariableConfig.getName(), value);
		}

		this.name = config.getName();
		this.description = config.getDescription();
		this.presentation = config.getPresentationText();
		this.thumbnailURL = config.getThumbnailURL();

		this.previewURLs = config.getPreviewURLs();

		if (StringUtils.isEmpty(config.getVariablesURL())) {
			throw new IllegalArgumentException("Variables URL is mandatory");
		}
		
		if (StringUtils.isEmpty(config.getTemplateURL())) {
			throw new IllegalArgumentException("Template URL is mandatory");
		}
		
		if (StringUtils.isEmpty(config.getOutputConfigURL())) {
			throw new IllegalArgumentException("Output URL is mandatory");
		}
		
		this.variablesResource = new URLResource(config.getVariablesURL());
		this.templateResource = new URLResource(config.getTemplateURL());
		this.outputConfigResource = new URLResource(config.getOutputConfigURL());
		if (StringUtils.isNotEmpty(config.getDynamicPreviewURL()) && StringUtils.isNotEmpty(config.getDynamicPreviewOutputURL())) {
			this.dynamicPreviewResource = new URLResource(config.getDynamicPreviewURL());
			this.dynamicPreviewOutputResource = new URLResource(config.getDynamicPreviewOutputURL());
		}

		try {
			this.variablesResource.open().close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot open variables URL '" + config.getVariablesURL() + "'", e);
		}
		
		try {
			this.templateResource.open().close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot open variables URL '" + config.getTemplateURL() + "'", e);
		}
		
		try {
			this.outputConfigResource.open().close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot open output config URL '" + config.getOutputConfigURL() + "'", e);
		}

		if (this.dynamicPreviewResource != null) {
			try {
				this.dynamicPreviewResource.open().close();
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot open dynamic preview config URL '" + config.getOutputConfigURL() + "'", e);
			}
		}

		if (StringUtils.isNotEmpty(config.getDocumentationURL())) {
			this.documentationResource = new URLResource(config.getDocumentationURL());
			try {
				this.documentationResource.open().close();
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot open documentation URL '" + config.getDocumentationURL() + "'", e);
			}
		}

		this.configured.set(true);
	}

	public String getVariableDisplayName(final String varName, final Locale language) {
		for (final DefaultVariableConfig defaultVariableConfig : this.config.getDefaultVariablesValues()) {
			if (defaultVariableConfig.getName().equals(varName)) {
				return defaultVariableConfig.getDisplayName();
			}
		}
		return null;
	}

	public String getVariableType(final String varName) {
		for (final DefaultVariableConfig defaultVariableConfig : this.config.getDefaultVariablesValues()) {
			if (defaultVariableConfig.getName().equals(varName)) {
				if (defaultVariableConfig.getVariableType() != null) {
					return defaultVariableConfig.getVariableType().getDisplayName();
				} else {
					return VariableType.NUMERIC.getDisplayName();
				}
			}
		}
		return VariableType.NUMERIC.getDisplayName();
	}

	public String getVariableDescription(final String varName, final Locale language) {
		for (final DefaultVariableConfig defaultVariableConfig : this.config.getDefaultVariablesValues()) {
			if (defaultVariableConfig.getName().equals(varName)) {
				return defaultVariableConfig.getDescription();
			}
		}
		return null;
	}

	public Resource generateThumbnail(final Map<String, String> newVariables, final int widthPixels, final int heightPixels) throws Exception {
		init();
		if (this.dynamicPreviewResource == null) {
			return null;
		}
		for (final Resource resource : generateProjectOutput(newVariables, this.dynamicPreviewResource, this.dynamicPreviewOutputResource, false)) {
			if ("svg".equalsIgnoreCase(FilenameUtils.getExtension(resource.getName()))) {
				final InputStreamReader inputStreamReader = new InputStreamReader(resource.open());
				final LineIterator lineIterator = new LineIterator(inputStreamReader);

				final StringBuilder result = new StringBuilder();

				while (lineIterator.hasNext()) {
					final String line = lineIterator.nextLine();
					final Matcher svgMatcher = SVG_MATCHER.matcher(line);
					if (svgMatcher.find()) {
						final int width = new Double(svgMatcher.group(1)).intValue();
						final int height = new Double(svgMatcher.group(2)).intValue();
						final StringBuilder widthAttribute = new StringBuilder();
						final StringBuilder heightAttribute = new StringBuilder();
						if (widthPixels > 0) {
							widthAttribute.append(" width=\"").append(widthPixels).append("px\"");
						} else {
							widthAttribute.append(" width=\"100%\"");
						}
						if (heightPixels > 0) {
							heightAttribute.append(" height=\"").append(heightPixels).append("px\"");
						}
						result.append("<svg width=\"").append(widthAttribute).append(heightAttribute).append(" viewBox=\"0 0 ").append(width).append(" ").append(height).append("\" preserveAspectRatio=\"xMinYMin meet\">");
//						result.append("<svg viewBox=\"0 0 ").append(widthPixels).append(" ").append(heightPixels).append("\" preserveAspectRatio=\"xMinYMin meet\" >");
					} else {
						result.append(StringUtils.replace(line, "mm", "px"));
					}
				}

				return new StringResource(resource.getName(), result.toString());
			}
		}
		return null;
	}

	public Iterable<Resource> generateProject(final Map<String, String> newVariables, final boolean skipOutputTransformations) throws Exception {
		init();
		return generateProjectOutput(newVariables, this.templateResource, this.outputConfigResource, skipOutputTransformations);
	}

	private Iterable<Resource> generateProjectOutput(final Map<String, String> newVariables, final Resource localTemplateResource, final Resource localOutputResource, final boolean skipOutputTransformations) throws Exception {
		init();
		final Variables variables;

		try {
			variables = JAXBUtils.unmarshal(Variables.class, this.variablesResource.open());
		} catch (JAXBException e) {
			throw new IllegalStateException("Wrong variables configuration file '" + this.variablesResource.getURL() + "' (malformed)", e);
		} catch (IOException e) {
			throw new IllegalStateException("Wrong variables configuration file '" + this.variablesResource.getURL() + "'", e);
		} catch (Exception e) {
			throw new IllegalStateException("Wrong variables configuration file '" + this.variablesResource.getURL() + "'", e);
		}

		for (final VariableDefinition variableDefinition : variables.getVariables()) {
			if (newVariables.containsKey(variableDefinition.getName())) {
				final String value;
				if (StringUtils.isEmpty(newVariables.get(variableDefinition.getName()))) {
					value = "0.0";
				} else {
					value = newVariables.get(variableDefinition.getName());
				}
				variableDefinition.setValue(value);
			} else if (this.customVariables.containsKey(variableDefinition.getName())) {
				final String value;
				if (StringUtils.isEmpty(this.customVariables.get(variableDefinition.getName()))) {
					value = "0.0";
				} else {
					value = this.customVariables.get(variableDefinition.getName());
				}
				variableDefinition.setValue(value);
			}
		}

		final Resource customVariablesResource = new JAXBResource<Variables>("Custom" + variablesResource.getName(), variables);

		final ImmutableMap.Builder<String, VariableTranslation> userVariableTranslations = ImmutableMap.builder();

		for (final DefaultVariableConfig defaultVariableConfig : this.config.getDefaultVariablesValues()) {
			userVariableTranslations.put(defaultVariableConfig.getName(), getTranslation(defaultVariableConfig));
		}

		final ImmutableMap.Builder<String, VariableTranslation> printableVariableTranslations = ImmutableMap.builder();

		for (final DefaultVariableConfig defaultVariableConfig : this.config.getPrintableVariablesValues()) {
			printableVariableTranslations.put(defaultVariableConfig.getName(), getTranslation(defaultVariableConfig));
		}

		return new LaserPlanGenerator(userVariableTranslations.build(), printableVariableTranslations.build(), customVariablesResource, localTemplateResource, localOutputResource, documentationResource, skipOutputTransformations).getLaserPlan();
	}

	private VariableTranslation getTranslation(final DefaultVariableConfig config) {
		return new VariableTranslation() {
			@Override
			public String getLabel(Locale locale) {
				return config.getDisplayName();
			}

			@Override
			public Optional<String> getUnit(Locale locale) {
				if (StringUtils.isEmpty(config.getUnit())) {
					return Optional.absent();
				}
				return Optional.of(config.getUnit());
			}
		};
	}

	public Map<String, String> getVariables() {
		try {
			init();
		} catch (Exception e) {
			throw new IllegalStateException("Wrong project configuration", e);
		}
		return ImmutableMap.<String, String>builder().putAll(customVariables).build();
	}

	public void updateVariables(final Map<String, String> newValues) {
		try {
			init();
		} catch (Exception e) {
			throw new IllegalStateException("Wrong project configuration", e);
		}
		for (final String key : newValues.keySet()) {
			if (!this.customVariables.containsKey(key)) {
				throw new IllegalArgumentException("Cannot modify variable named '" + key + "' because the project does not allow it");
			}
			try {
				Double.parseDouble(newValues.get(key));
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("Variable '" + key + "' has a value '" + newValues.get(key) + "' that is not a well formed double");
			}
			this.customVariables.put(key, newValues.get(key));
		}
	}

	public String getDescription() {
		try {
			init();
		} catch (Exception e) {
			throw new IllegalStateException("Wrong project configuration", e);
		}
		return this.description;
	}

	public String getPresentation() {
		try {
			init();
		} catch (Exception e) {
			throw new IllegalStateException("Wrong project configuration", e);
		}
		return presentation;
	}

	public String getThumbnailURL() {
		try {
			init();
		} catch (Exception e) {
			throw new IllegalStateException("Wrong project configuration", e);
		}
		return thumbnailURL;
	}

	public Iterable<String> getPreviewURLs() {
		try {
			init();
		} catch (Exception e) {
			throw new IllegalStateException("Wrong project configuration", e);
		}
		return previewURLs;
	}
}
