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

	private final static DateFormat FORMATTER = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
	private Calendar currentTime;

	private ChangeSetUpdateAble curTimeOpenChangeSet;

	private Node alaska;

	@SuppressWarnings("PMD.VariableNamingConventions")
	private Node argentinia_most_S;

	private Node europa;

	private Node australia;

	@Before
	public void setUp() throws Exception {

		String createdAt;

		currentTime = Calendar.getInstance();

		createdAt = ChangeSetToolkit.calToOsm(currentTime);
		curTimeOpenChangeSet = new ChangeSetUpdateAble(createdAt, 2, true);

		alaska = Node.getNode(65, -147);
		argentinia_most_S = Node.getDifferentNode(alaska, -35, -65);
		europa = Node.getDifferentNode(alaska, 52, 13);
		australia = Node.getDifferentNode(alaska, -27, 121);
	}

	@Test
	public void testGetArea() {
		double area;

		area = curTimeOpenChangeSet.getBoundingBoxSquareDegree();
		assertTrue(Double.isInfinite(area));

		Change change;
		change = Node.getBerlinAsNode();
		curTimeOpenChangeSet.updateBoundingBox(change);

		area = curTimeOpenChangeSet.getBoundingBoxSquareDegree();
		assertEquals(0.0, area, STRONG_DELTA);
	}

	@Test
	public void testGetArea2() {
		double area;
		List<Node> twoNodes;

		twoNodes = Node.getNodes(0.1);
		curTimeOpenChangeSet.updateBoundingBox(twoNodes.get(0));
		curTimeOpenChangeSet.updateBoundingBox(twoNodes.get(1));

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

		currentTimeAsString = FORMATTER.format(currentTime.getTime());
		closedTimeAsString = FORMATTER.format(curTimeOpenChangeSet.getClosed().getTime());

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
		curTimeOpenChangeSet.updateBoundingBox(twoNodes.get(0));

		assertEquals(	"bounding box ", 0.0, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);

		twoNodes.get(1).setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(twoNodes.get(1));
		assertEquals(	"bounding box", 0.1 * 0.1,
						curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);

	}

	@SuppressWarnings("PMD.MethodNamingConventions")
	@Test
	public void testUpdateArea_NW_SW() {

		alaska.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(alaska);
		assertEquals(	"bounding box ", 0.0, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);

		argentinia_most_S.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(argentinia_most_S);

		double lat = alaska.getLat() - argentinia_most_S.getLat();
		double lon = alaska.getLon() - argentinia_most_S.getLon();
		double area = Math.abs(lat * lon);

		assertEquals(	"bounding box ", area, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);
	}

	@SuppressWarnings("PMD.MethodNamingConventions")
	@Test
	public void testUpdateArea_SW_NW() {

		argentinia_most_S.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(argentinia_most_S);
		assertEquals(	"bounding box ", 0.0, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);

		alaska.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(alaska);
		double area =
			Math.abs((argentinia_most_S.getLat() - alaska.getLat())
				* (argentinia_most_S.getLon() - alaska.getLon()));
		assertEquals(	"bounding box ", area, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);
	}

	@SuppressWarnings("PMD.MethodNamingConventions")
	@Test
	public void testUpdateArea_SW_NW_NE() {

		argentinia_most_S.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(argentinia_most_S);
		assertEquals(	"bounding box ", 0.0, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);

		alaska.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(alaska);

		europa.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(europa);

		double lat = alaska.getLat() - argentinia_most_S.getLat();
		double lon = alaska.getLon() - europa.getLon();
		double area = Math.abs(lat * lon);
		Math.abs((argentinia_most_S.getLat() - alaska.getLat())
			* (argentinia_most_S.getLon() - alaska.getLon()));

		assertEquals(	"bounding box ", area, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);
	}

	
	@SuppressWarnings("PMD.MethodNamingConventions")
	@Test
	public void testUpdateArea_SW_NW_NE_SE() {

		argentinia_most_S.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(argentinia_most_S);
		assertEquals(	"bounding box ", 0.0, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);

		alaska.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(alaska);

		europa.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(europa);

		australia.setChangeset(curTimeOpenChangeSet.getId());
		curTimeOpenChangeSet.updateBoundingBox(australia);

		double lat = alaska.getLat() - argentinia_most_S.getLat();
		double lon = alaska.getLon() - australia.getLon();
		double area = Math.abs(lat * lon);
		Math.abs((argentinia_most_S.getLat() - alaska.getLat())
			* (argentinia_most_S.getLon() - alaska.getLon()));

		assertEquals(	"bounding box ", area, curTimeOpenChangeSet.getBoundingBoxSquareDegree(),
						STRONG_DELTA);
	}

	@Test
	public void testCloseNow() {

		curTimeOpenChangeSet.closeNow();

		assertFalse("closed", curTimeOpenChangeSet.isOpen());

	}
}
