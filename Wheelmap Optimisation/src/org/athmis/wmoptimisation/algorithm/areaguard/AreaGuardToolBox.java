package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.*;

public class AreaGuardToolBox {

	private AreaGuardToolBox() {}

	public static Area combine(Area area1, Area area2) {
		List<Area> areas = new ArrayList<>();

		areas.add(area1);
		areas.add(area2);

		return getBoundingAreaForAreas(areas);
	}

	public static Area getBoundingAreaForAreas(Collection<Area> areas) {
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

		Area boundingBox = new Area(latMax, lonMax, latMin, lonMin);
		return boundingBox;
	}

	public static double getMaxEdge(Area nextBox) {
		double southNorth, westEast;

		southNorth = nextBox.getLatMaxN() - nextBox.getLatMinS();
		westEast = nextBox.getLonMaxE() - nextBox.getLonMinW();

		assertThatValuesValid(southNorth, westEast, nextBox);

		return Math.max(southNorth, westEast);
	}

	private static void assertThatValuesValid(double southNorth, double westEast, Area nextBox) {
		// TODO Auto-generated method stub
	}
}
