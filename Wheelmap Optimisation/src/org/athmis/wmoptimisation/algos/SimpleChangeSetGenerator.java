package org.athmis.wmoptimisation.algos;

import java.text.ParseException;
import java.util.Calendar;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.athmis.wmoptimisation.versuche.ChangeSetZipContentData;

/**
 * This is the simulation of the actual implementation of changeset generation
 * by Wheelmap-Server. This class is used to verify the actual behavior of
 * Wheelmap-Server and OSM-Server.
 * <p>
 * note: architecture is <a
 * href="http://en.wikipedia.org/wiki/Template_method_pattern">Template method
 * pattern</a>
 * 
 * @author Marcus
 * 
 */
public class SimpleChangeSetGenerator extends ChangeSetGenerator {

	private Integer changeSetInUse;

	/**
	 * @throws IllegalArgumentException
	 *             if change's creation date ist not parseable
	 */
	@Override
	protected void add(Change change, OsmServer osmServer, ChangeSetZipContentData optimizedDataSet) {
		Calendar changeTime;
		ChangeSet changeSet;
		// TODO Eingabe-Parameter auf null prüfen

		try {

			changeTime = change.getCreatedAt();

			// first run
			if (changeSetInUse == null) {

				changeSetInUse = osmServer.createChangeSet(changeTime);

			} else {

				if (!osmServer.isChangeSetOpen(changeSetInUse, changeTime)) {
					changeSetInUse = osmServer.createChangeSet(changeTime);
				}
			}
		} catch (ParseException e) {
			throw new RuntimeException("can't read change: '" + change.toString() + "'", e);
		}

		assert changeSetInUse != null;

		changeSet = osmServer.getChangeSet(changeSetInUse);
		optimizedDataSet.addChangeForChangeSet(change, changeSet);
	}
}
