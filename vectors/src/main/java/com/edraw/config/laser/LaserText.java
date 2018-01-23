package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import com.edraw.config.LaserAction;

@XmlRootElement(name = "text")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserText extends LaserDrawing {

	@XmlAttribute
	private String x;
	
	@XmlAttribute
	private String y;
	
	@XmlAttribute
	private String action = LaserAction.MARK.name();
	
	@XmlAttribute
	private String size = "10.0";
	
	@XmlAttribute
	private String text;
	
	public LaserText() {
	}

	public LaserText(String name, String layer) {
		super(name, layer);
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	public String getAction() {
		return action;
	}

	public String getSize() {
		return size;
	}

	public String getText() {
		return text;
	}

}
