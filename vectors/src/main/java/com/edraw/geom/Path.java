package com.edraw.geom;

import com.edraw.config.LaserAction;
import com.google.common.base.Optional;

public interface Path extends Drawing {

	public Iterable<Point> getPoints();
	
	public LaserAction getBorderAction();

	public Optional<LaserAction> getHatchAction();

	public Iterable<Path> getHatchPath();

}
