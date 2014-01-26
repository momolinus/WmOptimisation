package org.athmis.wmoptimisation.algorithm;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSetTest;
import org.athmis.wmoptimisation.changeset.Node;
import org.athmis.wmoptimisation.changeset.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
		Calendar secondTime;

		changeGenerator.add(berlin, osmServer, content);

		List<Double> bboxes = content.getBoundingBoxesSquareDegree();

		assertEquals("number of bbox", 1, bboxes.size());
		assertEquals(	"bbox", 0.0, content.getBoundingBoxesSquareDegree().get(0),
						ChangeSetTest.STRONG_DELTA);

		Node berlinSpy = Mockito.spy(berlin);
		secondTime = (Calendar) berlin.getCreatedAt().clone();
		secondTime.add(Calendar.MINUTE, 5);

		Mockito.when(berlinSpy.getCreatedAt()).thenReturn(secondTime);
		// Mockito.when(berlinSpy.get
	}
}
