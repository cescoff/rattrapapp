package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(value = {LaserCircle.class, LaserRectangle.class, LaserPath.class, LaserPoint.class, LaserCrenel.class, LaserHinge.class, LaserText.class})
public class LaserDrawing {

	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String layer;

	@XmlAttribute(name = "display-name")
	private String displayName;
	
	public LaserDrawing() {
		super();
	}

	public LaserDrawing(String name, String layer) {
		super();
		this.name = name;
		this.layer = layer;
	}

	public String getName() {
		return name;
	}

	public String getLayer() {
		return layer;
	}

	public String getDisplayName() {
		return displayName;
	}

}
