package com.edraw;

import com.edraw.config.Distance;
import com.edraw.config.DistanceUnit;
import com.edraw.config.LaserAction;
import com.edraw.config.laser.*;
import com.edraw.config.laser.parser.LaserDrawingParser;
import com.edraw.geom.*;
import com.edraw.impl.BasicLayer;
import com.edraw.impl.BluePrintUtils;
import com.edraw.impl.DynamicLayer;
import com.edraw.impl.EmptyRectangle;
import com.edraw.utils.GeometryUtils;
import com.edraw.utils.RelativeFromPosition;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BluePrintParser {

	private static final Logger logger = LoggerFactory.getLogger(BluePrintParser.class);
	
	// Synthax is <DRAWING_NAME>/<TopLeft|Center>/<+|->[0-9]+<mm|px>
	
	private static Pattern RELATIVE_POSTION_PATTERN = Pattern.compile("(" + Constants.NAME_PATTERN + ")//([A-Za-z]+)//([\\+\\-0-9a-zA-Z\\$\\{\\}\\/\\*_\\(\\)\\.\\s]+)");

	private final VarContext varContext;
	
	private final PositionContext positionContext;
	
	private DistanceUnit defaultDistanceUnit = DistanceUnit.MILLIMETERS;
	
	public BluePrintParser(VarContext varContext) {
		super();
		this.varContext = varContext;
		this.positionContext = new PositionContext();
	}

	public BluePrint parse(final LaserBluePrint laserBluePrint) {
		this.defaultDistanceUnit = DistanceUnit.readStringValue(laserBluePrint.getDistanceUnit());

		final List<Drawing> drawings = Lists.newArrayList();
		
		for (final LaserDrawing laserDrawing : laserBluePrint.getDrawings()) {
		    final Optional<LaserDrawingParser<LaserDrawing>> parserOptional = getParser(laserDrawing);

		    if (parserOptional.isPresent()) {
                try {
		            drawings.add(parserOptional.get().handle(laserDrawing, getContext()));
                } catch (Throwable t) {
                    throw new IllegalStateException("Cannot parse drawing named '" + laserDrawing.getName() + "' in layer '" + laserDrawing.getLayer() + "'", t);
                }
            } else {
                try {
                    final Optional<Point> point = registerPoint(laserDrawing);
                    if (point.isPresent()) {
                        drawings.add(point.get());
                    }
                    final Optional<Drawing> rectangle = parseRectangle(laserDrawing);
                    if (rectangle.isPresent()) {
                        drawings.add(rectangle.get());
                    } else {
                        final Optional<Circle> circle = parseCircle(laserDrawing);
                        if (circle.isPresent()) {
                            drawings.add(circle.get());
                        } else {
                            final Optional<Path> path = parsePath(laserDrawing);
                            if (path.isPresent()) {
                                drawings.add(path.get());
                            } else {
                                final Optional<Path> crenel = parseCrenel(laserDrawing);
                                if (crenel.isPresent()) {
                                    drawings.add(crenel.get());
                                } else {
                                    final Optional<Text> text = parseText(laserDrawing);
                                    if (text.isPresent()) {
                                        drawings.add(text.get());
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable t) {
                    throw new IllegalStateException("Cannot parse drawing named '" + laserDrawing.getName() + "' in layer '" + laserDrawing.getLayer() + "'", t);
                }
            }
		}
		
		final Iterable<Position> allPositions = BluePrintUtils.getAllPositions(drawings);
		final Rectangle boundingRectangle = GeometryUtils.getBoundingRectangle(null, null, allPositions);



		return BluePrintUtils.getBluePrint(defaultDistanceUnit, drawings);

/*		return new BluePrint() {
			
			public Iterable<Drawing> getDrawings() {
				return drawings;
			}

			public DistanceUnit getDefaultDistanceUnit() {
				return defaultDistanceUnit;
			}

			public Iterable<String> getLayerNames() {
				final Set<String> alreadyAddedNames = Sets.newHashSet();
				final ImmutableList.Builder<String> result = ImmutableList.builder();
				for (final Drawing drawing : getDrawings()) {
					if (!alreadyAddedNames.contains(drawing.getLayer()) && drawing.getLayer().isActive()) {
						result.add(drawing.getLayer().getName());
						alreadyAddedNames.add(drawing.getLayer().getName());
					}
					for (final Layer extraLayer : drawing.getExtraLayers()) {
						if (extraLayer.isActive()) {
							result.add(extraLayer.getName());
							alreadyAddedNames.add(extraLayer.getName());
						}
					}
				}
				return result.build();
			}

			public Iterable<Drawing> getLayerDrawings(final Iterable<String> layerNames) {
				if (layerNames == null || Iterables.isEmpty(layerNames)) {
					return getDrawings();
				}
				final ImmutableList.Builder<Drawing> result = ImmutableList.builder();
				final Set<String> acceptedNames = Sets.newHashSet(layerNames);
				for (final Drawing drawing : getDrawings()) {
				    logger.info("Drawing '{}' is associated to layer '{}'", drawing.getName(), drawing.getLayer().getName());
					if (acceptedNames.contains(drawing.getLayer().getName())) {
						result.add(drawing);
					}
					for (final Layer extraLayer : drawing.getExtraLayers()) {
                        logger.info("Drawing '{}' is associated to layer '{}'", drawing.getName(), extraLayer.getName());
						if (extraLayer.isActive() && acceptedNames.contains(extraLayer.getName())) {
							result.add(drawing);
						}
					}
				}
				return result.build();
			}

			public Point getPoint(final String name) {
				final Position position = positionContext.getPosition(name, PositionType.CENTER);
				return new Point() {

					public String getName() {
						return name;
					}

					public Layer getLayer() {
						return null;
					}

					@Override
					public Iterable<Layer> getExtraLayers() {
						return Collections.emptyList();
					}

					public Position getCenter() {
						return position; 
					}

					public Rectangle getBoundingRectangle() {
						return new EmptyRectangle(position);
					}

					public Iterable<Drawing> getSubDrawings(
							Iterable<String> activeLayers) {
						return Collections.emptyList();
					}

					public Iterable<Drawing> getSubDrawings() {
						return Collections.emptyList();
					}
				};
			}

			public Rectangle getBoundingRectangle() {
				return boundingRectangle;
			}
		};*/
	}
	
	private Optional<Point> registerPoint(final LaserDrawing laserDrawing) {
		if (!(laserDrawing instanceof LaserPoint)) {
			return Optional.absent();
		}
		final LaserPoint laserPoint = (LaserPoint) laserDrawing;
		
		final Optional<Quartet<String, PositionType, Double, DistanceUnit>> xRelative = parseRelativePosition(laserPoint.getX());
		final Optional<Quartet<String, PositionType, Double, DistanceUnit>> yRelative = parseRelativePosition(laserPoint.getY());
		
		final Position center = getPosition(xRelative, yRelative, laserPoint.getX(), laserPoint.getY());
		
		return Optional.of(positionContext.registerPoint(laserDrawing.getName(), laserDrawing.getLayer(), center));
	}
	
	private Optional<Path> parsePath(final LaserDrawing laserDrawing) {
		if (!(laserDrawing instanceof LaserPath)) {
			return Optional.absent();
		}
		final LaserPath laserPath = (LaserPath) laserDrawing;
		
		final List<Position> points = Lists.newArrayList();
		
		for (final LaserPoint laserPoint : laserPath.getPoints()) {
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> xRelative = parseRelativePosition(laserPoint.getX());
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> yRelative = parseRelativePosition(laserPoint.getY());
			
			points.add(getPosition(xRelative, yRelative, laserPoint.getX(), laserPoint.getY()));
		}

		final Optional<LaserAction> hatchAction;
		if (StringUtils.isNotEmpty(laserPath.getHatchAction())) {
		    hatchAction = Optional.of(LaserAction.valueOf(laserPath.getHatchAction()));
        } else {
		    hatchAction = Optional.absent();
        }

		return Optional.of(positionContext.registerPath(laserDrawing.getName(), laserDrawing.getLayer(), Iterables.transform(Iterables.filter(laserDrawing.getExtraLayers(), GetActiveLayer(laserDrawing.getName())), LASER_LAYER_TO_STRING), points, defaultDistanceUnit, LaserAction.valueOf(laserPath.getAction()), hatchAction));
		
	}
	
	private Optional<Path> parseCrenel(final LaserDrawing laserDrawing) {
		if (!(laserDrawing instanceof LaserCrenel) && !(laserDrawing instanceof LaserHinge)) {
			return Optional.absent();
		}
		
		if (laserDrawing instanceof LaserCrenel) {
			final LaserCrenel laserCrenel = (LaserCrenel) laserDrawing;
			
			return Optional.<Path>of(new LateBindingCrenel(laserCrenel));
		} else {
			final LaserHinge laserHinge = (LaserHinge) laserDrawing;
			
			return Optional.<Path>of(new LateBindingCrenel(laserHinge));
		}
	}
	
	private Optional<Text> parseText(final LaserDrawing laserDrawing) {
		if (!(laserDrawing instanceof LaserText)) {
			return Optional.absent();
		}
		
		final LaserText laserText = (LaserText) laserDrawing;

		int size;
		try {
			size = new Long(Math.round(varContext.evaluate(laserText.getSize(), Double.class))).intValue();
		} catch (Exception e) {
			throw new IllegalStateException("Cannot parse expression '" + laserText.getSize() + "' on '" + laserText.getName() + "/" + laserText.getLayer() + "'");
		}
		
		final Optional<Quartet<String, PositionType, Double, DistanceUnit>> relativeX = parseRelativePosition(laserText.getX());
		final Optional<Quartet<String, PositionType, Double, DistanceUnit>> relativeY = parseRelativePosition(laserText.getY());
		
		final Position center = getPosition(relativeX, relativeY, laserText.getX(), laserText.getY());
		
		return Optional.of(this.positionContext.registerText(laserText.getName(), laserText.getLayer(), center, laserText.getText(), size, defaultDistanceUnit, LaserAction.valueOf(laserText.getAction())));
	}
	
	private Distance getDistance(final Position point1, final Position point2) {
		return new Distance(Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2)), point1.getUnit());
	}
	
	private Optional<Circle> parseCircle(final LaserDrawing laserDrawing) {
		if (!(laserDrawing instanceof LaserCircle)) {
			return Optional.absent();
		}
		final LaserCircle laserCircle = (LaserCircle) laserDrawing;
		
		final Distance radius = DistanceUnit.parse(varContext, laserCircle.getRadius(), defaultDistanceUnit);

		if (StringUtils.isEmpty(laserCircle.getCenterX()) || StringUtils.isEmpty(laserCircle.getCenterY())) {
			throw new IllegalStateException("Circle '" + laserCircle.getName() + "' has no reference Center position");
		}
		
		final Optional<Quartet<String, PositionType, Double, DistanceUnit>> xRelative = parseRelativePosition(laserCircle.getCenterX());
		final Optional<Quartet<String, PositionType, Double, DistanceUnit>> yRelative = parseRelativePosition(laserCircle.getCenterY());
		
		final Position center = getPosition(xRelative, yRelative, laserCircle.getCenterX(), laserCircle.getCenterY());
		
		
		return Optional.<Circle>of(positionContext.registerCircle(laserDrawing.getName(), laserDrawing.getLayer(), Iterables.transform(Iterables.filter(laserDrawing.getExtraLayers(), GetActiveLayer(laserDrawing.getName())), LASER_LAYER_TO_STRING), center, radius, LaserAction.valueOf(laserCircle.getAction())));
	}
	
	private Optional<Drawing> parseRectangle(final LaserDrawing laserDrawing) {
		if (!(laserDrawing instanceof LaserRectangle)) {
			return Optional.absent();
		}
		final LaserRectangle laserRectangle = (LaserRectangle) laserDrawing;
		
		final Distance width;
		final Distance height;
		
		final Position center;
		final Position topLeft;
		if (StringUtils.isNotEmpty(laserRectangle.getCenterPositionX()) && StringUtils.isNotEmpty(laserRectangle.getCenterPositionY())) {
			if (StringUtils.isNotEmpty(laserRectangle.getSquareRadius())) {
				final Distance radius = DistanceUnit.parse(varContext, laserRectangle.getSquareRadius(), defaultDistanceUnit);
				width = new Distance(Math.sqrt(2 * Math.pow(radius.getDistance(), 2)), radius.getUnit());
				height = width;
			} else {
				 width = DistanceUnit.parse(varContext, laserRectangle.getWidth(), defaultDistanceUnit);
				 height = DistanceUnit.parse(varContext, laserRectangle.getHeight(), defaultDistanceUnit);
			}
			
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> xRelative = parseRelativePosition(laserRectangle.getCenterPositionX());
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> yRelative = parseRelativePosition(laserRectangle.getCenterPositionY());
			
			center = getPosition(xRelative, yRelative, laserRectangle.getCenterPositionX(), laserRectangle.getCenterPositionY());

			return Optional.<Drawing>of(positionContext.registerRectangleFromCenter(
					laserRectangle.getName(), laserRectangle.getLayer(), Iterables.transform(Iterables.filter(laserDrawing.getExtraLayers(), GetActiveLayer(laserDrawing.getName())), LASER_LAYER_TO_STRING), center, width, height,
					LaserAction.valueOf(laserRectangle.getLeftAction()), LaserAction.valueOf(laserRectangle.getRightAction()), LaserAction.valueOf(laserRectangle.getTopAction()), LaserAction.valueOf(laserRectangle.getBottomAction())));
		} else if (StringUtils.isNotEmpty(laserRectangle.getTopLeftPositionX()) && StringUtils.isNotEmpty(laserRectangle.getTopLeftPositionY())) {
			 width = DistanceUnit.parse(varContext, laserRectangle.getWidth(), defaultDistanceUnit);
			 height = DistanceUnit.parse(varContext, laserRectangle.getHeight(), defaultDistanceUnit);
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> xRelative = parseRelativePosition(laserRectangle.getTopLeftPositionX());
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> yRelative = parseRelativePosition(laserRectangle.getTopLeftPositionY());
			
			topLeft = getPosition(xRelative, yRelative, laserRectangle.getTopLeftPositionX(), laserRectangle.getTopLeftPositionY());
			return Optional.<Drawing>of(positionContext.registerRectangleFromTopLeft(laserRectangle.getName(), laserRectangle.getLayer(), Iterables.transform(Iterables.filter(laserDrawing.getExtraLayers(), GetActiveLayer(laserDrawing.getName())), LASER_LAYER_TO_STRING), topLeft, width, height,
					LaserAction.valueOf(laserRectangle.getLeftAction()), LaserAction.valueOf(laserRectangle.getRightAction()), LaserAction.valueOf(laserRectangle.getTopAction()), LaserAction.valueOf(laserRectangle.getBottomAction())));
		} else {
			throw new IllegalStateException("Rectangle '" + laserRectangle.getName() + "' has no reference position (Center or TopLeft)");
		}
	}
	
	private Position getPosition(
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> xRelative, 
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> yRelative,
			final String xValue,
			final String yValue) {
		PositionType relativeFromSpecificPointX = null;
		PositionType relativeFromSpecificPointY = null;
		String relativeFromDrawingX = null;
		String relativeFromDrawingY = null;
		double distanceX = 0.0; 
		double distanceY = 0.0; 
		double xExact = -1.0;
		double yExact = -1.0; 
		DistanceUnit unit = DistanceUnit.PIXELS;
		
		if (xRelative.isPresent()) {
			relativeFromSpecificPointX = xRelative.get().getValue1();
			relativeFromDrawingX = xRelative.get().getValue0();
			distanceX = xRelative.get().getValue2();
			unit = xRelative.get().getValue3();
		} else {
			final Distance distance = DistanceUnit.parse(varContext, xValue, defaultDistanceUnit);
			xExact = distance.getDistance();
			unit = distance.getUnit();
		}
		
		if (yRelative.isPresent()) {
			relativeFromSpecificPointY = yRelative.get().getValue1();
			relativeFromDrawingY = yRelative.get().getValue0();
			distanceY = yRelative.get().getValue2();
			unit = yRelative.get().getValue3();
		} else {
			final Distance distance = DistanceUnit.parse(varContext, yValue, defaultDistanceUnit);
			yExact = distance.getDistance();
			unit = distance.getUnit();
		}
		return new RelativePosition(positionContext, relativeFromSpecificPointX, relativeFromSpecificPointY, relativeFromDrawingX, relativeFromDrawingY, distanceX, distanceY, xExact, yExact, unit);
	}

	private <L extends LaserDrawing> Optional<LaserDrawingParser<L>> getParser(final L laserDrawing) {
		final String classSignature = new StringBuilder(laserDrawing.getClass().getPackage().getName()).append(".parser.").append(laserDrawing.getClass().getSimpleName()).append("Parser").toString();
		final Class<LaserDrawingParser> resultingClass;
		try {
			resultingClass = (Class<LaserDrawingParser>) Class.forName(classSignature);
		} catch (Throwable t) {
			logger.info("No parser class of type '" + classSignature + "' found");
			return Optional.absent();
		}
		final LaserDrawingParser<L> result;
		try {
			result = resultingClass.newInstance();
		} catch (Throwable t) {
			throw new IllegalStateException("Cannot instanciate parser class '" + classSignature + "'", t);
		}
		return Optional.of(result);
	}

	private BluePrintContext getContext() {
		return new BluePrintContext() {

		    @Override
            public Optional<Position> resolvePosition(LaserPoint point) {
                final Optional<Quartet<String, PositionType, Double, DistanceUnit>> startXRelative = parseRelativePosition(point.getX());
                final Optional<Quartet<String, PositionType, Double, DistanceUnit>> startYRelative = parseRelativePosition(point.getY());

                if (!startXRelative.isPresent() || !startXRelative.isPresent()) {
                    return Optional.absent();
                }

                return Optional.of(getPosition(startXRelative, startYRelative, point.getX(), point.getY()));
            }

            @Override
			public Point registerPoint(LaserDrawing drawing, Position position) {
				return positionContext.registerPoint(drawing.getName(), drawing.getLayer(), position);
			}

			@Override
			public Text registerText(LaserDrawing drawing, String layer, Position center, String text, int size, DistanceUnit distanceUnit, LaserAction action) {
				return positionContext.registerText(drawing.getName(), drawing.getLayer(), center, text, size, distanceUnit, action);
			}

			@Override
			public Path registerPath(LaserDrawing drawing, Iterable<Position> points, LaserAction borderAction) {
				return positionContext.registerPath(drawing.getName(), drawing.getLayer(), getActiveLayerStrings(drawing), points, getDistanceUnit(), borderAction);
			}

			@Override
			public Circle registerCircle(LaserDrawing drawing, Position center, Distance radius, LaserAction borderAction) {
				return positionContext.registerCircle(drawing.getName(), drawing.getLayer(), getActiveLayerStrings(drawing), center, radius, borderAction);
			}

			@Override
			public <T> T evaluate(String expression, Class<T> dataType) throws Exception {
				return varContext.evaluate(expression, dataType);
			}

			@Override
			public String print(String expression) throws Exception {
				return varContext.print(expression);
			}

			@Override
			public DistanceUnit getDistanceUnit() {
				return defaultDistanceUnit;
			}

            @Override
            public Iterable<Layer> getActiveLayers(LaserDrawing drawing) {
                return Iterables.transform(getActiveLayerStrings(drawing), TO_ACTIVE_BLUEPRINT_LAYER);
            }

            public Iterable<String> getActiveLayerStrings(final LaserDrawing drawing) {
				final Iterable<String> extraActiveLayers;
				if (drawing != null) {
					return Iterables.transform(Iterables.filter(drawing.getExtraLayers(), GetActiveLayer(drawing.getName())), LASER_LAYER_TO_STRING);
				} else {
					return Collections.emptyList();
				}
			}
		};
	}

	private Optional<Quartet<String, PositionType, Double, DistanceUnit>> parseRelativePosition(final String value) {
		final Matcher valueMatcher = RELATIVE_POSTION_PATTERN.matcher(value);
		if (!valueMatcher.find()) {
			return Optional.absent();
		}
		final String drawingName = valueMatcher.group(1);
		final String positionTypeStr = valueMatcher.group(2);
		final String distanceStr = valueMatcher.group(3);
		
		final Distance distance = DistanceUnit.parse(varContext, distanceStr, defaultDistanceUnit);
		
		return Optional.of(Quartet.with(drawingName, PositionType.parse(positionTypeStr), distance.getDistance(), distance.getUnit()));
	}
	
	private static final class RelativePosition implements Position {
		
		private final PositionContext positionContext;
		
		private final PositionType relativeFromSpecificPointX;
		
		private final PositionType relativeFromSpecificPointY;
		
		private final String relativeFromDrawingX;
		
		private final String relativeFromDrawingY;
		
		private final double distanceX;
		
		private final double distanceY;
		
		private final double xExact;
		
		private final double yExact;
		
		private final DistanceUnit unit;

		public RelativePosition(PositionContext positionContext,
				PositionType relativeFromSpecificPointX,
				PositionType relativeFromSpecificPointY,
				String relativeFromDrawingX, String relativeFromDrawingY,
				double distanceX, double distanceY, double xExact,
				double yExact, DistanceUnit unit) {
			super();
			this.positionContext = positionContext;
			this.relativeFromSpecificPointX = relativeFromSpecificPointX;
			this.relativeFromSpecificPointY = relativeFromSpecificPointY;
			this.relativeFromDrawingX = relativeFromDrawingX;
			this.relativeFromDrawingY = relativeFromDrawingY;
			this.distanceX = distanceX;
			this.distanceY = distanceY;
			this.xExact = xExact;
			this.yExact = yExact;
			this.unit = unit;
			if (xExact < 0 && relativeFromDrawingX == null) {
				throw new IllegalStateException("Cannot create a relative position from any positions on X or X value is negative");
			}
			if (yExact < 0 && relativeFromDrawingY == null) {
				throw new IllegalStateException("Cannot create a relative position from any positions on Y or Y value is negative");
			}
		}

		public double getX() {
			if (xExact >= 0) {
				return xExact;
			}
			if (positionContext == null) {
				logger.error("Position context is null");
				return 0;
			}
			if (relativeFromDrawingX == null) {
				throw new IllegalStateException("RelativeFromDrawingX context is null");
			}
			final Position reference = positionContext.getPosition(relativeFromDrawingX, relativeFromSpecificPointX);
			
			return reference.getX() + distanceX;
		}

		public double getY() {
			if (yExact >= 0) {
				return yExact;
			}
			if (positionContext == null) {
				logger.error("Position context is null");
				return 0;
			}
			if (relativeFromDrawingY == null) {
				throw new IllegalStateException("RelativeFromDrawingY context is null");
			}
			final Position reference = positionContext.getPosition(relativeFromDrawingY, relativeFromSpecificPointY);
			
			return reference.getY() + distanceY;
		}

		public DistanceUnit getUnit() {
			return unit;
		}
		
		public String toString() {
			return new StringBuilder("Position[").append(getX()).append(unit.getSymbol()).append(", ").append(getY()).append(unit.getSymbol()).append("]").toString();
		}
		
	}
	
/*	private Pair<Position, Position> getOrthoParalellSegment(final Position point1, final Position point2, final Distance distanceFrom, final Direction direction) {
		double deltaXHinge = direction.getMultiplier() * (point1.getX() - point2.getX());
		double deltaYHinge = direction.getMultiplier() * (point1.getY() - point2.getY());
		
		final Distance pointsDistance = getDistance(point1, point2);
		
		return Pair.<Position, Position>of(
				new RelativeFromPosition(point1, (distanceFrom.getDistance() / pointsDistance.getDistance()) * deltaYHinge, (distanceFrom.getDistance() / pointsDistance.getDistance()) * deltaXHinge),
				new RelativeFromPosition(point2, (distanceFrom.getDistance() / pointsDistance.getDistance()) * deltaYHinge, (distanceFrom.getDistance() / pointsDistance.getDistance()) * deltaXHinge));
	}*/
	
	private static class ExactPosition implements Position {
		
		private final double x;
		
		private final double y;
		
		private final DistanceUnit distanceUnit;

		public ExactPosition(double x, double y, DistanceUnit distanceUnit) {
			super();
			this.x = x;
			this.y = y;
			this.distanceUnit = distanceUnit;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public DistanceUnit getUnit() {
			return this.distanceUnit;
		}
		
	}
	
	private static class CentroidPosition implements Position {

		private final Iterable<Position> points;
		
		private final DistanceUnit distanceUnit;
		
		public CentroidPosition(Iterable<Position> points, DistanceUnit distanceUnit) {
			super();
			if (points == null || Iterables.isEmpty(points)) {
				throw new IllegalArgumentException("Cannot define a centroid based on 0 points");
			}
			this.points = points;
			this.distanceUnit = distanceUnit;
		}

		public double getX() {
			if (Iterables.size(points) == 1) {
				return Iterables.get(points, 0).getX();
			}
			
			double sum = 0.0;
			
			for (final Position position : points) {
				sum += position.getX();
			}
			
			return sum / Iterables.size(points);
		}

		public double getY() {
			if (Iterables.size(points) == 1) {
				return Iterables.get(points, 0).getY();
			}
			
			double sum = 0.0;
			
			for (final Position position : points) {
				sum += position.getY();
			}
			
			return sum / Iterables.size(points);
		}

		public DistanceUnit getUnit() {
			return distanceUnit;
		}
		
	}

	private final Function<LaserLayer, Layer> Laser2BluePrintLayer(final String drawingName) {
	    return new Function<LaserLayer, Layer>() {
            @Override
            public Layer apply(final LaserLayer laserLayer) {
                return new DynamicLayer(laserLayer.getName(),
                        new Predicate<String>() {
                            @Override
                            public boolean apply(String s) {
                                try {
                                    return varContext.evaluate(laserLayer.getCondition(), Boolean.class);
                                } catch (Throwable t) {
                                    logger.error("Wrong layer condition '" + laserLayer.getCondition() + "' on drawing '" + drawingName + "'", t);
                                    throw new ValidationError(ErrorMessage.create("Wrong layer condition '" + laserLayer.getCondition() + "' on drawing '" + drawingName + "'"));
                                }
                            }

                            @Override
                            public boolean test(String input) {
                                return true;
                            }
                        }
                );
            }
        };
    }


	private static final Function<String, Layer> TO_ACTIVE_BLUEPRINT_LAYER = new Function<String, Layer>() {
		@Override
		public Layer apply(final String laserLayer) {
		    return new BasicLayer(laserLayer, true);
		}
	};

	private final Predicate<LaserLayer> GetActiveLayer(final String drawingName) {
		return new Predicate<LaserLayer>() {
			@Override
			public boolean apply(LaserLayer laserLayer) {
			    if (StringUtils.isEmpty(laserLayer.getName())) {
			        return true;
                }
				try {
					return varContext.evaluate(laserLayer.getCondition(), Boolean.class);
				} catch (Throwable t) {
					logger.error("Wrong layer condition '" + laserLayer.getCondition() + "' on drawing '" + drawingName + "'", t);
					throw new ValidationError(ErrorMessage.create("Wrong layer condition '" + laserLayer.getCondition() + "' on drawing '" + drawingName + "'"));
				}
			}
            // Compilation issue
            public boolean test(LaserLayer t) {
                return apply(t);
            }
		};
	}

	private static final Function<LaserLayer, String> LASER_LAYER_TO_STRING = new Function<LaserLayer, String>() {
		@Override
		public String apply(LaserLayer laserLayer) {
			return laserLayer.getName();
		}
	};

	private class LateBindingCrenel implements Path {
		
		private final LaserCrenel laserCrenel;
		
		private final LaserHinge laserHinge;
		
		private Path generatedPath = null;

		private Collection<Drawing> subPathes = Lists.newArrayList();
		
		public LateBindingCrenel(LaserCrenel laserCrenel) {
			super();
			this.laserCrenel = laserCrenel;
			this.laserHinge = null;
		}

		public LateBindingCrenel(LaserHinge laserHinge) {
			super();
			this.laserCrenel = null;
			this.laserHinge = laserHinge;
		}
		
		private void generatePath() {
			final String name;
			final String layer;
			final LaserAction action;
			
			final Distance offSet;
			final Distance minWidth;
			final int numberOfCrenels;
			final Distance height;
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> startXRelative;
			final Optional<Quartet<String, PositionType, Double, DistanceUnit>> startYRelative;
			
			final Position topLeft;
			final Position bottomLeft;
			
			final Position realStart;
			final Position realEnd;
			
			final Position hingeStart;
			final Position hingeEnd;
			
			final Distance crenelLength;
			
			final double thickness;
			final double hingeHoleRadius;
			final double hingeHoleDistance;
			
			final double extraHingeSpan;

			final String sharpAngleLayer;
			
			final Direction userSpecifiedDirection;
			
			final double firstCrenelLength;
			if (laserCrenel != null) {
				name = laserCrenel.getName();
				layer = laserCrenel.getLayer();
				action = LaserAction.valueOf(laserCrenel.getAction());
				
				offSet = DistanceUnit.parse(varContext, laserCrenel.getBorderOffSet(), defaultDistanceUnit);
				minWidth = DistanceUnit.parse(varContext, laserCrenel.getMinWidth(), defaultDistanceUnit);
				height = DistanceUnit.parse(varContext, laserCrenel.getHeight(), defaultDistanceUnit);
				
				final Optional<Quartet<String, PositionType, Double, DistanceUnit>> endXRelative;
				final Optional<Quartet<String, PositionType, Double, DistanceUnit>> endYRelative;

				startXRelative = parseRelativePosition(laserCrenel.getPoint1().getX());
				startYRelative = parseRelativePosition(laserCrenel.getPoint1().getY());

				endXRelative = parseRelativePosition(laserCrenel.getPoint2().getX());
				endYRelative = parseRelativePosition(laserCrenel.getPoint2().getY());

				realStart = getPosition(startXRelative, startYRelative, laserCrenel.getPoint1().getX(), laserCrenel.getPoint1().getY());
				realEnd = getPosition(endXRelative, endYRelative, laserCrenel.getPoint2().getX(), laserCrenel.getPoint2().getY());

				hingeStart = realStart;
				hingeEnd = realEnd;
				
				crenelLength = getDistance(realStart, realEnd);

				thickness = -1.0;
				hingeHoleRadius = -1.0;
				hingeHoleDistance = -1.0;
				
				userSpecifiedDirection = laserCrenel.getRealDirection();
				
				firstCrenelLength = 0.0;
				
				topLeft = null;
				bottomLeft = null;
				
				extraHingeSpan = 0.0;
				
				sharpAngleLayer = null;

				numberOfCrenels = new Double((crenelLength.getDistance() - 2*offSet.getDistance()) / minWidth.getDistance()).intValue();
			} else if (laserHinge != null) {
				name = laserHinge.getName();
				layer = laserHinge.getLayer();
				action = LaserAction.valueOf(laserHinge.getAction());
				
				offSet = DistanceUnit.parse(varContext, laserHinge.getBorderOffSet(), defaultDistanceUnit);
				height = DistanceUnit.parse(varContext, laserHinge.getHeight(), defaultDistanceUnit);
				
				Distance crenelSegmentDistance;
				try {
					crenelSegmentDistance = new Distance(
							varContext.evaluate(laserHinge.getAxleDistance1(), Double.class) + (height.getDistance() / 2) - (varContext.evaluate(laserHinge.getHeadSpan(), Double.class) + varContext.evaluate(laserHinge.getRadius(), Double.class)),
							defaultDistanceUnit);
				} catch (Exception e) {
					throw new IllegalStateException("Cannot evaluate crenel segment distance from start segment", e);
				}
				
				topLeft = getPosition(parseRelativePosition(laserHinge.getPoint1().getX()), parseRelativePosition(laserHinge.getPoint1().getY()), laserHinge.getPoint1().getX(), laserHinge.getPoint1().getY());
				bottomLeft = getPosition(parseRelativePosition(laserHinge.getPoint2().getX()), parseRelativePosition(laserHinge.getPoint2().getY()), laserHinge.getPoint1().getX(), laserHinge.getPoint1().getY());
				
				final Pair<Position, Position> crenelPositions = GeometryUtils.getRectangleEdges(
						topLeft, 
						bottomLeft, 
						crenelSegmentDistance, Direction.forward);

				realStart = crenelPositions.getValue0();
				realEnd = crenelPositions.getValue1();
				
				crenelLength = getDistance(realStart, realEnd);

				Distance minWidthCandidate = DistanceUnit.parse(varContext, laserHinge.getMinWidth(), defaultDistanceUnit);

				int numberOfCrenelsCandidate = new Double((crenelLength.getDistance() - 2*offSet.getDistance()) / minWidthCandidate.getDistance()).intValue();

				if (numberOfCrenelsCandidate % 2 > 0) {
					minWidth = minWidthCandidate;
					numberOfCrenels = numberOfCrenelsCandidate;
				} else {
					logger.info("Number of hinge crenel is even " + numberOfCrenelsCandidate + ", trying to determine an odd number depending on the min width var");
					for (int index = 1; index < 100; index++) {
						minWidthCandidate = new Distance(minWidthCandidate.getDistance() + (index / 10), minWidthCandidate.getUnit());
						numberOfCrenelsCandidate = new Double((crenelLength.getDistance() - 2*offSet.getDistance()) / minWidthCandidate.getDistance()).intValue();
						if (numberOfCrenelsCandidate % 2 > 0) {
							logger.info("Number of hinge crenel is auto set to " + numberOfCrenelsCandidate + " and width will be " + minWidthCandidate.getDistance() + " instead of " + DistanceUnit.parse(varContext, laserHinge.getMinWidth(), defaultDistanceUnit).getDistance());
							break;
						}
					}
					minWidth = minWidthCandidate;
					numberOfCrenels = numberOfCrenelsCandidate;
				}

				try {
					thickness = varContext.evaluate(laserHinge.getThickness(), Double.class);
					hingeHoleRadius = varContext.evaluate(laserHinge.getRadius(), Double.class);
					hingeHoleDistance = varContext.evaluate(laserHinge.getHeadSpan(), Double.class);
				} catch (Exception e) {
					throw new IllegalStateException("Cannot parse crenel '" + laserCrenel.getName() + "' hinge", e);
				}
				
				if (height.getDistance() < thickness) {
					throw new IllegalStateException("The height of the hinge must be > the thickness of the final part");
				}
				
				userSpecifiedDirection = Direction.valueOf(laserHinge.getDirection());
				
				extraHingeSpan = 1.1 * ((hingeHoleDistance + hingeHoleRadius) * Math.cos(Math.PI / 4) + (thickness / 2) * Math.sin(Math.PI / 4)) - (hingeHoleDistance + hingeHoleRadius);

				if (offSet.getDistance() > 0) {
/*					final double offSetSpacer = hingeHoleDistance + 3 * hingeHoleRadius;
					
					if (offSetSpacer <= ((height.getDistance() / 2))) {
						firstCrenelLength = height.getDistance() / 2;
						hingeStart = realStart;
						hingeEnd = realEnd;
					} else {
						firstCrenelLength = (hingeHoleDistance + 3 * hingeHoleRadius);
						hingeStart = GeometryUtils.getOrthoPoint(realStart, realEnd, new Distance((height.getDistance() / 2) - offSetSpacer, defaultDistanceUnit), userSpecifiedDirection.change());
						hingeEnd = GeometryUtils.getOrthoPoint(realEnd, realStart, new Distance((height.getDistance() / 2) - offSetSpacer, defaultDistanceUnit), userSpecifiedDirection.change());
					}*/
					firstCrenelLength = height.getDistance() + extraHingeSpan;
					hingeStart = GeometryUtils.getOrthoPoint(realStart, realEnd, new Distance((height.getDistance() / 2) + extraHingeSpan, defaultDistanceUnit), userSpecifiedDirection.change());
					hingeEnd = GeometryUtils.getOrthoPoint(realEnd, realStart, new Distance((height.getDistance() / 2) + extraHingeSpan, defaultDistanceUnit), userSpecifiedDirection.change());
				} else {
					hingeStart = realStart;
					hingeEnd = realEnd;
					firstCrenelLength = height.getDistance() / 2;
				}
				
				sharpAngleLayer = laserHinge.getSharpAngleLayer();
			} else {
				throw new IllegalStateException("You must specify a crenel or a hinge");
			}

            final Iterable<String> extraActiveLayers;
            if (laserCrenel != null) {
                extraActiveLayers = Iterables.transform(Iterables.filter(laserCrenel.getExtraLayers(), GetActiveLayer(laserCrenel.getName())), LASER_LAYER_TO_STRING);
            } else if (laserHinge != null) {
                extraActiveLayers = Iterables.transform(Iterables.filter(laserHinge.getExtraLayers(), GetActiveLayer(laserHinge.getName())), LASER_LAYER_TO_STRING);
            } else {
                extraActiveLayers = Collections.emptyList();
            }

            if (crenelLength.getDistance() < (2*offSet.getDistance() + 3*minWidth.getDistance())) {
				if (laserCrenel != null) {
					throw new IllegalStateException("Crenel '" + laserCrenel.getName() + "' is too short for width '" + minWidth + "' with offset '" + offSet+ "' and length '" + crenelLength + "'");
				} else if (laserHinge != null) {
					throw new IllegalStateException("Crenel '" + laserHinge.getName() + "' is too short for width '" + minWidth + "' with offset '" + offSet + "' and length '" + crenelLength + "'");
				}
			}
			
			final Distance realWidth = new Distance((crenelLength.getDistance() - 2*offSet.getDistance()) / numberOfCrenels, crenelLength.getUnit());
			
			if (laserHinge != null) {
				if (numberOfCrenels % 2 > 0) {
					logger.info("Hinge '" + name + "/" + layer + "' has real width '" + realWidth + "' and " + numberOfCrenels + " crenels");
				} else {
					logger.error("Hinge '" + name + "/" + layer + "' has real width '" + realWidth + "' and " + numberOfCrenels + " crenels (which will not look good, please update the width of the heads)");
					throw new ValidationError(ErrorMessage.create(
							"Your hinge tooth width has led to an even number of crenels (" + numberOfCrenels + ") the number of crenel must be odd, change this parameter please",
							"Hinge '" + name + "/" + layer + "' has real wdith '" + realWidth + "' and " + numberOfCrenels + " crenels (which will not look good, please update the width of the heads)"));
				}
			} else {
				logger.info("Crenel '" + name + "/" + layer + "' has real wdith '" + realWidth + "' and " + numberOfCrenels + " crenels");
			}

			final List<Position> points = Lists.newArrayList();
			
			Pair<Position, Position> previousSeg = Pair.with(hingeStart, hingeEnd);

			points.add(hingeStart);
			
			final ImmutableList.Builder<Quartet<String, String, Position, Position>> hingeExtraSpan = ImmutableList.builder();
			final ImmutableList.Builder<Triplet<String, Position, Position>> hingeAxleCuts = ImmutableList.builder();

			Position teethEndStart = null;
            Position teethEndEnd = null;

			if (offSet.getDistance() > 0) {
				final Position offSetNext = new RelativeFromPosition(hingeStart, (offSet.getDistance() / crenelLength.getDistance()) * (hingeEnd.getX() - hingeStart.getX()), (offSet.getDistance() / crenelLength.getDistance()) * (hingeEnd.getY() - hingeStart.getY()));
				points.add(offSetNext);
				previousSeg = Pair.with(hingeStart, offSetNext);
				
				final Pair<Position, Position> offSetSpanPath = GeometryUtils.getRectangleEdges(hingeStart, offSetNext, new Distance(firstCrenelLength + extraHingeSpan, defaultDistanceUnit), userSpecifiedDirection);
				hingeExtraSpan.add(Quartet.with(name + "_offset_span_start", layer, offSetSpanPath.getValue0(), offSetSpanPath.getValue1()));
				this.subPathes.add(positionContext.registerPath(name + "offset_start_mark_0", layer, extraActiveLayers, Lists.<Position>newArrayList(offSetSpanPath.getValue0(), offSetNext), defaultDistanceUnit, LaserAction.MARK));
                this.subPathes.add(positionContext.registerPath(name + "offset_start_mark_1", layer, extraActiveLayers, Lists.<Position>newArrayList(offSetSpanPath.getValue1(), hingeStart), defaultDistanceUnit, LaserAction.MARK));
				if (StringUtils.isNotEmpty(sharpAngleLayer)) {
					final Pair<Position, Position> offSetSpanPathSharpAngleOffSetStart = GeometryUtils.getRectangleEdges(previousSeg.getValue0(), previousSeg.getValue1(), new Distance(extraHingeSpan, defaultDistanceUnit), userSpecifiedDirection.change());
                    this.subPathes.add(positionContext.registerPath(name + "_offset_span_sharp_offset_0_start_mark", laserHinge.getSharpAngleLayer(), extraActiveLayers, Lists.<Position>newArrayList(previousSeg.getValue0(), offSetSpanPathSharpAngleOffSetStart.getValue1()), defaultDistanceUnit, LaserAction.MARK));
                    this.subPathes.add(positionContext.registerPath(name + "_offset_span_sharp_offset_1_start_mark", laserHinge.getSharpAngleLayer(), extraActiveLayers, Lists.<Position>newArrayList(previousSeg.getValue1(), offSetSpanPathSharpAngleOffSetStart.getValue0()), defaultDistanceUnit, LaserAction.MARK));
					hingeExtraSpan.add(Quartet.with(name + "_offset_span_sharp_offset_0_start", laserHinge.getSharpAngleLayer(), offSetSpanPathSharpAngleOffSetStart.getValue0(), offSetSpanPathSharpAngleOffSetStart.getValue1()));
					hingeExtraSpan.add(Quartet.with(name + "_offset_span_sharp_offset_1_start", laserHinge.getSharpAngleLayer(), offSetSpanPathSharpAngleOffSetStart.getValue1(), offSetNext));
					
					final Pair<Position, Position> offSetSpanPathSharpAngleStart = GeometryUtils.getRectangleEdges(offSetSpanPath.getValue0(), offSetSpanPath.getValue1(), new Distance(extraHingeSpan, defaultDistanceUnit), userSpecifiedDirection);
                    this.subPathes.add(positionContext.registerPath(name + "_offset_span_sharp_offset_2_start_mark", laserHinge.getSharpAngleLayer(), extraActiveLayers, Lists.<Position>newArrayList(offSetSpanPath.getValue0(), offSetSpanPathSharpAngleStart.getValue1()), defaultDistanceUnit, LaserAction.MARK));
                    this.subPathes.add(positionContext.registerPath(name + "_offset_span_sharp_offset_3_start_mark", laserHinge.getSharpAngleLayer(), extraActiveLayers, Lists.<Position>newArrayList(offSetSpanPath.getValue1(), offSetSpanPathSharpAngleStart.getValue0()), defaultDistanceUnit, LaserAction.MARK));
					hingeExtraSpan.add(Quartet.with(name + "_offset_span_sharp_offset_2_start", laserHinge.getSharpAngleLayer(), offSetSpanPathSharpAngleStart.getValue0(), offSetSpanPathSharpAngleStart.getValue1()));
					hingeExtraSpan.add(Quartet.with(name + "_offset_span_sharp_offset_3_start", laserHinge.getSharpAngleLayer(), offSetSpanPath.getValue1(), offSetSpanPathSharpAngleStart.getValue1()));
				}
			}			
			
			Direction currentDirection = userSpecifiedDirection;
			Direction paralellDirection = Direction.forward;
			if ((realEnd.getX() - realStart.getX()) < 0 && (realEnd.getY() - realStart.getY()) < 0) {
				paralellDirection = Direction.backward;
			}
			for (int index = 0; index < numberOfCrenels; index++) {
				final double h;
				
				if ((index == 0) && offSet.getDistance() > 0) {
					h = firstCrenelLength;
				} else {
					h = height.getDistance();
				}

				final Position next1 = GeometryUtils.getTranslatedPoint(GeometryUtils.getOrthoPoint(previousSeg.getValue0(), previousSeg.getValue1(), new Distance(h, defaultDistanceUnit), currentDirection), previousSeg.getValue0(), previousSeg.getValue1());
				final Position next2 = GeometryUtils.getTranslatedPoint(GeometryUtils.getOrthoPoint(previousSeg.getValue1(), next1, realWidth, paralellDirection), previousSeg.getValue1(), next1);

				if (logger.isDebugEnabled()) {
					logger.debug("Index=" + index + ", direction=" + currentDirection + ", paralellDirection=" + paralellDirection);
					logger.debug("PreviousSeg ([" + previousSeg.getValue0().getX() + ", " + previousSeg.getValue0().getY() + "] [" + previousSeg.getValue1().getX() + ", " + previousSeg.getValue1().getY() + "])");
					logger.debug("Next1 [" + next1.getX() + ", " + next1.getY() + "]");
					logger.debug("Next2 [" + next2.getX() + ", " + next2.getY() + "]");
				}
				
				points.add(next1);
				points.add(next2);

				if (laserHinge != null) {
					if (extraHingeSpan > 0) {
						final Pair<Position, Position> extraSpanCutSegment = GeometryUtils.getRectangleEdges(next1, next2, new Distance(extraHingeSpan, defaultDistanceUnit), currentDirection);
                        if (index == 0) {
                            teethEndStart = extraSpanCutSegment.getValue0();
                        } else if (index == (numberOfCrenels - 1)) {
                            teethEndEnd = extraSpanCutSegment.getValue1();
                        }
                        this.subPathes.add(positionContext.registerPath(name + "drop_mark_0_" + index, layer, extraActiveLayers, Lists.<Position>newArrayList(next1, extraSpanCutSegment.getValue1()), defaultDistanceUnit, LaserAction.MARK));
                        this.subPathes.add(positionContext.registerPath(name + "drop_mark_1_" + index, layer, extraActiveLayers, Lists.<Position>newArrayList(next2, extraSpanCutSegment.getValue0()), defaultDistanceUnit, LaserAction.MARK));
						hingeExtraSpan.add(Quartet.with(name + "_extra_hinge_span_0_" + index, layer, next1, extraSpanCutSegment.getValue0()));
						hingeExtraSpan.add(Quartet.with(name + "_extra_hinge_span_1_" + index, layer, extraSpanCutSegment.getValue0(), extraSpanCutSegment.getValue1()));
						hingeExtraSpan.add(Quartet.with(name + "_extra_hinge_span_2_" + index, layer, extraSpanCutSegment.getValue1(), next2));
						if (StringUtils.isNotEmpty(sharpAngleLayer)) {
							final Pair<Position, Position> extraSpanCutSegmentSharpAngle = GeometryUtils.getRectangleEdges(extraSpanCutSegment.getValue0(), extraSpanCutSegment.getValue1(), new Distance(extraHingeSpan, defaultDistanceUnit), currentDirection);
							hingeExtraSpan.add(Quartet.with(name + "_extra_hinge_sharp_span_0_" + index, sharpAngleLayer, extraSpanCutSegment.getValue0(), extraSpanCutSegmentSharpAngle.getValue0()));
							hingeExtraSpan.add(Quartet.with(name + "_extra_hinge_sharp_span_1_" + index, sharpAngleLayer, extraSpanCutSegmentSharpAngle.getValue0(), extraSpanCutSegmentSharpAngle.getValue1()));
							hingeExtraSpan.add(Quartet.with(name + "_extra_hinge_sharp_span_2_" + index, sharpAngleLayer, extraSpanCutSegmentSharpAngle.getValue1(), extraSpanCutSegment.getValue1()));
							this.subPathes.add(positionContext.registerPath(name + "_extra_hinge_sharp_span_mark_0_" + index, sharpAngleLayer, extraActiveLayers, Lists.<Position>newArrayList(extraSpanCutSegment.getValue0(), extraSpanCutSegmentSharpAngle.getValue1()), defaultDistanceUnit, LaserAction.MARK));
                            this.subPathes.add(positionContext.registerPath(name + "_extra_hinge_sharp_span_mark_1_" + index, sharpAngleLayer, extraActiveLayers, Lists.<Position>newArrayList(extraSpanCutSegment.getValue1(), extraSpanCutSegmentSharpAngle.getValue0()), defaultDistanceUnit, LaserAction.MARK));
						}
					} else {
                        if (index == 0) {
                            teethEndStart = next1;
                        } else if (index == (numberOfCrenels - 1)) {
                            teethEndEnd = next2;
                        }
                    }
				}
				
				if (currentDirection == Direction.forward) {
					currentDirection = Direction.backward;
				} else {
					currentDirection = Direction.forward;
				}
				
				if (laserHinge != null) {
					final Pair<Position, Position> topRectEdges = GeometryUtils.getRectangleEdges(next1, next2, new Distance(hingeHoleDistance,  defaultDistanceUnit), currentDirection);
					final Pair<Position, Position> bottomRectEdges = GeometryUtils.getRectangleEdges(next1, next2, new Distance(hingeHoleDistance + hingeHoleRadius * 2,  defaultDistanceUnit), currentDirection);
					hingeAxleCuts.add(Triplet.with(name + "_hinge_top_" + index, topRectEdges.getValue0(), topRectEdges.getValue1()));
					hingeAxleCuts.add(Triplet.with(name + "_hinge_bottom_" + index, bottomRectEdges.getValue0(), bottomRectEdges.getValue1()));
					this.subPathes.add(positionContext.registerPath(name + "_hinge_axle_0_mark_" + index, laserHinge.getAxleLayer(), extraActiveLayers, Lists.newArrayList(topRectEdges.getValue0(), bottomRectEdges.getValue1()), defaultDistanceUnit, LaserAction.MARK));
                    this.subPathes.add(positionContext.registerPath(name + "_hinge_axle_1_mark_" + index, laserHinge.getAxleLayer(), extraActiveLayers, Lists.newArrayList(topRectEdges.getValue1(), bottomRectEdges.getValue0()), defaultDistanceUnit, LaserAction.MARK));
				}

				previousSeg = Pair.with(next1, next2);
				
			}
			
			if (offSet.getDistance() > 0) {
				final Position offSetEnd = new RelativeFromPosition(hingeEnd, (offSet.getDistance() / crenelLength.getDistance()) * (hingeStart.getX() - hingeEnd.getX()), (offSet.getDistance() / crenelLength.getDistance()) * (hingeStart.getY() - hingeEnd.getY()));
				points.add(offSetEnd);

				final Pair<Position, Position> offSetSpanPath = GeometryUtils.getRectangleEdges(offSetEnd, hingeEnd, new Distance(firstCrenelLength + extraHingeSpan, defaultDistanceUnit), userSpecifiedDirection);
				hingeExtraSpan.add(Quartet.with(name + "_offset_span_end", layer, offSetSpanPath.getValue0(), offSetSpanPath.getValue1()));
				this.subPathes.add(positionContext.registerPath(name + "offset_end_mark_0", layer, extraActiveLayers, Lists.<Position>newArrayList(offSetEnd, offSetSpanPath.getValue1()), defaultDistanceUnit, LaserAction.MARK));
                this.subPathes.add(positionContext.registerPath(name + "offset_end_mark_1", layer, extraActiveLayers, Lists.<Position>newArrayList(hingeEnd, offSetSpanPath.getValue0()), defaultDistanceUnit, LaserAction.MARK));
				if (StringUtils.isNotEmpty(sharpAngleLayer)) {
					final Pair<Position, Position> offSetSpanPathSharpOffSetAngle = GeometryUtils.getRectangleEdges(offSetEnd, hingeEnd, new Distance(extraHingeSpan, defaultDistanceUnit), userSpecifiedDirection.change());
					this.subPathes.add(positionContext.registerPath(name + "_offset_span_sharp_offset_0_end_mark", laserHinge.getSharpAngleLayer(), extraActiveLayers, Lists.<Position>newArrayList(offSetEnd, offSetSpanPathSharpOffSetAngle.getValue1()), defaultDistanceUnit, LaserAction.MARK));
                    this.subPathes.add(positionContext.registerPath(name + "_offset_span_sharp_offset_1_end_mark", laserHinge.getSharpAngleLayer(), extraActiveLayers, Lists.<Position>newArrayList(hingeEnd, offSetSpanPathSharpOffSetAngle.getValue0()), defaultDistanceUnit, LaserAction.MARK));
					hingeExtraSpan.add(Quartet.with(name + "_offset_span_sharp_offset_0_end", laserHinge.getSharpAngleLayer(), offSetSpanPathSharpOffSetAngle.getValue0(), offSetSpanPathSharpOffSetAngle.getValue1()));
					hingeExtraSpan.add(Quartet.with(name + "_offset_span_sharp_offset_1_end", laserHinge.getSharpAngleLayer(), offSetEnd, offSetSpanPathSharpOffSetAngle.getValue0()));
					
					final Pair<Position, Position> offSetSpanPathSharpAngleStart = GeometryUtils.getRectangleEdges(offSetSpanPath.getValue0(), offSetSpanPath.getValue1(), new Distance(extraHingeSpan, defaultDistanceUnit), userSpecifiedDirection);
					final Pair<Position, Position> offSetSpanPathSharpAngleEnd = GeometryUtils.getRectangleEdges(offSetSpanPathSharpAngleStart.getValue0(), offSetSpanPathSharpAngleStart.getValue1(), new Distance(extraHingeSpan, defaultDistanceUnit), userSpecifiedDirection.change());
                    this.subPathes.add(positionContext.registerPath(name + "_offset_span_sharp_offset_2_end_mark", laserHinge.getSharpAngleLayer(), extraActiveLayers, Lists.<Position>newArrayList(offSetSpanPathSharpAngleStart.getValue0(), offSetSpanPathSharpAngleEnd.getValue1()), defaultDistanceUnit, LaserAction.MARK));
                    this.subPathes.add(positionContext.registerPath(name + "_offset_span_sharp_offset_3_end_mark", laserHinge.getSharpAngleLayer(), extraActiveLayers, Lists.<Position>newArrayList(offSetSpanPathSharpAngleEnd.getValue0(), offSetSpanPathSharpAngleStart.getValue1()), defaultDistanceUnit, LaserAction.MARK));
					hingeExtraSpan.add(Quartet.with(name + "_offset_span_sharp_1_end", laserHinge.getSharpAngleLayer(), offSetSpanPathSharpAngleStart.getValue0(), offSetSpanPathSharpAngleStart.getValue1()));
					hingeExtraSpan.add(Quartet.with(name + "_offset_span_sharp_2_end", laserHinge.getSharpAngleLayer(), offSetSpanPathSharpAngleEnd.getValue0(), offSetSpanPathSharpAngleStart.getValue0()));
				}
			}
			
			points.add(hingeEnd);

			this.generatedPath = positionContext.registerPath(name, layer, extraActiveLayers, points, defaultDistanceUnit, action);
			
			if (laserHinge != null) {
				for (final Triplet<String, Position, Position> hingeCut : hingeAxleCuts.build()) {
					this.subPathes.add(positionContext.registerPath(hingeCut.getValue0(), laserHinge.getAxleLayer(), Collections.<String>emptyList(), ImmutableList.<Position>of(hingeCut.getValue1(), hingeCut.getValue2()), defaultDistanceUnit, LaserAction.CUT));
				}
				for (final Quartet<String, String, Position, Position> hingeCut : hingeExtraSpan.build()) {
					this.subPathes.add(positionContext.registerPath(hingeCut.getValue0(), hingeCut.getValue1(), Collections.<String>emptyList(), ImmutableList.<Position>of(hingeCut.getValue2(), hingeCut.getValue3()), defaultDistanceUnit, LaserAction.CUT));
				}
				Distance crenelSegment2Distance;
				try {
					crenelSegment2Distance = new Distance(
							varContext.evaluate(laserHinge.getAxleDistance2(), Double.class) + (height.getDistance() / 2) - (varContext.evaluate(laserHinge.getHeadSpan(), Double.class) + varContext.evaluate(laserHinge.getRadius(), Double.class)),
							defaultDistanceUnit);
				} catch (Exception e) {
					throw new IllegalStateException("Cannot determine segment 2 distance", e);
				}
				final Pair<Position, Position> beforeAxleMountPoints = GeometryUtils.getRectangleEdges(realStart, realEnd, crenelSegment2Distance, Direction.forward);
				
				subPathes.add(positionContext.registerPoint(name + "TopLeft", layer, topLeft));
                subPathes.add(positionContext.registerPoint(name + "BottomLeft", layer, bottomLeft));
                subPathes.add(positionContext.registerPoint(name + "TopMiddle", layer, realStart));
                subPathes.add(positionContext.registerPoint(name + "BottomMiddle", layer, realEnd));


				// Axle points 1
				final Vector axle1;
				try {
					axle1 = GeometryUtils.getParalellVector(topLeft, bottomLeft, new Distance(
							varContext.evaluate(laserHinge.getAxleDistance1(), Double.class), defaultDistanceUnit), userSpecifiedDirection.change());
				} catch (Exception e) {
					throw new IllegalStateException("Cannot parse hinge '" + name + "' axle distance 1 '" + laserHinge.getAxleDistance1() + "'", e);
				}
                subPathes.add(positionContext.registerPoint(name + "TopAxle1", layer, axle1.getPoint1()));
                subPathes.add(positionContext.registerPoint(name + "BottomAxle1", layer, axle1.getPoint2()));
				logger.info("Adding Axle1 path from '" + axle1.getPoint1() + "' to '" + axle1.getPoint2() + "' on layer '" + laserHinge.getAxleLayer() + "'");
				this.subPathes.add(positionContext.registerPath(name + "Axle1Mark", laserHinge.getAxleLayer(), extraActiveLayers, Lists.<Position>newArrayList(axle1.getPoint1(), axle1.getPoint2()), defaultDistanceUnit, LaserAction.MARK));
				// End of Axle points 1

				final double mountSpan = 2 * Math.abs(height.getDistance() - hingeHoleDistance - (hingeHoleDistance + 2*hingeHoleRadius));
				if (mountSpan <= 0 || !laserHinge.isAutoAxleDistanceCut()) {
					if (mountSpan == 0) {
						logger.info("Hinge '" + name + "/" + layer + "' has no mount span");
					} else if (!laserHinge.isAutoAxleDistanceCut()) {
						logger.info("Hinge '" + name + "/" + layer + "' is configured not to take mount span into account");
					} else {
						logger.error("Hinge '" + name + "/" + layer + "' has negative mount span (" + mountSpan + ")");
					}
                    subPathes.add(positionContext.registerPoint(name + "TopRight", layer, beforeAxleMountPoints.getValue0()));
                    subPathes.add(positionContext.registerPoint(name + "BottomRight", layer, beforeAxleMountPoints.getValue1()));
                    subPathes.add(positionContext.registerPoint(name + "TopAxleCut", layer, beforeAxleMountPoints.getValue0()));
                    subPathes.add(positionContext.registerPoint(name + "BottomAxleCut", layer, beforeAxleMountPoints.getValue1()));

					// Axle points 2
					final Vector axle2;
					try {
						axle2 = GeometryUtils.getParalellVector(beforeAxleMountPoints.getValue0(), beforeAxleMountPoints.getValue1(), new Distance(
								varContext.evaluate(laserHinge.getAxleDistance2(), Double.class), defaultDistanceUnit), userSpecifiedDirection);
					} catch (Exception e) {
						throw new IllegalStateException("Cannot parse hinge '" + name + "' axle distance 1 '" + laserHinge.getAxleDistance1() + "'", e);
					}
                    subPathes.add(positionContext.registerPoint(name + "TopAxle2", layer, axle2.getPoint1()));
                    subPathes.add(positionContext.registerPoint(name + "BottomAxle2", layer, axle2.getPoint2()));
					logger.info("Adding Axle1 path from '" + axle1.getPoint1() + "' to '" + axle1.getPoint2() + "' on layer '" + laserHinge.getAxleLayer() + "'");
                    this.subPathes.add(positionContext.registerPath(name + "Axle2Mark", laserHinge.getAxleLayer(), Collections.<String>emptyList(), Lists.<Position>newArrayList(axle2.getPoint1(), axle2.getPoint2()), defaultDistanceUnit, LaserAction.MARK));
					// End of Axle points 2
				} else {
					logger.info("Hinge '" + name + "/" + layer + "' has mount span " + mountSpan + defaultDistanceUnit.getSymbol());
					final Pair<Position, Position> afterAxleMountPoints = GeometryUtils.getRectangleEdges(realStart, realEnd, new Distance(crenelSegment2Distance.getDistance() + 1.1 * mountSpan, defaultDistanceUnit) , Direction.forward);
                    subPathes.add(positionContext.registerPoint(name + "TopRight", layer, afterAxleMountPoints.getValue0()));
                    subPathes.add(positionContext.registerPoint(name + "BottomRight", layer, afterAxleMountPoints.getValue1()));
                    subPathes.add(positionContext.registerPoint(name + "TopAxleCut", layer, beforeAxleMountPoints.getValue0()));
                    subPathes.add(positionContext.registerPoint(name + "BottomAxleCut", layer, beforeAxleMountPoints.getValue1()));
                    this.subPathes.add(positionContext.registerPath(name + "AxleMountSpan", layer, Collections.<String>emptyList(), ImmutableList.of(beforeAxleMountPoints.getValue0(), beforeAxleMountPoints.getValue1()), defaultDistanceUnit, LaserAction.CUT));
					// Axle points 2
					final Vector axle2;
					try {
						axle2 = GeometryUtils.getParalellVector(beforeAxleMountPoints.getValue0(), beforeAxleMountPoints.getValue1(), new Distance(
								varContext.evaluate(laserHinge.getAxleDistance2(), Double.class), defaultDistanceUnit), userSpecifiedDirection);
					} catch (Exception e) {
						throw new IllegalStateException("Cannot parse hinge '" + name + "' axle distance 1 '" + laserHinge.getAxleDistance1() + "'", e);
					}
                    subPathes.add(positionContext.registerPoint(name + "TopAxle2", layer, axle2.getPoint1()));
                    subPathes.add(positionContext.registerPoint(name + "BottomAxle2", layer, axle2.getPoint2()));
					logger.info("Adding Axle1 path from '" + axle1.getPoint1() + "' to '" + axle1.getPoint2() + "' on layer '" + laserHinge.getAxleLayer() + "'");
                    this.subPathes.add(positionContext.registerPath(name + "Axle2Mark", laserHinge.getAxleLayer(), Collections.<String>emptyList(), Lists.<Position>newArrayList(axle2.getPoint1(), axle2.getPoint2()), defaultDistanceUnit, LaserAction.MARK));
					// End of Axle points 2
				}

				// Teeth start and end marks
				String defaultMinMaxSuffix = "Left";
				String otherMinMaxSuffix = "Right";
				if (userSpecifiedDirection == Direction.backward) {
                    defaultMinMaxSuffix = "Right";
                    otherMinMaxSuffix = "Left";
                }

                logger.info("Registering teeth starts '" + otherMinMaxSuffix + "' on hinge '" + name + "' from '" + hingeStart + "' to '" + hingeEnd + "'");
                subPathes.add(positionContext.registerPoint(name + defaultMinMaxSuffix + "TeethEndTop", laserHinge.getLayer(), hingeStart));
                subPathes.add(positionContext.registerPoint(name + defaultMinMaxSuffix + "TeethEndBottom", laserHinge.getLayer(), hingeEnd));
                this.subPathes.add(positionContext.registerPath(name + defaultMinMaxSuffix + "TeethEnd", laserHinge.getAxleLayer(), extraActiveLayers, Lists.<Position>newArrayList(hingeStart, hingeEnd), defaultDistanceUnit, LaserAction.MARK));

				if (teethEndStart != null && teethEndEnd != null) {
				    logger.info("Registering teeth ends '" + otherMinMaxSuffix + "' on hinge '" + name + "' from '" + teethEndStart + "' to '" + teethEndEnd + "'");
                    subPathes.add(positionContext.registerPoint(name + otherMinMaxSuffix + "TeethEndTop", laserHinge.getLayer(), teethEndStart));
                    subPathes.add(positionContext.registerPoint(name + otherMinMaxSuffix + "TeethEndBottom", laserHinge.getLayer(), teethEndEnd));
                    this.subPathes.add(positionContext.registerPath(name + otherMinMaxSuffix + "TeethEnd", laserHinge.getAxleLayer(), extraActiveLayers, Lists.<Position>newArrayList(teethEndStart, teethEndEnd), defaultDistanceUnit, LaserAction.MARK));
                }

            }
		}

		public String getName() {
			if (laserCrenel != null) {
				return laserCrenel.getName();
			} else if (laserHinge != null) {
				return laserHinge.getName();
			}
			return null;
		}



		public Layer getLayer() {
			if (laserCrenel != null) {
				return TO_ACTIVE_BLUEPRINT_LAYER.apply(laserCrenel.getLayer());
			} else if (laserHinge != null) {
				return TO_ACTIVE_BLUEPRINT_LAYER.apply(laserHinge.getLayer());
			}
			return null;
		}

		@Override
		public Iterable<Layer> getExtraLayers() {
			if (laserCrenel != null) {
				return Iterables.transform(laserCrenel.getExtraLayers(), Laser2BluePrintLayer(getName()));
			} else if (laserHinge != null) {
				return Iterables.transform(laserHinge.getExtraLayers(), Laser2BluePrintLayer(getName()));
			}
			return Collections.emptyList();
		}


		public Position getCenter() {
			if (this.generatedPath == null) {
				generatePath();
			}
			return this.generatedPath.getCenter();
		}

		public Rectangle getBoundingRectangle() {
			if (this.generatedPath == null) {
				generatePath();
			}
			return this.generatedPath.getBoundingRectangle();
		}

		public Iterable<Point> getPoints() {
			if (this.generatedPath == null) {
				generatePath();
			}
			return this.generatedPath.getPoints();
		}

		public LaserAction getBorderAction() {
			if (this.generatedPath == null) {
				generatePath();
			}
			return this.generatedPath.getBorderAction();
		}

        @Override
        public Optional<LaserAction> getHatchAction() {
            return Optional.absent();
        }

        @Override
        public Iterable<Path> getHatchPath() {
            return Collections.emptyList();
        }

        public Iterable<Drawing> getSubDrawings(final Iterable<String> activeLayers) {
			final ImmutableList.Builder<Drawing> result = ImmutableList.builder();
			for (final Drawing path : subPathes) {
				if (path.getLayer() == null || StringUtils.isEmpty(path.getLayer().getName()) || Iterables.contains(activeLayers, path.getLayer().getName())) {
					result.add(path);
				}
			}
			return result.build();
		}

		public Iterable<Drawing> getSubDrawings() {
			return ImmutableList.<Drawing>builder().addAll(subPathes).build();
		}
		
	}

	private static class PositionContext {

		private final Map<String, DrawingType> drawingTypes = Maps.newHashMap();

		private final Map<String, Position> centers = Maps.newHashMap();

		private final Map<String, Position> topLefts = Maps.newHashMap();

		private final Map<String, Position> topRights = Maps.newHashMap();

		private final Map<String, Position> bottomLefts = Maps.newHashMap();

		private final Map<String, Position> bottomRights = Maps.newHashMap();

		private final Map<String, Position> radiusTops = Maps.newHashMap();

		private final Map<String, Position> radiusBottoms = Maps.newHashMap();

		private final Map<String, Position> radiusLefts = Maps.newHashMap();

		private final Map<String, Position> radiusRights = Maps.newHashMap();

		private Position getPosition(final String name, final PositionType positionType) {
			if (!drawingTypes.containsKey(name)) {
				throw new IllegalStateException("Unknown drawing named '" + name + "' cannot fetch position of type '" + positionType.getVarName() + "'");
			}
			if (!positionType.accept(this.drawingTypes.get(name))) {
				throw new IllegalStateException("Position '" + positionType.getVarName() + "' is not supported by drawing of type '" + drawingTypes.get(name) + "'");
			}
			switch (positionType) {
			case CENTER:
				if (!centers.containsKey(name)) {
					throw new IllegalStateException("No center named '" + name + "' found");
				}
				return centers.get(name);

			case TOP_LEFT:
				if (!topLefts.containsKey(name)) {
					throw new IllegalStateException("No top left named '" + name + "' found");
				}
				return topLefts.get(name);

			case TOP_RIGHT:
				if (!topRights.containsKey(name)) {
					throw new IllegalStateException("No top right named '" + name + "' found");
				}
				return topRights.get(name);

			case BOTTOM_LEFT:
				if (!bottomLefts.containsKey(name)) {
					throw new IllegalStateException("No bottom left named '" + name + "' found");
				}
				return bottomLefts.get(name);

			case BOTTOM_RIGHT:
				if (!bottomRights.containsKey(name)) {
					throw new IllegalStateException("No bottom right named '" + name + "' found");
				}
				return bottomRights.get(name);

			case RADIUS_TOP:
				if (!radiusTops.containsKey(name)) {
					throw new IllegalStateException("No radius top named '" + name + "' found");
				}
				return radiusTops.get(name);

			case RADIUS_BOTTOM:
				if (!radiusBottoms.containsKey(name)) {
					throw new IllegalStateException("No radius bottom named '" + name + "' found");
				}
				return radiusBottoms.get(name);

			case RADIUS_LEFT:
				if (!radiusLefts.containsKey(name)) {
					throw new IllegalStateException("No radius left named '" + name + "' found");
				}
				return radiusLefts.get(name);

			case RADIUS_RIGHT:
				if (!radiusRights.containsKey(name)) {
					throw new IllegalStateException("No radius right named '" + name + "' found");
				}
				return radiusRights.get(name);

			default:
				throw new IllegalArgumentException("Unsupported position type '" + positionType.getVarName() + "'");
			}
		}

		private Point registerPoint(final String name, final String layer, final Position position) {
			if (drawingTypes.containsKey(name)) {
				throw new IllegalStateException("A drawing of type '" + drawingTypes.get(name) + "' has already been defined with name '" + name + "' cannot declare a circle with the same name");
			}
			drawingTypes.put(name, DrawingType.POINT);
			centers.put(name, position);
			logger.info("Point '" + name + "/" + layer + "' has been registered");
			return new Point() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Layer getLayer() {
                    return new BasicLayer(layer, true);
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

		private Text registerText(final String name, final String layer, final Position center, final String text, final int size, final DistanceUnit distanceUnit, final LaserAction action) {
			if (drawingTypes.containsKey(name)) {
				throw new IllegalStateException("A drawing of type '" + drawingTypes.get(name) + "' has already been defined with name '" + name + "' cannot declare a circle with the same name");
			}

			drawingTypes.put(name, DrawingType.PATH);
			centers.put(name, center);

			return new Text() {

				public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
					return Collections.emptyList();
				}

				public String getName() {
					return name;
				}

				public Layer getLayer() {
				    return new BasicLayer(layer, true);
				}

				@Override
				public Iterable<Layer> getExtraLayers() {
					return Collections.emptyList();
				}

				public Position getCenter() {
					return center;
				}

				public Rectangle getBoundingRectangle() {
					return new EmptyRectangle(center);
				}

				public String getText() {
					return text;
				}

				public int getSize() {
					return size;
				}

				public LaserAction getAction() {
					return action;
				}

				public Iterable<Drawing> getSubDrawings() {
					return Collections.emptyList();
				}
			};
		}

        private Path registerPath(final String name, final String layer, final Iterable<String> extraActiveLayers, final Iterable<Position> points, final DistanceUnit distanceUnit, final LaserAction borderAction) {
		    return registerPath(name, layer, extraActiveLayers, points, distanceUnit, borderAction, Optional.<LaserAction>absent());
        }

        private Path registerPath(final String name, final String layer, final Iterable<String> extraActiveLayers, final Iterable<Position> points, final DistanceUnit distanceUnit, final LaserAction borderAction, final Optional<LaserAction> hatchAction) {
			if (points == null || Iterables.isEmpty(points)) {
				throw new IllegalArgumentException("Cannot register a path without points");
			}
			if (drawingTypes.containsKey(name)) {
				throw new IllegalStateException("A drawing of type '" + drawingTypes.get(name) + "' has already been defined with name '" + name + "' cannot declare a circle with the same name");
			}
			final Position center = new CentroidPosition(points, distanceUnit);
			drawingTypes.put(name, DrawingType.PATH);
			centers.put(name, center);

			logger.info("Path '" + name + "/" + layer + "' has been registered");

			return new Path() {

				public String getName() {
					return name;
				}

				public Layer getLayer() {
					return TO_ACTIVE_BLUEPRINT_LAYER.apply(layer);
				}

				@Override
				public Iterable<Layer> getExtraLayers() {
					return Iterables.transform(extraActiveLayers, TO_ACTIVE_BLUEPRINT_LAYER);
				}

				public Position getCenter() {
					return center;
				}

				public Rectangle getBoundingRectangle() {
					double minX = 0.0;
					double minY = 0.0;

					double maxX = -1.0;
					double maxY = -1.0;

					for (final Position position : points) {
						// MIN
						if (position.getX() < minX) {
							minX = position.getX();
						}
						if (position.getY() < minY) {
							minY = position.getY();
						}

						// MAX
						if (maxX < 0) {
							maxX = position.getX();
						} else if (position.getX() > maxX) {
							maxX = position.getX();
						}

						if (maxY < 0) {
							maxY = position.getY();
						} else if (position.getY() > maxY) {
							maxY = position.getY();
						}
					}

					final double width = Math.abs(maxX - minX);
					final double height = Math.abs(maxY - minY);

					final Position topLeft = new RelativePosition(null, null, null, null, null, 0, 0, minX, minY, distanceUnit);
					final Position topRight = new RelativePosition(null, null, null, null, null, 0, 0, maxX, minY, distanceUnit);
					final Position bottomLeft = new RelativePosition(null, null, null, null, null, 0, 0, minX, maxY, distanceUnit);
					final Position bottomRight = new RelativePosition(null, null, null, null, null, 0, 0, maxX, maxY, distanceUnit);

					return new BoundingRectangle(center, topLeft, topRight, bottomLeft, bottomRight, width, height, name, layer, distanceUnit);

				}

				public Iterable<Point> getPoints() {
					return Iterables.transform(points, new Function<Position, Point>() {

						public Point apply(final Position position) {
							return new Point() {

								public String getName() {
									return null;
								}

								public Layer getLayer() {
									return TO_ACTIVE_BLUEPRINT_LAYER.apply(layer);
								}

								@Override
								public Iterable<Layer> getExtraLayers() {
									return Iterables.transform(extraActiveLayers, TO_ACTIVE_BLUEPRINT_LAYER);
								}

								public Position getCenter() {
									return position;
								}

								public Rectangle getBoundingRectangle() {
									return new EmptyRectangle(center);
								}

								public Iterable<Drawing> getSubDrawings(final Iterable<String> activeLayers) {
									return Collections.emptyList();
								}

								public Iterable<Drawing> getSubDrawings() {
									return Collections.emptyList();
								}
							};
						}

					});
				}

				public LaserAction getBorderAction() {
					return borderAction;
				}

                @Override
                public Optional<LaserAction> getHatchAction() {
				    if (hatchAction == null) {
				        return Optional.absent();
                    }
                    return hatchAction;
                }

                @Override
                public Iterable<Path> getHatchPath() {
                    if (!getHatchAction().isPresent()) {
                        return Collections.emptyList();
                    }
                    final String layerName;
                    if (getLayer().isActive()) {
                        layerName = getLayer().getName();
                    } else {
                        layerName = "void";
                    }
                    return Iterables.transform(GeometryUtils.getHatchVectors(getPoints(), DistanceUnit.MILLIMETERS), GeometryUtils.ToPath(getName() + "_hatch", layerName, Collections.<String>emptyList(), getHatchAction().get(), new AtomicInteger()));
                }

                public Iterable<Drawing> getSubDrawings(final Iterable<String> activeLayers) {
					return Collections.emptyList();
				}

				public Iterable<Drawing> getSubDrawings() {
					return Collections.emptyList();
				}
			};

		}

		private Circle registerCircle(final String name, final String layer, final Iterable<String> activeLayers, final Position center, final Distance radius, final LaserAction borderAction) {
			if (drawingTypes.containsKey(name)) {
				throw new IllegalStateException("A drawing of type '" + drawingTypes.get(name) + "' has already been defined with name '" + name + "' cannot declare a circle with the same name");
			}
			drawingTypes.put(name, DrawingType.CIRCLE);
			centers.put(name, center);

			final Position radiusTop = new RelativeFromPosition(center, 0, -radius.getDistance());
			final Position radiusBottom = new RelativeFromPosition(center, 0, radius.getDistance());
			final Position radiusLeft = new RelativeFromPosition(center, -radius.getDistance(), 0);
			final Position radiusRight = new RelativeFromPosition(center, radius.getDistance(), 0);

			radiusTops.put(name, radiusTop);
			radiusBottoms.put(name, radiusBottom);
			radiusLefts.put(name, radiusLeft);
			radiusRights.put(name, radiusRight);

			logger.info("Circle '" + name + "/" + layer + "' has been registered");

			return new Circle() {

				public String getName() {
					return name;
				}

				public Layer getLayer() {
					return TO_ACTIVE_BLUEPRINT_LAYER.apply(layer);
				}

				@Override
				public Iterable<Layer> getExtraLayers() {
					return Iterables.transform(activeLayers, TO_ACTIVE_BLUEPRINT_LAYER);
				}

				public Position getCenter() {
					return center;
				}

				public double getRadius() {
					return radius.getDistance();
				}

				public DistanceUnit getRadiusUnit() {
					return radius.getUnit();
				}

				public Position getRadiusTop() {
					return radiusTop;
				}

				public Position getRadiusBottom() {
					return radiusBottom;
				}

				public Position getRadiusLeft() {
					return radiusLeft;
				}

				public Position getRadiusRight() {
					return radiusRight;
				}

				public Rectangle getBoundingRectangle() {
					return new BoundingRectangle(
							center,
							new RelativeFromPosition(radiusTop, -radius.getDistance(), 0),
							new RelativeFromPosition(radiusTop, radius.getDistance(), 0),
							new RelativeFromPosition(radiusBottom, -radius.getDistance(), 0),
							new RelativeFromPosition(radiusBottom, radius.getDistance(), 0),
							radius.getDistance(),
							radius.getDistance(),
							name,
							layer,
							radius.getUnit());
				}

				public LaserAction getBorderAction() {
					return borderAction;
				}

				public Iterable<Drawing> getSubDrawings(final Iterable<String> activeLayers) {
					return Collections.emptyList();
				}

				public Iterable<Drawing> getSubDrawings() {
					return Collections.emptyList();
				}

			};
		}

		private Rectangle registerRectangleFromCenter(final String name, final String layer, final Iterable<String> activeLayers, final Position center, final Distance width, final Distance height,
				final LaserAction leftAction, final LaserAction rightAction, final LaserAction topAction, final LaserAction bottomAction) {
			return registerRectangle(name, layer, activeLayers,
					center,
					new RelativeFromPosition(center, -width.getDistance() / 2, -height.getDistance() / 2),
					new RelativeFromPosition(center, width.getDistance() / 2, -height.getDistance() / 2),
					new RelativeFromPosition(center, -width.getDistance() / 2, height.getDistance() / 2),
					new RelativeFromPosition(center, width.getDistance() / 2, height.getDistance() / 2), width, height,
					leftAction, rightAction, topAction, bottomAction);
		}

		private Rectangle registerRectangleFromTopLeft(final String name, final String layer, final Iterable<String> activeLayers, final Position topLeft, final Distance width, final Distance height,
				final LaserAction leftAction, final LaserAction rightAction, final LaserAction topAction, final LaserAction bottomAction) {
			return registerRectangle(name, layer, activeLayers,
					new RelativeFromPosition(topLeft, width.getDistance() / 2, height.getDistance() / 2),
					topLeft,
					new RelativeFromPosition(topLeft, width.getDistance(), 0),
					new RelativeFromPosition(topLeft, 0, height.getDistance()),
					new RelativeFromPosition(topLeft, width.getDistance(), height.getDistance()), width, height,
					leftAction, rightAction, topAction, bottomAction);
		}

		private Rectangle registerRectangle(final String name, final String layer,
				final Iterable<String> activeExtraLayer,
				final Position center, final Position topLeft,
				final Position topRight, final Position bottomLeft,
				final Position bottomRight, final Distance width, final Distance height,
				final LaserAction leftAction, final LaserAction rightAction, final LaserAction topAction, final LaserAction bottomAction) {
			if (drawingTypes.containsKey(name)) {
				throw new IllegalStateException("A drawing of type '" + drawingTypes.get(name) + "' has already been defined with name '" + name + "' cannot declare a rectangle with the same name");
			}
			drawingTypes.put(name, DrawingType.RECTANGLE);
			this.centers.put(name, center);
			this.topLefts.put(name, topLeft);
			this.topRights.put(name, topRight);
			this.bottomLefts.put(name, bottomLeft);
			this.bottomRights.put(name, bottomRight);

			logger.info("Rectangle '" + name + "/" + layer + "' has been registered");

			return new Rectangle() {

				public String getName() {
					return name;
				}

				public Layer getLayer() {
					return TO_ACTIVE_BLUEPRINT_LAYER.apply(layer);
				}

				@Override
				public Iterable<Layer> getExtraLayers() {
					return Iterables.transform(activeExtraLayer, TO_ACTIVE_BLUEPRINT_LAYER);
				}

				public Position getCenter() {
					return center;
				}

				public Position getTopLeft() {
					return topLeft;
				}

				public double getWidth() {
					return width.getDistance();
				}

				public double getHeight() {
					return height.getDistance();
				}

				public DistanceUnit getDimensionsUnit() {
					return width.getUnit();
				}

				public Rectangle getBoundingRectangle() {
					return this;
				}

				public Position getTopRight() {
					return topRight;
				}

				public Position getBottomRight() {
					return bottomRight;
				}

				public Position getBottomLeft() {
					return bottomLeft;
				}

				public LaserAction getLeftBorderAction() {
					return leftAction;
				}

				public LaserAction getRightBorderAction() {
					return rightAction;
				}

				public LaserAction getTopBorderAction() {
					return topAction;
				}

				public LaserAction getBottomBorderAction() {
					return bottomAction;
				}

				public Iterable<Drawing> getSubDrawings(final Iterable<String> activeLayers) {
					return Collections.emptyList();
				}

				public Iterable<Drawing> getSubDrawings() {
					return Collections.emptyList();
				}

			};
		}

	}

	private static class BoundingRectangle implements Rectangle {
		
		private final Position center;
		
		private final Position topLeft;
		private final Position topRight;
		private final Position bottomLeft;
		private final Position bottomRight;
		
		private final double width;
		private final double height;
		
		private final String name;
		private final String layer;
		
		private final DistanceUnit distanceUnit;
		
		public BoundingRectangle(Position center, Position topLeft,
				Position topRight, Position bottomLeft, Position bottomRight,
				double width, double height, String name, String layer,
				DistanceUnit distanceUnit) {
			super();
			this.center = center;
			this.topLeft = topLeft;
			this.topRight = topRight;
			this.bottomLeft = bottomLeft;
			this.bottomRight = bottomRight;
			this.width = width;
			this.height = height;
			this.name = name;
			this.layer = layer;
			this.distanceUnit = distanceUnit;
		}

		public String getName() {
			return "bounding_rectangle_of_" + name;
		}

		public Layer getLayer() {
			return TO_ACTIVE_BLUEPRINT_LAYER.apply(layer);
		}

		@Override
		public Iterable<Layer> getExtraLayers() {
			return Collections.emptyList();
		}

		public Position getCenter() {
			return center;
		}

		public Rectangle getBoundingRectangle() {
			throw new IllegalStateException("Not appliable");
		}

		public Position getTopLeft() {
			return topLeft;
		}

		public Position getTopRight() {
			return topRight;
		}

		public Position getBottomRight() {
			return bottomRight;
		}

		public Position getBottomLeft() {
			return bottomLeft;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		public DistanceUnit getDimensionsUnit() {
			return distanceUnit;
		}

		public LaserAction getLeftBorderAction() {
			return LaserAction.VIEW;
		}

		public LaserAction getRightBorderAction() {
			return LaserAction.VIEW;
		}

		public LaserAction getTopBorderAction() {
			return LaserAction.VIEW;
		}

		public LaserAction getBottomBorderAction() {
			return LaserAction.VIEW;
		}

		public Iterable<Drawing> getSubDrawings(final Iterable<String> activeLayers) {
			return Collections.emptyList();
		}

		public Iterable<Drawing> getSubDrawings() {
			return Collections.emptyList();
		}
	}

	/*
	 * 				// Nx = Bx + (h / w) * |By - Ay|
				// Ny = By + (h / w) * |Bx - Ax|
				final double w = getDistance(previousSeg.getValue0(), previousSeg.getValue1()).getDistance();
				final double h;
				
				if (index == 0 || index == (2 * numberOfCrenels)) {
					h = height.getDistance() / 2;
				} else {
					h = (((index + 1) % 2) * (height.getDistance())) + ((index % 2) * realWidth.getDistance());
				}
				
				if (index % 2 == 0) {
					if (direction == 1) {
						direction = -1;
					} else if (direction == -1) {
						direction = 1;
					}
				}
				
				
				final double aX = previousSeg.getValue0().getX();
				final double aY = previousSeg.getValue0().getY();
				
				final double bX = previousSeg.getValue1().getX();
				final double bY = previousSeg.getValue1().getY();
				
				final double deltaX = direction * (h / w) * (bY - aY);
				final double deltaY = direction * (h / w) * (bX - aX);
				
				logger.info("Crenel " + index + " has w=" + w + " and h=" + h + " deltaX=" + deltaX + " deltaY=" + deltaY);
				
				final Position next = new RelativeFromPosition(previousSeg.getValue1(), deltaX, deltaY);
				
				//final Position next = GeometryUtils.getOrthoPoint(previousSeg.getValue1(), previousSeg.getValue0(), new Distance(h, defaultDistanceUnit), direction);
				
				points.add(next);
				
				if (laserHinge != null) {
					if (index % 2 == 1 && hingeHoleRadius > 0 && hingeHoleDistance > 0) {
						double deltaXHinge = (previousSeg.getValue0().getX() - previousSeg.getValue1().getX());
						double deltaYHinge = (previousSeg.getValue0().getY() - previousSeg.getValue1().getY());
						
						// logger.info("Crenel CUT " + index + " direction=" + direction + " deltaXStart=" + deltaXStart);
						
						final Position cutTopStart = new RelativeFromPosition(previousSeg.getValue1(), (hingeHoleDistance / w) * deltaXHinge, (hingeHoleDistance / w) * deltaYHinge);
						final Position cutTopEnd = new RelativeFromPosition(next, ((hingeHoleDistance) / w) * deltaXHinge, ((hingeHoleDistance) / w) * deltaYHinge);
						hingePathes.add(Triplet.with(name + "_hinge_top_" + index, cutTopStart, cutTopEnd));
						
						final Position cutBottomStart = new RelativeFromPosition(previousSeg.getValue1(), ((hingeHoleDistance + hingeHoleRadius) / w) * deltaXHinge, ((hingeHoleDistance + hingeHoleRadius) / w) * deltaYHinge);
						final Position cutBottomEnd = new RelativeFromPosition(next, (((hingeHoleDistance + hingeHoleRadius)) / w) * deltaXHinge, (((hingeHoleDistance + hingeHoleRadius)) / w) * deltaYHinge);
						hingePathes.add(Triplet.with(name + "_hinge_bottom_" + index, cutBottomStart, cutBottomEnd));
						
						double extraHingeSpan = 1.1 * ((hingeHoleDistance + hingeHoleRadius) * Math.cos(Math.PI / 4) + (thickness / 2) * Math.sin(Math.PI / 4)) - height.getDistance();
						
						if (extraHingeSpan > 0) {
							final Pair<Position, Position> extraSpanCutSegment = GeometryUtils.getRectangleEdges(previousSeg.getValue1(), next, new Distance(extraHingeSpan, defaultDistanceUnit), Direction.right.getMultiplier());
							hingePathes.add(Triplet.with(name + "_extra_hinge_span_0_" + index, previousSeg.getValue1(), extraSpanCutSegment.getValue0()));
							hingePathes.add(Triplet.with(name + "_extra_hinge_span_1_" + index, extraSpanCutSegment.getValue0(), extraSpanCutSegment.getValue1()));
							hingePathes.add(Triplet.with(name + "_extra_hinge_span_2_" + index, extraSpanCutSegment.getValue1(), next));
						}
						
					}
				}
				
				previousSeg = Pair.with(previousSeg.getValue1(), next);
				

	 */
	
}
