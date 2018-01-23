package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.edraw.config.AngleUnit;

@XmlRootElement(name = "rotation")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserRotation extends LaserTransformation {

	@XmlAttribute(name = "center")
	private String centerName;
	
	@XmlAttribute
	private double angle;
	
	@XmlAttribute(name = "unit")
	private String angleUnit = AngleUnit.DEG.name();
	
	public LaserRotation() {
	}

	public String getCenterName() {
		return centerName;
	}

	public double getAngle() {
		return angle;
	}

	public String getAngleUnit() {
		return angleUnit;
	}

}
