package org.athmis.wmoptimisation.algorithm.areaguard;

import org.athmis.wmoptimisation.changeset.Change;

public class Area {

	/**
	 * maximum latitude (y: [-90, +90])
	 */
	private double maxLat;

	/**
	 * maximum longitude (x : [-180, +180])
	 */
	private double maxLon;

	/**
	 * minimum latitude (y: [-90, +90])
	 */
	private double minLat;

	/**
	 * minimum longitude (x : [-180, +180])
	 */
	private double minLon;

	public Area() {}

	public Area(Change node) {

		if (node.isWay()) {
			throw new IllegalArgumentException("ways still not supported");
		}

		minLon = node.getLon();
		maxLon = minLon;

		minLat = node.getLat();
		maxLat = minLat;
	}

	public Area(double maxLatN, double maxLonE, double minLatS, double minLonW) {
		this.maxLat = maxLatN;
		this.maxLon = maxLonE;

		this.minLat = minLatS;
		this.minLon = minLonW;
	}

	/**
	 * @return the most north latitude
	 */
	public double getLatMaxN() {
		return maxLat;
	}

	/**
	 * @return the most south latitude
	 */
	public double getLatMinS() {
		return minLat;
	}

	/**
	 * @return the most east longitude
	 */
	public double getLonMaxE() {
		return maxLon;
	}

	/**
	 * @return the most west longitude
	 */
	public double getLonMinW() {
		return minLon;
	}

	protected void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	protected void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}

	protected void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	protected void setMinLon(double minLon) {
		this.minLon = minLon;
	}
}
