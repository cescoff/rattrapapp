package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.edraw.config.LaserAction;

@XmlRootElement(name = "rectangle")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserRectangle extends LaserDrawing {

	@XmlAttribute(name = "center-x")
	private String centerPositionX;
	
	@XmlAttribute(name = "center-y")
	private String centerPositionY;

	@XmlAttribute(name = "top-left-x")
	private String topLeftPositionX;
	
	@XmlAttribute(name = "top-left-y")
	private String topLeftPositionY;
	
	@XmlAttribute
	private String width;
	
	@XmlAttribute
	private String height;
	
	@XmlAttribute(name = "square-radius")
	private String squareRadius;
	
	@XmlAttribute(name = "top-action")
	private String topAction = LaserAction.CUT.name();
	
	@XmlAttribute(name = "bottom-action")
	private String bottomAction = LaserAction.CUT.name();
	
	@XmlAttribute(name = "left-action")
	private String leftAction = LaserAction.CUT.name();
	
	@XmlAttribute(name = "right-action")
	private String rightAction = LaserAction.CUT.name();

	public LaserRectangle() {
		super();
	}

	public LaserRectangle(String name, String layer) {
		super(name, layer);
	}

	public LaserRectangle(String name, String layer, String centerPositionX, String centerPositionY,
			String topLeftPositionX, String topLeftPositionY, String width,
			String height, String squareRadius, String topAction,
			String bottomAction, String leftAction, String rightAction) {
		super();
		this.centerPositionX = centerPositionX;
		this.centerPositionY = centerPositionY;
		this.topLeftPositionX = topLeftPositionX;
		this.topLeftPositionY = topLeftPositionY;
		this.width = width;
		this.height = height;
		this.squareRadius = squareRadius;
		this.topAction = topAction;
		this.bottomAction = bottomAction;
		this.leftAction = leftAction;
		this.rightAction = rightAction;
	}

	public static LaserRectangle fromCenterWidthAndHeight(String name, String layer, String centerPositionX, String centerPositionY,
			String width, String height, String topAction,
			String bottomAction, String leftAction, String rightAction) {
		return new LaserRectangle(name, layer, centerPositionX, centerPositionY, null, null, width, height, null, topAction, bottomAction, leftAction, rightAction);
	}
	
	public static LaserRectangle fromCenterRadius(String name, String layer, String centerPositionX, String centerPositionY,
			String squareRadius, String topAction,
			String bottomAction, String leftAction, String rightAction) {
		return new LaserRectangle(name, layer, centerPositionX, centerPositionY, null, null, null, null, squareRadius, topAction, bottomAction, leftAction, rightAction);
	}
	
	public static LaserRectangle fromTopLeft(String name, String layer, String topLeftPositionX, String topLeftPositionY,
			String width, String height, String topAction,
			String bottomAction, String leftAction, String rightAction) {
		return new LaserRectangle(name, layer, null, null, topLeftPositionX, topLeftPositionY, width, height, null, topAction, bottomAction, leftAction, rightAction);
	}
	
	public String getCenterPositionX() {
		return centerPositionX;
	}

	public String getCenterPositionY() {
		return centerPositionY;
	}

	public String getTopLeftPositionX() {
		return topLeftPositionX;
	}

	public String getTopLeftPositionY() {
		return topLeftPositionY;
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	public String getSquareRadius() {
		return squareRadius;
	}

	public String getTopAction() {
		return topAction;
	}

	public String getBottomAction() {
		return bottomAction;
	}

	public String getLeftAction() {
		return leftAction;
	}

	public String getRightAction() {
		return rightAction;
	}

}
