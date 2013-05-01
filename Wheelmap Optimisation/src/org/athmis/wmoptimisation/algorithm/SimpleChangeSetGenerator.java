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

import java.text.ParseException;
import java.util.Calendar;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.osmserver.OsmServer;

/**
 * This is the simulation of the actual implementation of changeset generation
 * by Wheelmap-Server. This class is used to verify the actual behavior of
 * Wheelmap-Server and OSM-Server.
 * <p>
 * note: architecture is <a
 * href="http://en.wikipedia.org/wiki/Template_method_pattern">Template method
 * pattern</a>
 * 
 * @author Marcus Bleil<br>
 *         http://www.marcusbleil.de
 * 
 */
public class SimpleChangeSetGenerator extends ChangeSetGenerator {

	private Long changeSetInUse;

	/**
	 * @throws IllegalArgumentException
	 *             if change's creation date ist not parseable
	 */
	@Override
	protected void add(Change change, OsmServer osmServer, ChangeSetZipContentData optimizedDataSet) {
		Calendar changeTime;
		ChangeSet changeSet;

		// TODO Eingabe-Parameter auf null pr�fen

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
			// XXX pr�fen, wie die toString Implementierungen sind
			throw new RuntimeException("can't read change: '" + change.toString() + "'", e);
		}

		assert changeSetInUse != null;

		changeSet = osmServer.getChangeSet(changeSetInUse);
		optimizedDataSet.addChangeForChangeSet(change, changeSet);
	}
}
