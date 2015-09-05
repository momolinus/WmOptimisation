package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.Calendar;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSetUpdateAble;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

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
		Long cIdDebug;

		assertThatChangeAndServerNotNull(updatedItem, osmServer);

		changeTime = updatedItem.getCreatedAt();
		String user = updatedItem.getUser();

		// first run
		if (changeSetInUseId == null) {
			changeSetInUseId = osmServer.createChangeSet(changeTime, user);
		}
		else {
			boolean isOpen;

			isOpen = osmServer.isChangeSetOpen(changeSetInUseId, changeTime);
			if (!isOpen) {
				changeSetInUseId = osmServer.createChangeSet(changeTime, user);

				// FIXME siehe unten
				// changeSet = osmServer.getChangeSet(changeSetInUseId);
			}
		}

		if (changeSetInUseId == null) {
			throw new IllegalStateException("no change set created by osm server of type "
				+ osmServer.getClass().getSimpleName());
		}

		guard.closeAllInvalidChangesets(osmServer);
		cIdDebug = changeSetInUseId;
		changeSetInUseId = guard.getValidChangesetId(changeSetInUseId, updatedItem);

		if (changeSetInUseId == null) {
			changeSetInUseId = osmServer.createChangeSet(changeTime, updatedItem.getUser());

			// FIXME siehe unten
			// changeSet = osmServer.getChangeSet(changeSetInUseId);
		}

		// FIXME mit eine Test, diese Position fixieren: hier war es nich sonder oben
		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		guard.addUpdatedItem(changeSetInUseId, updatedItem);
		optimizedDataSet.addChangeForChangeSet(updatedItem, changeSet);

		if (changeSet.getBoundingBoxSquareDegree() > (Math.pow(guard.getMaxBboxEdge(), 2))) {
			System.out.println(cIdDebug + " -> " + changeSetInUseId + ", with change "
				+ updatedItem.getId());
		}
	}
}
