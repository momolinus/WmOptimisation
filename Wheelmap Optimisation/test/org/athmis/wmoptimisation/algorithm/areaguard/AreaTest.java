package org.athmis.wmoptimisation.algorithm.areaguard;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.athmis.wmoptimisation.changeset.Change;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AreaTest {

	private static final double LAT_LON_STD_MAX_ERROR = 0.00000001;
	@Mock
	private Change node;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() {
		Area nwArea;

		// somewhere in Germany
		when(node.getLat()).thenReturn(52.0);
		when(node.getLon()).thenReturn(13.2);

		nwArea = new Area(node);

		assertThat(nwArea.getLatMaxN(), is(closeTo(52.0, LAT_LON_STD_MAX_ERROR)));

	}

	@Test
	public void test_that_NW_Area_created_correct() {
		Area nwArea;

		nwArea = new Area(10, 10, 9, 11);

		// assertThat(nwArea.getHeight(), matcher);
	}
}
