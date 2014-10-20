package org.athmis.wmoptimisation.algorithm.areaguard;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class AreaGuardToolBoxTest {

	@Test
	public void test_that_area_contains_2nd_area_combined_correct() {
		Area area1, area2_inarea1, combination;

		area1 = new AreaBuilder().withNorth(50).withSouth(20).withEast(51).withWest(19).create();
		area2_inarea1 =
			new AreaBuilder().withNorth(49).withSouth(21).withEast(50).withWest(20).create();

		combination = AreaGuardToolBox.combine(area1, area2_inarea1);

		assertThat(combination.getLatMaxN(), is(closeTo(50, AreaTest.LAT_LON_STD_MAX_ERROR)));
		assertThat(combination.getLonMaxE(), is(closeTo(51, AreaTest.LAT_LON_STD_MAX_ERROR)));
		assertThat(combination.getLatMinS(), is(closeTo(20, AreaTest.LAT_LON_STD_MAX_ERROR)));
		assertThat(combination.getLonMinW(), is(closeTo(19, AreaTest.LAT_LON_STD_MAX_ERROR)));
	}

	@Test
	public void test_that_area_cuts_2nd_area_combined_correct() {
		Area area1, area2_inarea1, combination;

		area1 = new AreaBuilder().withNorth(10).withSouth(-10).withEast(20).withWest(-15).create();
		area2_inarea1 =
			new AreaBuilder().withNorth(-8).withSouth(-20).withEast(25).withWest(-5).create();

		combination = AreaGuardToolBox.combine(area1, area2_inarea1);

		assertThat(combination.getLatMaxN(), is(closeTo(10, AreaTest.LAT_LON_STD_MAX_ERROR)));
		assertThat(combination.getLonMaxE(), is(closeTo(25, AreaTest.LAT_LON_STD_MAX_ERROR)));
		assertThat(combination.getLatMinS(), is(closeTo(-20, AreaTest.LAT_LON_STD_MAX_ERROR)));
		assertThat(combination.getLonMinW(), is(closeTo(-15, AreaTest.LAT_LON_STD_MAX_ERROR)));
	}

	@Test
	public void test_that_area_is_exclusive_2nd_area_combined_correct() {
		Area area1, area2_inarea1, combination;

		area1 = new AreaBuilder().withNorth(30).withSouth(5).withEast(80).withWest(39).create();
		area2_inarea1 =
			new AreaBuilder().withNorth(-8).withSouth(-20).withEast(25).withWest(-5).create();

		combination = AreaGuardToolBox.combine(area1, area2_inarea1);

		assertThat(combination.getLatMaxN(), is(closeTo(30, AreaTest.LAT_LON_STD_MAX_ERROR)));
		assertThat(combination.getLonMaxE(), is(closeTo(80, AreaTest.LAT_LON_STD_MAX_ERROR)));
		assertThat(combination.getLatMinS(), is(closeTo(-20, AreaTest.LAT_LON_STD_MAX_ERROR)));
		assertThat(combination.getLonMinW(), is(closeTo(-5, AreaTest.LAT_LON_STD_MAX_ERROR)));
	}

	@Test
	public void testGetBoxForAreas() {

	}

	@Test
	public void testGetMaxEdge() {

	}
}
