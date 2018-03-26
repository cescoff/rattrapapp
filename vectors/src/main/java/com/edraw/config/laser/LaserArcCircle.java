package com.edraw.config.laser;

import com.edraw.config.LaserAction;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "arc")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaserArcCircle extends LaserDrawing {

    @XmlElement
    private LaserPoint center;

    @XmlElement
    private LaserPoint point1;

    @XmlElement
    private LaserPoint point2;

    @XmlAttribute
    private String radius;

    @XmlAttribute
    private String action = LaserAction.CUT.name();

    public LaserPoint getCenter() {
        return center;
    }

    public LaserPoint getPoint1() {
        return point1;
    }

    public LaserPoint getPoint2() {
        return point2;
    }

    public String getRadius() {
        return radius;
    }

    public String getAction() {
        return action;
    }
}
