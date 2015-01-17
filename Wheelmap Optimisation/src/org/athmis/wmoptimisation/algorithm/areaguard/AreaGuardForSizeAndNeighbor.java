/**
 * created at 17.01.2015 (11:47:55)
 */
package org.athmis.wmoptimisation.algorithm.areaguard;

import org.athmis.wmoptimisation.changeset.Change;

/**
 * @author Marcus
 */
public class AreaGuardForSizeAndNeighbor extends AreaGuard {

	/**
	 * @param maxBboxEdge
	 */
	public AreaGuardForSizeAndNeighbor(double maxBboxEdge) {
		super(maxBboxEdge);
	}

	public void closeChangeSetId(Long changeSetInUseId) {
		edges.removeAll(changeSetInUseId);
	}

	public Long getValidChangesetId(Long changeSetInUseId, Change updatedItem) {

		if (edges.size() == 0) {
			// addUpdatedItem(changeSetInUseId, updatedItem);
			return changeSetInUseId;
		}

		return validId(changeSetInUseId, updatedItem);
	}

	private boolean isChangeSetInArea(Long changeSetInUseId, Change updatedItem) {
		Area actualBox;
		Area nextBox;
		double maxEdge;

		actualBox = AreaGuardToolBox.getBoundingAreaForAreas(edges.get(changeSetInUseId));
		nextBox = AreaGuardToolBox.combine(actualBox, new Area(updatedItem));
		maxEdge = AreaGuardToolBox.getMaxEdge(nextBox);

		maxEdge = Math.round(10_000_000.0 * maxEdge) / 10_000_000.0;

		return !(maxEdge > maxBboxEdge);
	}

	private Long seekValidId(Change updatedItem) {
		Long result = null;

		for (Long id : edges.asMap().keySet()) {
			if (isChangeSetInArea(id, updatedItem)) {
				result = id;
				break;
			}
		}

		return result;
	}

	private Long validId(Long changeSetInUseId, Change updatedItem) {
		if (isChangeSetInArea(changeSetInUseId, updatedItem)) {
			return changeSetInUseId;
		}
		else {
			return seekValidId(updatedItem);
		}
	}
}
