package org.athmis.wmoptimisation.algorithm.areaguard;

import javafx.geometry.Rectangle2D;

import org.athmis.wmoptimisation.changeset.Change;

public class Area extends Rectangle2D {

	public Area(double minX, double minY, double width, double height) {
		super(minX, minY, width, height);
	}

	public Area(Change node) {
		super(node.getLon(), node.getLat(), 0, 0);
	}

	// public void
}
