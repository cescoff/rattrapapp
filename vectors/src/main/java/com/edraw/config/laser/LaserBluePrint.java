package com.edraw.config.laser;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.edraw.config.DistanceUnit;
import com.google.common.collect.Lists;

@XmlRootElement(name = "blue-print")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserBluePrint {

	public LaserBluePrint() {
		super();
	}

	public LaserBluePrint(String distanceUnit) {
		super();
		this.distanceUnit = distanceUnit;
	}

	@XmlElement(name = "distance-unit")
	private String distanceUnit = DistanceUnit.MILLIMETERS.name();
	
	@XmlElementRef
	private Collection<LaserDrawing> drawings = Lists.newArrayList();

	public String getDistanceUnit() {
		return distanceUnit;
	}

	public Collection<LaserDrawing> getDrawings() {
		return drawings;
	}
	
}
