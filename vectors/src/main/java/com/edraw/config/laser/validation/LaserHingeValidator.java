package com.edraw.config.laser.validation;

import com.edraw.VarContext;
import com.edraw.config.LaserAction;
import com.edraw.config.laser.LaserHinge;
import com.edraw.config.laser.LaserPoint;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

public class LaserHingeValidator implements LaserValidator<LaserHinge> {

	public LaserHingeValidator() {
	}

	public Iterable<ValidationMessage> getValidationErrorMessages(VarContext varContext,
			LaserHinge laserHinge) {
		final ImmutableList.Builder<ValidationMessage> result = ImmutableList.builder();
		
		if (laserHinge.getPoint1() == null) {
			result.add(getErrorValidationMessage("Missing attribute 'point1'"));
		} else {
			result.addAll(getPointValidationMessages(laserHinge.getPoint1(), "point1"));
		}

		if (laserHinge.getPoint2() == null) {
			result.add(getErrorValidationMessage("Missing attribute 'point2'"));
		} else {
			result.addAll(getPointValidationMessages(laserHinge.getPoint2(), "point2"));
		}

		if (StringUtils.isEmpty(laserHinge.getAction())) {
			result.add(getErrorValidationMessage("Attribute 'action' is mandatory and should be one of '" + Joiner.on("', '").join(Lists.newArrayList(LaserAction.values())) + "'"));
		} else {
			try {
				LaserAction.valueOf(laserHinge.getAction());
			} catch (Throwable t) {
				result.add(getErrorValidationMessage("Attribute 'action' is invalid and should be one of '" + Joiner.on("', '").join(Lists.newArrayList(LaserAction.values())) + "'"));
			}
		}
		
		if (StringUtils.isEmpty(laserHinge.getAxleDistance1())) {
			result.add(getErrorValidationMessage("Attribute 'axle-distance1' is mandatory"));
		}
		
		if (StringUtils.isEmpty(laserHinge.getAxleDistance2())) {
			result.add(getErrorValidationMessage("Attribute 'axle-distance2' is mandatory"));
		}
		
		if (StringUtils.isNotEmpty(laserHinge.getHeadSpan())) {
			if (StringUtils.isNotEmpty(laserHinge.getHeadSpan1())) {
				result.add(getErrorValidationMessage("You have specified 'head-span' and 'head-span1' which is not compatible, you must specify weither 'head-span' or 'head-span1' AND 'head-span2'"));
			}
			if (StringUtils.isNotEmpty(laserHinge.getHeadSpan2())) {
				result.add(getErrorValidationMessage("You have specified 'head-span' and 'head-span2' which is not compatible, you must specify weither 'head-span' or 'head-span1' AND 'head-span2'"));
			}
			result.addAll(getVarValidationMessages(varContext, "head-span", laserHinge.getHeadSpan()));
		} else {
			if (StringUtils.isEmpty(laserHinge.getHeadSpan1())) {
				result.add(getErrorValidationMessage("You have not specified 'head-span' and 'head-span1' is empty, you must specify weither 'head-span' or 'head-span1' AND 'head-span2'"));
			} else {
				result.addAll(getVarValidationMessages(varContext, "head-span1", laserHinge.getHeadSpan1()));
			}
			if (StringUtils.isEmpty(laserHinge.getHeadSpan2())) {
				result.add(getErrorValidationMessage("You have not specified 'head-span' and 'head-span2' is empty, you must specify weither 'head-span' or 'head-span1' AND 'head-span2'"));
			} else {
				result.addAll(getVarValidationMessages(varContext, "head-span2", laserHinge.getHeadSpan2()));
			}
		}
		
		return result.build();
	}

	private Iterable<ValidationMessage> getVarValidationMessages(final VarContext varContext, final String attributeName, final String varValue) {
		try {
			varContext.evaluate(varValue);
		} catch (Exception e) {
			return ImmutableList.of(getErrorValidationMessage("Attribute '" + attributeName + "' has an invalid var value '" + varValue + "'"));
		}
		return Collections.emptyList();
	}
	
	private Iterable<ValidationMessage> getPointValidationMessages(final LaserPoint point, final String name) {
		final ImmutableList.Builder<ValidationMessage> result = ImmutableList.builder();
		
		if (StringUtils.isEmpty(point.getX())) {
			result.add(getErrorValidationMessage("Point '" + name + "' has no x attribute defined"));
		}
		if (StringUtils.isEmpty(point.getY())) {
			result.add(getErrorValidationMessage("Point '" + name + "' has no y attribute defined"));
		}
		return result.build();
	}
	
	private ValidationMessage getInfoValidationMessage(final String message) {
		return getMessage(ValidationMessageLevel.INFO, message);
	}
	
	private ValidationMessage getWarningValidationMessage(final String message) {
		return getMessage(ValidationMessageLevel.WARNING, message);
	}
	
	private ValidationMessage getErrorValidationMessage(final String message) {
		return getMessage(ValidationMessageLevel.ERROR, message);
	}
	
	private ValidationMessage getMessage(final ValidationMessageLevel level, final String message) {
		return new ValidationMessage() {
			
			public String getMessage() {
				return message;
			}
			
			public ValidationMessageLevel getLevel() {
				return level;
			}
		};
	}
	
}
