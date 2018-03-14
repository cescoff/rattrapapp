package com.edraw.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.edraw.Position;
import com.edraw.Transformation;
import com.edraw.config.LaserAction;
import com.edraw.geom.*;
import com.edraw.utils.GeometryUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Splitter implements Transformation {

	private static final Logger logger = LoggerFactory.getLogger(Splitter.class);
	
	private final Pair<Position, Position> splitterLine;
	
	public Splitter(final Position lineStart, final Position lineEnd) {
		this.splitterLine = Pair.with(lineStart, lineEnd);
	}

	@Override
	public Iterable<BluePrint> transform(Iterable<BluePrint> bluePrints) {
		final ImmutableList.Builder<BluePrint> result = ImmutableList.builder();
		for (final BluePrint bluePrint : bluePrints) {
			logger.info("Splitting BluePrint");
			final Optional<Pair<Iterable<Position>, Iterable<Position>>> globalSplit = splitBoundingRectangle(bluePrint.getBoundingRectangle());
			if (!globalSplit.isPresent()) {
				logger.info("No split bounding rectangle found for positons start='{}', end='{}'", splitterLine.getValue0(), splitterLine.getValue1());
				result.add(bluePrint);
			} else {
				logger.info("Splitting...");
				Pair<Iterable<Drawing>, Iterable<Drawing>> split = split(bluePrint.getDrawings(), globalSplit.get(), true);
				result.add(BluePrintUtils.getBluePrint(bluePrint.getDefaultDistanceUnit(), split.getValue0()));
				result.add(BluePrintUtils.getBluePrint(bluePrint.getDefaultDistanceUnit(), split.getValue1()));
			}
		}
		return result.build();
	}

	private Pair<Iterable<Drawing>, Iterable<Drawing>> split(final Iterable<Drawing> drawings, final Pair<Iterable<Position>, Iterable<Position>> globalSplit, final boolean addCut) {
		final ImmutableList.Builder<Drawing> drawings0 = ImmutableList.builder();
		final ImmutableList.Builder<Drawing> drawings1 = ImmutableList.builder();
		
		for (final Drawing drawing : drawings) {
			logger.debug("Splitting drawing '" + drawing.getName() + "'");
			if ((drawing instanceof Point) || (drawing instanceof Circle)) {
				if (GeometryUtils.isInPolygon(drawing.getCenter(), globalSplit.getValue0())) {
					drawings0.add(drawing);
				} else if (GeometryUtils.isInPolygon(drawing.getCenter(), globalSplit.getValue1())) {
					drawings1.add(drawing);
				} else {
					throw new IllegalStateException("Internal error : Cannot determine the point split position (" + drawing.getName() + "/" + drawing.getLayer() + ")");
				}
			} else if (drawing instanceof Rectangle) {
				final Optional<Pair<Path, Path>> split = splitRectangle((Rectangle) drawing);
				if (!split.isPresent()) {
					throw new IllegalStateException("Internal error : Cannot determine the rectangle split position (" + drawing.getName() + "/" + drawing.getLayer() + ")");
				} else {
					drawings0.add(split.get().getValue0());
					drawings1.add(split.get().getValue1());
				}
			} else if (drawing instanceof Path) {
				final List<Position> positions0 = Lists.newArrayList();
				final List<Position> positions1 = Lists.newArrayList();
				for (final Pair<Position, Position> segment : getPathSegments((Path) drawing)) {
					final Optional<Position> intersection = GeometryUtils.getIntersection(segment, splitterLine, true);
					if (intersection.isPresent()) {
						if (GeometryUtils.isInPolygon(segment.getValue0(), globalSplit.getValue0())) {
							positions0.add(segment.getValue0());
							positions0.add(intersection.get());
							
							positions1.add(intersection.get());
							positions1.add(segment.getValue1());
						} else {
							positions1.add(segment.getValue0());
							positions1.add(intersection.get());

							positions0.add(intersection.get());
							positions0.add(segment.getValue1());
						}
					} else {
						if (GeometryUtils.isInPolygon(segment.getValue0(), globalSplit.getValue0())) {
							if (logger.isDebugEnabled()) logger.debug("Adding drawing '" + drawing.getName() + "' to first split");
							positions0.add(segment.getValue0());
							positions0.add(segment.getValue1());
						} else {
							if (logger.isDebugEnabled()) logger.debug("Adding drawing '" + drawing.getName() + "' to second split");
							positions1.add(segment.getValue0());
							positions1.add(segment.getValue1());
						}
					}
				}
				if (positions0.size() >= 2) {
					drawings0.add(getPath(drawing.getName(), drawing.getLayer().getName(), positions0, ((Path) drawing).getBorderAction()));
				}
				if (positions1.size() >= 2) {
					drawings1.add(getPath(drawing.getName(), drawing.getLayer().getName(), positions1, ((Path) drawing).getBorderAction()));
				}
			}
			final Pair<Iterable<Drawing>, Iterable<Drawing>> subdrawings = split(drawing.getSubDrawings(), globalSplit, false);
			drawings0.addAll(subdrawings.getValue0());
			drawings1.addAll(subdrawings.getValue1());
		}
		if (addCut) {
			logger.info("Adding 'Splitter' path");
			return Pair.<Iterable<Drawing>, Iterable<Drawing>>with(
					drawings0.add(getPath("Splitter", null, ImmutableList.<Position>of(splitterLine.getValue0(),  splitterLine.getValue1()), LaserAction.CUT)).build(), 
					drawings1.add(getPath("Splitter", null, ImmutableList.<Position>of(splitterLine.getValue0(),  splitterLine.getValue1()), LaserAction.CUT)).build());
		}
		return Pair.<Iterable<Drawing>, Iterable<Drawing>>with(
				drawings0.build(), 
				drawings1.build());
	}
	
	private Iterable<Pair<Position, Position>> getPathSegments(final Path path) {
		final ImmutableList.Builder<Pair<Position, Position>> result = ImmutableList.builder();
		Position previous = null;
		for (final Point point : path.getPoints()) {
			if (previous != null) {
				result.add(Pair.with(previous, point.getCenter()));
			}
			previous = point.getCenter();
		}
		return result.build();
	}
	
	private Optional<Pair<Iterable<Position>, Iterable<Position>>> splitBoundingRectangle(final Rectangle boundingRectangle) {
		final Optional<Position> topIntersection = GeometryUtils.getIntersection(Pair.with(boundingRectangle.getTopLeft(), boundingRectangle.getTopRight()), splitterLine, true);
		final Optional<Position> bottomIntersection = GeometryUtils.getIntersection(Pair.with(boundingRectangle.getBottomLeft(), boundingRectangle.getBottomRight()), splitterLine, true);
		final Optional<Position> leftIntersection = GeometryUtils.getIntersection(Pair.with(boundingRectangle.getTopLeft(), boundingRectangle.getBottomLeft()), splitterLine, true);
		final Optional<Position> rightIntersection = GeometryUtils.getIntersection(Pair.with(boundingRectangle.getTopRight(), boundingRectangle.getBottomRight()), splitterLine, true);
		
		final List<Optional<Position>> intersections = Lists.<Optional<Position>>newArrayList(topIntersection, bottomIntersection, leftIntersection, rightIntersection);
		
		int numberOfIntersections = 0;
		
		for (final Optional<Position> intersection : intersections) {
			if (intersection.isPresent()) {
				numberOfIntersections++;
			}
		}
		
		if (numberOfIntersections <= 1) {
			return Optional.absent();
		}
		
		if (numberOfIntersections > 2) {
			throw new IllegalStateException("Internal error : found " + numberOfIntersections + " when calculating rectangle and line intersection");
		}
		
		final Iterable<Position> polygon1;
		final Iterable<Position> polygon2;
		
		if (topIntersection.isPresent() && bottomIntersection.isPresent()) {
			polygon1 = ImmutableList.of(boundingRectangle.getTopLeft(), topIntersection.get(), bottomIntersection.get(), boundingRectangle.getBottomLeft());
			polygon2 = ImmutableList.of(topIntersection.get(), boundingRectangle.getTopRight(), boundingRectangle.getBottomRight(), bottomIntersection.get());
		} else if (leftIntersection.isPresent() && rightIntersection.isPresent()) {
			polygon1 = ImmutableList.of(boundingRectangle.getTopLeft(), boundingRectangle.getTopRight(), rightIntersection.get(), leftIntersection.get());
			polygon2 = ImmutableList.of(leftIntersection.get(), rightIntersection.get(), boundingRectangle.getBottomRight(), boundingRectangle.getBottomLeft());
		} else if (leftIntersection.isPresent() && topIntersection.isPresent()) {
			polygon1 = ImmutableList.of(boundingRectangle.getTopLeft(), topIntersection.get(), leftIntersection.get());
			polygon2 = ImmutableList.of(topIntersection.get(), boundingRectangle.getTopRight(), boundingRectangle.getBottomRight(), boundingRectangle.getBottomLeft(), leftIntersection.get());
		} else if (leftIntersection.isPresent() && bottomIntersection.isPresent()) {
			polygon1 = ImmutableList.of(boundingRectangle.getTopLeft(), boundingRectangle.getTopRight(), boundingRectangle.getBottomRight(), bottomIntersection.get(), leftIntersection.get());
			polygon2 = ImmutableList.of(leftIntersection.get(), bottomIntersection.get(), boundingRectangle.getBottomLeft());
		} else if (rightIntersection.isPresent() && topIntersection.isPresent()) {
			polygon1 = ImmutableList.of(boundingRectangle.getTopLeft(), topIntersection.get(), rightIntersection.get(), boundingRectangle.getBottomRight(), boundingRectangle.getBottomLeft());
			polygon2 = ImmutableList.of(topIntersection.get(), boundingRectangle.getTopRight(), rightIntersection.get());
		} else if (rightIntersection.isPresent() && bottomIntersection.isPresent()) {
			polygon1 = ImmutableList.of(boundingRectangle.getTopLeft(), boundingRectangle.getTopRight(), rightIntersection.get(), bottomIntersection.get(), boundingRectangle.getBottomRight());
			polygon2 = ImmutableList.of(bottomIntersection.get(), boundingRectangle.getBottomRight(), rightIntersection.get());
		} else {
			throw new IllegalStateException("Internal error : cannot split bounding rectangle, because pair of intersections is not described");
		}
		
		return Optional.of(Pair.with(polygon1, polygon2));
		
	}
	
	private Optional<Pair<Path, Path>> splitRectangle(final Rectangle rectangle) {
		final Optional<Pair<Iterable<Position>, Iterable<Position>>> spliRectangles = splitBoundingRectangle(rectangle);
		if (!spliRectangles.isPresent()) {
			return Optional.absent();
		}
		
//		final Iterable<Position> path0 = ImmutableList.<Position>builder().addAll(spliRectangles.get().getValue0()).add(Iterables.getFirst(spliRectangles.get().getValue0(), null)).build();
//		final Iterable<Position> path1 = ImmutableList.<Position>builder().addAll(spliRectangles.get().getValue1()).add(Iterables.getFirst(spliRectangles.get().getValue1(), null)).build();
		
		return Optional.of(Pair.with(getPath(rectangle.getName(), rectangle.getLayer().getName(), spliRectangles.get().getValue0(), rectangle.getBottomBorderAction()),
				getPath(rectangle.getName(), rectangle.getLayer().getName(), spliRectangles.get().getValue0(), rectangle.getBottomBorderAction())));
	}
	
	private final Path getPath(final String name, final String layer, final Iterable<Position> positions, final LaserAction action) {
		if (positions == null || Iterables.size(positions) < 2) {
			throw new IllegalArgumentException("Cannot create a path with less than 2 positions");
		}
		return new Path() {
			
			@Override
			public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
				return Collections.emptyList();
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
				return Collections.emptyList();
			}

			@Override
			public Position getCenter() {
				return GeometryUtils.getBaryCentricPosition(positions);
			}
			
			@Override
			public Rectangle getBoundingRectangle() {
				return GeometryUtils.getBoundingRectangle(name, layer, positions);
			}
			
			@Override
			public Iterable<Point> getPoints() {
				final AtomicInteger counter = new AtomicInteger(0);
				return Iterables.transform(positions, new Function<Position, Point>() {

					
					@Override
					public Point apply(final Position position) {
						counter.incrementAndGet();
						return new Point() {
							
							@Override
							public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
								return Collections.emptyList();
							}
							
							@Override
							public String getName() {
								return name + "_" + counter.get();
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
			public Iterable<Drawing> getSubDrawings() {
				return Collections.emptyList();
			}
		};
	}

	
	
}
