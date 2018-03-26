package com.edraw.config.laser.parser;

import com.edraw.BluePrintContext;
import com.edraw.Position;
import com.edraw.PositionType;
import com.edraw.config.LaserAction;
import com.edraw.config.laser.LaserArcCircle;
import com.edraw.geom.Drawing;
import com.edraw.utils.GeometryUtils;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaserArcCircleParser implements LaserDrawingParser<LaserArcCircle> {

    private static final Logger logger = LoggerFactory.getLogger(LaserArcCircleParser.class);

    @Override
    public Drawing handle(LaserArcCircle drawing, BluePrintContext context) throws Exception {
        final Position pointC = context.resolvePosition(drawing.getCenter().getName(), PositionType.CENTER);
        final Position pointA = context.resolvePosition(drawing.getPoint1().getName(), PositionType.CENTER);
        final Position pointB = context.resolvePosition(drawing.getPoint1().getName(), PositionType.CENTER);

        final double angleAlpha = GeometryUtils.getAngleInRad(GeometryUtils.getVector(pointC, pointA, context.getDistanceUnit()), GeometryUtils.getVector(pointC, pointB, context.getDistanceUnit()));

        final double direction = (-1) * (angleAlpha / Math.abs(angleAlpha));

        final Double radius = context.evaluate(drawing.getRadius(), Double.class);

        logger.info("Parsing arc circle '{}' with center '[{}, {}]', alpha {}rad, direction {}", drawing.getName(), pointC.getX(), pointC.getY(), angleAlpha, direction);

        final ImmutableList.Builder<Position> path = ImmutableList.builder();
        path.add(pointA);

        double angleTeta = direction / radius;

        while (Math.abs(angleTeta) < Math.abs(angleAlpha)) {
            final double xV = 0.0;
            final double yV = 0.0;

            path.add(GeometryUtils.getPosition(xV, yV, context.getDistanceUnit()));

            angleTeta = angleTeta + (direction / radius);
        }

        path.add(pointB);

        return context.registerPath(drawing, path.build(), LaserAction.valueOf(drawing.getAction()));
    }



}
