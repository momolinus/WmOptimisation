package org.athmis.wmoptimisation.osmserver;

import static org.athmis.wmoptimisation.changeset.ChangeSetToolkit.calToOsm;
import static org.athmis.wmoptimisation.changeset.ChangeSetToolkit.osmToCal;

import java.awt.geom.Rectangle2D;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A OsmServer object simulates an <a
 * href="http://wiki.openstreetmap.org/wiki/API_v0.6">OSM Server</a>. Most
 * methods has a parameter for calling time. This prevents, that any clock must
 * be running in any thread; in other words 'the time ist simulated', too.
 * 
 * @author Marcus
 * 
 */
public class OsmServer {

	private Map<Long, ChangeSet> changeSets;
	private Multimap<Long, Change> changes;
	private long index;

	public OsmServer() {
		index = System.currentTimeMillis();
		changeSets = new HashMap<>();
		changes = ArrayListMultimap.create();
	}

	/**
	 * Closes the changeset for given id.
	 * 
	 * @param id
	 *            changesets id
	 * @param closeTime
	 *            the closing time
	 * @return <code>true</code> if changeset was open, <code>false</code> if
	 *         changeset still was closed.
	 */
	public boolean closeChangeSet(Long id, Calendar closeTime) {
		return false;
	}

	/**
	 * Creates a changeset and keeps it's state in memory.
	 * 
	 * @param creationTime
	 *            in the simulation clients set the creation time, so no clock
	 *            must run in any thread
	 * @return the unique (during one run) id of the changeset
	 */
	public Long createChangeSet(Calendar creationTime) {
		ChangeSet changeSet;
		Object result;

		index++;
		changeSet = new ChangeSet(calToOsm(creationTime), index, true);
		result = changeSets.put(index, changeSet);

		// POSTCONDITION
		assert result == null : "can't add new changeset to list";

		return Long.valueOf(index);
	}

	// XXX f�r eine ung�ltige id k�nnte man eine andere Exception nehmen
	/**
	 * Expands the given changesets bounding box.
	 * 
	 * @param id
	 *            changesets id
	 * @param boundingBox
	 *            the new bounding box
	 * @param now
	 *            the time of this request
	 * @return <code>true</code> if was successful, false if changeset was
	 *         closed
	 * @throws IllegalArgumentException
	 *             in case of illegal bounding box or illegal id
	 */
	public boolean expandBoundingBox(Long id, Rectangle2D boundingBox, Calendar now) {
		return false;
	}

	/**
	 * Returns the changeset open state.
	 * 
	 * @param id
	 *            the id of changeset
	 * @param the
	 *            actual time
	 * @return <code>true</code> if the changeset ist open
	 * @throws ParseException
	 */
	public boolean isChangeSetOpen(Long id, Calendar now) throws ParseException {
		if (!changeSets.containsKey(id)) {
			throw new IllegalArgumentException("unknown changeset with 'id = " + String.valueOf(id)
					+ "'");
		}
		checkForClosingChangesets(now);

		return changeSets.get(id).isOpen();
	}

	private void checkForClosingChangesets(Calendar now) throws ParseException {
		for (ChangeSet changeSet : changeSets.values()) {

			if (changeSet.isOpen()) {

				List<Change> changesForChangeSet;
				long diff;

				changesForChangeSet = new ArrayList<>(changes.get(changeSet.getId()));

				if (changesForChangeSet.size() > 0) {
					Calendar youngestChangeTime;
					Change youngestChange;

					Collections.sort(changesForChangeSet);
					youngestChange = changesForChangeSet.get(changesForChangeSet.size() - 1);

					youngestChangeTime = osmToCal(youngestChange.getTimestamp());
					diff = now.getTimeInMillis() - youngestChangeTime.getTimeInMillis();

				} else {
					diff = now.getTimeInMillis() - changeSet.getCreated().getTimeInMillis();
				}

				if (TimeUnit.MILLISECONDS.toMinutes(diff) >= 60) {
					now.add(Calendar.MINUTE, -1);
					changeSet.close(now);
				}
			}
		}
	}

	// TODO f�r welche Parameter ist die IllegalArgumentException sinnvoll?
	/**
	 * Returns the changeset with given id.
	 * 
	 * @param id
	 *            the if of the changeset which should be returned
	 * @return the changeset with given id
	 * @throws IllegalArgumentException
	 */
	public ChangeSet getChangeSet(Long id) {
		return null;
	}
}
