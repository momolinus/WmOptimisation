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
		Long id;

		id = guard.getValidChangesetId(2l, nodeNW);

		assertThat("box is to large for first change", id, is(equalTo(2l)));
	}

	@Test
	public void test_that_near_change_could_be_stored() {
		Long id, idInuse;

		idInuse = 2l;
		guard.addUpdatedItem(idInuse, nodeNW2);
		id = guard.getValidChangesetId(idInuse, nodeNW);

		assertThat("box is to large for first change", id, is(sameInstance(idInuse)));
	}

	@Test
	public void test_that_faraway_change_could_be_not_stored() {
		Long id, idInuse;

		idInuse = 2l;
		guard.addUpdatedItem(idInuse, nodeNW);
		id = guard.getValidChangesetId(idInuse, nodeNW3);

		assertThat("box is to large for first change", id, is(not(sameInstance(idInuse))));
	}

	@Test
	public void test_that_faraway_change_toggles_change_id() {
		Long id2nd, id1st, id3ed;

		id1st = 2l;
		guard.addUpdatedItem(id1st, nodeNW);
		id2nd = guard.getValidChangesetId(id1st, nodeNW4);

		assertThat(id2nd, is(nullValue()));
		assertThat(id2nd, is(not(sameInstance(id1st))));

		// 'simulate' OSM-Server
		id2nd = 3l;
		guard.addUpdatedItem(id2nd, nodeNW4);

		id3ed = guard.getValidChangesetId(id2nd, nodeNW2);

		assertThat(id3ed, is(notNullValue()));
		assertThat(id1st, is(sameInstance(id3ed)));
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
