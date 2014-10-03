package org.athmis.wmoptimisation.algorithm;

import java.util.Calendar;

import org.athmis.wmoptimisation.changeset.ChangeSetUpdateAble;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

public class MinimizeAreaChangeSetGenartor extends ChangeSetGenerator {

	private Long changeSetInUseId;

	private void initChangeSetInUseId(OsmServer osmServer, Calendar changeTime)
																				throws IllegalStateException {

		// first run
		if (changeSetInUseId == null) {
			changeSetInUseId = osmServer.createChangeSet(changeTime);
		}
		else {
			boolean isOpen;

			isOpen = osmServer.isChangeSetOpen(changeSetInUseId, changeTime);
			if (!isOpen) {
				changeSetInUseId = osmServer.createChangeSet(changeTime);
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

		initChangeSetInUseId(osmServer, changeTime);

		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		if (changeSet.getBoundingBoxSquareDegree() > 0.00116) {
			changeSet.close(changeTime);
			changeSetInUseId = osmServer.createChangeSet(changeTime);
			changeSet = osmServer.getChangeSet(changeSetInUseId);
		}
		optimizedDataSet.addChangeForChangeSet(change, changeSet);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "minimize area (1)";
	}
}
