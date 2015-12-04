package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.Calendar;

import org.athmis.wmoptimisation.algorithm.ChangeSetGenerator;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSetUpdateAble;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

/**
 * A ChangeSetGenerator which uses {@linkplain AreaGuardForSizeAndNeighbor} as guard for size of
 * changesets. This class has a new implementation of
 * {@linkplain AreaGuardChangeSetGenerator#add(Change, OsmServer, OsmChangeContent)}.
 *
 * @author Marcus Bleil, http://www.marcusbleil.de
 */
public class AreaGuardSizeAndNeighborChangesetGenerator extends ChangeSetGenerator {

	private AreaGuardForSizeAndNeighbor guard;

	public AreaGuardSizeAndNeighborChangesetGenerator(double maxBboxSize) {
		guard = new AreaGuardForSizeAndNeighbor(maxBboxSize);
		name = "ag sn (" + maxBboxSize + ")";
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void add(Change updatedItem, OsmServer osmServer, OsmChangeContent optimizedDataSet) {

		assertThatChangeAndServerNotNull(updatedItem, osmServer);

		Calendar changeTime;
		ChangeSetUpdateAble changeSet;

		// the change time means the actual time for working with server
		changeTime = updatedItem.getCreatedAt();
		String user = updatedItem.getUser();

		osmServer.closeChangesetsNeededToBeClosed(changeTime);

		guard.removeAllChangesetsClosedByServer(osmServer);

		if (changeSetInUseId != null && !osmServer.isChangeSetOpen(changeSetInUseId)) {
			changeSetInUseId = null;
		}

		if (!guard.isChangeSetInArea(changeSetInUseId, updatedItem)) {
			changeSetInUseId = null;
		}

		Long olderChangeSetId = guard.searchOtherChangeSetForChange(changeSetInUseId, updatedItem);
		if (olderChangeSetId != null) {
			changeSetInUseId = olderChangeSetId;
		}

		// if there was no valid changeset left, an new must be created
		if (changeSetInUseId == null) {
			changeSetInUseId = osmServer.createChangeSet(changeTime, user);
		}

		// note: first now is the correct time to call for changeset, because now changeset id is
		// valid
		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		guard.addUpdatedItem(changeSetInUseId, updatedItem);
		optimizedDataSet.addChangeForChangeSet(updatedItem, changeSet);
	}
}
