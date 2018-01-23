package com.edraw;

import java.io.ByteArrayOutputStream;

import com.rattrap.utils.JAXBUtils;
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

	private final Resource variables;
	
	private final Resource template;
	
	private final Resource outputConfig;

	public LaserPlanGenerator(Resource variables, Resource template,
			Resource outputConfig) {
		super();
		this.variables = variables;
		this.template = template;
		this.outputConfig = outputConfig;
	}
	
	public Iterable<Resource> getLaserPlan() throws Exception {
		final LaserBluePrint laserBluePrint = JAXBUtils.unmarshal(LaserBluePrint.class, this.template.open());
		
		final VarContext varContext = new ConfigurableVariablesContext(this.variables);
		
		final Iterable<ValidationMessage> validationMessages = new ConfigurationValidator(varContext).getValidationErrorMessages(laserBluePrint);
		
		handleValidationMessages(validationMessages);
		
		final BluePrint bluePrint = new BluePrintParser(varContext).parse(laserBluePrint);
		
		final OutputConfig outputConfig = JAXBUtils.unmarshal(OutputConfig.class, this.outputConfig.open());
		
		final ImmutableList.Builder<Resource> result = ImmutableList.builder();
		
		for (final Output output : outputConfig.getOutputs()) {

			final Transformation transformation = TransformationFactory.getInstance().getTransformation(output.getTransformations(), bluePrint);
			final Iterable<BluePrint> outputBluePrints = transformation.transform(ImmutableList.of(bluePrint));
			
			final OutputFormat outputFormat = OutputFormat.valueOf(output.getOutputFormat());
			for (final String extension : outputFormat.getExtensions()) {
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
						result.add(new StringResource(resourceName, new String(outputStream.toByteArray())));
						position++;
					}
				} else {
					throw new Exception("OutputFormat '" + outputFormat + "' is not supported yet");
				}
			}
		}
		return result.build();
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
