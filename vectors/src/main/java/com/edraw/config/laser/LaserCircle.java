package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.edraw.config.LaserAction;

@XmlRootElement(name = "circle")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserCircle extends LaserDrawing {

	@XmlAttribute(name = "center-x")
	private String centerX;
	
	@XmlAttribute(name = "center-y")
	private String centerY;
	
	@XmlAttribute
	private String radius;
	
	@XmlAttribute
	private String action = LaserAction.CUT.name();

	public LaserCircle() {
		super();
	}

	public LaserCircle(String name, String layer, String centerX, String centerY, String radius,
			String action) {
		super(name, layer);
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		this.action = action;
	}

	public String getCenterX() {
		return centerX;
	}

	public String getCenterY() {
		return centerY;
	}

	public String getRadius() {
		return radius;
	}

	public String getAction() {
		return action;
	}

}
