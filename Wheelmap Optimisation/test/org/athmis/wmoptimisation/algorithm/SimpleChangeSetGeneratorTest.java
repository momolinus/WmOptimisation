package org.athmis.wmoptimisation.algorithm;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSetTest;
import org.athmis.wmoptimisation.changeset.Node;
import org.athmis.wmoptimisation.changeset.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.junit.Before;
import org.junit.Test;

/**
 * This test is an integration test for {@linkplain OsmServer},
 * {@linkplain SimpleChangeSetGenerator} and {@linkplain OsmChangeContent}.
 * 
 * @author Marcus
 */
public class SimpleChangeSetGeneratorTest {

	private SimpleChangeSetGenerator changeGenerator;
	private OsmServer osmServer;
	private OsmChangeContent content;

	@Before
	public void setUp() throws Exception {
		changeGenerator = new SimpleChangeSetGenerator();
		osmServer = new OsmServer();
		content = new OsmChangeContent();
	}

	@Test
	public void testAddOneNode() {
		Change berlin = null;
		berlin = Node.getBerlin();

		changeGenerator.add(berlin, osmServer, content);

		List<Double> bboxes = content.getBoundingBoxesSquareDegree();

		assertEquals("number of bbox", 1, bboxes.size());
	}

	@Test
	public void testAddTwoNode() {
		Node berlin = null;
		berlin = Node.getBerlin();

		changeGenerator.add(berlin, osmServer, content);
		List<Double> bboxes = content.getBoundingBoxesSquareDegree();
		assertEquals("number of bbox", 1, bboxes.size());
		assertEquals(	"bbox", 0.0, content.getBoundingBoxesSquareDegree().get(0),
						ChangeSetTest.STRONG_DELTA);

		changeGenerator.add(Node.getDifferentNode(berlin, 5, 0.2, 0.1), osmServer, content);
		bboxes = content.getBoundingBoxesSquareDegree();
		assertEquals("number of bbox", 1, bboxes.size());
		assertEquals(	"bbox", 0.2 * 0.1, content.getBoundingBoxesSquareDegree().get(0),
						ChangeSetTest.STRONG_DELTA);

	}

	@Test
	public void testAddNodeDifferentDays() {
		Node berlin = null;
		berlin = Node.getBerlin();

		changeGenerator.add(berlin, osmServer, content);
		List<Double> bboxes = content.getBoundingBoxesSquareDegree();
		assertEquals("number of bbox", 1, bboxes.size());
		assertEquals(	"bbox", 0.0, content.getBoundingBoxesSquareDegree().get(0),
						ChangeSetTest.STRONG_DELTA);

		Node nextDayNode =
			Node.getDifferentNode(berlin, (int) TimeUnit.HOURS.toMinutes(25), 0.2, 0.1);
		changeGenerator.add(nextDayNode, osmServer, content);
		bboxes = content.getBoundingBoxesSquareDegree();

		assertEquals("number of bbox", 2, bboxes.size());
		assertEquals(	"bbox (0)", 0, content.getBoundingBoxesSquareDegree().get(0),
						ChangeSetTest.STRONG_DELTA);
		assertEquals(	"bbox (1)", 0, content.getBoundingBoxesSquareDegree().get(1),
						ChangeSetTest.STRONG_DELTA);

	}
}
