package com.edraw.config.laser.validation;

import com.edraw.VarContext;
import com.edraw.config.laser.LaserDrawing;

public interface LaserValidator<L extends LaserDrawing> {

	public Iterable<ValidationMessage> getValidationErrorMessages(final VarContext varContext, final L laserDrawing);
	
}
