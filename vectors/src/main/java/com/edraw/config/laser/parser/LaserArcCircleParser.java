package com.edraw.config.laser.parser;

import com.edraw.*;
import com.edraw.config.DistanceUnit;
import com.edraw.config.LaserAction;
import com.edraw.config.laser.LaserArcCircle;
import com.edraw.geom.*;
import com.edraw.utils.GeometryUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

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

        final Double radius = context.evaluate(drawing.getRadius(), Double.class);

        return new LateBindingArcPath(LaserAction.valueOf(drawing.getAction()), drawing.getName(), drawing.getLayer(), radius, context.getActiveLayers(drawing), pointC.get(), pointC.get(), pointA.get(), pointB.get(), context);
    }

    private static class LateBindingArcPath implements Path {

        private final LaserAction action;

        private final String name;

        private final String layer;

        private final double radius;

        private final Iterable<Layer> extraActiveLayers;

        private final Position center;

        private final Position pointC;

        private final Position pointA;

        private final Position pointB;

        private final BluePrintContext context;

        public LateBindingArcPath(LaserAction action, String name, String layer, double radius, Iterable<Layer> extraActiveLayers, Position center, Position pointC, Position pointA, Position pointB, BluePrintContext context) {
            this.action = action;
            this.name = name;
            this.layer = layer;
            this.radius = radius;
            this.extraActiveLayers = extraActiveLayers;
            this.center = center;
            this.pointC = pointC;
            this.pointA = pointA;
            this.pointB = pointB;
            this.context = context;
        }

        @Override
        public Iterable<Point> getPoints() {
            final double angleAlpha = GeometryUtils.getAngleInRad(GeometryUtils.getVector(pointC, pointA, context.getDistanceUnit()), GeometryUtils.getVector(pointC, pointB, context.getDistanceUnit()));

            logger.info("Parsing arc circle '{}' with center '[{}, {}]', alpha {}rad, point1=[{}, {}], point2=[{}, {}]", name, pointC.getX(), pointC.getY(), angleAlpha, pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());

            final ImmutableList.Builder<Position> path = ImmutableList.builder();

            final double caDistance = GeometryUtils.getVector(pointA, pointC, context.getDistanceUnit()).getLength().getDistance();
            final Position start = GeometryUtils.getPosition((radius / caDistance) * (pointA.getX() - pointC.getX()) + pointC.getX(), (radius / caDistance) * (pointA.getY() - pointC.getY()) + pointC.getY(), context.getDistanceUnit());
            path.add(start);
            logger.debug("[{}] Adding position ({}, {})", name, start.getX(), start.getY());

            double angleTeta = 1 / radius;

            Position current = start;
            while (GeometryUtils.getDistance(current, pointB, context.getDistanceUnit()).getDistance() > 1) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[{}] Distance to target {}{}", name, GeometryUtils.getDistance(current, pointB, context.getDistanceUnit()).getDistance(), context.getDistanceUnit().getSymbol());
                }
                final double xV = ((radius * Math.cos(angleTeta) + radius * Math.sin(angleTeta) * ((pointA.getY() - pointC.getY()) / (pointA.getX() - pointC.getX()))) / GeometryUtils.getVector(pointC, pointA, context.getDistanceUnit()).getLength().getDistance()) * (pointA.getX() - pointC.getX()) + pointC.getX();
                final double yV = ((radius * Math.sin(angleTeta) - radius * Math.cos(angleTeta) * ((pointA.getY() - pointC.getY()) / (pointA.getX() - pointC.getX()))) / GeometryUtils.getVector(pointC, pointA, context.getDistanceUnit()).getLength().getDistance()) * (pointA.getX() - pointC.getX()) + pointC.getY();;

                current = GeometryUtils.getPosition(xV, yV, context.getDistanceUnit());

                logger.debug("[{}] Adding position ({}, {})", name, xV, yV);

                path.add(current);

                angleTeta = angleTeta + (1 / radius);
            }

            final double cbDistance = GeometryUtils.getVector(pointB, pointC, context.getDistanceUnit()).getLength().getDistance();
            final Position end = GeometryUtils.getPosition((radius / cbDistance) * (pointB.getX() - pointC.getX()) + pointC.getX(), (radius / cbDistance) * (pointB.getY() - pointC.getY()) + pointC.getY(), context.getDistanceUnit());
            path.add(end);
            logger.debug("[{}] Adding position ({}, {})", name, end.getX(), end.getY());

            return Iterables.transform(path.build(), new Function<Position, Point>() {

                @Override
                public Point apply(final Position position) {
                    return new Point() {
                        @Override
                        public String getName() {
                            return null;
                        }

                        @Override
                        public Layer getLayer() {
                            return new Layer() {
                                @Override
                                public String getName() {
                                    return layer;
                                }

                                @Override
                                public boolean isActive() {
                                    return true;
                                }
                            };
                        }

                        @Override
                        public Iterable<Layer> getExtraLayers() {
                            return Collections.emptyList();
                        }

                        @Override
                        public Position getCenter() {
                            return position;
                        }

                        @Override
                        public Rectangle getBoundingRectangle() {
                            return null;
                        }

                        @Override
                        public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
                            return Collections.emptyList();
                        }

                        @Override
                        public Iterable<Drawing> getSubDrawings() {
                            return Collections.emptyList();
                        }
                    };
                }
            });
        }

        @Override
        public LaserAction getBorderAction() {
            return action;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Layer getLayer() {
            return new Layer() {
                @Override
                public String getName() {
                    return layer;
                }

                @Override
                public boolean isActive() {
                    return true;
                }
            };
        }

        @Override
        public Iterable<Layer> getExtraLayers() {
            return extraActiveLayers;
        }

        @Override
        public Position getCenter() {
            return center;
        }

        @Override
        public Rectangle getBoundingRectangle() {
            return null;
        }

        @Override
        public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
            return Collections.emptyList();
        }

        @Override
        public Iterable<Drawing> getSubDrawings() {
            return Collections.emptyList();
        }
    }

}
