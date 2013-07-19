/*
Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012

This file is part of Wheelmap Optimization.

Wheelmap Optimization is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Wheelmap Optimization is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Athmis. If not, see <http://www.gnu.org/licenses/>.

Diese Datei ist Teil von Wheelmap Optimization.

Wheelmap Optimization ist Freie Software: Sie können es unter den Bedingungen
der GNU General Public License, wie von der Free Software Foundation,
Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
veröffentlichten Version, weiterverbreiten und/oder modifizieren.

Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird, aber
OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
Siehe die GNU General Public License für weitere Details.

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
 * 
 * 
 */
public class OsmServer {

	/**
	 * map with changesets, the key is the ID of the changeset stored as value
	 */
	private Map<Long, ChangeSet> changeSets;
	/**
	 * a multimap which contains all changes (as values) for a changeset (with
	 * its ID as key of the multimap)
	 */
	private Multimap<Long, Change> changes;
	/**
	 * used for building an ID for changesets
	 */
	private long index;

	public OsmServer() {
		index = System.currentTimeMillis();
		changeSets = new HashMap<>();
		changes = ArrayListMultimap.create();
	}

	// XXX ist die Exception hier überhaupt passend???
	/**
	 * Closes the changeset for given id.
	 * 
	 * @param id
	 *            changesets id
	 * @param closeTime
	 *            the closing time
	 * @return <code>true</code> if changeset was open, <code>false</code> if
	 *         changeset still was closed.
	 * 
	 * @throws ParseException
	 */
	public boolean closeChangeSet(Long id, Calendar closeTime) throws ParseException {
		boolean wasOpenOnClosingTime;

		checkForClosingChangesets(closeTime);
		wasOpenOnClosingTime = changeSets.get(id).isOpen();

		if (wasOpenOnClosingTime) {
			changeSets.get(id).close(closeTime);
		}

		return wasOpenOnClosingTime;
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

	// XXX für eine ungültige id könnte man eine andere Exception nehmen
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
	 * Checks for closing task at given time and returns the changeset open
	 * state.
	 * 
	 * @param id
	 *            the id of changeset
	 * @param the
	 *            actual time used for checking for closing time
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

				// change set contains changes
				if (changesForChangeSet.size() > 0) {
					Calendar youngestChangeTime;
					Change youngestChange;

					Collections.sort(changesForChangeSet);
					youngestChange = changesForChangeSet.get(changesForChangeSet.size() - 1);

					youngestChangeTime = osmToCal(youngestChange.getTimestamp());
					diff = now.getTimeInMillis() - youngestChangeTime.getTimeInMillis();

				}

				// change set is empty
				else {
					diff = now.getTimeInMillis() - changeSet.getCreated().getTimeInMillis();
				}

				// change set was'nt used since 60 minutes, so close now
				if (TimeUnit.MILLISECONDS.toMinutes(diff) >= 60) {
					now.add(Calendar.MINUTE, -1);
					changeSet.close(now);
				}
			}
		}
	}

	// TODO für welche Parameter ist die IllegalArgumentException sinnvoll?
	/**
	 * Returns the changeset with given id.
	 * 
	 * @param id
	 *            the if of the changeset which should be returned
	 * @return the changeset with given id
	 * @throws IllegalArgumentException
	 */
	public ChangeSet getChangeSet(Long id) {
		return changeSets.get(id);
	}
}
