/* Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012 This file is part
 * of Wheelmap Optimization. Wheelmap Optimization is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Wheelmap Optimization is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with
 * Athmis. If not, see <http://www.gnu.org/licenses/>. Diese Datei ist Teil von
 * Wheelmap Optimization. Wheelmap Optimization ist Freie Software: Sie können
 * es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder
 * späteren veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 * Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details. Sie sollten eine
 * Kopie der GNU General Public License zusammen mit diesem Programm erhalten
 * haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>. */
package org.athmis.wmoptimisation.osmserver;

import static org.athmis.wmoptimisation.changeset.ChangeSetToolkit.calToOsm;
import static org.athmis.wmoptimisation.changeset.ChangeSetToolkit.osmToCal;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.algorithm.ChangeSetGenerator;
import org.athmis.wmoptimisation.changeset.CangeSetUpdateAble;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.Node;
import org.athmis.wmoptimisation.changeset.OsmChangeContent;
import org.athmis.wmoptimisation.changeset.Way;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A OsmServer object simulates an <a
 * href="http://wiki.openstreetmap.org/wiki/API_v0.6">OSM Server</a>. Most
 * methods has a parameter for calling time. This prevents, that any clock must
 * be running in any thread; in other words 'the time ist simulated', too.
 * <p>
 * The main task of this class is to ensure the changeset closing behavior of an
 * real osm server. When a client uses
 * {@linkplain #isChangeSetOpen(Long, Calendar)} there are three cases when an
 * OsmServer object returns false:
 * <ul>
 * <li>the given changeset has more than 50000 single edits</li>
 * <li>the given changeset was unused for more than 1 hour</li>
 * <li>the given changeset was open for more then 24 hours</li>
 * </ul>
 * <p>
 * For simulation and/or generating a changeset generation strategy use an
 * OsmServer object and a {@link ChangeSetGenerator} implementation and a amount
 * of changes (edits) which stored in a {@link OsmChangeContent}.
 * 
 * @see <a
 *      href="http://wiki.openstreetmap.org/wiki/API_v0.6#Changesets_2">OSM-Wiki:
 *      API_v0.6 Changesets</a>
 */
public class OsmServer {

	private final static Logger LOGGER = Logger.getLogger(OsmServer.class);

	/**
	 * a multimap which contains all changes (as values) for a changeset (with
	 * its ID as key of the multimap)
	 */
	private Multimap<Long, Change> changes;

	/**
	 * map with changesets, the key is the ID of the changeset (which itself is
	 * stored as value)
	 */
	private Map<Long, CangeSetUpdateAble> changeSets;

	/**
	 * used for building an ID for changesets
	 */
	private long index;

	/**
	 * Constructs an OsmServer object. As start index for changes and changesets
	 * {@linkplain System#currentTimeMillis()} is used.
	 */
	public OsmServer() {
		index = System.currentTimeMillis();
		changeSets = new HashMap<>();
		changes = ArrayListMultimap.create();
	}

	private void checkForClosingChangesets(final Calendar now) {

		Calendar nowCopy = (Calendar) now.clone();

		for (CangeSetUpdateAble changeSet : changeSets.values()) {

			// only open change sets needs to be closed
			if (changeSet.isOpen()) {

				boolean closingNeeded = false;
				List<Change> changesForChangeSet;
				long diff, age;

				changesForChangeSet = new ArrayList<>(changes.get(changeSet.getId()));

				// there are any changes in given change set
				if (changesForChangeSet.size() > 0) {
					Calendar youngestChangeTime;
					Change youngestChange;

					Collections.sort(changesForChangeSet);
					youngestChange = changesForChangeSet.get(changesForChangeSet.size() - 1);

					youngestChangeTime = osmToCal(youngestChange.getTimestamp());
					diff = nowCopy.getTimeInMillis() - youngestChangeTime.getTimeInMillis();
				}

				// change set is empty
				else {
					diff = nowCopy.getTimeInMillis() - changeSet.getCreated().getTimeInMillis();
				}

				LOGGER.trace("diff = " + TimeUnit.MILLISECONDS.toMinutes(diff));

				// 60 min not in used
				closingNeeded = TimeUnit.MILLISECONDS.toMinutes(diff) >= 60;

				// older than 24 hours
				age = nowCopy.getTimeInMillis() - changeSet.getCreated().getTimeInMillis();
				LOGGER.trace("diff = " + TimeUnit.MILLISECONDS.toMinutes(age));
				closingNeeded = closingNeeded || (TimeUnit.MILLISECONDS.toHours(age) >= 24);

				// more than 50 000
				closingNeeded = closingNeeded || changesForChangeSet.size() >= 50000;

				if (closingNeeded) {
					nowCopy.add(Calendar.MINUTE, -1);
					changeSet.close(now);
				}
			}
		}
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
	 *            must run in any thread, all values are allowed, client is
	 *            responsible for sensible values
	 * @return the unique (during one run) id of the changeset
	 * @throws IllegalStateException
	 *             if changeset could not be created, possible reason: changeset
	 *             was previous added by any illegal call
	 */
	public Long createChangeSet(Calendar creationTime) {
		CangeSetUpdateAble changeSet;
		Object result;

		index++;
		changeSet = new CangeSetUpdateAble(calToOsm(creationTime), index, true);
		result = changeSets.put(index, changeSet);

		// POSTCONDITION
		if (result != null)
			throw new IllegalStateException("can't add new changeset to list at index " + index);

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

	// XXX gibt es eine bessere Lösung, als null zurück geben
	/**
	 * Returns the changeset with given id.
	 * 
	 * @param id
	 *            the if of the changeset which should be returned
	 * @return the changeset with given id, <code>null</code> if no changeset
	 *         for given id was stored
	 */
	public CangeSetUpdateAble getChangeSet(Long id) {
		return changeSets.get(id);
	}

	// XXX Benennung der Methode überdenken, sie ändert auch den Status des
	// aktuellen Changesets
	/**
	 * Checks for closing task at given time and returns the changeset open
	 * state.
	 * 
	 * @param id
	 *            the id of changeset, if id is illegal
	 *            {@linkplain IllegalArgumentException} will be thrown
	 * @param the
	 *            actual time used for checking and for closing time
	 * @return <code>true</code> if the changeset ist open
	 * @throws IllegalArgumentException
	 *             if id is illegal, meaning no changeset exits with given id
	 */
	public boolean isChangeSetOpen(Long id, Calendar now) {
		if (!changeSets.containsKey(id))
			throw new IllegalArgumentException("unknown changeset with 'id = " + String.valueOf(id)
				+ "'");

		checkForClosingChangesets(now);

		return changeSets.get(id).isOpen();
	}

	/**
	 * Checks if given changeset id is valid, meaning server contains a
	 * changeset with given id.
	 * 
	 * @param id
	 *            id for a changeset
	 * @return <code>true</code> if server contains a changeset with that id,
	 *         <code>false</code> otherwise
	 */
	public boolean isChangeSetIdValid(Long id) {
		return changeSets.containsKey(id);
	}

	/**
	 * "Stores the node", really it sets the changeset id of the node and adds
	 * node to an internal map. Before call check with
	 * {@linkplain #isChangeSetOpen(Long, Calendar)} for changeset is open.
	 * 
	 * @param changesetId
	 *            previous generated (and stored by client) changeset is
	 * @param node
	 *            a deep copy will be taken after changeset id was set with
	 *            given changesetId, <code>null</code> is not permitted
	 * @throws IllegalArgumentException
	 *             if tried to store change to an closed change set or node ==
	 *             <code>null</code>
	 */
	public void storeNode(Long changesetId, Node node) {

		checkParametersNotNull(changesetId, node);

		checkForClosingChangesets(node.getCreatedAt());

		checkChangesetIsStillOpen(changesetId, node);

		node.setChangeset(changesetId);
		changes.put(changesetId, new Node(node));
	}

	/**
	 * "Stores the node", really it sets the changeset id of the node and adds
	 * node to an internal map. Before call check with
	 * {@linkplain #isChangeSetOpen(Long, Calendar)} for changeset is open.
	 * 
	 * @param changesetId
	 *            previous generated (and stored by client) changeset is
	 * @param node
	 *            a deep copy will be taken after changeset id was set with
	 *            given changesetId, <code>null</code> is not permitted
	 * @throws IllegalArgumentException
	 *             if tried to store change to an closed change set or node ==
	 *             <code>null</code>
	 */
	public void storeWay(Long changesetId, Way way) {

		checkParametersNotNull(changesetId, way);

		checkForClosingChangesets(way.getCreatedAt());

		checkChangesetIsStillOpen(changesetId, way);

		way.setChangeset(changesetId);
		changes.put(changesetId, new Way(way));
	}

	/**
	 * checks if given changeset id is still open
	 * 
	 * @param changesetId
	 *            will be checked for open
	 * @param change
	 *            only used for exceptions message
	 */
	private void checkChangesetIsStillOpen(Long changesetId, Change change) {
		if (!changeSets.get(changesetId).isOpen()) {
			throw new IllegalArgumentException("can't store change " + change.getId()
				+ " to closed changeset " + changesetId);
		}
	}

	private void checkParametersNotNull(Long changesetId, Change node)
		throws IllegalArgumentException {

		if (node == null)
			throw new IllegalArgumentException("null as node/way is not permitted");
		if (changesetId == null)
			throw new IllegalArgumentException("null as changeset id is not permitted");
	}
}
