package com.edraw;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rattrap.utils.JAXBUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;


import com.edraw.config.ConfigurationValidator;
import com.edraw.config.OutputConfig;
import com.edraw.config.OutputConfig.Output;
import com.edraw.config.OutputFormat;
import com.edraw.config.laser.LaserBluePrint;
import com.edraw.config.laser.validation.ValidationMessage;
import com.edraw.convert.SVGConverter;
import com.edraw.geom.BluePrint;
import com.edraw.impl.ConfigurableVariablesContext;
import com.edraw.impl.StringResource;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaserPlanGenerator {

	private final Pattern VAR_PATTERN = Pattern.compile("\\$\\{[A-Za-z\\.0-9]+\\}");

    private final Map<String, VariableTranslation> userVariablesTranslations;

	private final Map<String, VariableTranslation> printableVariablesTranslations;

	private final Resource variables;
	
	private final Resource template;
	
	private final Resource outputConfig;

	private final Resource documentation;

	private final boolean skipOutputTransformations;

	public LaserPlanGenerator(Map<String, VariableTranslation> userVariablesTranslations,
							  Map<String, VariableTranslation> printableVariablesTranslations,
                              Resource variables, Resource template,
                              Resource outputConfig,
                              Resource documentation,
                              boolean skipOutputTransformations) {
		super();
		this.userVariablesTranslations = userVariablesTranslations;
		this.printableVariablesTranslations = printableVariablesTranslations;
		this.variables = variables;
		this.template = template;
		this.outputConfig = outputConfig;
		this.documentation = documentation;
		this.skipOutputTransformations = skipOutputTransformations;
	}
	
	public Iterable<Resource> getLaserPlan() throws Exception {
		final LaserBluePrint laserBluePrint = JAXBUtils.unmarshal(LaserBluePrint.class, this.template.open());
		
		final VarContext varContext = new ConfigurableVariablesContext(this.variables);
		
		final Iterable<ValidationMessage> validationMessages = new ConfigurationValidator(varContext).getValidationErrorMessages(laserBluePrint);
		
		handleValidationMessages(validationMessages);
		
		final BluePrint bluePrint = new BluePrintParser(varContext).parse(laserBluePrint);
		
		final OutputConfig outputConfig = JAXBUtils.unmarshal(OutputConfig.class, this.outputConfig.open());
		
		final ImmutableList.Builder<Resource> result = ImmutableList.builder();

        final Map<String, Resource> graphicOutputs = Maps.newLinkedHashMap();
		for (final Output output : outputConfig.getOutputs()) {

            final Iterable<BluePrint> outputBluePrints;

            if (!this.skipOutputTransformations) {
                final Transformation transformation = TransformationFactory.getInstance().getTransformation(output.getTransformations(), bluePrint);
                outputBluePrints = transformation.transform(ImmutableList.of(bluePrint));
            } else {
                outputBluePrints = ImmutableList.of(bluePrint);
            }

			final OutputFormat outputFormat = OutputFormat.valueOf(output.getOutputFormat());
			for (final String extension : outputFormat.getExtensions()) {
				if (!StringUtils.equalsIgnoreCase("html", extension)) {
					if (outputFormat == OutputFormat.SVG) {
						int position = 0;

						for (final BluePrint outputBluePrint : outputBluePrints) {
							final String positionSuffix;
							if (Iterables.size(outputBluePrints) > 1) {
								positionSuffix = "_" + position;
							} else {
								positionSuffix = "";
							}
							final String resourceName = output.getName() + positionSuffix + "." + extension;
							final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							new SVGConverter().convertTo(outputBluePrint, output.getLayers(), outputStream, outputFormat.getVars(extension));
							final Resource resource = new StringResource(resourceName, new String(outputStream.toByteArray()));
							result.add(resource);
							graphicOutputs.put(resourceName, resource);
							position++;
						}
					} else {
						throw new Exception("OutputFormat '" + outputFormat + "' is not supported yet");
					}
				}
			}
		}

        result.add(getHTMLResource(graphicOutputs));
		result.add(getReadmeHtml(varContext));
		final Resource documentationResource = getDocumentation(varContext);
		if (documentationResource != null) {
			result.add(documentationResource);
		}
		return result.build();
	}

	private Resource getDocumentation(final VarContext varContext) throws Exception {
		if (this.documentation == null) {
			return null;
		}
		final InputStream docIn = this.documentation.open();
		final String documentationString;
		try {
			documentationString = IOUtils.toString(new InputStreamReader(docIn));
		} finally {
			docIn.close();
		}
		if (StringUtils.isEmpty(documentationString)) {
			LoggerFactory.getLogger(getClass()).info("Documentation resource provided an empty string");
			return null;
		}
		final List<String> docVars = Lists.newArrayList();
		final Matcher varMatcher = VAR_PATTERN.matcher(documentationString);
		while (varMatcher.find()) {
			docVars.add(varMatcher.group());
		}
		String resultDoc = documentationString;
		for (final String varName : docVars) {
			final String varValue = varContext.evaluate(varName) + "";
			resultDoc = StringUtils.replace(resultDoc, "${" + varName + "}", varValue);
		}
		return new StringResource("documentation." + FilenameUtils.getExtension(this.documentation.getName()), resultDoc);
	}

	private Resource getReadmeHtml(final VarContext varContext) throws Exception {
	    final StringBuilder result = new StringBuilder("<html>\n\n\t<head>\n\t\t<title>Project summary</title>\n\t</head>\n\n\t<body>\n\n\t\t<h1>User variables and values:</h1>\n\n\t\t<ul>\n");
	    for (final String varName : this.userVariablesTranslations.keySet()) {
	    	final String label = this.userVariablesTranslations.get(varName).getLabel(Locale.US);
	    	final String unit;
	    	if (this.userVariablesTranslations.get(varName).getUnit(Locale.US).isPresent()) {
	    		unit = " " + this.userVariablesTranslations.get(varName).getUnit(Locale.US).get();
			} else {
	    		unit = "";
			}
	        result.append("\t\t\t<li>").append(label).append(" : ").append(varContext.evaluate(varName)).append(unit).append("</li>\n");
        }
		result.append("\t\t</ul>");

        if (this.printableVariablesTranslations != null && !this.printableVariablesTranslations.isEmpty()) {
			result.append("\t\t<h1>Project output variables and values:</h1>\n\n\t\t<ul>\n");
			for (final String varName : this.printableVariablesTranslations.keySet()) {
				final String label = this.printableVariablesTranslations.get(varName).getLabel(Locale.US);
				final String unit;
				if (this.printableVariablesTranslations.get(varName).getUnit(Locale.US).isPresent()) {
					unit = " " + this.printableVariablesTranslations.get(varName).getUnit(Locale.US).get();
				} else {
					unit = "";
				}
				result.append("\t\t\t<li>").append(label).append(" : ").append(varContext.evaluate(varName)).append(unit).append("</li>\n");
			}
			result.append("\t\t</ul>");
		}

        result.append("</body>");
        return new StringResource("README.html", result.toString());
    }

	private Resource getHTMLResource(final Map<String, Resource> graphicResources) throws Exception {
		final StringBuilder result = new StringBuilder("<html>\n\n\t<head>\n\t\t<title>Preview</title>\n\t</head>\n\n\t<body>\n\n");
		for (final String resourceName : graphicResources.keySet()) {
			final String graphic = IOUtils.toString(new InputStreamReader(graphicResources.get(resourceName).open()));
			result.append("\t\t<H1>").append(resourceName).append("</H1>\n\n").append(graphic).append("\n\n");
		}
		result.append("</body>");
		return new StringResource("Preview.html", result.toString());
	}
	
	private void handleValidationMessages(final Iterable<ValidationMessage> messages) {
		if (Iterables.isEmpty(messages)) {
			return;
		}
		final Logger logger = LoggerFactory.getLogger(Test.class);
		boolean hasErrors = false;
		for (final ValidationMessage validationMessage : messages) {
			switch (validationMessage.getLevel()) {
			case INFO:
				logger.info(validationMessage.getMessage());
				break;

			case WARNING:
				logger.warn(validationMessage.getMessage());
				break;

			case ERROR:
				logger.error(validationMessage.getMessage());
				hasErrors = true;
				break;

			default:
				break;
			}
		}
		if (hasErrors) {
			throw new IllegalArgumentException("You have validation error messages, check logs for more details");
		}
	}
	
	
}
