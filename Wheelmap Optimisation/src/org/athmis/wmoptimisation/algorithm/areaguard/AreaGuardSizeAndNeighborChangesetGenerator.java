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
		Calendar changeTime;
		ChangeSetUpdateAble changeSet;

		assertThatChangeAndServerNotNull(updatedItem, osmServer);

		// the change time means the actual time for working with server
		changeTime = updatedItem.getCreatedAt();
		String user = updatedItem.getUser();

		// first run change set id is null and must be build
		if (changeSetInUseId == null) {
			changeSetInUseId = osmServer.createChangeSet(changeTime, user);
		}
		// now this osm client has to check if its actual changeset id is still open
		else {
			boolean isOpen;

			// note: following method also changes state of changeset, in future release method
			// would be divided in two methods
			isOpen = osmServer.isChangeSetOpen(changeSetInUseId, changeTime);

			if (!isOpen) {
				changeSetInUseId = osmServer.createChangeSet(changeTime, user);
			}
		}

		// no this client must have an open changeset id
		assertThatChangeSetIsNotNull(osmServer, changeSetInUseId);

		guard.removeAllChangesetsClosedByServer(osmServer);

		Long olderChangeSetId = guard.searchOtherChangeSetForChange(changeSetInUseId, updatedItem);
		if (olderChangeSetId != null) {
			changeSetInUseId = olderChangeSetId;
		}
		else {
			if (!guard.isChangeSetInArea(changeSetInUseId, updatedItem)) {
				changeSetInUseId = null;
			}
		}

		// if there was no valid changeset left, an new must be created
		if (changeSetInUseId == null) {
			changeSetInUseId = osmServer.createChangeSet(changeTime, updatedItem.getUser());
		}

		// note: first now is the correct time to call for changeset, because now changeset id is
		// valid
		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		guard.addUpdatedItem(changeSetInUseId, updatedItem);
		optimizedDataSet.addChangeForChangeSet(updatedItem, changeSet);
	}
}
