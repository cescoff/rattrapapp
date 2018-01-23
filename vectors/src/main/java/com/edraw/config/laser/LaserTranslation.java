package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "translation")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserTranslation extends LaserTransformation {

	@XmlAttribute
	private String point1;
	
	@XmlAttribute
	private String point2;
	
	public LaserTranslation() {
	}

	public String getPoint1() {
		return point1;
	}

	public String getPoint2() {
		return point2;
	}

}
