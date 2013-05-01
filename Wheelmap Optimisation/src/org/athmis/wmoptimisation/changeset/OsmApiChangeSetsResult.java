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
