package org.athmis.wmoptimisation.changeset;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OsmChangeUpdateAbleTest {

	@Test
	public void test_that_empty_osmchnage_has_illegal_id() {
		OsmChangeUpdateAble osmChange = new OsmChangeUpdateAble();

		assertThat(osmChange.getNumber(), is(0));
		assertThat(osmChange.getChangeSetId(), is(-1l));
	}

	@Test
	public void test_that_chnages_with_different_changeset_id_is_not_permitted() {
		OsmChangeUpdateAble osmChange = new OsmChangeUpdateAble();
		boolean exception = false;

		Node berlin1 = Node.getBerlin();
		berlin1.setChangeset(1);
		Node berlin2 = Node.getBerlin();
		berlin2.setChangeset(2);

		osmChange.addChange(berlin1);
		assertThat(osmChange.getNumber(), is(1));
		assertThat(osmChange.getChangeSetId(), is(1l));

		try {
			osmChange.addChange(berlin2);
		}
		catch (IllegalArgumentException e) {
			exception = true;
		}

		assertThat("IllegalArgumentException expected", exception, is(true));
	}
}
