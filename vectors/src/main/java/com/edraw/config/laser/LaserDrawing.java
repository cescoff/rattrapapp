package com.edraw.config.laser;

import com.google.common.collect.Lists;

import javax.xml.bind.annotation.*;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(value = {
		LaserCircle.class,
		LaserArcCircle.class,
		LaserRectangle.class,
		LaserPath.class,
		LaserPoint.class,
		LaserCrenel.class,
		LaserHinge.class,
		LaserText.class})
public class LaserDrawing {

	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String layer;

	@XmlAttribute(name = "display-name")
	private String displayName;

	@XmlElementWrapper(name = "extra-layers") @XmlElement(name = "layer")
	private Collection<LaserLayer> extraLayers = Lists.newArrayList();

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

	public Collection<LaserLayer> getExtraLayers() { return extraLayers; }

}
