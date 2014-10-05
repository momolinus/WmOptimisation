package org.athmis.wmoptimisation.algorithm.areaguard;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import javafx.geometry.Rectangle2D;

import org.junit.Test;

public class AreaGuardToolBoxTest {

	@Test
	public void test_that_combintaion_NW_NW_works() {
		Rectangle2D combined;
		Rectangle2D r1 = new Rectangle2D(20, 20, 0, 0);
		Rectangle2D r2 = new Rectangle2D(21, 21, 0, 0);

		combined = AreaGuardToolBox.combine(r1, r2);
	}
}
