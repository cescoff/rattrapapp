package com.edraw;

public enum Direction {
	forward,
	backward;
	
	public Direction change() {
		switch (this) {
		case forward:
			return backward;

		case backward:
			return forward;

		default:
			break;
		}
		return this;
	}
	
/*	private final int multiplier;

	private Direction(int multiplier) {
		this.multiplier = multiplier;
	}

	public int getMultiplier() {
		return multiplier;
	}*/
	
}