/**
 *
 */
package org.athmis.wmoptimisation.algorithm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.Node;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marcus Bleil
 */
public class MinimizeAreaSelfChangeSetGenartorTest {

	private static final double MAX_BBOX_SIZE = 0.0016;
	private MinimizeAreaSelfChangeSetGenartor generator;
	private OsmServer osmServer;
	private OsmChangeContent optimizedDataSet;
	private Change nodeNW;
	private Node nodeNW2;
	private Node nodeNW3;

	@Before
	public void setUp() {
		generator = new MinimizeAreaSelfChangeSetGenartor(MAX_BBOX_SIZE);
		osmServer = new OsmServer();
		optimizedDataSet = new OsmChangeContent();

		nodeNW = Node.getNode(1, 1);
		nodeNW2 = Node.getNode(1.04, 1.04);
		nodeNW3 = Node.getNode(1.04, 1.05);
	}

	@Test
	public void test() {
		Long firstId, secondId, thirdId;

		generator.add(nodeNW, osmServer, optimizedDataSet);
		firstId = generator.changeSetInUseId;
		generator.add(nodeNW2, osmServer, optimizedDataSet);
		secondId = generator.changeSetInUseId;
		generator.add(nodeNW3, osmServer, optimizedDataSet);
		thirdId = generator.changeSetInUseId;

		assertThat(firstId, is(notNullValue()));

		assertThat(secondId, is(sameInstance(firstId)));

		assertThat(thirdId, is(notNullValue()));
		assertThat(thirdId, is(not(sameInstance(firstId))));
		assertThat(thirdId, is(not(sameInstance(secondId))));
	}
}
