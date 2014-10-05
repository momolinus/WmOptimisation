package org.athmis.wmoptimisation.algorithm.areaguard;

import javafx.geometry.Rectangle2D;

import org.athmis.wmoptimisation.changeset.Change;

// TODO kann man das nun mittels AffineTransformation nutzen???
public class Area extends Rectangle2D {

	public Area(Change node) {
		super(node.getLon(), node.getLat(), 0, 0);
	}

	public Area(double latN, double lonE, double latS, double lonW) {
		// x,y | width, height
		super(lonE, latN, lonE - lonW, latN - latS);
	}

	/**
	 * @return the most north latitude
	 */
	public double getLatMaxN() {
		return this.getMinY();
	}

	/**
	 * @return the most south latitude
	 */
	public double getLatMinS() {
		return this.getMaxY();
	}

	/**
	 * @return the most east longitude
	 */
	public double getLonMaxE() {
		return this.getMaxY();
	}

	/**
	 * @return the most west longitude
	 */
	public double getLonMinW() {
		return this.getMinY();
	}
}
