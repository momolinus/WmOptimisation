package org.athmis.wmoptimisation.algorithm.areaguard;

import org.athmis.wmoptimisation.changeset.Change;

/**
 * @author Marcus Bleil, http://www.marcusbleil.de
 */
public class AreaGuardForSize extends AreaGuard {

	/**
	 * {@inheritDoc}
	 */
	public AreaGuardForSize(final double maxBboxEdge) {
		super(maxBboxEdge);
	}

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
