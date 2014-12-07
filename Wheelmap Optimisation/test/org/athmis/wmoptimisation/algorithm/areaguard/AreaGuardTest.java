package org.athmis.wmoptimisation.algorithm.areaguard;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.athmis.wmoptimisation.changeset.Node;
import org.junit.BeforeClass;
import org.junit.Test;

public class AreaGuardTest {

	private static Node node_id1_at_01_01;
	private static Node node_id2_at_010011_01;

	private static Node node_id3_at_1_1;
	private static Node node_id4_at_1_1p1;
	private static Node node_id5_at_1p1_1;
	private static Node node_id6_at_1p1_1p1;

	private static Node node_id7_at_1_1p2;
	private static Node node_id8_at_1p2_1;

	@BeforeClass
	public static void setUpSomeTestData() {
		node_id1_at_01_01 = Node.getNode(0.1, 0.1);
		node_id2_at_010011_01 = Node.getNode(0.1 + 0.00011, 0.1);
		node_id2_at_010011_01.setId(2);

		node_id3_at_1_1 = Node.getNode(1, 1);
		node_id3_at_1_1.setId(3);

		node_id4_at_1_1p1 = Node.getNode(1, 1.1);
		node_id4_at_1_1p1.setId(4);

		node_id5_at_1p1_1 = Node.getNode(1.1, 1);
		node_id5_at_1p1_1.setId(5);

		node_id6_at_1p1_1p1 = Node.getNode(1.1, 1.1);
		node_id6_at_1p1_1p1.setId(6);

		node_id7_at_1_1p2 = Node.getNode(1, 1.2);
		node_id7_at_1_1p2.setId(7);

		node_id8_at_1p2_1 = Node.getNode(1.2, 1);
		node_id8_at_1p2_1.setId(8);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_that_constructor_rejects_illegal_avules_1() {
		new AreaGuard(-1.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_that_constructor_rejects_illegal_avules_2() {
		new AreaGuard(0.0);
	}

	@Test
	public void test_that_edge_lon_closes_changeset() {
		AreaGuard guard;
		boolean isToLarge;
		Long changeSetId;

		// max edge 0.0001
		guard = new AreaGuard(0.0001);
		changeSetId = Long.valueOf(1);
		guard.addUpdatedItem(changeSetId, node_id1_at_01_01);

		isToLarge = guard.isNextBoxToLarge(changeSetId, node_id2_at_010011_01);

		assertThat(isToLarge, is(true));
	}

	@Test
	public void test_that_first_id_is_not_to_large() {
		AreaGuard guard;
		boolean isToLarge;

		guard = new AreaGuard(0.0001);

		isToLarge = guard.isNextBoxToLarge(1l, Node.getBerlin());

		assertThat(isToLarge, is(false));
	}

	@Test
	public void test_that_guard_respects_its_border() {
		AreaGuard guard;
		boolean isToLarge;
		Long changeSetId;

		changeSetId = Long.valueOf(1);
		guard = new AreaGuard(0.1);
		guard.addUpdatedItem(changeSetId, node_id3_at_1_1);

		isToLarge = guard.isNextBoxToLarge(changeSetId, node_id4_at_1_1p1);

		assertThat(isToLarge, is(false));
	}
}
