package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "point")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserPoint extends LaserDrawing {

	@XmlAttribute
	private String x;
	
	@XmlAttribute
	private String y;

	public LaserPoint() {
		super();
	}

	public LaserPoint(String x, String y) {
		super();
		this.x = x;
		this.y = y;
	}

	public LaserPoint(String name, String layer, String x, String y) {
		super(name, layer);
		this.x = x;
		this.y = y;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

}
