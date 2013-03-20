package org.athmis.wmoptimisation.changeset;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.fetch_changesets.FetchChangeSets;
import org.junit.Before;
import org.junit.Test;

public class FetchChangeSetsTest {

	private Map<Long, ChangeSet> changeSets;
	private ChangeSet cSet2009;
	private ChangeSet cSet2010;
	private ChangeSet cSet2011;
	private ChangeSet cSet2012;

	@Before
	public void setUp() throws Exception {
		changeSets = new HashMap<>();

		cSet2009 = new ChangeSet();
		cSet2009.createdAt = ChangeSet.OSM_DATE_TO_JAVA.format(new GregorianCalendar(2009, 0, 1)
				.getTime());
		cSet2009.id = 0;

		cSet2010 = new ChangeSet();
		cSet2010.createdAt = ChangeSet.OSM_DATE_TO_JAVA.format(new GregorianCalendar(2010, 0, 1)
				.getTime());
		cSet2010.id = 1;

		cSet2011 = new ChangeSet();
		cSet2011.createdAt = ChangeSet.OSM_DATE_TO_JAVA.format(new GregorianCalendar(2011, 0, 1)
				.getTime());
		cSet2011.id = 2;

		cSet2012 = new ChangeSet();
		cSet2012.createdAt = ChangeSet.OSM_DATE_TO_JAVA.format(new GregorianCalendar(2012, 0, 1)
				.getTime());
		cSet2012.id = 3;

		changeSets.put(cSet2011.getId(), cSet2011);
		changeSets.put(cSet2009.getId(), cSet2009);
		changeSets.put(cSet2010.getId(), cSet2010);
		changeSets.put(cSet2012.getId(), cSet2012);
	}

	@Test
	public void testFindOldestChangeset() throws ParseException {
		GregorianCalendar oldest;

		assertEquals(4, changeSets.size());
		oldest = FetchChangeSets.findOldestChangeset(changeSets);
		assertEquals(2009, oldest.get(Calendar.YEAR));
	}
}
