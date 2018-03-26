package com.edraw.config.laser.validation;

import com.edraw.VarContext;
import com.edraw.config.LaserAction;
import com.edraw.config.laser.LaserArcCircle;
import com.edraw.config.laser.LaserPoint;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

public class LaserArcCircleValidator implements LaserValidator<LaserArcCircle> {

    @Override
    public Iterable<ValidationMessage> getValidationErrorMessages(VarContext varContext, LaserArcCircle laserArcCircle) {
        final ImmutableList.Builder<ValidationMessage> result = ImmutableList.builder();

        if (laserArcCircle.getCenter() == null) {
            result.add(getErrorValidationMessage("Missing attribute 'center'"));
        } else {
            result.addAll(getPointValidationMessages(laserArcCircle.getPoint1(), "point1"));
        }

        if (laserArcCircle.getPoint1() == null) {
            result.add(getErrorValidationMessage("Missing attribute 'point1'"));
        } else {
            result.addAll(getPointValidationMessages(laserArcCircle.getPoint1(), "point1"));
        }

        if (laserArcCircle.getPoint2() == null) {
            result.add(getErrorValidationMessage("Missing attribute 'point2'"));
        } else {
            result.addAll(getPointValidationMessages(laserArcCircle.getPoint2(), "point2"));
        }

        if (StringUtils.isEmpty(laserArcCircle.getAction())) {
            result.add(getErrorValidationMessage("Attribute 'action' is mandatory and should be one of '" + Joiner.on("', '").join(Lists.newArrayList(LaserAction.values())) + "'"));
        } else {
            try {
                LaserAction.valueOf(laserArcCircle.getAction());
            } catch (Throwable t) {
                result.add(getErrorValidationMessage("Attribute 'action' is invalid and should be one of '" + Joiner.on("', '").join(Lists.newArrayList(LaserAction.values())) + "'"));
            }
        }

        return null;
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
