package org.athmis.wmoptimisation.fetch_changesets;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;

import org.athmis.wmoptimisation.changeset.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Table;

public class OsmChangeContentTest {

	private static OsmChangeContent changeContent;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		//@formatter:off

		/*** note ***

		10671963 has 223 modified and 0 created,
		157 Nodes, 66 Ways

		12214085 has 180 modified and 0 created,
		141 Nodes, 39 Ways

		*/

		//@formatter:on

		changeContent =
			OsmChangeContent.readOsmChangeContent(new String[] { "ressources/examples/10671963",
				"ressources/examples/12214085" });
	}

	@Before
	public void setUp() throws Exception {}

	@Test
	public void testReadOsmChangeContent() {
		boolean failed = false;

		try {
			OsmChangeContent.readOsmChangeContent(new String[] { "xyz", "xyz2" });
			fail("exception expected");
		}
		catch (Exception e) {
			failed = true;
		}

		assertThat(failed, is(true));
	}

	@Test
	public void testGetAllChanges() {
		List<Change> changes = changeContent.getAllChanges();

		assertThat(changes, hasSize(223 + 180));
	}

	// XXX Ergebnisse sind ermittelt -> nochmal prüfen
	@Test
	public void testGetBoundingBoxesSquareDegree() {
		List<Double> bb = changeContent.getBoundingBoxesSquareDegree();

		assertThat(bb, hasSize(2));
		assertThat(bb.get(0), is(closeTo(20664.8, 0.1)));
		assertThat(bb.get(1), is(closeTo(4004.1, 0.1)));
	}

	// XXX Ergebnisse sind ermittelt -> nochmal prüfen
	@Test
	public void testGetMeanAreaOfChangeSetsForNodes() {
		double meanArea = changeContent.getMeanArea();

		assertThat(meanArea, is(closeTo(12334.5, 0.1)));
	}

	@Test
	public void testGetNoChangeSets() {
		int noChangeSets = changeContent.getNoChangeSets();

		assertThat(noChangeSets, is(2));
	}

	@Test
	public void testGetNodes() {
		int noNodes = changeContent.getNodes();

		assertThat(noNodes, is(157 + 141));
	}

	@Test
	public void testSize() {
		int size = changeContent.size();

		assertThat(size, is(4));
	}

	@Test
	public void testGetAllWays() {
		List<Way> ways = changeContent.getAllWays();

		assertThat(ways, hasSize(66 + 39));
	}

	@Test
	public void test_that_correct_number_of_changes_returned() {
		Table<Long, String, String> table;
		OsmChangeContent content = new OsmChangeContent();

		table = content.getChangeSets("test");

		assertThat(table.size(), is(0));
	}

	@Test
	public void test_that_correct_number_of_changes_returned2() {
		Table<Long, String, String> table;
		OsmChangeContent content = new OsmChangeContent();
		ChangeSetUpdateAble changeSet;

		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);

		content.addChangeForChangeSet(Node.getBerlinAsNode(), changeSet);

		table = content.getChangeSets("test");

		assertThat(table.size(), is(0));
	}
}
