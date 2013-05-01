/*
Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012

This file is part of Wheelmap Optimization.

Wheelmap Optimization is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Wheelmap Optimization is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Athmis. If not, see <http://www.gnu.org/licenses/>.

Diese Datei ist Teil von Wheelmap Optimization.

Wheelmap Optimization ist Freie Software: Sie können es unter den Bedingungen
der GNU General Public License, wie von der Free Software Foundation,
Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
veröffentlichten Version, weiterverbreiten und/oder modifizieren.

Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird, aber
OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
Siehe die GNU General Public License für weitere Details.

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
		cSet2009.createdAt = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(new GregorianCalendar(2009, 0, 1)
				.getTime());
		cSet2009.id = 0;

		cSet2010 = new ChangeSet();
		cSet2010.createdAt = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(new GregorianCalendar(2010, 0, 1)
				.getTime());
		cSet2010.id = 1;

		cSet2011 = new ChangeSet();
		cSet2011.createdAt = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(new GregorianCalendar(2011, 0, 1)
				.getTime());
		cSet2011.id = 2;

		cSet2012 = new ChangeSet();
		cSet2012.createdAt = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(new GregorianCalendar(2012, 0, 1)
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
