package com.edraw.impl;

import com.edraw.Position;
import com.edraw.Transformation;
import com.edraw.config.DistanceUnit;
import com.edraw.config.LaserAction;
import com.edraw.geom.*;
import com.edraw.utils.GeometryUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractSingleTransformation implements Transformation, Function<Drawing, Drawing> {

	public final Iterable<BluePrint> transform(final Iterable<BluePrint> bluePrints) {
		return Iterables.transform(bluePrints, new Function<BluePrint, BluePrint>() {

			public BluePrint apply(final BluePrint bluePrint) {
				final Iterable<Position> allPositions = Iterables.transform(BluePrintUtils.getAllPositions(bluePrint.getDrawings()), new Function<Position, Position>() {

					public Position apply(Position position) {
						return transformPosition(position);
					}
					
				});
				final Rectangle boundingRectangle = GeometryUtils.getBoundingRectangle(null, null, allPositions);
				return new BluePrint() {
					
					public Point getPoint(String name) {
						return transformPoint(bluePrint.getPoint(name));
					}
					
					public Iterable<String> getLayerNames() {
						return bluePrint.getLayerNames();
					}
					
					public Iterable<Drawing> getLayerDrawings(Iterable<String> layerNames) {
						return transformDrawings(bluePrint.getLayerDrawings(layerNames));
					}
					
					public Iterable<Drawing> getDrawings() {
						return transformDrawings(bluePrint.getDrawings());
					}
					
					public DistanceUnit getDefaultDistanceUnit() {
						return bluePrint.getDefaultDistanceUnit();
					}

					public Rectangle getBoundingRectangle() {
						return boundingRectangle;
					}
				};
			}});
	}
	

//	@Override
	public final Drawing transform(Drawing drawing) {
		if (drawing instanceof Point) {
			return transformPoint((Point) drawing);
		} else if (drawing instanceof Circle) {
			return transformCircle((Circle) drawing);
		} else if (drawing instanceof Path) {
			return transformPath((Path) drawing);
		} else if (drawing instanceof Rectangle) {
			return transformRectangle((Rectangle) drawing);
		}
		throw new IllegalArgumentException("Drawing of type '" + drawing.getClass().getName() + "' is not supported");
	}

	public Drawing apply(Drawing arg0) {
		return transform(arg0);
	}

	private Iterable<Drawing> transformDrawings(final Iterable<Drawing> drawings) {
		return Iterables.transform(drawings, this);
	}
	
	private Circle transformCircle(final Circle circle) {
		if (circle == null) {
			return null;
		}
		return new Circle() {
			
			public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
				return transformDrawings(circle.getSubDrawings(activeLayers));
			}
			
			public String getName() {
				return circle.getName();
			}
			
			public Layer getLayer() {
				return circle.getLayer();
			}

			@Override
			public Iterable<Layer> getExtraLayers() {
				return circle.getExtraLayers();
			}

			public Position getCenter() {
				return transformPosition(circle.getCenter());
			}
			
			public Rectangle getBoundingRectangle() {
				return transformRectangle(circle.getBoundingRectangle());
			}
			
			public DistanceUnit getRadiusUnit() {
				return circle.getRadiusUnit();
			}
			
			public Position getRadiusTop() {
				return transformPosition(circle.getRadiusTop());
			}
			
			public Position getRadiusRight() {
				return transformPosition(circle.getRadiusRight());
			}
			
			public Position getRadiusLeft() {
				return transformPosition(circle.getRadiusLeft());
			}
			
			public Position getRadiusBottom() {
				return transformPosition(circle.getRadiusBottom());
			}
			
			public double getRadius() {
				return circle.getRadius();
			}
			
			public LaserAction getBorderAction() {
				return circle.getBorderAction();
			}

			public Iterable<Drawing> getSubDrawings() {
				return transformDrawings(circle.getSubDrawings());
			}
		};
	}
	
	private Rectangle transformRectangle(final Rectangle rectangle) {
		if (rectangle == null) {
			return null;
		}
		return new Rectangle() {
			
			public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
				return transformDrawings(rectangle.getSubDrawings(activeLayers));
			}
			
			public Iterable<Drawing> getSubDrawings() {
				return transformDrawings(rectangle.getSubDrawings());
			}

			public String getName() {
				return rectangle.getName();
			}
			
			public Layer getLayer() {
				return rectangle.getLayer();
			}

			@Override
			public Iterable<Layer> getExtraLayers() {
				return rectangle.getExtraLayers();
			}

			public Position getCenter() {
				return transformPosition(rectangle.getCenter());
			}
			
			public Rectangle getBoundingRectangle() {
				return transformRectangle(rectangle.getBoundingRectangle());
			}
			
			public double getWidth() {
				return rectangle.getWidth();
			}
			
			public Position getTopRight() {
				return transformPosition(rectangle.getTopRight());
			}
			
			public Position getTopLeft() {
				return transformPosition(rectangle.getTopLeft());
			}
			
			public LaserAction getTopBorderAction() {
				return rectangle.getTopBorderAction();
			}
			
			public LaserAction getRightBorderAction() {
				return rectangle.getRightBorderAction();
			}
			
			public LaserAction getLeftBorderAction() {
				return rectangle.getLeftBorderAction();
			}
			
			public double getHeight() {
				return rectangle.getHeight();
			}
			
			public DistanceUnit getDimensionsUnit() {
				return rectangle.getDimensionsUnit();
			}
			
			public Position getBottomRight() {
				return transformPosition(rectangle.getBottomRight());
			}
			
			public Position getBottomLeft() {
				return transformPosition(rectangle.getBottomLeft());
			}
			
			public LaserAction getBottomBorderAction() {
				return rectangle.getBottomBorderAction();
			}
		};
	}
	
	private Path transformPath(final Path path) {
		return new Path() {
			
			public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
				return transformDrawings(path.getSubDrawings(activeLayers));
			}
			
			public Iterable<Drawing> getSubDrawings() {
				return transformDrawings(path.getSubDrawings());
			}

			public String getName() {
				return path.getName();
			}
			
			public Layer getLayer() {
				return path.getLayer();
			}

			@Override
			public Iterable<Layer> getExtraLayers() {
				return path.getExtraLayers();
			}

			public Position getCenter() {
				return transformPosition(path.getCenter());
			}
			
			public Rectangle getBoundingRectangle() {
				return transformRectangle(path.getBoundingRectangle());
			}
			
			public Iterable<Point> getPoints() {
				return Iterables.transform(path.getPoints(), new Function<Point, Point>() {

					public Point apply(Point point) {
						return transformPoint(point);
					}});
			}
			
			public LaserAction getBorderAction() {
				return path.getBorderAction();
			}

			@Override
			public Optional<LaserAction> getHatchAction() {
				return path.getHatchAction();
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

        };
	}
	
	private Point transformPoint(final Point source) {
		return new Point() {
			
			public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
				return transformDrawings(source.getSubDrawings(activeLayers));
			}
			
			public String getName() {
				return source.getName();
			}
			
			public Layer getLayer() {
				return source.getLayer();
			}

			@Override
			public Iterable<Layer> getExtraLayers() {
				return source.getExtraLayers();
			}

			public Position getCenter() {
				return transformPosition(source.getCenter());
			}
			
			public Rectangle getBoundingRectangle() {
				return transformRectangle(source.getBoundingRectangle());
			}

			public Iterable<Drawing> getSubDrawings() {
				return transformDrawings(source.getSubDrawings());
			}

		};
	}

	protected abstract Position transformPosition(final Position position);
	
}
