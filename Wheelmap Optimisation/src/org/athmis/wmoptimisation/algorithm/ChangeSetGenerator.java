/*
Copyright Marcus Bleil, Oliver Rudzik, Christoph B�nte 2012

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

Wheelmap Optimization ist Freie Software: Sie k�nnen es unter den Bedingungen
der GNU General Public License, wie von der Free Software Foundation,
Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

Wheelmap Optimization wird in der Hoffnung, dass es n�tzlich sein wird, aber
OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
Siehe die GNU General Public License f�r weitere Details.

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.athmis.wmoptimisation.algorithm;

import java.util.List;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSetZipContentData;
import org.athmis.wmoptimisation.osmserver.OsmServer;

/**
 * @author Marcus, Oliver
 * ChangeSetGenerator implementations are used for test of a changeset
 * generation algorithm. Architecture is <a
 * href="http://en.wikipedia.org/wiki/Template_method_pattern">Template method
 * pattern</a>.
 */
public abstract class ChangeSetGenerator {
	private static Logger LOGGER = Logger.getLogger(ChangeSetGenerator.class);
	private OsmServer osmServer;

	public ChangeSetGenerator() {
		osmServer = new OsmServer();
	}

	/**
	 * Stores all changes of given ChangeSetZipContentData object into a new
	 * ChangeSetZipContentData object. Uses specialized changeset generation
	 * algorithm to minimize the size of changesets.
	 * 
	 * @param changesFromZip
	 *            a set of (real) changes and changesets
	 * @return new storage of the changes, generated by implementations of the
	 *         template method
	 */
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

	/**
	 * Implementations of ChangeSetGerator must implement here their changeset
	 * generation algorithm.
	 * 
	 * @param change
	 *            this change is new and must be added to any changest
	 * @param osmServer
	 *            the OSM Server
	 * @param optimizedDataSet
	 *            the object wich stores the change and (generated) changesets,
	 *            the task of this object is to analyze the changests after all
	 *            changes added
	 */
	protected abstract void add(Change change, OsmServer osmServer,
			ChangeSetZipContentData optimizedDataSet);
}
