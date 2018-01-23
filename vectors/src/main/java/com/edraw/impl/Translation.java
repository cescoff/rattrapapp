package com.edraw.impl;

import com.edraw.Position;
import com.edraw.config.DistanceUnit;
import com.edraw.geom.Point;

public class Translation extends AbstractSingleTransformation {

	private final Point point1;
	
	private final Point point2;

	public Translation(Point point1, Point point2) {
		super();
		this.point1 = point1;
		this.point2 = point2;
	}

	@Override
	protected Position transformPosition(final Position position) {
		final double newX = point2.getCenter().getX() - point1.getCenter().getX() + position.getX();
		final double newY = point2.getCenter().getY() - point1.getCenter().getY() + position.getY();
		return new Position() {
			
			@Override
			public double getY() {
				return newY;
			}
			
			@Override
			public double getX() {
				return newX;
			}
			
			@Override
			public DistanceUnit getUnit() {
				return position.getUnit();
			}
		};
	}

	
	
}
