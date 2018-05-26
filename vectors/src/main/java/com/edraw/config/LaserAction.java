package com.edraw.config;

public enum LaserAction {

	HIDDEN("1px", "none"),
	VIEW("1.5px", "black"),
	CUT("0.1px", "red"),
	MARK("0.1px", "blue");

	private final String width;
	
	private final String color;

	private LaserAction(String width, String color) {
		this.width = width;
		this.color = color;
	}

	public String getWidth() {
		return width;
	}

	public String getColor() {
		return color;
	}
	
}
