package com.edraw.config.laser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(value = {LaserRotation.class, LaserTranslation.class, LaserSplitter.class})
public abstract class LaserTransformation {

	public LaserTransformation() {
	}

}
