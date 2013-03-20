/**
 * created at 17.07.2012
 */
package org.athmis.wmoptimisation.changeset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "osm", strict = false)
public class OsmApiChangeSetsResult {

	@ElementList(inline = true, required = false)
	private List<ChangeSet> changeSets = new ArrayList<ChangeSet>();

	@Attribute(name = "generator")
	private String generator;

	@Attribute(name = "version")
	private double version;

	public List<ChangeSet> getChangeSets() {
		return Collections.unmodifiableList(changeSets);
	}

	public String getGenerator() {
		return generator;
	}

	public int getNumberChangesets() {
		return changeSets.size();
	}

	public double getVersion() {
		return version;
	}

	/**
	 * Returns the changesets with an unique mapping to changesets id. A OSM API
	 * result could contain one changeset more times, so it is useful to uses
	 * this method to become a "filtered" map.
	 *
	 * @return the changesets with an unique mapping to changesets id
	 */
	public Map<Long, ChangeSet> asMap() {
		HashMap<Long, ChangeSet> result = new HashMap<Long, ChangeSet>();

		for (ChangeSet changeset : changeSets) {
			Long id;

			id = Long.valueOf(changeset.getId());

			if (!result.containsKey(id)) {
				result.put(id, changeset);
			}
		}

		return result;
	}
}
