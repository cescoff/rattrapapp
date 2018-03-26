package com.edraw.config.laser.parser;

import com.edraw.BluePrintContext;
import com.edraw.config.laser.LaserDrawing;
import com.edraw.geom.Drawing;

public interface LaserDrawingParser<L extends LaserDrawing> {

    public Drawing handle(final L drawing, final BluePrintContext context) throws Exception;

}
