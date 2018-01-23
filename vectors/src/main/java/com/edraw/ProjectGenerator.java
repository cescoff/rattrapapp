package com.edraw;

import java.io.File;
import java.io.FileOutputStream;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.rattrap.utils.JAXBUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
	
	private Resource variablesResource = null;
	
	private Resource templateResource = null;
	
	private Resource outputConfigResource = null;

	private Resource dynamicPreviewResource = null;

	private Resource dynamicPreviewOutputResource = null;

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
				try {
					Double.parseDouble(value);
				} catch (NumberFormatException nfe) {
					throw new IllegalArgumentException("Variable '" + defaultVariableConfig.getName() + "' has a value '" + value + "' that is not a well formed double");
				}
			}
			this.customVariables.put(defaultVariableConfig.getName(), value);
		}

		this.name = config.getName();
		this.description = config.getDescription();
		this.presentation = config.getPresentationText();
		this.thumbnailURL = config.getThumbnailURL();
		
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
		for (final Resource resource : generateProjectOutput(newVariables, this.dynamicPreviewResource, this.dynamicPreviewOutputResource)) {
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
						result.append("<svg width=\"").append(widthPixels).append("px\" height=\"").append(heightPixels).append("px\"").append(" viewBox=\"0 0 ").append(width).append(" ").append(height).append("\" preserveAspectRatio=\"xMinYMin meet\">");
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

	public Iterable<Resource> generateProject(final Map<String, String> newVariables) throws Exception {
		init();
		return generateProjectOutput(newVariables, this.templateResource, this.outputConfigResource);
	}

	private Iterable<Resource> generateProjectOutput(final Map<String, String> newVariables, final Resource localTemplateResource, final Resource localOutputResource) throws Exception {
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
				final double value;
				if (StringUtils.isEmpty(newVariables.get(variableDefinition.getName()))) {
					value = 0.0;
				} else {
					value = Double.parseDouble(newVariables.get(variableDefinition.getName()));
				}
				variableDefinition.setValue(value);
			} else if (this.customVariables.containsKey(variableDefinition.getName())) {
				final double value;
				if (StringUtils.isEmpty(this.customVariables.get(variableDefinition.getName()))) {
					value = 0.0;
				} else {
					value = Double.parseDouble(this.customVariables.get(variableDefinition.getName()));
				}
				variableDefinition.setValue(value);
			}
		}

		final Resource customVariablesResource = new JAXBResource<Variables>("Custom" + variablesResource.getName(), variables);

		return new LaserPlanGenerator(customVariablesResource, localTemplateResource, localOutputResource).getLaserPlan();
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

	public static  void main(String[] args) throws Exception {
		final URLResource projectURL = new URLResource("https://raw.githubusercontent.com/cescoff/rattrapchair/master/project.xml");
		final ProjectGenerator generator = new ProjectGenerator(projectURL);
		final File outputDir = new File("C:\\Users\\g3q\\Desktop\\Perso\\CHAIR2\\output");
		for (final Resource resource : generator.generateProject(Maps.<String, String>newHashMap())) {
			final File outputFile = new File(outputDir, resource.getName());
			final FileOutputStream fos = new FileOutputStream(outputFile);
			try {
				IOUtils.copy(resource.open(), fos);
			} finally {
				fos.close();
			}
		}
	}

}
