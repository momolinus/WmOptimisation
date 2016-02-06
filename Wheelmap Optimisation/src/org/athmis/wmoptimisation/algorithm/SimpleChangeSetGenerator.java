/*
 * Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012 This file is part of Wheelmap
 * Optimization. Wheelmap Optimization is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. Wheelmap Optimization is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with Athmis. If not, see <http://www.gnu.org/licenses/>. Diese Datei ist Teil von
 * Wheelmap Optimization. Wheelmap Optimization ist Freie Software: Sie können es unter den
 * Bedingungen der GNU General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version, weiterverbreiten
 * und/oder modifizieren. Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird,
 * aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public License für
 * weitere Details. Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.athmis.wmoptimisation.algorithm;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.ChangeSetUpdateAble;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;

/**
 * This is the simulation of the actual implementation of changeset generation by Wheelmap-Server.
 * This class is used to verify the actual behavior of Wheelmap-Server and OSM-Server.
 * <p>
 * note: architecture is <a href="http://en.wikipedia.org/wiki/Template_method_pattern">Template
 * method pattern</a>
 */
public class SimpleChangeSetGenerator extends ChangeSetGenerator {

	private final static Logger LOGGER = Logger.getLogger(SimpleChangeSetGenerator.class);

	private Long changeSetInUseId;

	/**
	 * Tests <code>changeSetInUseId == null</code>, if <code>true</code> throws
	 * {@link IllegalStateException}
	 *
	 * @param osmServer
	 *            used for exception message
	 * @throws IllegalStateException
	 *             if {@linkplain SimpleChangeSetGenerator#changeSetInUseId} == null
	 */
	private void assertThatChangeSetIdIsNotNull(String osmServerName) {
		if (changeSetInUseId == null) {
			throw new IllegalStateException(
				"no change set created by osm server of type " + String.valueOf(osmServerName));
		}
	}

	/**
	 *
	 */
	@Override
	protected void add(Change change, OsmServer osmServer, OsmChangeContent optimizedDataSet) {
		Calendar changeTime;
		ChangeSetUpdateAble changeSet;

		assertThatChangeAndServerNotNull(change, osmServer);

		changeTime = change.getCreatedAt();

		// first run
		if (changeSetInUseId == null) {
			changeSetInUseId = osmServer.createChangeSet(changeTime, change.getUser());

			LOGGER.debug("initial changeset id = " + String.valueOf(changeSetInUseId));
		}
		else {
			boolean isOpen;
			osmServer.closeChangesetsNeededToBeClosed(changeTime);
			isOpen = osmServer.isChangeSetOpen(changeSetInUseId);

			if (!isOpen) {

				LOGGER.debug("new changeset request for change: " + change.toString());
				// LOGGER.debug("old changeset id = " + String.valueOf(changeSetInUseId));

				changeSetInUseId = osmServer.createChangeSet(changeTime, change.getUser());
			}
		}

		assertThatChangeSetIdIsNotNull(osmServer.getClass().getSimpleName());

		changeSet = osmServer.getChangeSet(changeSetInUseId);

		assertThatChangeSetNotNull(changeSet);

		optimizedDataSet.addChangeForChangeSet(change, changeSet);
	}

	@Override
	public String getName() {
		return "simple";
	}
}
