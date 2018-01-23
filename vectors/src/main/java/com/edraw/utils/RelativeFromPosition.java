package com.edraw.utils;

import com.edraw.Position;
import com.edraw.config.DistanceUnit;

public final class RelativeFromPosition implements Position {
	
	private final Position reference;
	
	private final double relativeX;
	
	private final double relativeY;

	public RelativeFromPosition(Position reference, double relativeX,
			double relativeY) {
		super();
		this.reference = reference;
		this.relativeX = relativeX;
		this.relativeY = relativeY;
	}

	public double getX() {
		return reference.getX() + relativeX;
	}

	public double getY() {
		return reference.getY() + relativeY;
	}

	public DistanceUnit getUnit() {
		return reference.getUnit();
	}
	
	public String toString() {
		return new StringBuilder("Position[").append(getX()).append(reference.getUnit().getSymbol()).append(", ").append(getY()).append(reference.getUnit().getSymbol()).append("]").toString();
	}
	
}