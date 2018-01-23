package com.edraw.impl;

import com.edraw.Position;
import com.edraw.config.AngleUnit;
import com.edraw.config.DistanceUnit;
import com.edraw.geom.Point;

public class Rotation extends AbstractSingleTransformation {

	private final Point center;

	private final double sin;
	
	private final double cos;
	
	public Rotation(double angle, AngleUnit angleUnit, Point center) {
		super();
		this.center = center;
		this.sin = Math.sin(angleUnit.to(angle, AngleUnit.RAD));
		this.cos = Math.cos(angleUnit.to(angle, AngleUnit.RAD));
	}

	
	@Override
	protected Position transformPosition(final Position position) {
		final double deltaX = position.getX() - center.getCenter().getX();
		final double deltaY = position.getY() - center.getCenter().getY();
		
		final double newX = deltaX * cos + deltaY * sin + center.getCenter().getX();
		final double newY = -1 * deltaX * sin + deltaY * cos + center.getCenter().getY();
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
