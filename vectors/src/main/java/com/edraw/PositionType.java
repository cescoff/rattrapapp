package com.edraw;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;

public enum PositionType {
    CENTER("Center", DrawingType.CIRCLE, DrawingType.RECTANGLE, DrawingType.POINT),
    TOP_LEFT("TopLeft", DrawingType.RECTANGLE),
    TOP_RIGHT("TopRight", DrawingType.RECTANGLE),
    BOTTOM_LEFT("BottomLeft", DrawingType.RECTANGLE),
    BOTTOM_RIGHT("BottomRight", DrawingType.RECTANGLE),
    RADIUS_TOP("RadiusTop", DrawingType.CIRCLE),
    RADIUS_BOTTOM("RadiusBottom", DrawingType.CIRCLE),
    RADIUS_LEFT("RadiusLeft", DrawingType.CIRCLE),
    RADIUS_RIGHT("RadiusRight", DrawingType.CIRCLE);

    private final String varName;

    private final Set<DrawingType> supportedDrawings;

    private PositionType(String varName, DrawingType...supportedDrawings) {
        this.varName = varName;
        if (supportedDrawings == null) {
            this.supportedDrawings = Collections.emptySet();
        } else {
            this.supportedDrawings = Sets.newHashSet(supportedDrawings);
        }
    }

    public  static PositionType parse(final String value) {
        for (final PositionType positionType : PositionType.values()) {
            if (value.equals(positionType.varName)) {
                return positionType;
            }
        }
        throw new IllegalArgumentException("Unknown position type '" + value + "'");
    }

    public boolean accept(final DrawingType drawingType) {
        if (supportedDrawings.size() == 0) {
            return true;
        }
        return supportedDrawings.contains(drawingType);
    }

    public String getVarName() {
        return varName;
    }
}
