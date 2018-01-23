package com.edraw;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rattrap.utils.JAXBUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

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

public class LaserPlanGenerator {

    private final Map<String, String> variablesTranslations;

	private final Resource variables;
	
	private final Resource template;
	
	private final Resource outputConfig;

	private final boolean skipOutputTransformations;

	public LaserPlanGenerator(Map<String, String> variablesTranslations,
                              Resource variables, Resource template,
                              Resource outputConfig,
                              boolean skipOutputTransformations) {
		super();
		this.variablesTranslations = variablesTranslations;
		this.variables = variables;
		this.template = template;
		this.outputConfig = outputConfig;
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
		return result.build();
	}

	private Resource getReadmeHtml(final VarContext varContext) throws Exception {
	    final StringBuilder result = new StringBuilder("<html>\n\n\t<head>\n\t\t<title>Project summary</title>\n\t</head>\n\n\t<body>\n\n\t\t<h1>Variables and values:</h1>\n\n\t\t<ul>\n");
	    for (final String varName : this.variablesTranslations.keySet()) {
	        result.append("\t\t\t<li>").append(this.variablesTranslations.get(varName)).append(" : ").append(varContext.evaluate(varName)).append("</li>\n");
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
		final Logger logger = Logger.getLogger(Test.class);
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
