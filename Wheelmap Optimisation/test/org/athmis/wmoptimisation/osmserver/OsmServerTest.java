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
