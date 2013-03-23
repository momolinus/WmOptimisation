package org.athmis.wmoptimisation.algos;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.athmis.wmoptimisation.versuche.ChangeSetZipContentData;

public abstract class ChangeSetGenerator {
	private static Logger LOGGER = Logger.getLogger(ChangeSetGenerator.class);
	private OsmServer osmServer;

	public ChangeSetGenerator() {
		osmServer = new OsmServer();
	}

	public final ChangeSetZipContentData createOptimizedChangeSets(
			ChangeSetZipContentData changesFromZip) {
		List<Change> changes;
		ChangeSetZipContentData optimizedDataSet;

		optimizedDataSet = new ChangeSetZipContentData();
		changes = changesFromZip.getAllChanges();

		LOGGER.info("use " + changes.size() + " for optimization");

		for (Change change : changes) {
			add(change, osmServer, optimizedDataSet);
		}

		return optimizedDataSet;
	}

	protected abstract void add(Change change, OsmServer osmServer,
			ChangeSetZipContentData optimizedDataSet);
}
