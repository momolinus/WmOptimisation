package org.athmis.wmoptimisation.osmserver;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.Node;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class OsmServerAddChangesTest {

	@BeforeClass
	public static void setUpTest() {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	private Calendar calendar;

	private OsmServer osmServer;
	private Calendar startTime;

	@Before
	public void setUp() throws Exception {
		osmServer = new OsmServer();

		calendar = GregorianCalendar.getInstance();
		calendar.set(2012, 4, 4, 0, 1);
		startTime = (GregorianCalendar) calendar.clone();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullAsNode() {
		Long changesetId;
		changesetId = osmServer.createChangeSet(startTime);
		osmServer.storeNode(changesetId, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullAsChangesetId() {
		osmServer.storeNode(null, Node.getBerlinAsNode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullToClosedChangeset() throws ParseException {
		Long changesetId;

		changesetId = osmServer.createChangeSet(startTime);
		startTime.add(Calendar.HOUR, 24);
		osmServer.closeChangeSet(changesetId, startTime);

		assertFalse(osmServer.isChangeSetOpen(changesetId, startTime));

		osmServer.storeNode(changesetId, null);
	}
}
