package com.edraw.config;

import com.edraw.VarContext;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DistanceUnit {

	PIXELS("px"),
	MILLIMETERS("mm");
	
	private String symbol;
	
	private DistanceUnit(final String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public static DistanceUnit readStringValue(final String value) {
		for (final DistanceUnit distanceUnit : values()) {
			if (distanceUnit.name().equals(value)) {
				return distanceUnit;
			}
			if (distanceUnit.symbol.equals(value)) {
				return distanceUnit;
			}
		}
		return DistanceUnit.PIXELS;
	}
	
	public static Distance parse(final VarContext varContext, final String value, final DistanceUnit defaultUnit) {
		if (StringUtils.isEmpty(value)) {
			return new Distance(0, defaultUnit);
		}
		for (final DistanceUnit distanceUnit : DistanceUnit.values()) {
			final Pattern parser = Pattern.compile("([0-9\\.\\,]+)\\s*(" + distanceUnit.symbol + ")");
			final Matcher matcher = parser.matcher(value);
			if (matcher.find()) {
				final String distanceStr = matcher.group(1);
				try {
					return new Distance(varContext.evaluate(distanceStr), distanceUnit);
				} catch (Exception e) {
					throw new IllegalStateException("Cannot evaluate expression '" + distanceStr + "'", e);
				}
			}
		}
		try {
			return new Distance(varContext.evaluate(value), defaultUnit);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot evaluate expression '" + value + "'", e);
		}
	}
	
}
