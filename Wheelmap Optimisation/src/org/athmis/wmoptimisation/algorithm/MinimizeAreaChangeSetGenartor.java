package org.athmis.wmoptimisation.algorithm;

import java.util.Calendar;

import org.athmis.wmoptimisation.changeset.ChangeSetUpdateAble;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

/**
 * The MinimizeAreaChangeSetGenartor was the first optimization algorithm, but it was not
 * successful, so don't use any more.
 *
 * @author Marcus Bleil, http://www.marcusbleil.de
 * @deprecated algorithm was not useful, class will be deleted in future release
 */
@Deprecated
public class MinimizeAreaChangeSetGenartor extends ChangeSetGenerator {

	private Long changeSetInUseId;

	private void initChangeSetInUseId(OsmServer osmServer, Calendar changeTime, String user)
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
	protected void add(Change change, OsmServer osmServer, OsmChangeContent optimizedDataSet) {
		Calendar changeTime;
		ChangeSetUpdateAble changeSet;

		assertThatChangeAndServerNotNull(change, osmServer);

		changeTime = change.getCreatedAt();

		initChangeSetInUseId(osmServer, changeTime, change.getUser());

		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		if (changeSet.getBoundingBoxSquareDegree() > 0.00116) {
			changeSet.close(changeTime);
			changeSetInUseId = osmServer.createChangeSet(changeTime, change.getUser());
			changeSet = osmServer.getChangeSet(changeSetInUseId);
		}

		optimizedDataSet.addChangeForChangeSet(change, changeSet);
	}

	@Override
	public String getName() {
		return "minimize area (1)";
	}
}
