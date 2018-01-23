package com.edraw.config.laser;

import com.edraw.config.LaserAction;
import com.rattrap.utils.JAXBUtils;

public class LaserConfigTest {

	public static void main(String[] args) throws Exception {
		final LaserBluePrint bluePrint = new LaserBluePrint();
		
		bluePrint.getDrawings().add(new LaserCircle("Global", "Bg", "50mm", "50mm", "40mm", LaserAction.MARK.name()));
		//String centerPositionX, String centerPositionY, String squareRadius, String topAction, String bottomAction, String leftAction, String rightAction
		bluePrint.getDrawings().add(LaserRectangle.fromCenterRadius("Emptiness", "Bg", "50mm", "50mm", "40mm", LaserAction.CUT.name(), LaserAction.CUT.name(), LaserAction.CUT.name(), LaserAction.CUT.name()));
		bluePrint.getDrawings().add(LaserRectangle.fromCenterWidthAndHeight("Emptiness", "Bg", "50mm", "50mm", "40mm", "40mm", LaserAction.CUT.name(), LaserAction.CUT.name(), LaserAction.CUT.name(), LaserAction.CUT.name()));
		
		System.out.println(JAXBUtils.marshal(bluePrint, true));
		
	}
	
}
