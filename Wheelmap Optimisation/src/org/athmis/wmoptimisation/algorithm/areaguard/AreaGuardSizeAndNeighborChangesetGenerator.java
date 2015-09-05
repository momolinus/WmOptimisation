package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.Calendar;

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
public class AreaGuardSizeAndNeighborChangesetGenerator extends AreaGuardChangeSetGenerator {

	private AreaGuardForSizeAndNeighbor guard;

	public AreaGuardSizeAndNeighborChangesetGenerator(double maxBboxSize) {
		guard = new AreaGuardForSizeAndNeighbor(maxBboxSize);
		name = "ag sn (" + maxBboxSize + ")";
	}

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
		assertThatChangeSetIdIsNotNull(osmServer, changeSetInUseId);

		guard.removeAllChangesetsClosedByServer(osmServer);
		// note: method could return an a new changeset or null, in future release method will be
		// divided in two methods
		changeSetInUseId = guard.getValidChangesetId(changeSetInUseId, updatedItem);

		// if there was no valid changeset left, an new must be ceated
		if (changeSetInUseId == null) {
			changeSetInUseId = osmServer.createChangeSet(changeTime, updatedItem.getUser());
		}

		// FIXME mit eine Test, diese Position fixieren: hier war es nich sonder oben
		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		guard.addUpdatedItem(changeSetInUseId, updatedItem);
		optimizedDataSet.addChangeForChangeSet(updatedItem, changeSet);
	}

	private static void assertThatChangeSetIdIsNotNull(OsmServer osmServer, Long changeSetInUseId) {
		if (changeSetInUseId == null) {
			throw new IllegalStateException("no change set created by osm server of type "
				+ osmServer.getClass().getSimpleName());
		}
	}
}
