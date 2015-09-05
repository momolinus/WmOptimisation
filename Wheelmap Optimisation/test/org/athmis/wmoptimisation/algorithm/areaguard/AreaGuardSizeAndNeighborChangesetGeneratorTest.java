/**
 * created at 18.01.2015 (12:50:05)
 */
package org.athmis.wmoptimisation.algorithm.areaguard;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.*;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.Node;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.junit.*;

/**
 * @author Marcus
 */
public class AreaGuardSizeAndNeighborChangesetGeneratorTest {

	private static final double MAX_EDGE_0_04 = 0.04;

	@BeforeClass
	public static void initTest() {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);
	}

	private static void assertThatDeltaTimeIsLessThan60Minutes(Change... changes) {

		for (int i = 0; i < changes.length; i++) {
			for (int successor = i + 1; successor < changes.length; successor++) {
				long deltaTime =
					Math.abs(TimeUnit.MILLISECONDS.toMinutes(changes[i].getCreatedAt()
							.getTimeInMillis()
						- changes[successor].getCreatedAt().getTimeInMillis()));
				assertThat("delta time is too large for index = " + i + " and it's successor = "
					+ successor, deltaTime, is(lessThan(60l)));
			}

		}
	}

	private static void assertThatDeltaTimeIsMoreTHan60Minutes(Change change, Change... changes) {

		for (int i = 0; i < changes.length; i++) {
			long deltaTime =
				Math.abs(TimeUnit.MILLISECONDS.toMinutes(changes[i].getCreatedAt()
						.getTimeInMillis() - change.getCreatedAt().getTimeInMillis()));
			assertThat("delta time is too less for index = " + i, deltaTime, is(lessThan(60l)));

		}
	}

	private AreaGuardSizeAndNeighborChangesetGenerator generator;
	private Change node_in_11_1;
	private Change node_out_11_1;
	private Change node11;
	private Change node11_Border;

	// XXX Regel für die Konfiguration des Loggers implementieren

	private OsmChangeContent optimizedDataSet;

	private OsmServer osmServer;

	@Before
	public void setUp() {
		generator = new AreaGuardSizeAndNeighborChangesetGenerator(MAX_EDGE_0_04);
		osmServer = new OsmServer();
		optimizedDataSet = new OsmChangeContent();

		node11 = Node.getNode(1, 1);
		node11_Border = Node.getNode(1.04, 1.04);
		node_out_11_1 = Node.getNode(1.04, 1.05);
		Node.getNode(1.09, 1.09);
		node_in_11_1 = Node.getNode(1.02, 1.02);
	}

	@Test
	public void test_that_border_is_respected() {
		Long firstId, secondId, thirdId;

		generator.add(node11, osmServer, optimizedDataSet);
		firstId = generator.changeSetInUseId;

		generator.add(node11_Border, osmServer, optimizedDataSet);
		secondId = generator.changeSetInUseId;

		generator.add(node_out_11_1, osmServer, optimizedDataSet);
		thirdId = generator.changeSetInUseId;

		assertThatDeltaTimeIsLessThan60Minutes(node11, node11_Border, node_out_11_1);

		assertThat(firstId, is(notNullValue()));

		assertThat(secondId, is(sameInstance(firstId)));
		assertThat(thirdId, is(not(equalTo(firstId))));
		assertThat(thirdId, is(not(equalTo(secondId))));
	}

	@Test
	public void test_that_near_but_time_distant_nodes_later_stored_together() {
		Long firstId, secondId, thirdId, forthId;

		generator.add(node11, osmServer, optimizedDataSet);
		firstId = generator.changeSetInUseId;

		generator.add(node11_Border, osmServer, optimizedDataSet);
		secondId = generator.changeSetInUseId;

		generator.add(node_out_11_1, osmServer, optimizedDataSet);
		thirdId = generator.changeSetInUseId;

		generator.add(Node.later(node_in_11_1, 60), osmServer, optimizedDataSet);
		forthId = generator.changeSetInUseId;

		assertThatDeltaTimeIsLessThan60Minutes(node11, node11_Border, node_out_11_1);
		assertThatDeltaTimeIsMoreTHan60Minutes(node_in_11_1, node11, node11_Border, node_out_11_1);

		assertThat(forthId, is(notNullValue()));
		assertThat(forthId, is(not(sameInstance(thirdId))));
		assertThat(forthId, is(not(sameInstance(firstId))));
		assertThat(forthId, is(not(sameInstance(secondId))));
	}

	@Test
	public void test_that_near_nodes_later_stored_together_2() {
		Long firstId, secondId, thirdId, forthId;

		generator.add(node11, osmServer, optimizedDataSet);
		firstId = generator.changeSetInUseId;

		generator.add(node11_Border, osmServer, optimizedDataSet);
		secondId = generator.changeSetInUseId;

		generator.add(node_out_11_1, osmServer, optimizedDataSet);
		thirdId = generator.changeSetInUseId;

		generator.add(Node.later(node_in_11_1, 59), osmServer, optimizedDataSet);
		forthId = generator.changeSetInUseId;

		assertThatDeltaTimeIsLessThan60Minutes(node_in_11_1, node11, node11_Border, node_out_11_1);

		assertThat(forthId, is(notNullValue()));
		assertThat(forthId, is(not(sameInstance(thirdId))));
		assertThat(forthId, is(sameInstance(firstId)));
		assertThat(forthId, is(sameInstance(secondId)));
	}

	@Test
	public void test_that_near_nodes_later_stored_together() {
		Long firstId, secondId, thirdId, forthId;

		generator.add(node11, osmServer, optimizedDataSet);
		firstId = generator.changeSetInUseId;

		generator.add(node11_Border, osmServer, optimizedDataSet);
		secondId = generator.changeSetInUseId;

		generator.add(node_out_11_1, osmServer, optimizedDataSet);
		thirdId = generator.changeSetInUseId;

		generator.add(node_in_11_1, osmServer, optimizedDataSet);
		forthId = generator.changeSetInUseId;

		assertThatDeltaTimeIsLessThan60Minutes(node11, node11_Border, node_out_11_1, node_in_11_1);

		assertThat(firstId, is(notNullValue()));
		assertThat(secondId, is(sameInstance(firstId)));

		assertThat(thirdId, is(notNullValue()));
		assertThat(thirdId, is(not(sameInstance(firstId))));
		assertThat(thirdId, is(not(sameInstance(secondId))));

		assertThat(forthId, is(notNullValue()));
		assertThat(forthId, is(not(sameInstance(thirdId))));
		assertThat(forthId, is(sameInstance(firstId)));
		assertThat(forthId, is(sameInstance(secondId)));
	}

	@Test
	public void test_that_near_nodes_stored_together() {
		Long firstId, secondId, thirdId;

		generator.add(node11, osmServer, optimizedDataSet);
		firstId = generator.changeSetInUseId;

		generator.add(node11_Border, osmServer, optimizedDataSet);
		secondId = generator.changeSetInUseId;

		generator.add(node_out_11_1, osmServer, optimizedDataSet);
		thirdId = generator.changeSetInUseId;

		assertThat(firstId, is(notNullValue()));

		assertThat(secondId, is(sameInstance(firstId)));

		assertThat(thirdId, is(notNullValue()));
		assertThat(thirdId, is(not(sameInstance(firstId))));
		assertThat(thirdId, is(not(sameInstance(secondId))));
	}

	@Test
	public void test_that_near_nodes_stored_together_2() {
		Long firstId, secondId, thirdId, fourthID;

		generator.add(node11, osmServer, optimizedDataSet);
		firstId = generator.changeSetInUseId;

		generator.add(node11_Border, osmServer, optimizedDataSet);
		secondId = generator.changeSetInUseId;

		generator.add(node_out_11_1, osmServer, optimizedDataSet);
		thirdId = generator.changeSetInUseId;

		generator.add(	Node.later(Node.getMovedNode((Node) node11, 0.0, 0.01), 59 - 5), osmServer,
						optimizedDataSet);
		fourthID = generator.changeSetInUseId;

		assertThat(fourthID, is(notNullValue()));

		assertThat(fourthID, is(sameInstance(firstId)));
		assertThat(fourthID, is(sameInstance(secondId)));
		assertThat(fourthID, is(not(sameInstance(thirdId))));
	}
}
