package com.edraw.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;


import com.edraw.Position;
import com.edraw.config.DistanceUnit;
import com.edraw.geom.BluePrint;
import com.edraw.geom.Circle;
import com.edraw.geom.Drawing;
import com.edraw.geom.Path;
import com.edraw.geom.Point;
import com.edraw.geom.Rectangle;
import com.edraw.utils.GeometryUtils;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

public abstract class BluePrintUtils {

	private BluePrintUtils() {};
	
	public static <D extends Drawing> Iterable<Position> getAllPositions(final Iterable<D> drawings) {
		final ImmutableList.Builder<Position> result = ImmutableList.builder();
		
		for (final Drawing drawing : drawings) {
			result.add(drawing.getCenter());
			if (drawing instanceof Circle) {
				final Circle circle = (Circle) drawing;
				result.add(circle.getRadiusTop());
				result.add(circle.getRadiusBottom());
				result.add(circle.getRadiusLeft());
				result.add(circle.getRadiusRight());
			} else if (drawing instanceof Path) {
				final Path path = (Path) drawing;
				result.addAll(getAllPositions(path.getPoints()));
			} else if (drawing instanceof Rectangle) {
				final Rectangle rectangle = (Rectangle) drawing;
				result.add(rectangle.getTopLeft());
				result.add(rectangle.getTopRight());
				result.add(rectangle.getBottomLeft());
				result.add(rectangle.getBottomRight());
			}
			result.addAll(getAllPositions(drawing.getSubDrawings(Collections.<String>emptyList())));
		}
		
		return result.build();
	}
	
	public static final BluePrint getBluePrint(final DistanceUnit defaultDistanceUnit, final Iterable<Drawing> drawings) {
		if (drawings == null || Iterables.isEmpty(drawings)) {
			throw new IllegalArgumentException("No drawings in blueprint");
		}
		final Map<String, Point> positionIndex = Maps.newHashMap();
		final Iterable<Position> allPositions = getAllPositions(drawings);

		final Set<String> allLayers = Sets.newLinkedHashSet();

		

		for (final Drawing drawing : drawings) {
			if (StringUtils.isNotEmpty(drawing.getName()) && (drawing instanceof Point)) {
				positionIndex.put(drawing.getName(), (Point) drawing);
			}
		}
		
		final Rectangle boundingRectangle = GeometryUtils.getBoundingRectangle(null, null, allPositions);
		
		return new BluePrint() {
			
			public Point getPoint(String name) {
				return positionIndex.get(name);
			}
			
			public Iterable<String> getLayerNames() {
				return allLayers;
			}
			
			public Iterable<Drawing> getLayerDrawings(Iterable<String> layerNames) {
				final Set<String> indexedLayerNames = Sets.newHashSet(layerNames);
				final ImmutableList.Builder<Drawing> result = ImmutableList.builder();
				for (final Drawing drawing : drawings) {
					if (StringUtils.isNotEmpty(drawing.getLayer())) {
						if (indexedLayerNames.contains(drawing.getLayer())) {
							result.add(drawing);
						}
					} else {
						result.add(drawing);
					}
				}
				return result.build();
			}
			
			public Iterable<Drawing> getDrawings() {
				return drawings;
			}
			
			public DistanceUnit getDefaultDistanceUnit() {
				return defaultDistanceUnit;
			}
			
			public Rectangle getBoundingRectangle() {
				return boundingRectangle;
			}
		};
	}
	
	public static final Function<Position, Point> ToPoint() {
		return new Function<Position, Point>() {

			public Point apply(final Position position) {
				return new Point() {
					
					public Iterable<Drawing> getSubDrawings(Iterable<String> activeLayers) {
						return Collections.emptyList();
					}
					
					public String getName() {
						return null;
					}
					
					public String getLayer() {
						return null;
					}
					
					public Position getCenter() {
						return position;
					}
					
					public Rectangle getBoundingRectangle() {
						return new EmptyRectangle(position);
					}

					public Iterable<Drawing> getSubDrawings() {
						return Collections.emptyList();
					}
				};
			}
		};
	}
	
}
