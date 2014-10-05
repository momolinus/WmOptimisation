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
	public static Area combine(Rectangle2D r1, Rectangle2D r2) {
		Area combinedRectangle;


		return null;
	}

	public static Area getBoxForAreas(Collection<Area> areas) {
		Rectangle2D boundingBox;
		double latMin, latMax;
		double lonMin, lonMax;

		// latMin is the largest latitude in south, max value could be -90°
		latMin = 1000.0;
		// latMax is the largest latitude in north, max value could be 90°
		latMax = -1000.0;

		lonMin = 1000.0;
		lonMax = -1000.0;

		for (Area area : areas) {
			latMin = Math.min(latMin, area.getLatMinS());
			latMax = Math.max(latMax, area.getLatMaxN());

			lonMin = Math.min(lonMin, area.getLonMinW());
			lonMax = Math.max(lonMax, area.getLonMaxE());
		}

		// boundingBox = new Area(minX, minY, width, height)
		return null;
	}

	public static double getMaxEdge(Rectangle2D nextBox) {
		// TODO implement in next sprint
		return 0;
	}
}
