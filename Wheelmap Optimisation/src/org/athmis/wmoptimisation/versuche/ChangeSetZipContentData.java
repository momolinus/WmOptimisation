package org.athmis.wmoptimisation.versuche;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.changeset.OsmChange;

public class ChangeSetZipContentData {
	private List<OsmChange> changes;
	private Map<Long, ChangeSet> changeSets;

	public ChangeSetZipContentData() {
		changeSets = new HashMap<>();
		changes = new ArrayList<>();
	}

	public ChangeSet add(ChangeSet changeSet) {
		return changeSets.put(Long.valueOf(changeSet.getId()), changeSet);
	}

	// XXX wenn OsmChange mal equals() unterstützt kann geprüft werden, ob das
	// object schon gespeichert war
	public void add(OsmChange changeContent) {
		changes.add(changeContent);
	}

	public int size() {
		return changes.size() + changeSets.size();
	}

	public String getAreasAsCSV(String header) {
		StringBuilder result;
		result = new StringBuilder();

		result.append(header);
		result.append("\n");

		Iterator<ChangeSet> chs = changeSets.values().iterator();
		while (chs.hasNext()) {
			result.append(String.format("%.12f", chs.next().getArea()));
			if (chs.hasNext())
				result.append("\n");
		}

		return result.toString();
	}

	/**
	 * 
	 * @return
	 * @throws ParseException
	 *             in case of syntax error in date or time string of OSM raw
	 *             data
	 */
	public String asTable() throws ParseException {
		StringBuilder table;
		table = new StringBuilder();

		table.append("id;user;closed;opentime;area");
		table.append("\n");

		Iterator<ChangeSet> chs = changeSets.values().iterator();
		while (chs.hasNext()) {
			ChangeSet chSet = chs.next();

			table.append(chSet.getId());
			table.append(";");
			table.append(chSet.getUser());
			table.append(";");
			table.append(String.format("%tF", chSet.getClosed()));
			table.append(";");
			table.append(String.format("%.12f", chSet.getOpenTimeInHours()));
			table.append(";");
			table.append(String.format("%.12f", chSet.getArea()));

			if (chs.hasNext())
				table.append("\n");
		}

		return table.toString();
	}
}