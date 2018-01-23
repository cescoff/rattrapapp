package com.edraw;

import com.edraw.geom.BluePrint;
import com.edraw.geom.Drawing;

public interface Transformation {

	public Iterable<BluePrint> transform(final Iterable<BluePrint> bluePrint);
	
//	public Drawing transform(final Drawing drawing);
	
}
