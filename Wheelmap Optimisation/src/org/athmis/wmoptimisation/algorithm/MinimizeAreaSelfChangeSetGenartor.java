package org.athmis.wmoptimisation.algorithm;

import java.awt.Rectangle;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.athmis.wmoptimisation.changeset.CangeSetUpdateAble;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

public class MinimizeAreaSelfChangeSetGenartor extends ChangeSetGenerator {

	public MinimizeAreaSelfChangeSetGenartor(double maxBboxSize) {
		this.maxBboxSize = maxBboxSize;
	}

	private Map<Long, Rectangle> bBoxes = new HashMap<>();
	protected Long changeSetInUseId;
	private double maxBboxSize = 0.00116;

	@Override
	public String getName() {
		return "self minimize area";
	}

	private Rectangle getBBoxForId(Long changeSetInUseId) {
		if (bBoxes.containsKey(changeSetInUseId)) {
			return bBoxes.get(changeSetInUseId);
		}
		else {
			Rectangle bbox = new Rectangle();
			bBoxes.put(changeSetInUseId, bbox);
			return bBoxes.get(changeSetInUseId);
		}
	}

	private Long initChangeSetInUseId(OsmServer osmServer, Calendar changeTime)
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

		return changeSetInUseId;
	}

	@Override
	protected void add(Change change, OsmServer osmServer, OsmChangeContent optimizedDataSet) {
		Calendar changeTime;
		CangeSetUpdateAble changeSet;
		double nextBBox;
		Rectangle bBoxForChangeSet;

		assertThatChangeAndServerNotNull(change, osmServer);

		changeTime = change.getCreatedAt();

		changeSetInUseId = initChangeSetInUseId(osmServer, changeTime);

		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		bBoxForChangeSet = getBBoxForId(changeSetInUseId);
		nextBBox = calculateNextBBox(bBoxForChangeSet, change);

		if (nextBBox > maxBboxSize) {
			changeSet.close(changeTime);
			changeSetInUseId = osmServer.createChangeSet(changeTime);
			changeSet = osmServer.getChangeSet(changeSetInUseId);
		}
		else {
			addChangeToBBox(change);
		}

		optimizedDataSet.addChangeForChangeSet(change, changeSet);
	}

	private void addChangeToBBox(Change change) {
		// TODO Auto-generated method stub

	}

	protected double calculateNextBBox(Rectangle bBoxForChangeSet, Change change) {
		return 0;
	}
}
