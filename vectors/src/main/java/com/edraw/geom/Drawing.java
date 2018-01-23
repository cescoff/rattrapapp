package com.edraw.geom;

import com.edraw.Position;

public interface Drawing {

	public String getName();
	
	public String getLayer();
	
	public Position getCenter();
	
	public Rectangle getBoundingRectangle();
	
	public Iterable<Drawing> getSubDrawings(final Iterable<String> activeLayers);
	
	public Iterable<Drawing> getSubDrawings();
	
}
