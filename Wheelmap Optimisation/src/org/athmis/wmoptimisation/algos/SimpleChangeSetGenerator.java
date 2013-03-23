package org.athmis.wmoptimisation.algos;

import java.text.ParseException;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.athmis.wmoptimisation.versuche.ChangeSetZipContentData;

public class SimpleChangeSetGenerator extends ChangeSetGenerator {

	private ChangeSet changeSetInUse;

	/**
	 * @throws IllegalArgumentException
	 *             if change's creation date ist not parseable
	 */
	@Override
	protected void add(Change change, OsmServer osmServer, ChangeSetZipContentData optimizedDataSet) {

		// the very first call
		if (changeSetInUse == null) {
			try {
				changeSetInUse = osmServer.createChangeSet(change.getCreatedAt());
			} catch (ParseException e) {
				throw new IllegalArgumentException("can't read change = '" + change.toString()
						+ "'", e);
			}
		} else {
			if (!osmServer.isOpenFor(changeSetInUse, change)) {

			}
		}
	}
}
