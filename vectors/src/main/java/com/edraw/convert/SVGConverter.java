package com.edraw.convert;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edraw.BluePrintConverter;
import com.edraw.config.DistanceUnit;
import com.edraw.geom.BluePrint;
import com.edraw.geom.Circle;
import com.edraw.geom.Drawing;
import com.edraw.geom.Path;
import com.edraw.geom.Point;
import com.edraw.geom.Rectangle;
import com.edraw.geom.Text;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class SVGConverter implements BluePrintConverter {

	private static final Logger logger = Logger.getLogger(SVGConverter.class);
	
	private static final String circleTemplate = "<circle id=\"${layer}\" cx=\"${cx}\" cy=\"${cy}\" r=\"${r}\" stroke=\"${scolor}\" stroke-width=\"${swidth}\" fill=\"none\" />";
	
	private static final String rectangleTemplate = "<rect id=\"${layer}\" x=\"${x}\" y=\"${y}\" width=\"${width}\" height=\"${height}\" style=\"fill:none;stroke-width:${swidth};stroke:${scolor}\" />";
	
	private static final String pathTemplate = "<path id=\"${layer}\" d=\"${path}\" stroke=\"${scolor}\" stroke-width=\"${swidth}\" fill=\"none\" />";
	
	private static final String lineTemplate = "<line id=\"${layer}\" x1=\"${x1}\" y1=\"${y1}\" x2=\"${x2}\" y2=\"${y2}\" style=\"stroke:${scolor};stroke-width:${swidth}\" />";
	
	public SVGConverter() {
	}

	public void convertTo(final BluePrint bluePrint, final Iterable<String> layers, final OutputStream outputStream, final Map<String, String> parameters) {
		final boolean html = "true".equals(parameters.get("html"));
		final StringBuilder stringBuilder = new StringBuilder();
		if (html) {
			stringBuilder.append("<html>\n<body>\n\n");
		}
		
		final Rectangle boundingRectangle = bluePrint.getBoundingRectangle();
		
		final double[] maxXAndY = new double[] {boundingRectangle.getTopRight().getX(), boundingRectangle.getBottomRight().getY()};
		
		stringBuilder.append("\t<svg width=\"").append(maxXAndY[0] + 10).append(bluePrint.getDefaultDistanceUnit().getSymbol()).append("\" height=\"").append(maxXAndY[1] + 10).append(bluePrint.getDefaultDistanceUnit().getSymbol()).append("\">\n");
		stringBuilder.append("\n\t<!-- Delta_X=").append((boundingRectangle.getTopRight().getX() - boundingRectangle.getTopLeft().getX())).append(", Delta_Y=").append((boundingRectangle.getBottomLeft().getY() - boundingRectangle.getTopLeft().getY())).append(" -->\n");

		final Map<String, Iterable<Drawing>> layers2Drawings = Maps.newLinkedHashMap();
		if (layers == null || Iterables.isEmpty(layers)) {
			layers2Drawings.put("all", bluePrint.getLayerDrawings(layers));
		} else {
			for (final String layer : layers) {
				layers2Drawings.put(layer, bluePrint.getLayerDrawings(ImmutableList.<String>of(layer)));
			}
		}
		
		for (final String layer : layers2Drawings.keySet()) {
			logger.debug("Handling layer '" + layer + "'");
			stringBuilder.append("\t\t<g id=\"").append(layer).append("_layer\">\n");
			for (final Drawing drawing : layers2Drawings.get(layer)) {
				logger.debug("Handling drawing '" + drawing.getName() + "'");
				stringBuilder.append(getSVG(drawing, layers));
			}
			stringBuilder.append("\t\t</g>\n\n");
		}
		stringBuilder.append("\t</svg>\n");
		
		if (html) {
			stringBuilder.append("\n</body></html>");
		}
		
		try {
			outputStream.write(stringBuilder.toString().getBytes());
		} catch (IOException e) {
			throw new IllegalStateException("Cannot write to output stream", e);
		}
	}

/*	private double[] getMaxXAndY(final Iterable<Drawing> drawings, final Iterable<String> layers) {
		double maxX = 0.0;
		double maxY = 0.0;
		
		for (final Drawing drawing : drawings) {
			try {
				final Rectangle bound = drawing.getBoundingRectangle();
				if (bound.getBottomRight().getX() > maxX) {
					maxX = bound.getBottomRight().getX();
				}
				if (bound.getBottomRight().getY() > maxY) {
					maxY = bound.getBottomRight().getY();
				}
				double[] subDrawingMaxXAndY = getMaxXAndY(drawing.getSubDrawings(layers), layers);
				if (subDrawingMaxXAndY[0] > maxX) {
					maxX = subDrawingMaxXAndY[0];
				}
				if (subDrawingMaxXAndY[1] > maxY) {
					maxY = subDrawingMaxXAndY[1];
				}
			} catch (Throwable t) {
				throw new IllegalStateException("Cannot get max X or max Y on drawing '" + drawing.getLayer() + "/" + drawing.getName() + "'", t);
			}
		}
		
		return new double[] {maxX, maxY};
	}*/
	
	private String getSVG(final Drawing drawing, final Iterable<String> layers) {
		final StringBuilder stringBuilder = new StringBuilder();

		for (final Drawing subDrawing : drawing.getSubDrawings(layers)) {
			stringBuilder.append(getSVG(subDrawing, layers));
		}
		
		final String layer;
		if (StringUtils.isEmpty(drawing.getLayer())) {
			layer = "default_layer";
		} else {
			layer = drawing.getLayer() + "_layer";
		}
		
		stringBuilder.append("\n\t\t\t<!-- Start of ").append(drawing.getLayer()).append("/").append(drawing.getName()).append(" -->\n");
		if (drawing instanceof Circle) {
			final Circle circle = (Circle) drawing;
			final String cx = circle.getCenter().getX() + circle.getCenter().getUnit().getSymbol();
			final String cy = circle.getCenter().getY() + circle.getCenter().getUnit().getSymbol();
			final String r = circle.getRadius() + circle.getRadiusUnit().getSymbol();
			final String strokeColor = circle.getBorderAction().getColor();
			final String strokeWidth = circle.getBorderAction().getWidth();
			stringBuilder.append("\t\t\t").append(
					StringUtils.replace(
					StringUtils.replace(
					StringUtils.replace(
					StringUtils.replace(
					StringUtils.replace(
					StringUtils.replace(circleTemplate,
							"${layer}", layer),
							"${cx}", cx),
							"${cy}", cy),
							"${r}", r),
							"${scolor}", strokeColor),
							"${swidth}", strokeWidth)).append("\n");
		} else if (drawing instanceof Rectangle) {
			final Rectangle rectangle = (Rectangle) drawing;
			final String cx = rectangle.getTopLeft().getX() + rectangle.getTopLeft().getUnit().getSymbol();
			final String cy = rectangle.getTopLeft().getY() + rectangle.getTopLeft().getUnit().getSymbol();
			final String width = rectangle.getWidth() + rectangle.getDimensionsUnit().getSymbol();
			final String height = rectangle.getHeight() + rectangle.getDimensionsUnit().getSymbol();
			final String strokeColor = rectangle.getLeftBorderAction().getColor();
			final String strokeWidth = rectangle.getLeftBorderAction().getWidth();
			stringBuilder.append("\t\t").append(
					StringUtils.replace(
					StringUtils.replace(
					StringUtils.replace(
					StringUtils.replace(
					StringUtils.replace(
					StringUtils.replace(
					StringUtils.replace(rectangleTemplate,
							"${layer}", layer),
							"${x}", cx),
							"${y}", cy),
							"${width}", width),
							"${height}", height),
							"${scolor}", strokeColor),
							"${swidth}", strokeWidth)).append("\n");
		} else if (drawing instanceof Path) {
			final Path path = (Path) drawing;

			final String strokeColor = path.getBorderAction().getColor();
			final String strokeWidth = path.getBorderAction().getWidth();
			
			if (path.getCenter().getUnit() == DistanceUnit.PIXELS) {
				// M 100 350 l 150 -300
				final String pathStr = new StringBuilder("M").append(Joiner.on(" L").join(Iterables.transform(path.getPoints(), new Function<Point, String>() {

					public String apply(Point point) {
						return new StringBuilder().append(point.getCenter().getX()).append(" ").append(point.getCenter().getY()).toString().toString();
					}}))).toString();
				
				stringBuilder.append("\t\t\t").append(
						StringUtils.replace(
						StringUtils.replace(
						StringUtils.replace(
						StringUtils.replace(pathTemplate,
								"${layer}", layer),
								"${path}", pathStr),
								"${scolor}", strokeColor),
								"${swidth}", strokeWidth)).append("\n");
				
/*				System.out.println("Drawing path : '" + Joiner.on("','").join(Iterables.transform(((Path) drawing).getPoints(), new Function<Point, String>() {

					@Override
					public String apply(Point point) {
						return new StringBuilder("[").append(point.getCenter().getX()).append(", ").append(point.getCenter().getY()).append("]").toString();
					}})));*/
			} else {
				Point previous = null;
				for (final Point point : path.getPoints()) {
					if (previous == null) {
						previous = point;
					} else {
						// <line x1="0mm" y1="0mm" x2="200mm" y2="200mm" style="stroke:rgb(255,0,0);stroke-width:2" />
						
						final String x1 = new StringBuilder().append(previous.getCenter().getX()).append(previous.getCenter().getUnit().getSymbol()).toString();
						final String y1 = new StringBuilder().append(previous.getCenter().getY()).append(previous.getCenter().getUnit().getSymbol()).toString();
						
						final String x2 = new StringBuilder().append(point.getCenter().getX()).append(point.getCenter().getUnit().getSymbol()).toString();
						final String y2 = new StringBuilder().append(point.getCenter().getY()).append(point.getCenter().getUnit().getSymbol()).toString();
						
						stringBuilder.append("\t\t\t").append(
								StringUtils.replace(
								StringUtils.replace(
								StringUtils.replace(
								StringUtils.replace(
								StringUtils.replace(
								StringUtils.replace(
								StringUtils.replace(lineTemplate,
										"${layer}", layer),
										"${x1}", x1),
										"${x2}", x2),
										"${y1}", y1),
										"${y2}", y2),
										"${scolor}", strokeColor),
										"${swidth}", strokeWidth)).append("\n");
						previous = point;
					}
				}
			}
		} else if (drawing instanceof Text) {
			final Text text = (Text) drawing;
			stringBuilder.
				append("\t\t\t<text x=\"").append(text.getCenter().getX()).append(text.getCenter().getUnit().getSymbol()).
				append("\" y=\"").append(text.getCenter().getY()).append(text.getCenter().getUnit().getSymbol()).
				append("\" style=\"").
					append("fill:none;size:").append(text.getSize()).append(text.getCenter().getUnit().getSymbol()).
					append(";stroke:").append(text.getAction().getColor()).
					append(";stroke-width:").append(text.getAction().getWidth()).
					append(";writting-mode:tb").
					append(";glyph-orientation-vertical:").append("90").
				append(";\">").append(text.getText()).
				append("</text>\n");
		} else {
			throw new IllegalStateException("Unsupported drawing of type '" + drawing.getClass().getName() + "'");
		}
		stringBuilder.append("\t\t\t<!-- End of ").append(drawing.getLayer()).append("/").append(drawing.getName()).append(" -->\n\n");
		return stringBuilder.toString();
	}
	
}
