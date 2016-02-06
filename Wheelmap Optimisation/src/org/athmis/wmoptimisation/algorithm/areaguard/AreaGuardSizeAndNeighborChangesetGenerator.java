package org.athmis.wmoptimisation.algorithm.areaguard;

import java.util.Calendar;
import java.util.Optional;

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

	private AreaGuardForSizeAndNeighbor areaGuard;

	public AreaGuardSizeAndNeighborChangesetGenerator(double maxBboxSize) {
		areaGuard = new AreaGuardForSizeAndNeighbor(maxBboxSize);
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

		Optional<Long> validatedChangeSetInUseId =
			areaGuard.validateAllStoredChangesets(osmServer, changeSetInUseId);

		Optional<Long> idOfFittingChangeset = areaGuard
			.lookForChangesetWhereChangeFits(updatedItem, validatedChangeSetInUseId.orElse(null));

		// if there was no valid changeset left, an new must be created
		if (!idOfFittingChangeset.isPresent()) {
			changeSetInUseId = osmServer.createChangeSet(changeTime, user);
		}
		else {
			changeSetInUseId = idOfFittingChangeset.get();
		}

		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		areaGuard.addUpdatedItem(changeSetInUseId, updatedItem);

		optimizedDataSet.addChangeForChangeSet(updatedItem, changeSet);
	}
}
