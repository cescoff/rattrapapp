package com.edraw.geom;

import com.edraw.Position;
import com.edraw.config.DistanceUnit;
import com.edraw.config.LaserAction;

public interface Circle extends Drawing {

	public double getRadius();
	
	public DistanceUnit getRadiusUnit();
	
	public Position getRadiusTop();
	
	public Position getRadiusBottom();
	
	public Position getRadiusLeft();
	
	public Position getRadiusRight();
	
	public LaserAction getBorderAction();
	
}
