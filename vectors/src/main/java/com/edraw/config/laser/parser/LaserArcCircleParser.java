package com.edraw.config.laser.parser;

import com.edraw.*;
import com.edraw.config.LaserAction;
import com.edraw.config.laser.LaserArcCircle;
import com.edraw.geom.Drawing;
import com.edraw.utils.GeometryUtils;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaserArcCircleParser implements LaserDrawingParser<LaserArcCircle> {

    private static final Logger logger = LoggerFactory.getLogger(LaserArcCircleParser.class);

    @Override
    public Drawing handle(LaserArcCircle drawing, BluePrintContext context) throws Exception {
        final Optional<Position> pointC = context.resolvePosition(drawing.getCenter());
        final Optional<Position> pointA = context.resolvePosition(drawing.getPoint1());
        final Optional<Position> pointB = context.resolvePosition(drawing.getPoint2());

        if (!pointC.isPresent()) {
            throw new ValidationError(ErrorMessage.create("Cannot parse center position of arc named '" + drawing.getName() + "'"));
        }

        if (!pointA.isPresent()) {
            throw new ValidationError(ErrorMessage.create("Cannot parse position 1 of arc named '" + drawing.getName() + "' (" + drawing.getPoint1().getX() + ", " + drawing.getPoint1().getY() + ")"));
        }

        if (!pointB.isPresent()) {
            throw new ValidationError(ErrorMessage.create("Cannot parse position 2 of arc named '" + drawing.getName() + "' (" + drawing.getPoint1().getX() + ", " + drawing.getPoint1().getY() + ")"));
        }

        final double angleAlpha = GeometryUtils.getAngleInRad(GeometryUtils.getVector(pointC.get(), pointA.get(), context.getDistanceUnit()), GeometryUtils.getVector(pointC.get(), pointB.get(), context.getDistanceUnit()));

        final double direction = (-1) * (angleAlpha / Math.abs(angleAlpha));

        final Double radius = context.evaluate(drawing.getRadius(), Double.class);

        logger.info("Parsing arc circle '{}' with center '[{}, {}]', alpha {}rad, direction {}, point1=[{}, {}], point2=[{}, {}]", drawing.getName(), pointC.get().getX(), pointC.get().getY(), angleAlpha, direction, pointA.get().getX(), pointA.get().getY(), pointB.get().getX(), pointB.get().getY());

        final ImmutableList.Builder<Position> path = ImmutableList.builder();

        final double caDistance = GeometryUtils.getVector(pointA.get(), pointC.get(), context.getDistanceUnit()).getLength().getDistance();
        final Position start = GeometryUtils.getPosition((radius / caDistance) * (pointA.get().getX() - pointC.get().getX()) + pointC.get().getX(), (radius / caDistance) * (pointA.get().getY() - pointC.get().getY()) + pointC.get().getY(), context.getDistanceUnit());
        path.add(start);
        logger.debug("[{}] Adding position ({}, {})", drawing.getName(), start.getX(), start.getY());

        double angleTeta = direction / radius;

        while (Math.abs(angleTeta) < Math.abs(angleAlpha)) {
            final double xV = ((radius * Math.cos(angleTeta) + radius * Math.sin(angleTeta) * ((pointA.get().getY() - pointC.get().getY()) / (pointA.get().getX() - pointC.get().getX()))) / GeometryUtils.getVector(pointC.get(), pointA.get(), context.getDistanceUnit()).getLength().getDistance()) * (pointA.get().getX() - pointC.get().getX()) + pointC.get().getX();
            final double yV = ((radius * Math.sin(angleTeta) - radius * Math.cos(angleTeta) * ((pointA.get().getY() - pointC.get().getY()) / (pointA.get().getX() - pointC.get().getX()))) / GeometryUtils.getVector(pointC.get(), pointA.get(), context.getDistanceUnit()).getLength().getDistance()) * (pointA.get().getX() - pointC.get().getX()) + pointC.get().getY();;

            final Position pathElement = GeometryUtils.getPosition(xV, yV, context.getDistanceUnit());

            logger.debug("[{}] Adding position ({}, {})", drawing.getName(), xV, yV);

            path.add(pathElement);

            angleTeta = angleTeta + (direction / radius);
        }

        final double cbDistance = GeometryUtils.getVector(pointB.get(), pointC.get(), context.getDistanceUnit()).getLength().getDistance();
        final Position end = GeometryUtils.getPosition((radius / cbDistance) * (pointB.get().getX() - pointC.get().getX()) + pointC.get().getX(), (radius / cbDistance) * (pointB.get().getY() - pointC.get().getY()) + pointC.get().getY(), context.getDistanceUnit());
        path.add(end);
        logger.debug("[{}] Adding position ({}, {})", drawing.getName(), end.getX(), end.getY());

        return context.registerPath(drawing, path.build(), LaserAction.valueOf(drawing.getAction()));
    }



}
