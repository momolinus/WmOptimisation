package org.athmis.wmoptimisation.fetch_changesets;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.athmis.wmoptimisation.changeset.*;
import org.junit.*;

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

	private Node berlin, spandau, steglitz;

	@Before
	public void setUp() {
		berlin = Node.getBerlinAsNode();
		spandau = Node.getDifferentNode(berlin, 0.1, 0.1);
		steglitz = Node.getDifferentNode(berlin, -0.1, -0.1);
	}

	@Test
	public void test_that_correct_number_of_changes_returned_0() {
		Table<Long, String, String> table;
		OsmChangeContent content = new OsmChangeContent();

		table = content.getChangeSets("test");
		Map<String, String> header = table.row(1l);

		assertThat(table.size(), is(0));
		assertThat(header.get("area"), is(nullValue()));
		assertThat(header.get("no_changes"), is(nullValue()));
		assertThat(header.get("user"), is(nullValue()));
		assertThat(header.get("algorithm"), is(nullValue()));
	}

	@Test
	public void test_that_correct_number_of_changes_returned_1() {
		Table<Long, String, String> table;
		ChangeSetUpdateAble changeSet;
		OsmChangeContent content;

		content = new OsmChangeContent();
		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);
		changeSet.setUser("test-case_1");
		content.add(changeSet);

		table = content.getChangeSets("test");
		Map<String, String> header = table.row(1l);

		assertThat(table.size(), is(4));
		assertThat(header.get("area"), is(equalTo("-1.0")));
		assertThat(header.get("no_changes"), is(equalTo("0.0")));
		assertThat(header.get("user"), is(equalTo("test-case_1")));
		assertThat(header.get("algorithm"), is(equalTo("test")));
	}

	@Test
	public void test_that_correct_number_of_changes_returned_1_1() {
		Table<Long, String, String> table;
		ChangeSetUpdateAble changeSet;
		OsmChangeContent content;
		Change c1, c2;

		content = new OsmChangeContent();
		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);
		changeSet.setUser("test-case_1");

		c1 = Node.getBerlinAsNode();
		c2 = Node.getDifferentNode(Node.getBerlinAsNode(), 1, 0.1, 0.1);
		content.addChangeForChangeSet(c1, changeSet);
		content.addChangeForChangeSet(c2, changeSet);
		content.add(changeSet);

		table = content.getChangeSets("test");
		Map<String, String> header = table.row(1l);

		assertThat(table.size(), is(4));
		assertThat(header.get("area"), startsWith("0.010000"));
		assertThat(header.get("no_changes"), is(equalTo("2.0")));
		assertThat(header.get("user"), is(equalTo("test-case_1")));
		assertThat(header.get("algorithm"), is(equalTo("test")));
	}

	@Test
	public void test_that_correct_number_of_changes_returned_1_2() {
		Table<Long, String, String> table;
		ChangeSetUpdateAble changeSet;
		OsmChangeContent content;
		Change berlin;

		content = new OsmChangeContent();
		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);
		changeSet.setUser("test-case_1");
		berlin = Node.getBerlinAsNode();
		content.addChangeForChangeSet(berlin, changeSet);
		content.add(changeSet);

		table = content.getChangeSets("test");
		Map<String, String> header = table.row(1l);

		assertThat(table.size(), is(4));
		assertThat(header.get("area"), is(equalTo("0.0")));
		assertThat(header.get("no_changes"), is(equalTo("1.0")));
		assertThat(header.get("user"), is(equalTo("test-case_1")));
		assertThat(header.get("algorithm"), is(equalTo("test")));
	}

	@Test
	public void test_that_correct_number_of_changes_returned_1_3() {
		Table<Long, String, String> table;
		ChangeSetUpdateAble changeSet, changeSet2;
		OsmChangeContent content;
		Change berlin;

		content = new OsmChangeContent();

		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);
		changeSet2 =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 3)), 5,
					true);
		changeSet.setUser("test-case_1");
		changeSet2.setUser("test-case_1");

		berlin = Node.getBerlinAsNode();
		content.addChangeForChangeSet(berlin, changeSet);
		content.add(changeSet);
		content.add(changeSet2);

		table = content.getChangeSets("test");
		Map<String, String> row1 = table.row(1l);
		Map<String, String> row5 = table.row(5l);

		assertThat(table.size(), is(8));
		assertThat(row1.get("area"), is(equalTo("0.0")));
		assertThat(row1.get("no_changes"), is(equalTo("1.0")));
		assertThat(row1.get("user"), is(equalTo("test-case_1")));
		assertThat(row1.get("algorithm"), is(equalTo("test")));
		assertThat(row5.get("area"), is(equalTo("-1.0")));
		assertThat(row5.get("no_changes"), is(equalTo("0.0")));
		assertThat(row1.get("user"), is(equalTo("test-case_1")));
		assertThat(row1.get("algorithm"), is(equalTo("test")));
	}

	@Test
	public void test_that_correct_number_of_changes_returned_2() {
		Table<Long, String, String> table;
		OsmChangeContent content = new OsmChangeContent();
		ChangeSetUpdateAble changeSet;
		Change berlin;

		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);
		berlin = Node.getBerlinAsNode();
		content.addChangeForChangeSet(berlin, changeSet);

		table = content.getChangeSets("test");
		Map<String, String> header = table.row(1l);
		String[] columnNames = header.keySet().toArray(new String[4]);

		assertThat(table.size(), is(4));
		assertThat(header.size(), is(4));
		// TODO ist die Ordnung festgelegt?
		// note: indices determined by inspection, actual the order is not knwon by me
		assertThat(columnNames[0], is(equalTo("area")));
		assertThat(columnNames[1], is(equalTo("no_changes")));
		assertThat(columnNames[2], is(equalTo("user")));
		assertThat(columnNames[3], is(equalTo("algorithm")));

		assertThat(header.get("area"), is(equalTo("0.0")));
		assertThat(header.get("no_changes"), is(equalTo("1.0")));
		assertThat(header.get("user"), is(equalTo("no_user")));
		assertThat(header.get("algorithm"), is(equalTo("test")));
	}

	@Test
	public void test_that_correct_number_of_changes_returned_3() {
		Table<Long, String, String> table;
		OsmChangeContent content = new OsmChangeContent();
		ChangeSetUpdateAble changeSet;

		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1,
					true);
		content.addChangeForChangeSet(Node.getBerlinAsNode(), changeSet);
		content.addChangeForChangeSet(	Node.getDifferentNode(Node.getBerlinAsNode(), 1, 0.1, 0.1),
										changeSet);

		table = content.getChangeSets("test");
		Map<String, String> header = table.row(1l);
		String[] columnNames = header.keySet().toArray(new String[4]);

		assertThat(table.size(), is(4));
		assertThat(header.size(), is(4));
		// TODO ist die Ordnung festgelegt?
		// note: indices determined by inspection, actual the order is not knwon by me
		assertThat(columnNames[0], is(equalTo("area")));
		assertThat(columnNames[1], is(equalTo("no_changes")));
		assertThat(columnNames[2], is(equalTo("user")));
		assertThat(columnNames[3], is(equalTo("algorithm")));

		// note: use startsWith(..) because of rounding errors, exact answer should be 0.01, wich is
		// 0.1 * 0.1
		assertThat(header.get("area"), startsWith("0.0100000"));
		assertThat(header.get("no_changes"), is(equalTo("2.0")));
		assertThat(header.get("user"), is(equalTo("no_user")));
		assertThat(header.get("algorithm"), is(equalTo("test")));
	}

	@Test
	public void test_add_one_change_2_times() throws Exception {
		ChangeSetUpdateAble changeSet, changeSet2;
		OsmChangeContent content;
		long berlinChangeSetId;

		content = new OsmChangeContent();
		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1l,
					true);
		changeSet2 =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 2l,
					true);
		berlinChangeSetId = berlin.getChangeset();

		content.addChangeForChangeSet(berlin, changeSet);
		assertThat(berlin.getChangeset(), is(berlinChangeSetId));

		content.addChangeForChangeSet(berlin, changeSet2);
		assertThat(berlin.getChangeset(), is(berlinChangeSetId));
	}

	@Test
	public void test_add_two_changesets() {
		ChangeSetUpdateAble changeSet, changeSet2;
		OsmChangeContent content;

		content = new OsmChangeContent();
		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1l,
					true);
		changeSet2 =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 3)), 2l,
					true);

		content.addChangeForChangeSet(berlin, changeSet);
		content.addChangeForChangeSet(berlin, changeSet);
		content.addChangeForChangeSet(spandau, changeSet);
		content.addChangeForChangeSet(	Node.getDifferentNode(	berlin,
																(int) TimeUnit.DAYS.toMinutes(2),
																0, 0), changeSet2);
		content.addChangeForChangeSet(	Node.getDifferentNode(	spandau,
																(int) TimeUnit.DAYS.toMinutes(2),
																0, 0), changeSet2);
		content.addChangeForChangeSet(	Node.getDifferentNode(	steglitz,
																(int) TimeUnit.DAYS.toMinutes(2),
																0, 0), changeSet2);
		List<Change> changes = content.getAllChanges();
		List<Double> bboxes = content.getBoundingBoxesSquareDegree();

		assertThat(changes, hasSize(6));
		assertThat(bboxes, hasSize(2));
		assertThat(bboxes.get(0).doubleValue(), is(closeTo(0.01, 0.00001)));
		assertThat(bboxes.get(1).doubleValue(), is(closeTo(0.02, 0.00001)));
	}

	@Test
	public void test_add_one_changesets() {
		ChangeSetUpdateAble changeSet;
		OsmChangeContent content;

		content = new OsmChangeContent();
		changeSet =
			new ChangeSetUpdateAble(ChangeSetToolkit.localDateToOsm(LocalDate.of(2010, 1, 1)), 1l,
					true);
		content.addChangeForChangeSet(berlin, changeSet);
		content.addChangeForChangeSet(berlin, changeSet);
		content.addChangeForChangeSet(spandau, changeSet);
		content.closeAllChangeSets();
		List<Change> changes = content.getAllChanges();
		List<Double> bboxes = content.getBoundingBoxesSquareDegree();

		// assertThat(content.getNoChangeSets(), is(1));
		assertThat(changes, hasSize(3));
		assertThat(bboxes, hasSize(1));
		assertThat(changeSet.getBoundingBoxSquareDegree(), is(closeTo(0.01, 0.00001)));
		assertThat(bboxes.get(0).doubleValue(), is(closeTo(0.01, 0.00001)));
	}

	@Test
	public void testGetAllChanges() {
		List<Change> changes = changeContent.getAllChanges();

		assertThat(changes, hasSize(223 + 180));
	}

	@Test
	public void testGetAllWays() {
		List<Way> ways = changeContent.getAllWays();

		assertThat(ways, hasSize(66 + 39));
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
	public void testSize() {
		int size = changeContent.size();

		assertThat(size, is(4));
	}
}
