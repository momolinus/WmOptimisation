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

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for OsmServer class, used for Test Driven Development (TDD). The
 * behavior of the OsmServer class should correspond to the real server (<a
 * href="http://wiki.openstreetmap.org/wiki/API_v0.6">OSM API v0.6</a>), but
 * could be limited by the need of the optimization project.
 * 
 * @author Marcus
 * 
 */
public class OsmServerTest {

	private OsmServer osmServer;

	@Before
	public void setUp() throws Exception {
		osmServer = new OsmServer();
	}

	/**
	 * Test if server is closing a unused changeset after one hour ("time out").
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testOneHourClosingChangeSet() throws ParseException {
		Calendar calendar;
		Long changesetId;

		calendar = GregorianCalendar.getInstance();
		calendar.set(2012, 4, 4, 10, 10);
		changesetId = osmServer.createChangeSet(calendar);
		assertNotNull("changeset creation failed", changesetId);

		calendar.add(Calendar.MINUTE, 59);
		assertTrue("changeset must be open", osmServer.isChangeSetOpen(changesetId, calendar));

		// now 60 minutes later and no changes added
		calendar.add(Calendar.MINUTE, 1);
		assertFalse("changeset must be closed", osmServer.isChangeSetOpen(changesetId, calendar));
	}

	@Test
	public void testClosingAnOpenChangeset() throws ParseException {
		Calendar calendar;
		Long changesetId;

		calendar = GregorianCalendar.getInstance();
		calendar.set(2012, 4, 4, 10, 10);
		changesetId = osmServer.createChangeSet(calendar);
		assertNotNull("changeset creation failed", changesetId);

		calendar.add(Calendar.MINUTE, 59);
		assertTrue("changeset was closed", osmServer.closeChangeSet(changesetId, calendar));
	}

	@Test
	public void testClosingAnClosingChangeset() throws ParseException {
		Calendar calendar;
		Long changesetId;

		calendar = GregorianCalendar.getInstance();
		calendar.set(2012, 4, 4, 10, 10);
		changesetId = osmServer.createChangeSet(calendar);
		assertNotNull("changeset creation failed", changesetId);

		calendar.add(Calendar.MINUTE, 60);
		assertFalse("changeset still was open, but must be closed",
				osmServer.closeChangeSet(changesetId, calendar));
	}
}
