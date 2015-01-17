package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.Calendar;

import org.athmis.wmoptimisation.algorithm.ChangeSetGenerator;
import org.athmis.wmoptimisation.changeset.ChangeSetUpdateAble;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

public class AreaGuardChangeSetGenerator extends ChangeSetGenerator {

	private AreaGuardForSize areaGuard;
	protected Long changeSetInUseId;
	protected String name;

	public AreaGuardChangeSetGenerator(double maxBboxSize) {
		areaGuard = new AreaGuardForSize(maxBboxSize);
		name = "area guard (" + maxBboxSize + ")";
	}

	protected AreaGuardChangeSetGenerator() {}

	@Override
	public String getName() {
		return name;
	}

	// XXX besseren Methoden Namen finden
	/**
	 * Inits changesets ID. Method ensures that
	 * <p>
	 * 1. {@linkplain #changeSetInUseId} != null and<br>
	 * 2. changeset with {@linkplain #changeSetInUseId} is an open changeset and could be used
	 * <p>
	 * so following code could use {@linkplain #changeSetInUseId} without worry about state of
	 * {@linkplain #changeSetInUseId}
	 *
	 * @param osmServer
	 *            used to control if changeset is open or get a new changeset
	 * @param changeTime
	 *            the 'actual' time of day
	 * @throws IllegalStateException
	 *             if it was not possible to get a changeset if from server
	 */
	protected void initChangeSetInUseId(OsmServer osmServer, Calendar changeTime, String user)
																							throws IllegalStateException {
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

		if (changeSetInUseId == null) {
			throw new IllegalStateException("no change set created by osm server of type "
				+ osmServer.getClass().getSimpleName());
		}
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
