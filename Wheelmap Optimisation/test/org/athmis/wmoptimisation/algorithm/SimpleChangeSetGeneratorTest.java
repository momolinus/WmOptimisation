package org.athmis.wmoptimisation.algorithm;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.changeset.ChangeSetTest;
import org.athmis.wmoptimisation.changeset.Node;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.junit.Before;
import org.junit.Test;

/**
 * This test is an integration test for {@linkplain OsmServer},
 * {@linkplain SimpleChangeSetGenerator} and {@linkplain OsmChangeContent}. It
 * tests some change adding constrains.
 * 
 * @author Marcus
 */
public class SimpleChangeSetGeneratorTest {

	private ChangeSetGenerator simpleChangeSetGenerator;
	private OsmChangeContent content;
	private OsmServer osmServer;

	@Before
	public void setUp() throws Exception {
		simpleChangeSetGenerator = new SimpleChangeSetGenerator();
		osmServer = new OsmServer();
		content = new OsmChangeContent();
	}

	/**
	 * Method tests adding two (different) nodes at two different days. The
	 * SimpleChangeSetGenerator has to create two {@linkplain ChangeSet}s with
	 * an area of 0 (because a single node has no area).
	 */
	@Test
	public void testAddNodeDifferentDays() {
		Node berlin = null;
		berlin = Node.getBerlin();

		simpleChangeSetGenerator.add(berlin, osmServer, content);
		List<Double> bboxes = content.getBoundingBoxesSquareDegree();
		assertEquals("number of bbox", 1, bboxes.size());
		assertEquals(	"bbox", 0.0, content.getBoundingBoxesSquareDegree().get(0),
						ChangeSetTest.STRONG_DELTA);

		Node nextDayNode =
			Node.getDifferentNode(berlin, (int) TimeUnit.HOURS.toMinutes(25), 0.2, 0.1);
		simpleChangeSetGenerator.add(nextDayNode, osmServer, content);
		bboxes = content.getBoundingBoxesSquareDegree();

		assertEquals("number of bbox", 2, bboxes.size());

		assertEquals(	"bbox (0)", 0, content.getBoundingBoxesSquareDegree().get(0),
						ChangeSetTest.STRONG_DELTA);

		assertEquals(	"bbox (1)", 0, content.getBoundingBoxesSquareDegree().get(1),
						ChangeSetTest.STRONG_DELTA);
	}

	/**
	 * Methode tests adding only one change (a {@linkplain Node}. The
	 * {@link SimpleChangeSetGenerator} has to create one {@linkplain ChangeSet}
	 * .
	 */
	@Test
	public void testAddOneNode() {
		Change berlin = null;
		berlin = Node.getBerlin();

		simpleChangeSetGenerator.add(berlin, osmServer, content);

		List<Double> bboxes = content.getBoundingBoxesSquareDegree();

		assertEquals("number of bbox", 1, bboxes.size());
	}

	@Test
	public void testAddTwoNode() {
		Node berlin = null;
		berlin = Node.getBerlin();

		simpleChangeSetGenerator.add(berlin, osmServer, content);
		List<Double> bboxes = content.getBoundingBoxesSquareDegree();
		assertEquals("number of bbox", 1, bboxes.size());
		assertEquals(	"bbox", 0.0, content.getBoundingBoxesSquareDegree().get(0),
						ChangeSetTest.STRONG_DELTA);

		simpleChangeSetGenerator
				.add(Node.getDifferentNode(berlin, 5, 0.2, 0.1), osmServer, content);
		bboxes = content.getBoundingBoxesSquareDegree();

		assertEquals("number of bbox", 1, bboxes.size());

		assertEquals(	"bbox", 0.2 * 0.1, content.getBoundingBoxesSquareDegree().get(0),
						ChangeSetTest.STRONG_DELTA);
	}
}
