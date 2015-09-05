/**
 * created at 17.01.2015 (11:47:55)
 */
package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.*;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.osmserver.OsmServer;

/**
 * @author Marcus Bleil, http://www.marcusbleil.de
 */
public class AreaGuardForSizeAndNeighbor extends AreaGuard {

	/**
	 * @param maxBboxEdge
	 */
	public AreaGuardForSizeAndNeighbor(double maxBboxEdge) {
		super(maxBboxEdge);
	}

	public Long getValidChangesetId(Long changeSetInUseId, Change updatedItem) {
		Long result = null;

		for (Long id : edges.asMap().keySet()) {

			if (!id.equals(changeSetInUseId)) {

				if (isChangeSetInArea(id, updatedItem)) {
					result = id;
					break;
				}
			}
		}

		if (result == null) {
			if (isChangeSetInArea(changeSetInUseId, updatedItem)) {
				return changeSetInUseId;
			}
		}

		return result;
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

	public void closeAllInvalidChangesets(OsmServer osmServer) {
		List<Long> remove = new ArrayList<>();

		for (Long id : edges.asMap().keySet()) {
			boolean isOpen;
			isOpen = osmServer.isChangeSetOpen(id);

			if (!isOpen) {
				remove.add(id);
			}
		}

		for (Long id : remove) {
			edges.removeAll(id);
		}
	}

	public double getMaxBboxEdge() {
		return maxBboxEdge;
	}
}
