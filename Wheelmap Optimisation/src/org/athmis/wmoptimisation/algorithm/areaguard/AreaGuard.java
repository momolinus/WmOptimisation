package org.athmis.wmoptimisation.algorithm.areaguard;

import javafx.geometry.Rectangle2D;

import org.athmis.wmoptimisation.changeset.Change;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * https://de.wikipedia.org/wiki/Geographische_Koordinaten
 *
 * @author Marcus
 */
public class AreaGuard {

	private Multimap<Long, Area> edges;

	private final double maxBboxEdge;

	/**
	 * @param maxBboxEdge
	 */
	public AreaGuard(final double maxBboxEdge) {
		if (maxBboxEdge > 0.0) {
			this.maxBboxEdge = maxBboxEdge;
			edges = ArrayListMultimap.create();
		}
		else {
			throw new IllegalArgumentException("max bounding box must be > 0.0");
		}
	}

	public boolean isNextBoxToLarge(Long changeSetId, Change updatedItem) {
		Rectangle2D actualBox, nextBox;
		double maxEdge;

		actualBox = AreaGuardToolBox.getBoxForAreas(edges.get(changeSetId));
		nextBox = AreaGuardToolBox.combine(actualBox, new Area(updatedItem));
		maxEdge = AreaGuardToolBox.getMaxEdge(nextBox);

		return maxEdge > maxBboxEdge;
	}

	public void addUpdatedItem(Long changeSetId, Change updatedItem) {
		boolean success;

		success = edges.put(changeSetId, new Area(updatedItem));

		if (!success) {
			throw new IllegalStateException("can't add item " + String.valueOf(updatedItem)
				+ " to changeset id " + String.valueOf(changeSetId));
		}
	}
}
