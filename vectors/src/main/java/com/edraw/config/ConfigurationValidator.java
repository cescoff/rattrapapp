package com.edraw.config;

import com.edraw.VarContext;
import com.edraw.config.laser.LaserBluePrint;
import com.edraw.config.laser.LaserDrawing;
import com.edraw.config.laser.validation.LaserValidator;
import com.edraw.config.laser.validation.ValidationMessage;
import com.edraw.config.laser.validation.ValidationMessageLevel;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Collections;
import java.util.Map;

public class ConfigurationValidator {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationValidator.class);
	
	private final Map<String, LaserValidator<?>> cachedValidators = Maps.newHashMap();
	
	private final VarContext varContext;
	
	public ConfigurationValidator(VarContext varContext) {
		this.varContext = varContext;
	}

	public Iterable<ValidationMessage> getValidationErrorMessages(final LaserBluePrint laserBluePrint) {
		final ImmutableList.Builder<ValidationMessage> result = ImmutableList.builder();

		for (final LaserDrawing laserDrawing : laserBluePrint.getDrawings()) {
			result.addAll(Iterables.transform(getValidator(laserDrawing).getValidationErrorMessages(varContext, laserDrawing), getMessageWrapper(laserDrawing)));
		}
		
		return result.build();
	}

	private Function<ValidationMessage, ValidationMessage> getMessageWrapper(final LaserDrawing laserDrawing) {
		return new Function<ValidationMessage, ValidationMessage>() {

			public ValidationMessage apply(ValidationMessage validationMessage) {
				return wrapMessage(laserDrawing, validationMessage);
			}
			
		};
	}
	
	private ValidationMessage wrapMessage(final LaserDrawing laserDrawing, final ValidationMessage message) {
		final String newMessage = new StringBuilder("[").
										append(StringUtils.remove(laserDrawing.getClass().getSimpleName(), "Laser")).
										append("/").append(laserDrawing.getLayer()).append("/").append(laserDrawing.getName()).append("] : ").append(message.getMessage()).toString();
		return new ValidationMessage() {
			
			public String getMessage() {
				return newMessage;
			}
			
			public ValidationMessageLevel getLevel() {
				return message.getLevel();
			}
		};
	}
	
	private <L extends LaserDrawing> LaserValidator<L> getValidator(final L drawing) {
		final String validatorClassName = drawing.getClass().getPackage().getName() + ".validation." + drawing.getClass().getSimpleName() + "Validator";
		
		try {
			final Class<?> validatorClass = Class.forName(validatorClassName);
			final Object validator = validatorClass.newInstance();
			if (!(validator instanceof LaserValidator)) {
				throw new IllegalStateException("Invalid validator class '" + validatorClassName + "' does not implement '" + LaserValidator.class.getName() + "'");
			}
			this.cachedValidators.put(drawing.getClass().getName(), (LaserValidator<?>) validator);
			logger.info("Validator for class '" + drawing.getClass().getName() + "' is '" + validatorClassName + "'");
		} catch (ClassNotFoundException e) {
			logger.debug("No validator for class '" + drawing.getClass().getName() + "'");
			this.cachedValidators.put(drawing.getClass().getName(), new YesValidator<LaserDrawing>());
		} catch (InstantiationException e) {
			throw new IllegalStateException("Invalid validator class '" + validatorClassName + "' has no public default constructor", e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Invalid validator class '" + validatorClassName + "' has no public default constructor", e);
		}
		return (LaserValidator<L>) this.cachedValidators.get(drawing.getClass().getName());
	}
	
	private static class YesValidator<L extends LaserDrawing> implements LaserValidator<L> {

		public Iterable<ValidationMessage> getValidationErrorMessages(VarContext varContext, L laserDrawing) {
			return Collections.emptyList();
		}
		
	}
	
}
