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
}
