package com.edraw.utils;

import com.edraw.Direction;
import com.edraw.Position;
import com.edraw.config.Distance;
import com.edraw.config.DistanceUnit;
import com.edraw.config.LaserAction;
import com.edraw.geom.Drawing;
import com.edraw.geom.Rectangle;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public abstract class GeometryUtils {

	private static Logger logger = LoggerFactory.getLogger(GeometryUtils.class);
	
	private GeometryUtils() {
	}

	public static Optional<Position> getIntersection(final Pair<Position, Position> line1, final Pair<Position, Position> line2, final boolean belong2Segment1) {
		if (det(line1, line2) == 0) {
			return Optional.absent();
		}
		final double colin = -1 * ((line1.getValue0().getY() - line1.getValue1().getY()) * (line2.getValue0().getX() - line2.getValue1().getX())
				- (line1.getValue0().getX() - line1.getValue1().getX()) * (line2.getValue0().getY() - line2.getValue1().getY()));
		final double alpha = ((line2.getValue0().getY() - line2.getValue1().getY()) * (line1.getValue0().getX() - line2.getValue0().getX())
				- (line2.getValue0().getX() - line2.getValue1().getX()) * (line1.getValue0().getY() - line2.getValue0().getY())) / colin;
		
		if (belong2Segment1 && (alpha < 0 || alpha > 1)) {
			return Optional.absent();
		}
		
		final double resultX = line1.getValue0().getX() - alpha * (line1.getValue0().getX() - line1.getValue1().getX());
		final double resultY = line1.getValue0().getY() - alpha * (line1.getValue0().getY() - line1.getValue1().getY());
		
		return Optional.of(getPosition(resultX, resultY, line1.getValue0().getUnit()));
//		return Optional.<Position>of(new RelativeFromPosition(line1.getValue0(), alpha * (line1.getValue0().getX() - line1.getValue1().getX()), alpha * (line1.getValue0().getY() - line1.getValue1().getY())));
	}
	
	public static boolean isInPolygon(final Position toTest, final Iterable<Position> polygon) {
		if (polygon == null || Iterables.size(polygon) < 3) {
			throw new IllegalArgumentException("Cannot evaluate if a position belongs to a polygon of " + Iterables.size(polygon) + " points");
		}
		final List<Triplet<Position, Position, Position>> triangles = Lists.newArrayList();
		for (int i = 0; i < Iterables.size(polygon); i++) {
			for (int j = i + 1; j < Iterables.size(polygon); j++) {
				for (int k = j + 1; k < Iterables.size(polygon); k++) {
					if (i != j && i != k && j != k) {
						triangles.add(Triplet.with(Iterables.get(polygon, i), Iterables.get(polygon, j), Iterables.get(polygon, k)));
					}
				}
			}
		}
		
		for (final Triplet<Position, Position, Position> triangle : triangles) {
			if (isInTriangle(toTest, triangle.getValue0(), triangle.getValue1(), triangle.getValue2())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Position getBaryCentricPosition(final Iterable<Position> positions) {
		if (positions == null || Iterables.size(positions) < 2) {
			throw new IllegalArgumentException("Cannot calculate a barycentric position with a number of position < 2 (" + Iterables.size(positions) + ")");
		}
		
		final DistanceUnit distanceUnit = Iterables.get(positions, 0).getUnit();
		
		final double x = sum(Iterables.transform(positions, new Function<Position, Double>() {

			public Double apply(Position position) {
				return position.getX();
			}})) / Iterables.size(positions);
		final double y = sum(Iterables.transform(positions, new Function<Position, Double>() {

			public Double apply(Position position) {
				return position.getY();
			}})) / Iterables.size(positions);
		
		return new Position() {
			
			public double getY() {
				return y;
			}
			
			public double getX() {
				return x;
			}
			
			public DistanceUnit getUnit() {
				return distanceUnit;
			}
		};
	}
	
	private static double sum(final Iterable<Double> values) {
		double result = 0;
		for (final Double value : values) {
			result+=value;
		}
		return result;
	}
	
	public static boolean isInRectangle(final Position toTest, final Position position1, final Position position2, final Position position3, final Position position4) {
		return isInTriangle(toTest, position1, position2, position3)
				|| isInTriangle(toTest, position1, position2, position4)
				|| isInTriangle(toTest, position1, position3, position4)
				|| isInTriangle(toTest, position2, position3, position4);
	}
	
	public static boolean isInTriangle(final Position toTest, final Position position1, final Position position2, final Position position3) {
		final Pair<Position, Position> segment1 = Pair.with(position1, position2);
		final Pair<Position, Position> segment2 = Pair.with(position1, position3);
		if (isParalell(segment1, segment2)) {
			throw new IllegalArgumentException("You are testing 3 points that are not a triangle");
		}
		
		double alpha = ((position2.getY() - position3.getY())*(toTest.getX() - position3.getX()) + (position3.getX() - position2.getX())*(toTest.getY() - position3.getY())) /
		        ((position2.getY() - position3.getY())*(position1.getX() - position3.getX()) + (position3.getX() - position2.getX())*(position1.getY() - position3.getY()));
		double beta = ((position3.getY() - position1.getY())*(toTest.getX() - position3.getX()) + (position1.getX() - position3.getX())*(toTest.getY() - position3.getY())) /
		       ((position2.getY() - position3.getY())*(position1.getX() - position3.getX()) + (position3.getX() - position2.getX())*(position1.getY() - position3.getY()));
		double gamma = 1.0f - alpha - beta;
		
		if (!(alpha >= 0 && beta >= 0 && gamma >= 0)) {
			if (logger.isDebugEnabled()) logger.debug("Position " + toTest + " is NOT in triangle " + position1 + ", " + position2 + ", " + position3 + " alpha=" + alpha + " and beta=" + beta + " gamma=" + gamma);
			return false;
		} else {
			if (logger.isDebugEnabled()) logger.debug("Position " + toTest + " is in triangle " + position1 + ", " + position2 + ", " + position3);
			return true;
		}
	}
	
	public static double det(final Pair<Position, Position> vector1, final Pair<Position, Position> vector2) {
		return (vector1.getValue0().getX() - vector1.getValue1().getX()) * (vector2.getValue0().getY() - vector2.getValue1().getY())
				- (vector1.getValue0().getY() - vector1.getValue1().getY()) * (vector2.getValue0().getX() - vector2.getValue1().getX());
	}
	
	
	public static boolean isParalell(final Pair<Position, Position> segment1, final Pair<Position, Position> segment2) {
		return det(segment1, segment2) == 0;
	}
	
	public static Position getOrthoPoint(final Position point1, final Position point2, final Distance distance, final Direction direction) {
		if (direction == null) {
			throw new IllegalStateException("Cannot move to a direction equals to 0");
		}
		
		final Distance vectorLength = getDistance(point1, point2, distance.getUnit());
		final double resultX1 = ((distance.getDistance() / vectorLength.getDistance()) * (point2.getY() - point1.getY()));
		final double resultY1 = ((distance.getDistance() / vectorLength.getDistance()) * (point1.getX() - point2.getX()));
		
		final double resultX2 = ((distance.getDistance() / vectorLength.getDistance()) * (point1.getY() - point2.getY()));
		final double resultY2 = ((distance.getDistance() / vectorLength.getDistance()) * (point2.getX() - point1.getX()));
		
		if (logger.isDebugEnabled()) logger.debug("[ORTHO_POINT] Direction='" + direction + "', Point1=[" + point1.getX() + ", " + point1.getY() + "], Point2=[" + point2.getX() + ", " + point2.getY() + "], DeltaX1=" + resultX1 + ", DeltaY1=" + resultY1 + " and DeltaX2=" + resultX2 + ", DeltaY2=" + resultY2);

		if (direction == Direction.forward) {
			if (resultX1 + resultY1 >= 0) {
				return new RelativeFromPosition(point1, resultX1, resultY1);
			} else if (resultX2 + resultY2 >= 0) {
				return new RelativeFromPosition(point1, resultX2, resultY2);
			}
		} else {
			if (resultX1 + resultY1 <= 0) {
				return new RelativeFromPosition(point1, resultX1, resultY1);
			} else if (resultX2 + resultY2 <= 0) {
				return new RelativeFromPosition(point1, resultX2, resultY2);
			}
		}
		throw new IllegalStateException("Cannot determine forward or backward vector with : Direction='" + direction + "', Point1=[" + point1.getX() + ", " + point1.getY() + "], Point2=[" + point2.getX() + ", " + point2.getY() + "], DeltaX1=" + resultX1 + ", DeltaY1=" + resultY1 + " and DeltaX2=" + resultX2 + ", DeltaY2=" + resultY2);
	}
	
	public static Pair<Position, Position> getRectangleEdges(final Position point1, final Position point2, final Distance distance, final Direction direction) {
		if (logger.isDebugEnabled()) logger.debug("[RECTANGLE] Direction='" + direction + "', Point1=[" + point1.getX() + ", " + point1.getY() + "], Point2=[" + point2.getX() + ", " + point2.getY() + "]");
		final Position nextEdge = getOrthoPoint(point1, point2, distance, direction);
		return Pair.<Position, Position>with(nextEdge, new RelativeFromPosition(nextEdge, point2.getX() - point1.getX(), point2.getY() - point1.getY()));
	}
	
	public static Position getTranslatedPoint(final Position origin, final Position point1, final Position point2) {
		return new RelativeFromPosition(origin, point2.getX() - point1.getX(), point2.getY() - point1.getY());
	}
	
	public static Distance getDistance(final Position point1, final Position point2, final DistanceUnit distanceUnit) {
		return new Distance(Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2)), distanceUnit);
	}
	
	public static Rectangle getBoundingRectangle(final String name, final String layer, final Iterable<Position> positions) {
		if (positions == null || Iterables.size(positions) < 1) {
			throw new IllegalArgumentException("Cannot find bounding rectangle without position");
		}
		double minX = 0.0;
		double maxX = 0.0;
		double minY = 0.0;
		double maxY = 0.0;
		
		for (final Position position : positions) {
			if (minX == 0) {
				minX = position.getX();
			}
			if (minY == 0) {
				minY = position.getY();
			}
			if (position.getX() < minX) {
				minX = position.getX();
			}
			if (position.getY() < minY) {
				minY = position.getY();
			}
			if (position.getX() > maxX) {
				maxX = position.getX();
			}
			if (position.getY() > maxY) {
				maxY = position.getY();
			}
		}
		
		final Position topLeft = getPosition(minX, minY, Iterables.getFirst(positions, null).getUnit());
		final Position topRight = getPosition(maxX, minY, Iterables.getFirst(positions, null).getUnit());
		final Position bottomRight = getPosition(maxX, maxY, Iterables.getFirst(positions, null).getUnit());
		final Position bottomLeft = getPosition(minX, maxY, Iterables.getFirst(positions, null).getUnit());
		
		final double width = getDistance(topLeft, topRight, topLeft.getUnit()).getDistance();
		final double height = getDistance(topLeft, bottomLeft, topLeft.getUnit()).getDistance();
		
		return new Rectangle() {
			
			public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
				return Collections.emptyList();
			}
			
			public String getName() {
				return name;
			}
			
			public String getLayer() {
				return layer;
			}
			
			public Position getCenter() {
				return getBaryCentricPosition(ImmutableList.<Position>of(topLeft, topRight, bottomRight, bottomLeft));
			}
			
			public Rectangle getBoundingRectangle() {
				return this;
			}
			
			public double getWidth() {
				return width;
			}
			
			public Position getTopRight() {
				return topRight;
			}
			
			public Position getTopLeft() {
				return topLeft;
			}
			
			public LaserAction getTopBorderAction() {
				return LaserAction.HIDDEN;
			}
			
			public LaserAction getRightBorderAction() {
				return LaserAction.HIDDEN;
			}
			
			public LaserAction getLeftBorderAction() {
				return LaserAction.HIDDEN;
			}
			
			public double getHeight() {
				return height;
			}
			
			public DistanceUnit getDimensionsUnit() {
				return topLeft.getUnit();
			}
			
			public Position getBottomRight() {
				return bottomRight;
			}
			
			public Position getBottomLeft() {
				return bottomLeft;
			}
			
			public LaserAction getBottomBorderAction() {
				return LaserAction.HIDDEN;
			}

			public Iterable<Drawing> getSubDrawings() {
				return Collections.emptyList();
			}
		};
	}
	
	private static Position getPosition(final double x, final double y, final DistanceUnit distanceUnit) {
		return new Position() {
			
			public double getY() {
				return y;
			}
			
			public double getX() {
				return x;
			}
			
			public DistanceUnit getUnit() {
				return distanceUnit;
			}
			
			public String toString() {
				return new StringBuilder("Position[").append(getX()).append(distanceUnit.getSymbol()).append(", ").append(getY()).append(distanceUnit.getSymbol()).append("]").toString();
			}
			
		};
	}
	
}
