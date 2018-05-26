package com.edraw;

import com.edraw.config.AngleUnit;
import com.edraw.config.laser.LaserRotation;
import com.edraw.config.laser.LaserSplitter;
import com.edraw.config.laser.LaserTransformation;
import com.edraw.config.laser.LaserTranslation;
import com.edraw.geom.BluePrint;
import com.edraw.impl.Rotation;
import com.edraw.impl.Splitter;
import com.edraw.impl.Translation;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class TransformationFactory {

	private static final TransformationFactory INSTANCE = new TransformationFactory();
	
	public TransformationFactory() {
	}

	public static TransformationFactory getInstance() {
		return INSTANCE;
	}
	
	public Transformation getTransformation(final Iterable<LaserTransformation> transformations, final BluePrint source) {
		return new AggregatedTransformation(Iterables.transform(transformations, ConfigToTransformation(source)));
	}
	
	private static class AggregatedTransformation implements Transformation {

		private final Iterable<Transformation> transformations;
		
		public AggregatedTransformation(Iterable<Transformation> transformations) {
			super();
			this.transformations = transformations;
		}

		@Override
		public Iterable<BluePrint> transform(final Iterable<BluePrint> bluePrints) {
			Iterable<BluePrint> current = bluePrints;
			for (final Transformation transformation : transformations) {
				current = transformation.transform(current);
			}
			return current;
		}

/*		@Override
		public Drawing transform(Drawing drawing) {
			Drawing current = drawing;
			for (final Transformation transformation : transformations) {
				current = transformation.transform(current);
			}
			return current;
		}*/
		
	}
	
	private static final Function<LaserTransformation, Transformation> ConfigToTransformation(final BluePrint source) { 
	
		return new Function<LaserTransformation, Transformation>() {

			@Override
			public Transformation apply(LaserTransformation laserTransformation) {
				if (laserTransformation instanceof LaserRotation) {
					final LaserRotation laserRotation = (LaserRotation) laserTransformation;
					if (source.getPoint(laserRotation.getCenterName())== null) {
						throw new ValidationError(ErrorMessage.create("No splitter point named '" + laserRotation.getCenterName() + "' found in drawing"));
					}
					return new Rotation(laserRotation.getAngle(), AngleUnit.valueOf(laserRotation.getAngleUnit()), source.getPoint(laserRotation.getCenterName()));
				}
				if (laserTransformation instanceof LaserTranslation) {
					final LaserTranslation laserTranslation = (LaserTranslation) laserTransformation;
					if (source.getPoint(laserTranslation.getPoint1())== null) {
						throw new ValidationError(ErrorMessage.create("No splitter point named '" + laserTranslation.getPoint1() + "' found in drawing"));
					}
					if (source.getPoint(laserTranslation.getPoint2())== null) {
						throw new ValidationError(ErrorMessage.create("No splitter point named '" + laserTranslation.getPoint2() + "' found in drawing"));
					}
					return new Translation(source.getPoint(laserTranslation.getPoint1()), source.getPoint(laserTranslation.getPoint2()));
				}
				if (laserTransformation instanceof LaserSplitter) {
					final LaserSplitter laserSplitter = (LaserSplitter) laserTransformation;
					if (source.getPoint(laserSplitter.getPoint1())== null) {
						throw new ValidationError(ErrorMessage.create("No splitter point named '" + laserSplitter.getPoint1() + "' found in drawing"));
					}
					if (source.getPoint(laserSplitter.getPoint2())== null) {
						throw new ValidationError(ErrorMessage.create("No splitter point named '" + laserSplitter.getPoint2() + "' found in drawing"));
					}
					return new Splitter(source.getPoint(laserSplitter.getPoint1()).getCenter(), source.getPoint(laserSplitter.getPoint2()).getCenter());
				}
				throw new IllegalStateException("Transformation of type '" + laserTransformation.getClass().getName() + "' is not supported yet");
			}
		};
	};
	
}
