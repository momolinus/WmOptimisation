package org.athmis.wmoptimisation.algorithm.areaguard;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.Node;
import org.junit.Before;
import org.junit.Test;

public class AreaGuardForSizeAndNeighborTest {

	private static final double MAX_EDGE_SIZE = 0.04;
	private AreaGuardForSizeAndNeighbor guard;

	private Change nodeNW;
	private Change nodeNW2;
	private Change nodeNW3;
	private Change nodeNW4;

	@Before
	public void setUp() {
		guard = new AreaGuardForSizeAndNeighbor(MAX_EDGE_SIZE);

		nodeNW = Node.getNode(1, 1);
		nodeNW2 = Node.getNode(1.04, 1.04);
		nodeNW3 = Node.getNode(1.04, 1.05);
		nodeNW4 = Node.getNode(1.09, 1.09);
	}

	@Test
	public void test_that_first_change_could_always_be_stored() {
		Long olderId;

		olderId = guard.searchOtherChangeSetForChange(2l, nodeNW);
		boolean match = guard.isChangeSetInArea(2l, nodeNW);

		assertThat("error: found older changeset", olderId, is(nullValue()));
		assertThat("error: first change did'nt matched with box", match, is(true));
	}

	@Test
	public void test_that_near_change_could_be_stored() {
		Long olderId, idInuse;

		idInuse = 2l;
		guard.addUpdatedItem(idInuse, nodeNW2);
		olderId = guard.searchOtherChangeSetForChange(idInuse, nodeNW);
		boolean match = guard.isChangeSetInArea(idInuse, nodeNW);

		assertThat("error: found older changeset", olderId, is(nullValue()));
		assertThat("error: box is to large with near change", match, is(true));
	}

	@Test
	public void test_that_faraway_change_could_be_not_stored() {
		Long olderId, idInuse;

		idInuse = 2l;
		guard.addUpdatedItem(idInuse, nodeNW);
		olderId = guard.searchOtherChangeSetForChange(idInuse, nodeNW3);
		boolean match = guard.isChangeSetInArea(idInuse, nodeNW3);

		assertThat("error: found older changeset", olderId, is(nullValue()));
		assertThat("error: fare away change could be stored", match, is(false));
	}

	@Test
	public void test_that_faraway_change_toggles_change_id() {
		Long id2nd, id1st, olderId;

		id1st = 2l;
		guard.addUpdatedItem(id1st, nodeNW);
		olderId = guard.searchOtherChangeSetForChange(id1st, nodeNW4);
		boolean match = guard.isChangeSetInArea(id1st, nodeNW4);

		assertThat("error: found older changeset", olderId, is(nullValue()));
		assertThat("error: fare away change could be stored", match, is(false));

		// 'simulate' OSM-Server
		id2nd = 3l;
		guard.addUpdatedItem(id2nd, nodeNW4);
		olderId = guard.searchOtherChangeSetForChange(id2nd, nodeNW2);
		match = guard.isChangeSetInArea(id2nd, nodeNW2);

		assertThat("error: found older changeset", olderId, is(sameInstance(id1st)));
		assertThat("error: near change could not be stored", match, is(false));
	}

	@Test
	public void test_that_AreaGuardForSize_faraway_change_toggles_not_change_id() {
		Long id2nd, id1st;
		boolean toLarge;

		AreaGuardForSize sizeGuard = new AreaGuardForSize(MAX_EDGE_SIZE);

		id1st = 2l;
		sizeGuard.addUpdatedItem(id1st, nodeNW);
		toLarge = sizeGuard.isNextBoxToLarge(id1st, nodeNW4);

		assertThat(toLarge, is(true));

		// 'simulate' OSM-Server
		id2nd = 3l;
		sizeGuard.addUpdatedItem(id2nd, nodeNW4);

		toLarge = sizeGuard.isNextBoxToLarge(id2nd, nodeNW2);

		assertThat(toLarge, is(true));
	}
}
