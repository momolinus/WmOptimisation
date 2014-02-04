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
package org.athmis.wmoptimisation.algorithm;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.changeset.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

/**
 * This is the simulation of the actual implementation of changeset generation
 * by Wheelmap-Server. This class is used to verify the actual behavior of
 * Wheelmap-Server and OSM-Server.
 * <p>
 * note: architecture is <a
 * href="http://en.wikipedia.org/wiki/Template_method_pattern">Template method
 * pattern</a>
 */
public class SimpleChangeSetGenerator extends ChangeSetGenerator {

	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(SimpleChangeSetGenerator.class);

	private Long changeSetInUseId;

	private void checkChangeSet(ChangeSet changeSet) {
		if (changeSet == null)
			throw new IllegalArgumentException("changeSet could not be found");

	}

	private void checkChangeAndServerNotNull(Change change, OsmServer osmServer)
		throws IllegalArgumentException {
		if (change == null)
			throw new IllegalArgumentException("null as Change is not permitted");
		if (osmServer == null)
			throw new IllegalArgumentException("null as OsmServer is not permitted");
	}

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

		if (changeSetInUseId == null)
			throw new IllegalStateException("no change set created by osm server of type "
				+ osmServer.getClass().getSimpleName());
	}

	/**
	 * @throws IllegalArgumentException
	 *             if change's creation date ist not parseable
	 */
	@Override
	protected void add(Change change, OsmServer osmServer, OsmChangeContent optimizedDataSet) {
		Calendar changeTime;
		ChangeSet changeSet;

		checkChangeAndServerNotNull(change, osmServer);

		changeTime = change.getCreatedAt();

		initChangeSetInUseId(osmServer, changeTime);

		changeSet = osmServer.getChangeSet(changeSetInUseId);
		checkChangeSet(changeSet);

		optimizedDataSet.addChangeForChangeSet(change, changeSet);
	}
}
