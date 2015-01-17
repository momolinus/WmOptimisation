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
		name = "area guard sn (" + maxBboxSize + ")";
	}

	@Override
	protected void add(Change updatedItem, OsmServer osmServer, OsmChangeContent optimizedDataSet) {
		Calendar changeTime;
		ChangeSetUpdateAble changeSet;

		assertThatChangeAndServerNotNull(updatedItem, osmServer);

		changeTime = updatedItem.getCreatedAt();
		initChangeSetInUseId(osmServer, changeTime, updatedItem.getUser());
		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		changeSetInUseId = guard.getValidChangesetId(changeSetInUseId, updatedItem);

		if (changeSetInUseId != null && !osmServer.isChangeSetOpen(changeSetInUseId, changeTime)) {
			guard.closeChangeSetId(changeSetInUseId);
			changeSetInUseId = null;
		}

		if (changeSetInUseId == null) {
			changeSet.close(changeTime);
			changeSetInUseId = osmServer.createChangeSet(changeTime, updatedItem.getUser());
			changeSet = osmServer.getChangeSet(changeSetInUseId);
		}

		guard.addUpdatedItem(changeSetInUseId, updatedItem);

		optimizedDataSet.addChangeForChangeSet(updatedItem, changeSet);
	}
}
