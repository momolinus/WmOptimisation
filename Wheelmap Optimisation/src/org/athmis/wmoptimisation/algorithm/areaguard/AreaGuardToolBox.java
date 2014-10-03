package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.Collection;

import org.athmis.wmoptimisation.changeset.ChangeSetUpdateAble;

import javafx.geometry.Rectangle2D;

public class AreaGuardToolBox {

	private AreaGuardToolBox() {}

	/**
	 * @param r1
	 * @param r2
	 * @return
	 * @see ChangeSetUpdateAble#updateBoundingBox(org.athmis.wmoptimisation.changeset.Change)
	 */
	public static Rectangle2D combine(Rectangle2D r1, Rectangle2D r2) {
		Rectangle2D combinedRectangle;

		combinedRectangle = Rectangle2D.EMPTY;

		return combinedRectangle;
	}

	public static Rectangle2D getBoxForAreas(Collection<Area> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	public static double getMaxEdge(Rectangle2D nextBox) {
		// TODO Auto-generated method stub
		return 0;
	}
}
