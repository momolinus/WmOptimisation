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

import static org.athmis.wmoptimisation.changeset.ChangeSetToolkit.FORMATTER;
import static org.athmis.wmoptimisation.changeset.ChangeSetToolkit.calToOsm;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.Node;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for OsmServer class, used for Test Driven Development (TDD). The
 * behavior of the OsmServer class should correspond to the real server (<a
 * href="http://wiki.openstreetmap.org/wiki/API_v0.6">OSM API v0.6</a>), but
 * could be limited by the need of the optimization project.
 * 
 */
public class OsmServerTest {

	@BeforeClass
	public static void setUpTest() {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	private OsmServer osmServer;

	@Before
	public void setUp() throws Exception {
		osmServer = new OsmServer();
	}

	@Test
	public void testCloseAn24hOpenChangeSet() throws ParseException {
		Calendar calendar, startTime;
		Long changesetId;

		calendar = GregorianCalendar.getInstance();
		calendar.set(2012, 4, 4, 0, 1);
		startTime = (GregorianCalendar) calendar.clone();

		changesetId = osmServer.createChangeSet(calendar);
		assertNotNull("changeset creation failed", changesetId);

		long id = System.nanoTime();
		int lat = 52, lon = 13;

		long diff = 0;
		int counterAgainstInfiniteLoop = 0;
		while (true) {
			boolean changeSetIsOpen;

			calendar.add(Calendar.MINUTE, 30);
			changeSetIsOpen = osmServer.isChangeSetOpen(changesetId, calendar);
			diff = calendar.getTimeInMillis() - startTime.getTimeInMillis();

			if (TimeUnit.MILLISECONDS.toHours(diff) >= 24) {
				String msg = "changeset must be closed , start: "
						+ FORMATTER.format(startTime.getTime()) + ", now: "
						+ FORMATTER.format(calendar.getTime());
				assertFalse(msg, changeSetIsOpen);
				break;
			} else {
				String msg = "changeset must be open  , start: "
						+ FORMATTER.format(startTime.getTime()) + ", now: "
						+ FORMATTER.format(calendar.getTime());
				assertTrue(msg, changeSetIsOpen);

				String timeStamp = calToOsm(calendar);
				Node node = new Node(id++, lat++, lon++, timeStamp);
				osmServer.storeChange(changesetId, node);
			}

			counterAgainstInfiniteLoop++;

			if (counterAgainstInfiniteLoop > 1000) {
				String msg = "after iterations " + counterAgainstInfiniteLoop
						+ " changeset still not closed, start: "
						+ FORMATTER.format(startTime.getTime()) + ", now: "
						+ FORMATTER.format(calendar.getTime());
				fail(msg);
			}
		}

		String msg = "time diff must be >= 24 h, but " + TimeUnit.MILLISECONDS.toHours(diff) + " h";
		assertTrue(msg, TimeUnit.MILLISECONDS.toHours(diff) >= 24);

		assertFalse("changeset must be closed", osmServer.isChangeSetOpen(changesetId, calendar));
	}

	@Test
	public void testCloseChangeSet() throws ParseException {
		Calendar calendar;
		Long changesetId;

		calendar = GregorianCalendar.getInstance();
		calendar.set(2012, 4, 4, 10, 0);

		changesetId = osmServer.createChangeSet(calendar);
		assertNotNull("changeset creation failed", changesetId);

		calendar.set(Calendar.MINUTE, 1);
		assertTrue("changeset must be open", osmServer.isChangeSetOpen(changesetId, calendar));

		calendar.set(Calendar.MINUTE, 30);
		assertTrue("changeset must be open", osmServer.isChangeSetOpen(changesetId, calendar));

		calendar.set(Calendar.MINUTE, 58);
		assertTrue("changeset must be open", osmServer.isChangeSetOpen(changesetId, calendar));

		calendar.set(Calendar.MINUTE, 59);
		assertTrue("changeset must be open", osmServer.isChangeSetOpen(changesetId, calendar));

		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		assertFalse("changeset still was open, but must be closed",
				osmServer.closeChangeSet(changesetId, calendar));
		assertFalse(osmServer.isChangeSetOpen(changesetId, calendar));
	}

	@Test
	public void testCloseChangeSet2() throws ParseException {
		Calendar calendar;
		Long changesetId;

		calendar = GregorianCalendar.getInstance();
		calendar.set(2012, 4, 4, 10, 0);

		changesetId = osmServer.createChangeSet(calendar);
		assertNotNull("changeset creation failed", changesetId);

		calendar.set(Calendar.MINUTE, 59);
		assertTrue("changeset must be open", osmServer.isChangeSetOpen(changesetId, calendar));
		assertTrue("changeset was closed", osmServer.closeChangeSet(changesetId, calendar));
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

	@Ignore("needs to much time")
	@Test
	public void testStoreChange() {
		Calendar calendar, startTime;
		Long changesetId;

		calendar = GregorianCalendar.getInstance();
		calendar.set(2012, 4, 4, 0, 0, 0);
		startTime = (GregorianCalendar) calendar.clone();
		changesetId = osmServer.createChangeSet(calendar);
		assertNotNull("changeset creation failed", changesetId);

		for (int i = 1; i <= 50000; i++) {
			Node node;
			calendar.add(Calendar.SECOND, 1);
			node = createDummyNode(i, calToOsm(calendar));

			String msg = "changeset must be open  , start: "
					+ FORMATTER.format(startTime.getTime()) + ", now: "
					+ FORMATTER.format(calendar.getTime()) + ", number of changes: " + i;

			boolean changeSetIsOpen;
			changeSetIsOpen = osmServer.isChangeSetOpen(changesetId, calendar);
			assertTrue(msg, changeSetIsOpen);
			osmServer.storeChange(changesetId, node);
		}

		assertFalse("changeset must be closed", osmServer.isChangeSetOpen(changesetId, calendar));

	}

	private Node createDummyNode(long i, String timeStamp) {
		return new Node(i, 52.0 + i * 0.000001, 13.0 + i * 0.000001, timeStamp);
	}
}
