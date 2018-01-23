package com.edraw.config.laser;

import com.edraw.Direction;
import com.edraw.config.LaserAction;
import com.rattrap.utils.JAXBUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "crenel")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserCrenel extends LaserDrawing {

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
	private String action = LaserAction.CUT.name();
	
	@XmlAttribute
	private String direction = Direction.forward.name();
	
	@XmlAttribute(name = "hinge-hole-width")
	private String hingeHoleWidth;
	
	@XmlAttribute(name = "hinge-hole-distance")
	private String hingeHoleDistance;
	
	public LaserCrenel() {
	}

	public LaserCrenel(String name, String layer, LaserPoint point1, LaserPoint point2, String minWidth,
			String height, String borderOffSet) {
		super(name, layer);
		this.point1 = point1;
		this.point2 = point2;
		this.minWidth = minWidth;
		this.height = height;
		this.borderOffSet = borderOffSet;
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

	public String getAction() {
		return action;
	}

	public String getDirection() {
		return direction;
	}

	public String getHingeHoleWidth() {
		return hingeHoleWidth;
	}

	public String getHingeHoleDistance() {
		return hingeHoleDistance;
	}

	public Direction getRealDirection() {
		if (StringUtils.isEmpty(direction)) {
			return Direction.forward;
		}
		return Direction.valueOf(direction);
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(JAXBUtils.marshal(new LaserCrenel("Crenel", "Layer", new LaserPoint("10.0", "10.0"), new LaserPoint("20.0", "20.0"), "2.0", "height", "borderOffSet"), true));
	}
	
}
