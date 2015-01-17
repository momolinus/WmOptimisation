package org.athmis.wmoptimisation.algorithm.areaguard;

import org.athmis.wmoptimisation.changeset.Change;

/**
 * https://de.wikipedia.org/wiki/Geographische_Koordinaten
 *
 * @author Marcus
 */
public class AreaGuardForSize extends AreaGuard {

	/**
	 * @param maxBboxEdge
	 */
	public AreaGuardForSize(final double maxBboxEdge) {
		super(maxBboxEdge);
	}

	@Override
	public boolean isNextBoxToLarge(Long changeSetId, Change updatedItem) {
		Area actualBox;
		Area nextBox;
		double maxEdge;

		actualBox = AreaGuardToolBox.getBoundingAreaForAreas(edges.get(changeSetId));
		nextBox = AreaGuardToolBox.combine(actualBox, new Area(updatedItem));
		maxEdge = AreaGuardToolBox.getMaxEdge(nextBox);

		maxEdge = Math.round(10_000_000.0 * maxEdge) / 10_000_000.0;

		return maxEdge > maxBboxEdge;
	}
}
