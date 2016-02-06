package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.Calendar;

import org.athmis.wmoptimisation.algorithm.ChangeSetGenerator;
import org.athmis.wmoptimisation.changeset.ChangeSetUpdateAble;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

/**
 * @author Marcus Bleil, http://www.marcusbleil.de
 * @deprecated use {@link AreaGuardForSizeAndNeighbor} it is (a little) better
 */
@Deprecated
public class AreaGuardChangeSetGenerator extends ChangeSetGenerator {

	private AreaGuardForSize areaGuard;

	public AreaGuardChangeSetGenerator(double maxBboxSize) {
		areaGuard = new AreaGuardForSize(maxBboxSize);
		name = "ag (" + maxBboxSize + ")";
	}

	protected AreaGuardChangeSetGenerator() {}

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
