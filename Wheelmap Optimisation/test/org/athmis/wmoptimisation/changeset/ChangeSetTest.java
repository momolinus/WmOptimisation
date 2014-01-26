package org.athmis.wmoptimisation.changeset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ChangeSetTest {

	public static final double STRONG_DELTA = 0.00000001;
	private final static DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
	private Calendar currentTime;

	private ChangeSet curTimeOpenChangeSet;

	@Before
	public void setUp() throws Exception {

		String createdAt;

		currentTime = Calendar.getInstance();

		createdAt = ChangeSetToolkit.calToOsm(currentTime);
		curTimeOpenChangeSet = new ChangeSet(createdAt, 2, true);
	}

	@Test
	public void testGetArea() {
		double area;

		area = curTimeOpenChangeSet.getBoundingBoxSquareDegree();
		assertTrue(Double.isInfinite(area));

		Change change;
		change = Node.getBerlinAsNode();
		curTimeOpenChangeSet.updateArea(change);

		area = curTimeOpenChangeSet.getBoundingBoxSquareDegree();
		assertEquals(0.0, area, STRONG_DELTA);
	}

	@Test
	public void testGetArea2() {
		double area;
		List<Node> twoNodes;

		twoNodes = Node.getNodes(0.1);
		curTimeOpenChangeSet.updateArea(twoNodes.get(0));
		curTimeOpenChangeSet.updateArea(twoNodes.get(1));

		area = curTimeOpenChangeSet.getBoundingBoxSquareDegree();
		assertEquals(0.1 * 0.1, area, STRONG_DELTA);
	}

	@Test
	public void testGetClosed() {
		assertTrue(curTimeOpenChangeSet.isOpen());

		currentTime.add(Calendar.HOUR, 24);
		curTimeOpenChangeSet.close(currentTime);
		assertFalse(curTimeOpenChangeSet.isOpen());

		String currentTimeAsString, closedTimeAsString;

		currentTimeAsString = formatter.format(currentTime.getTime());
		closedTimeAsString = formatter.format(curTimeOpenChangeSet.getClosed().getTime());

		assertEquals("currentTime = " + currentTimeAsString + ", closedTime = "
			+ currentTimeAsString, currentTimeAsString, closedTimeAsString);
	}

	@Test
	public void testGetOpenTimeInHours() {
		double openHours;
		currentTime.add(Calendar.HOUR, 1);
		curTimeOpenChangeSet.close(currentTime);
		openHours = curTimeOpenChangeSet.getOpenTimeInHours();

		assertEquals(1, openHours, 0.1);
	}

	@Test
	public void testIsOpen() {
		assertTrue(curTimeOpenChangeSet.isOpen());
		currentTime.add(Calendar.HOUR, 24);
		curTimeOpenChangeSet.close(currentTime);
		assertFalse(curTimeOpenChangeSet.isOpen());
	}

	@Test
	public void testUpdateArea() {
		List<Node> twoNodes;

		twoNodes = Node.getNodes(0.1);

		twoNodes.get(0).setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateArea(twoNodes.get(0));

		assertEquals(	"bounding box ", 0.0, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);

		twoNodes.get(1).setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateArea(twoNodes.get(1));
		assertEquals(	"bounding box", 0.1 * 0.1,
						curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);

	}

	@Test
	public void testCloseNow() {

		curTimeOpenChangeSet.closeNow();

		assertFalse("closed", curTimeOpenChangeSet.isOpen());

	}

}
