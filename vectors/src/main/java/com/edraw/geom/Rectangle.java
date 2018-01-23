package com.edraw.geom;

import com.edraw.Position;
import com.edraw.config.DistanceUnit;
import com.edraw.config.LaserAction;

public interface Rectangle extends Drawing {

	public Position getTopLeft();
	
	public Position getTopRight();
	
	public Position getBottomRight();
	
	public Position getBottomLeft();
	
	public double getWidth();
	
	public double getHeight();
	
	public DistanceUnit getDimensionsUnit();
	
	public LaserAction getLeftBorderAction();
	
	public LaserAction getRightBorderAction();
	
	public LaserAction getTopBorderAction();
	
	public LaserAction getBottomBorderAction();
	
}
