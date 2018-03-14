package com.edraw.impl;

import java.util.Collections;

import com.edraw.Position;
import com.edraw.config.DistanceUnit;
import com.edraw.config.LaserAction;
import com.edraw.geom.Drawing;
import com.edraw.geom.Layer;
import com.edraw.geom.Rectangle;

public class EmptyRectangle implements Rectangle {
	
	private final Position center;

	public EmptyRectangle(Position center) {
		super();
		this.center = center;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Layer getLayer() {
		return null;
	}

	@Override
	public Iterable<Layer> getExtraLayers() {
		return Collections.emptyList();
	}

	@Override
	public Position getCenter() {
		return center;
	}

	@Override
	public Rectangle getBoundingRectangle() {
		return new EmptyRectangle(center);
	}

	@Override
	public Position getTopLeft() {
		return center;
	}

	@Override
	public Position getTopRight() {
		return center;
	}

	@Override
	public Position getBottomRight() {
		return center;
	}

	@Override
	public Position getBottomLeft() {
		return center;
	}

	@Override
	public double getWidth() {
		return 0;
	}

	@Override
	public double getHeight() {
		return 0;
	}

	@Override
	public DistanceUnit getDimensionsUnit() {
		return center.getUnit();
	}

	@Override
	public LaserAction getLeftBorderAction() {
		return LaserAction.VIEW;
	}

	@Override
	public LaserAction getRightBorderAction() {
		return LaserAction.VIEW;
	}

	@Override
	public LaserAction getTopBorderAction() {
		return LaserAction.VIEW;
	}

	@Override
	public LaserAction getBottomBorderAction() {
		return LaserAction.VIEW;
	}

	@Override
	public Iterable<Drawing> getSubDrawings(final Iterable<String> activeLayers) {
		return Collections.emptyList();
	}

	@Override
	public Iterable<Drawing> getSubDrawings() {
		return Collections.emptyList();
	}
	
}