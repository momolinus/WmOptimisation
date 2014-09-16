/* Copyright Marcus Bleil, Oliver Rudzik, Christoph B�nte 2012 This file is part of Wheelmap
 * Optimization. Wheelmap Optimization is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. Wheelmap Optimization is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with Athmis. If not, see <http://www.gnu.org/licenses/>. Diese Datei ist Teil von
 * Wheelmap Optimization. Wheelmap Optimization ist Freie Software: Sie k�nnen es unter den
 * Bedingungen der GNU General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder sp�teren ver�ffentlichten Version, weiterverbreiten
 * und/oder modifizieren. Wheelmap Optimization wird in der Hoffnung, dass es n�tzlich sein wird,
 * aber OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite Gew�hrleistung der
 * MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public License f�r
 * weitere Details. Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>. */
package org.athmis.wmoptimisation.algorithm;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.changeset.Way;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

/**
 * , Oliver ChangeSetGenerator implementations are used for test of a changeset generation
 * algorithm. Architecture is <a
 * href="http://en.wikipedia.org/wiki/Template_method_pattern">Template method pattern</a>.
 */
public abstract class ChangeSetGenerator {

	private static final Logger LOGGER = Logger.getLogger(ChangeSetGenerator.class);

	/**
	 * @param change
	 *            checked for <code>== null</code>
	 * @param osmServer
	 *            checked for <code>== null</code>
	 * @throws IllegalArgumentException
	 *             if change or server is <code>null</code>
	 */
	protected static void assertThatChangeAndServerNotNull(Change change, OsmServer osmServer) {
		if (change == null) {
			throw new IllegalArgumentException("null as Change is not permitted");
		}
		if (osmServer == null) {
			throw new IllegalArgumentException("null as OsmServer is not permitted");
		}
	}

	/**
	 * @param changeSet
	 *            checked for <code>== null</code>
	 * @throws IllegalArgumentException
	 *             if changeSet is <code>null</code>
	 */
	protected static void assertThatChangeSetNotNull(ChangeSet changeSet) {
		if (changeSet == null) {
			throw new IllegalArgumentException("changeSet could not be found");
		}
	}

	private String name;
	private int nodes;
	private OsmServer osmServer;
	private int ways;

	public ChangeSetGenerator() {
		osmServer = new OsmServer();
		name = this.getClass().getSimpleName();
	}

	// XXX Versions attribute und/oder @JavaDoc Kommentar
	/**
	 * Stores all changes of given ChangeSetZipContentData object into a new ChangeSetZipContentData
	 * object. Uses specialized changeset generation algorithm to minimize the size of changesets.
	 * <p>
	 * This version omits {@linkplain Change}s which are {@linkplain Way}s.
	 *
	 * @param changesToOptimize
	 *            a set of (real) changes and changesets
	 * @return new storage of the changes, generated by implementations of the template method
	 */
	public final OsmChangeContent createOptimizedChangeSets(OsmChangeContent changesToOptimize) {

		List<Change> changes;
		OsmChangeContent optimizedDataSet;
		long createdTimeMillis = Long.MIN_VALUE;

		optimizedDataSet = new OsmChangeContent();
		changes = changesToOptimize.getAllChanges();
		Collections.sort(changes);

		LOGGER.info("use " + changesToOptimize.getNodes() + " nodes and "
			+ changesToOptimize.getNoChangeSets() + " changeSets for optimization");

		ways = 0;
		nodes = 0;

		for (Change change : changes) {

			if (change.isWay()) {
				ways++;
			}
			else {
				nodes++;

				assertThatChangeIsYoungerThanItsPredecessor(createdTimeMillis, change);

				createdTimeMillis = change.getCreatedAt().getTimeInMillis();

				add(change, osmServer, optimizedDataSet);
			}
		}

		LOGGER.info(ways + " ways omitted");
		LOGGER.info(nodes + " nodes processed");

		optimizedDataSet.closeAllChangeSets();

		return optimizedDataSet;
	}

	public abstract String getName();

	public final int getNodes() {
		return nodes;
	}

	public final int getWays() {
		return ways;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if creation time of change is before createdTimeMillis, means new change is older
	 *             than predecessor, possible reasons: comparable or sorting was wrong implemented
	 */
	private void assertThatChangeIsYoungerThanItsPredecessor(long createdTimeMillis, Change change) {
		if (change.getCreatedAt().getTimeInMillis() < createdTimeMillis) {
			throw new IllegalArgumentException("change " + change.verbose()
				+ " is older than it's predecessor but must be younger");
		}
	}

	/**
	 * Implementations of ChangeSetGerator must implement here their changeset generation algorithm.
	 *
	 * @param change
	 *            this change is new and must be added to any changeset
	 * @param osmServer
	 *            the OSM Server
	 * @param optimizedDataSet
	 *            the object wich stores the change and (generated) changesets, the task of this
	 *            object is to analyze the changesets after all changes added
	 */
	protected abstract void add(Change change, OsmServer osmServer,
								OsmChangeContent optimizedDataSet);
}
