/**
 * created at 18.01.2015 (12:50:05)
 */
package org.athmis.wmoptimisation.algorithm.areaguard;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.Node;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;
import org.athmis.wmoptimisation.osmserver.OsmServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

	/**
	 * Methods tests that 'delta creation time' between all given Changes is less than 60 minute. If
	 * not method throws an exception using
	 * {@linkplain Assert#assertThat(String, Object, org.hamcrest.Matcher)}.
	 */
	private static void assertThatDeltaTimeIsLessThan60Minutes(Change... changes) {

		for (int i = 0; i < changes.length; i++) {

			for (int successor = i + 1; successor < changes.length; successor++) {
				long changeTime, changeTimeSuccesor;

				changeTime = changes[i].getCreatedAt().getTimeInMillis();
				// meaning later -> greater
				changeTimeSuccesor = changes[successor].getCreatedAt().getTimeInMillis();

				long deltaTime = Math.abs(changeTimeSuccesor - changeTime);

				assertThat("delta time is too large for index = " + i + " and it's successor = "
					+ successor, deltaTime, is(lessThan(60l)));
			}
		}
	}

	/**
	 * Methods tests that 'delta creation time' between first change and all other given Changes is
	 * more or equal than 60 minutes. If not method throws an exception using
	 * {@linkplain Assert#assertThat(String, Object, org.hamcrest.Matcher)}.
	 */
	private static void assertThatDeltaTimeIsMoreOrEqualThan60Minutes(Change change60MinutesYounger,
		Change... changes) {

		for (int i = 0; i < changes.length; i++) {
			long olderCreationTime, youngerCreationTime;

			olderCreationTime = changes[i].getCreatedAt().getTimeInMillis();
			// meaning later -> greater
			youngerCreationTime = change60MinutesYounger.getCreatedAt().getTimeInMillis();

			long deltaTime = youngerCreationTime - olderCreationTime;

			assertThat(	"delta time is too less for index = " + i, deltaTime,
						is(greaterThanOrEqualTo(60l)));
		}
	}

	private AreaGuardSizeAndNeighborChangesetGenerator generatorGuradingSizeAndNeigbors;
	private Change node11;
	private Change nodeOutOfNode11Borders;
	private Change nodeWithin_2_Node11Borders;
	private Change nodeWithinNode11Borders;

	// XXX Regel für die Konfiguration des Loggers implementieren

	private OsmChangeContent optimizedDataSet;

	private OsmServer osmServer;

	@Before
	public void setUp() {

		generatorGuradingSizeAndNeigbors =
			new AreaGuardSizeAndNeighborChangesetGenerator(MAX_EDGE_0_04);
		osmServer = new OsmServer();
		optimizedDataSet = new OsmChangeContent();

		node11 = Node.getNode(1, 1);
		nodeWithinNode11Borders = Node.getNode(1.04, 1.04);
		nodeOutOfNode11Borders = Node.getNode(1.04, 1.05);
		Node.getNode(1.09, 1.09);
		nodeWithin_2_Node11Borders = Node.getNode(1.02, 1.02);
	}

	@Test
	public void test_that_border_is_respected() {

		Long firstId, secondId, thirdId;

		generatorGuradingSizeAndNeigbors.add(node11, osmServer, optimizedDataSet);
		firstId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeWithinNode11Borders, osmServer, optimizedDataSet);
		secondId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeOutOfNode11Borders, osmServer, optimizedDataSet);
		thirdId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		assertThatDeltaTimeIsLessThan60Minutes(	node11, nodeWithinNode11Borders,
												nodeOutOfNode11Borders);

		assertThat(firstId, is(notNullValue()));

		assertThat(secondId, is(sameInstance(firstId)));
		assertThat(thirdId, is(not(equalTo(firstId))));
		assertThat(thirdId, is(not(equalTo(secondId))));
	}

	@Test
	public void test_that_near_but_time_distant_nodes_later_stored_together() {

		Long firstId, secondId, forthId;

		generatorGuradingSizeAndNeigbors.add(node11, osmServer, optimizedDataSet);
		firstId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();
		generatorGuradingSizeAndNeigbors.add(nodeWithinNode11Borders, osmServer, optimizedDataSet);
		secondId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();
		generatorGuradingSizeAndNeigbors.add(nodeOutOfNode11Borders, osmServer, optimizedDataSet);
		Node laterChange = Node.later(nodeWithin_2_Node11Borders, 60);
		generatorGuradingSizeAndNeigbors.add(laterChange, osmServer, optimizedDataSet);
		forthId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		assertThatDeltaTimeIsLessThan60Minutes(	node11, nodeWithinNode11Borders,
												nodeOutOfNode11Borders);
		assertThatDeltaTimeIsMoreOrEqualThan60Minutes(	laterChange, node11, nodeWithinNode11Borders,
														nodeOutOfNode11Borders);

		assertThat(forthId, is(notNullValue()));
		assertThat(forthId, is(not(sameInstance(firstId))));
		assertThat(forthId, is(not(sameInstance(secondId))));
	}

	/**
	 * Test was implemented after little change on algorithm let fail test for thirId
	 */
	@Test
	public void test_that_near_but_time_distant_nodes_later_stored_together2() {

		Long thirdId, forthId;

		generatorGuradingSizeAndNeigbors.add(node11, osmServer, optimizedDataSet);
		generatorGuradingSizeAndNeigbors.add(nodeWithinNode11Borders, osmServer, optimizedDataSet);
		generatorGuradingSizeAndNeigbors.add(nodeOutOfNode11Borders, osmServer, optimizedDataSet);
		thirdId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();
		Node laterChange = Node.later(nodeWithin_2_Node11Borders, 60);
		generatorGuradingSizeAndNeigbors.add(laterChange, osmServer, optimizedDataSet);
		forthId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		assertThatDeltaTimeIsLessThan60Minutes(	node11, nodeWithinNode11Borders,
												nodeOutOfNode11Borders);
		assertThatDeltaTimeIsMoreOrEqualThan60Minutes(	laterChange, node11, nodeWithinNode11Borders,
														nodeOutOfNode11Borders);

		assertThat(forthId, is(not(sameInstance(thirdId))));
	}

	@Test
	public void test_that_near_nodes_later_stored_together() {

		Long firstId, secondId, thirdId, forthId;

		generatorGuradingSizeAndNeigbors.add(node11, osmServer, optimizedDataSet);
		firstId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeWithinNode11Borders, osmServer, optimizedDataSet);
		secondId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeOutOfNode11Borders, osmServer, optimizedDataSet);
		thirdId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(	nodeWithin_2_Node11Borders, osmServer,
												optimizedDataSet);
		forthId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		assertThatDeltaTimeIsLessThan60Minutes(	node11, nodeWithinNode11Borders,
												nodeOutOfNode11Borders, nodeWithin_2_Node11Borders);

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
	public void test_that_near_nodes_later_stored_together_2() {

		Long firstId, secondId, thirdId, forthId;

		generatorGuradingSizeAndNeigbors.add(node11, osmServer, optimizedDataSet);
		firstId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeWithinNode11Borders, osmServer, optimizedDataSet);
		secondId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeOutOfNode11Borders, osmServer, optimizedDataSet);
		thirdId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(	Node.later(nodeWithin_2_Node11Borders, 59), osmServer,
												optimizedDataSet);
		forthId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		assertThatDeltaTimeIsLessThan60Minutes(	nodeWithin_2_Node11Borders, node11,
												nodeWithinNode11Borders, nodeOutOfNode11Borders);

		assertThat(forthId, is(notNullValue()));
		assertThat(forthId, is(not(sameInstance(thirdId))));
		assertThat(forthId, is(sameInstance(firstId)));
		assertThat(forthId, is(sameInstance(secondId)));
	}

	@Test
	public void test_that_near_nodes_stored_together() {

		Long firstId, secondId, thirdId;

		generatorGuradingSizeAndNeigbors.add(node11, osmServer, optimizedDataSet);
		firstId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeWithinNode11Borders, osmServer, optimizedDataSet);
		secondId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeOutOfNode11Borders, osmServer, optimizedDataSet);
		thirdId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		assertThat(firstId, is(notNullValue()));

		assertThat(secondId, is(sameInstance(firstId)));

		assertThat(thirdId, is(notNullValue()));
		assertThat(thirdId, is(not(sameInstance(firstId))));
		assertThat(thirdId, is(not(sameInstance(secondId))));
	}

	@Test
	public void test_that_near_nodes_stored_together_2() {

		Long firstId, secondId, thirdId, fourthID;

		generatorGuradingSizeAndNeigbors.add(node11, osmServer, optimizedDataSet);
		firstId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeWithinNode11Borders, osmServer, optimizedDataSet);
		secondId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(nodeOutOfNode11Borders, osmServer, optimizedDataSet);
		thirdId = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		generatorGuradingSizeAndNeigbors.add(
												Node.later(	Node.getMovedNode(	(Node) node11, 0.0,
																				0.01),
															59 - 5),
												osmServer, optimizedDataSet);
		fourthID = generatorGuradingSizeAndNeigbors.getChangeSetInUseId();

		assertThat(fourthID, is(notNullValue()));

		assertThat(fourthID, is(sameInstance(firstId)));
		assertThat(fourthID, is(sameInstance(secondId)));
		assertThat(fourthID, is(not(sameInstance(thirdId))));
	}
}
