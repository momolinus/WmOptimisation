package org.athmis.wmoptimisation.changeset;

import static org.junit.Assert.*;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ChangeSetTest {

	private ChangeSet curTimeOpenChangeSet;
	private Calendar currentTime;

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

		area = curTimeOpenChangeSet.getArea();
		assertTrue(Double.isInfinite(area));

		Change change;
		change = Node.getBerlinAsNode();
		curTimeOpenChangeSet.updateArea(change);

		area = curTimeOpenChangeSet.getArea();
		assertEquals(0.0, area, 0.0001);
	}

	@Test
	public void testGetArea2() {
		double area;
		List<Node> twoNodes;

		// FIXME die Zeit der Nodes kann nicht stimmen
		twoNodes = Node.getNodes(0.1);
		curTimeOpenChangeSet.updateArea(twoNodes.get(0));
		curTimeOpenChangeSet.updateArea(twoNodes.get(1));

		area = curTimeOpenChangeSet.getArea();
		assertEquals(0.1 * 0.1, area, 0.00000001);
	}

	@Test
	public void testGetClosed() {
		assertTrue(curTimeOpenChangeSet.isOpen());

		currentTime.add(Calendar.HOUR, 24);
		curTimeOpenChangeSet.close(currentTime);
		assertFalse(curTimeOpenChangeSet.isOpen());

		assertEquals(currentTime.getTimeInMillis(), curTimeOpenChangeSet.getClosed()
				.getTimeInMillis());
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
		fail("Not yet implemented");
	}

}
