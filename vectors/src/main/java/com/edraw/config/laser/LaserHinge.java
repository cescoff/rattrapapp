package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.edraw.Direction;
import com.edraw.config.LaserAction;

@XmlRootElement(name = "hinge")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserHinge extends LaserDrawing {

	@XmlElement
	private LaserPoint point1;
	
	@XmlElement
	private LaserPoint point2;
	
	@XmlAttribute(name = "min-width")
	private String minWidth = "0.0";
	
	@XmlAttribute(name = "head-height")
	private String height = "1.0";
	
	@XmlAttribute(name = "border-offset")
	private String borderOffSet = "0.0";
	
	@XmlAttribute
	private String direction = Direction.forward.name();
	
	@XmlAttribute(name = "axle-distance1")
	private String axleDistance1 = "1.0";
	
	@XmlAttribute(name = "axle-distance2")
	private String axleDistance2 = "1.0";
	
	@XmlAttribute(name = "axle-layer")
	private String axleLayer;

	@XmlAttribute
	private String thickness;

	@XmlAttribute(name = "head-span")
	private String headSpan;
	
	@XmlAttribute(name = "head-span1")
	private String headSpan1;
	
	@XmlAttribute(name = "head-span2")
	private String headSpan2;
	
	@XmlAttribute
	private String radius;
	
	@XmlAttribute
	private String action = LaserAction.CUT.name();
	
	@XmlAttribute(name = "sharp-angle-layer")
	private String sharpAngleLayer;
	
	public LaserHinge() {
	}

	public LaserPoint getPoint1() {
		return point1;
	}

	public LaserPoint getPoint2() {
		return point2;
	}

	public String getMinWidth() {
		return minWidth;
	}

	public String getHeight() {
		return height;
	}

	public String getBorderOffSet() {
		return borderOffSet;
	}

	public String getDirection() {
		return direction;
	}

	public String getAxleDistance1() {
		return axleDistance1;
	}

	public String getAxleDistance2() {
		return axleDistance2;
	}

	public String getAxleLayer() {
		return axleLayer;
	}

	public String getThickness() {
		return thickness;
	}

	public String getHeadSpan() {
		return headSpan;
	}

	public String getRadius() {
		return radius;
	}

	public String getAction() {
		return action;
	}

	public String getHeadSpan1() {
		return headSpan1;
	}

	public String getHeadSpan2() {
		return headSpan2;
	}

	public String getSharpAngleLayer() {
		return sharpAngleLayer;
	}

}
