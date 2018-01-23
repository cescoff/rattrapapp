package com.edraw.geom;

import com.edraw.config.DistanceUnit;

public interface BluePrint {

	public DistanceUnit getDefaultDistanceUnit();
	
	public Point getPoint(final String name);
	
	public Iterable<Drawing> getDrawings();
	
	public Iterable<String> getLayerNames();
	
	public Iterable<Drawing> getLayerDrawings(final Iterable<String> layerNames);
	
	public Rectangle getBoundingRectangle();
	
}
