package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.Calendar;

import org.athmis.wmoptimisation.algorithm.ChangeSetGenerator;
import org.athmis.wmoptimisation.changeset.ChangeSetUpdateAble;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

public class AreaGuardChangeSetGenerator extends ChangeSetGenerator {

	private static void assertThatChangeSetIsNotNull(OsmServer osmServer, Long changeSetInUseId) {
		if (changeSetInUseId == null) {
			throw new IllegalStateException("no change set created by osm server of type "
				+ osmServer.getClass().getSimpleName());
		}
	}
	private AreaGuardForSize areaGuard;
	protected Long changeSetInUseId;

	protected String name;

	public AreaGuardChangeSetGenerator(double maxBboxSize) {
		areaGuard = new AreaGuardForSize(maxBboxSize);
		name = "ag (" + maxBboxSize + ")";
	}

	protected AreaGuardChangeSetGenerator() {}

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected void add(Change updatedItem, OsmServer osmServer, OsmChangeContent optimizedDataSet) {
		Calendar changeTime;
		ChangeSetUpdateAble changeSet;

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
			}
		}

		assertThatChangeSetIsNotNull(osmServer, changeSetInUseId);

		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		boolean isToLarge;
		isToLarge = areaGuard.isNextBoxToLarge(changeSetInUseId, updatedItem);

		if (isToLarge) {
			changeSet.close(changeTime);
			changeSetInUseId = osmServer.createChangeSet(changeTime, updatedItem.getUser());
			changeSet = osmServer.getChangeSet(changeSetInUseId);
		}

		areaGuard.addUpdatedItem(changeSetInUseId, updatedItem);

		optimizedDataSet.addChangeForChangeSet(updatedItem, changeSet);
	}
}
