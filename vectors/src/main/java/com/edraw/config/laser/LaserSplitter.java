package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "splitter")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserSplitter extends LaserTransformation {

	@XmlAttribute
	private String point1;
	
	@XmlAttribute
	private String point2;
	
	public LaserSplitter() {
	}

	public String getPoint1() {
		return point1;
	}

	public String getPoint2() {
		return point2;
	}

}
