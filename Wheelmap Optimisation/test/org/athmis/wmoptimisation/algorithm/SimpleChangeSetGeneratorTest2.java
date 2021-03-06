package org.athmis.wmoptimisation.algorithm;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.athmis.wmoptimisation.changeset.ChangeSetTest;
import org.athmis.wmoptimisation.changeset.Node;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.junit.Before;
import org.junit.Test;

/**
 * This test is an integration test for {@linkplain OsmServer},
 * {@linkplain SimpleChangeSetGenerator} and {@linkplain OsmChangeContent}. It tests, that
 * optimization keep care on sizes of changesets.
 *
 * @author Marcus
 */
public class SimpleChangeSetGeneratorTest2 {

	private OsmChangeContent content;
	private OsmServer osmServer;
	private ChangeSetGenerator simpleChangeSetGenerator;

	@Before
	public void setUp() throws Exception {
		simpleChangeSetGenerator = new SimpleChangeSetGenerator();
		osmServer = new OsmServer();
		content = new OsmChangeContent();
	}

	@Test
	public void testCreateOptimizedChangeSets() {
		Node berlin, berlin2;

		berlin = Node.getBerlin();
		berlin2 = Node.getMovedNode(berlin, 5, 0.2, 0.1);

		simpleChangeSetGenerator.add(berlin, osmServer, content);
		simpleChangeSetGenerator.add(berlin2, osmServer, content);

		List<Double> bboxes = content.getBoundingBoxesSquareDegree();
		double nodesBbox = Node.getBbox(berlin, berlin2);
		assertEquals("one bbox - one changeset - expected", 1, bboxes.size());
		assertEquals(	"bounding box comparison: nodes and generated changesets", nodesBbox,
						bboxes.get(0), ChangeSetTest.STRONG_DELTA);
	}
}
