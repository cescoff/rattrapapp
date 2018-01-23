package com.edraw.config;

public class Distance {

	private final double distance;
	
	private final DistanceUnit unit;

	public Distance(double distance, DistanceUnit unit) {
		super();
		this.distance = distance;
		this.unit = unit;
	}

	public double getDistance() {
		return distance;
	}

	public DistanceUnit getUnit() {
		return unit;
	}
	
	public String toString() {
		return new StringBuilder().append(distance).append(unit.getSymbol()).toString();
	}
	
}
