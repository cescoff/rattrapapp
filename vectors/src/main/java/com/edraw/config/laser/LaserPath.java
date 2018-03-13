package com.edraw.config.laser;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.edraw.config.LaserAction;
import com.google.common.collect.Lists;

@XmlRootElement(name = "path")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserPath extends LaserDrawing {

	@XmlAttribute(name = "filet-radius")
	private String filetRadius;

	@XmlElement(name = "point")
	private Collection<LaserPoint> points = Lists.newArrayList();
	
	@XmlAttribute
	private String action = LaserAction.CUT.name();
	
	public LaserPath() {
	}

	public Collection<LaserPoint> getPoints() {
		return points;
	}

	public String getAction() {
		return action;
	}

	public String getFiletRadius() { return filetRadius; }

}
