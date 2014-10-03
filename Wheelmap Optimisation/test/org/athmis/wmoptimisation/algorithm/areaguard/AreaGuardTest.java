package org.athmis.wmoptimisation.algorithm.areaguard;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.athmis.wmoptimisation.changeset.Node;
import org.junit.Test;

public class AreaGuardTest {

	@Test(expected = IllegalArgumentException.class)
	public void test_that_constructor_rejects_illegal_avules_1() {
		new AreaGuard(-1.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_that_constructor_rejects_illegal_avules_2() {
		new AreaGuard(0.0);
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
	public void test_that_edge_lon_closes_changeset() {
		AreaGuard guard;
		boolean isToLarge;
		Long changeSetId;

		// max edge 0.0001
		guard = new AreaGuard(0.0001);
		changeSetId = Long.valueOf(1);
		guard.addUpdatedItem(changeSetId, Node.getNode(0.1, 0.1));

		isToLarge = guard.isNextBoxToLarge(changeSetId, Node.getNode(0.1 + 0.00011, 0.1));

		assertThat(isToLarge, is(true));
	}
}
