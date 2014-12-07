package org.athmis.wmoptimisation.algorithm.areaguard;

public class AreaBuilder {

	// XXX statt ein Area north, south, west east als double
	private Area area;

	public AreaBuilder() {
		area = new Area();
	}

	// TODO mal bei Apache Command schauen, wie die es machen -> Bloggen
	public Area create() {
		Area result;
		result = area;
		area = new Area();
		return result;
	}

	public AreaBuilder withEast(double east) {
		area.setMaxLon(east);
		return this;
	}

	public AreaBuilder withNorth(double north) {
		area.setMaxLat(north);
		return this;
	}

	public AreaBuilder withSouth(double south) {
		area.setMinLat(south);
		return this;
	}

	public AreaBuilder withWest(double west) {
		area.setMinLon(west);
		return this;
	}
}
