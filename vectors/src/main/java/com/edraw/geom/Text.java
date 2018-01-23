package com.edraw.geom;

import com.edraw.config.LaserAction;

public interface Text extends Drawing {

	public int getSize();
	
	public String getText();
	
	public LaserAction getAction();
	
}
