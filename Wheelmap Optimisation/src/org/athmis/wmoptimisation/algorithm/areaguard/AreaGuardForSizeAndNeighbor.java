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

	/**
	 * Method returns <code>true</code> if given change is in (maximum) area of given changeset.
	 *
	 * @param changeSetInUseId
	 *            used for check areas size; <code>null</code> is permitted, then method returns
	 *            <code>false</code>
	 * @param updatedItem
	 *            method checks if this change will fit to area of given changeset
	 * @return <code>true</code> if given change is in (maximum) area of given changeset,
	 *         <code>false</code> otherwise
	 */
	public boolean isChangeSetInArea(Long changeSetInUseId, Change updatedItem) {
		if (changeSetInUseId != null) {

			Area actualBox;
			Area nextBox;
			double maxEdge;

			actualBox = AreaGuardToolBox.getBoundingAreaForAreas(edges.get(changeSetInUseId));
			nextBox = AreaGuardToolBox.combine(actualBox, new Area(updatedItem));
			maxEdge = AreaGuardToolBox.getMaxEdge(nextBox);

			maxEdge = Math.round(10_000_000.0 * maxEdge) / 10_000_000.0;

			return !(maxEdge > maxBboxEdge);
		}
		else {
			return false;
		}
	}

	public Long removeAllChangesetsClosedByServer(OsmServer osmServer, Long changeSetInUseId) {

		// remove all closed Changesets from internal list
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

		// check if actual used changeset must be "removed" too, meaning returning null
		if (changeSetInUseId != null && !osmServer.isChangeSetOpen(changeSetInUseId)) {
			return null;
		}
		else {
			return changeSetInUseId;
		}
	}

	/**
	 * Method searches in stored changesets for one where given change will fit. While searching it
	 * omits given changeset. If no changeset found method returns <code>null</code>.
	 *
	 * @param changeSetInUseId
	 *            will be omitted in search, <code>null</code> is permitted, because
	 *            {@link Long#equals(Object)} could handle <code>null</code> as parameter
	 * @param updatedItem
	 *            method searches a changeset (the first), where this change fits
	 * @return a changeset where given change fits, or <code>null</code>
	 */
	public Long searchOtherChangeSetForChange(Long changeSetInUseId, Change updatedItem) {
		Long result = null;

		for (Long id : edges.asMap().keySet()) {

			if (!id.equals(changeSetInUseId)) {

				if (isChangeSetInArea(id, updatedItem)) {
					result = id;
					break;
				}
			}
		}

		return result;
	}

	public Optional<Long> lookForChangesetWhereChangeMatches(Change updatedItem,
		Long changeSetInUseId) {

		Optional<Long> fittingChangeset = null;

		if (!isChangeSetInArea(changeSetInUseId, updatedItem)) {
			fittingChangeset = Optional.empty();
		}
		else {
			fittingChangeset = Optional.ofNullable(changeSetInUseId);
		}

		Long olderChangeSetId = searchOtherChangeSetForChange(changeSetInUseId, updatedItem);
		if (olderChangeSetId != null) {
			fittingChangeset = Optional.ofNullable(olderChangeSetId);
		}

		return fittingChangeset;
	}
}
