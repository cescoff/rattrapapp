package com.edraw;

import com.edraw.config.Distance;
import com.edraw.config.DistanceUnit;
import com.edraw.config.LaserAction;
import com.edraw.config.laser.LaserDrawing;
import com.edraw.config.laser.LaserPoint;
import com.edraw.geom.Circle;
import com.edraw.geom.Path;
import com.edraw.geom.Text;

public interface BluePrintContext {

    public Position resolvePosition(final String name, final PositionType type);

    public void registerPoint(final LaserDrawing drawing, final Position position);

    public Text registerText(final LaserDrawing drawing, final String layer, final Position center, final String text, final int size, final DistanceUnit distanceUnit, final LaserAction action);

    public Path registerPath(final LaserDrawing drawing, final Iterable<Position> points, final LaserAction borderAction);

    public Circle registerCircle(final LaserDrawing drawing, final Position center, final Distance radius, final LaserAction borderAction);

    public <T> T evaluate(final String expression, Class<T> dataType) throws Exception;

    public String print(final String expression) throws Exception;

    public DistanceUnit getDistanceUnit();

}
