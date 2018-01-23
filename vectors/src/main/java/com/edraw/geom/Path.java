package com.edraw.geom;

import com.edraw.config.LaserAction;

public interface Path extends Drawing {

	public Iterable<Point> getPoints();
	
	public LaserAction getBorderAction();
	
}
