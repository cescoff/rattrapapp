package com.edraw.config;

public enum AngleUnit {

	DEG,
	RAD;

	public double to(final double value, final AngleUnit unit) {
		if (unit == this) {
			return value;
		}
		if (this == DEG && unit == RAD) {
			return (value * Math.PI) / 180;
		} else if (this == RAD && unit == DEG) {
			return (value * 180) / Math.PI;
		}
		throw new IllegalArgumentException("Cannot convert from '" + this + "' to '" + unit + "'");
	}
	
}
