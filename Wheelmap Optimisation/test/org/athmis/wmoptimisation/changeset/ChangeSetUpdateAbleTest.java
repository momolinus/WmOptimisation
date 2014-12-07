package org.athmis.wmoptimisation.changeset;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;

import org.junit.Test;

public class ChangeSetUpdateAbleTest {

	@Test
	public void testChangeSetUpdateAbleStringLongBoolean() {
		ChangeSetUpdateAble changeSet;
		double area;

		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);

		area = changeSet.getBoundingBoxSquareDegree();

		assertThat(area, is(lessThan((0.0))));
	}

	@Test
	public void test_that_bbox_is_calculated_correct_1() {
		ChangeSetUpdateAble changeSet;
		double area;
		Change berlin1;

		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);
		berlin1 = Node.getBerlin();
		changeSet.updateBoundingBox(berlin1);
		area = changeSet.getBoundingBoxSquareDegree();

		assertThat(area, is(closeTo(0.0, 0.00001)));
	}

	@Test
	public void test_that_bbox_is_calculated_correct_2() {
		ChangeSetUpdateAble changeSet;
		double area;
		Change berlin1;

		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);
		berlin1 = Node.getBerlin();
		changeSet.updateBoundingBox(berlin1);
		changeSet.updateBoundingBox(berlin1);
		area = changeSet.getBoundingBoxSquareDegree();

		assertThat(area, is(closeTo(0.0, 0.0000001)));
	}

	@Test
	public void test_that_bbox_is_calculated_correct_3() {
		ChangeSetUpdateAble changeSet;
		double area;
		Node berlin1, berlin2;

		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);
		berlin1 = Node.getBerlin();
		berlin2 = Node.getMovedNode(berlin1, 0.1, 0.2);
		changeSet.updateBoundingBox(berlin1);
		changeSet.updateBoundingBox(berlin2);
		area = changeSet.getBoundingBoxSquareDegree();

		assertThat(area, is(closeTo(0.02, 0.0000001)));
	}
}
