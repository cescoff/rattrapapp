package com.edraw.config.laser.validation;

import com.edraw.VarContext;
import com.edraw.config.laser.LaserPath;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class LaserPathValidator implements LaserValidator<LaserPath> {


    @Override
    public Iterable<ValidationMessage> getValidationErrorMessages(VarContext varContext, final LaserPath laserDrawing) {
        final ImmutableList.Builder<ValidationMessage> result = ImmutableList.builder();
        if (StringUtils.isNotEmpty(laserDrawing.getFiletRadius())) {
            if (laserDrawing.getPoints().size() <= 2) {
                result.add(new ValidationMessage() {
                    @Override
                    public ValidationMessageLevel getLevel() {
                        return ValidationMessageLevel.ERROR;
                    }

                    @Override
                    public String getMessage() {
                        return "Path '" + laserDrawing.getName() + "' has a filet radius but only " + laserDrawing.getPoints().size() + " points, at least 3 points are required to create filets";
                    }
                });
            }
        }
        return result.build();
    }
}
