/* Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012 This file is part of Wheelmap
 * Optimization. Wheelmap Optimization is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. Wheelmap Optimization is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with Athmis. If not, see <http://www.gnu.org/licenses/>. Diese Datei ist Teil von
 * Wheelmap Optimization. Wheelmap Optimization ist Freie Software: Sie können es unter den
 * Bedingungen der GNU General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version, weiterverbreiten
 * und/oder modifizieren. Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird,
 * aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public License für
 * weitere Details. Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>. */
package org.athmis.wmoptimisation.changeset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.*;

/**
 * An OsmChange object acts for the root element of OsmAPI response on changed nodes and ways. It
 * contains two {@linkplain NodeContainer} lists: one for changed OSM-objects and one for new
 * created OSM-objects. The OSM-objects (modes or ways) contains an id for their changeset.
 */
@Root(name = "osmChange", strict = false)
public class OsmChange {

	// "required=false" is important for empty changes or missing creations
	@ElementList(entry = "create", inline = true, required = false)
	private List<NodeContainer> created = new ArrayList<NodeContainer>();
	@Attribute
	private String generator;
	/**
	 * <strong>important note:</strong> this id is <strong>not</strong> returned by OSM API, planned
	 * for using in database, but development of database version is canceled
	 */
	@Attribute(required = false)
	private long id;

	// "required=false" is important for empty changes or missing modifications
	@ElementList(entry = "modify", inline = true, required = false)
	private List<NodeContainer> modified = new ArrayList<NodeContainer>();
	@Attribute
	private double version;

	public OsmChange() {}

	// TODO inspect next sprint: kommentieren und prüfen, added nur zur modified liste, da die
	// Wheelmap die ersten Jahre nur modifizieren konnte, die Methode wird nur so gebraucht (vgl.
	// Suche) -> also kommentieren
	public void addChange(Change change) {
		NodeContainer container;

		container = new NodeContainer();
		container.addChange(change);

		modified.add(container);
	}

	public Collection<Change> getChanges() {
		Collection<Change> allChanges;

		allChanges = new ArrayList<>();

		for (NodeContainer container : created) {
			if (container.getNode() != null) {
				allChanges.add(container.getNode());
			}

			if (container.getWay() != null) {
				allChanges.add(container.getWay());
			}
		}

		for (NodeContainer container : modified) {
			if (container.getNode() != null) {
				allChanges.add(container.getNode());
			}

			if (container.getWay() != null) {
				allChanges.add(container.getWay());
			}
		}

		return allChanges;
	}

	public String getGenerator() {
		return generator;
	}

	/**
	 * Returns an id useful for storing in databases. It is not returned by OSM API, if using ist
	 * must be set.
	 *
	 * @deprecated not in use anymore, was planed for using in database, but developing of database
	 *             version is canceled
	 * @return the id
	 */
	@Deprecated
	public long getId() {
		return id;
	}

	// TODO wichtig aber später: Prüfen ob die Referenzen o.k. sind
	/**
	 * Returns the nodes of this OsmChange object. Map could be empty. Key value is the id of the
	 * changeset.
	 *
	 * @return
	 */
	public Map<Long, Node> getNodes() {
		Map<Long, Node> nodes;
		nodes = new HashMap<>();

		for (NodeContainer container : created) {
			if (container.getNode() != null) {
				nodes.put(container.getNode().getChangeset(), container.getNode());
			}
		}

		for (NodeContainer container : modified) {
			if (container.getNode() != null) {
				nodes.put(container.getNode().getChangeset(), container.getNode());
			}
		}

		return nodes;
	}

	public long getChangeSetId() {
		long changeSetId = -1;

		if (modified.size() > 0) {
			if (modified.get(0).getNode() != null) {
				changeSetId = modified.get(0).getNode().getChangeset();
			}

			if (modified.get(0).getWay() != null) {
				changeSetId = modified.get(0).getWay().getChangeset();
			}
		}

		if (created.size() > 0) {
			if (created.get(0).getNode() != null) {
				changeSetId = created.get(0).getNode().getChangeset();
			}

			if (created.get(0).getWay() != null) {
				changeSetId = created.get(0).getWay().getChangeset();
			}
		}

		return changeSetId;
	}

	public List<NodeContainer> getNodesCreate() {
		return Collections.unmodifiableList(created);
	}

	public List<NodeContainer> getNodesModify() {
		return Collections.unmodifiableList(modified);
	}

	/**
	 * Returns the number of created and modified changes.
	 *
	 * @return the number of created and modified changes
	 */
	public int getNumber() {
		return created.size() + modified.size();
	}

	public int getNumberCreated() {
		return created.size();
	}

	public int getNumberModified() {
		return modified.size();
	}

	/**
	 * Returns the number of {@linkplain Node}s stored in this OsmChange object.
	 * <p>
	 * Important note: this number mostly differs to size of the map returned by {@link #getNodes()}
	 * , because {@link #getNodes()} returns a map with an unique key which stores the nodes
	 * {@link Node#getId() Id}. But one unique id could be store more times in an OsmChnage object.
	 *
	 * @return number of {@linkplain Node} stored in this OsmChange object
	 */
	public int getNumberNodes() {
		int numberNodes = 0;
		for (NodeContainer container : created) {
			if (container.getNode() != null) {
				numberNodes++;
			}
		}

		for (NodeContainer container : modified) {
			if (container.getNode() != null) {
				numberNodes++;
			}
		}
		return numberNodes;
	}

	public double getVersion() {
		return version;
	}

	/**
	 * Returns a copy of all ways this object contains.
	 *
	 * @return copy of all ways this object contains, could be empty but not <code>null</code>
	 */
	public List<Way> getWays() {
		List<Way> ways;

		ways = new ArrayList<>();

		for (NodeContainer container : created) {
			if (container.getWay() != null) {
				ways.add(new Way(container.getWay()));
			}
		}

		for (NodeContainer container : modified) {
			if (container.getWay() != null) {
				ways.add(new Way(container.getWay()));
			}
		}

		return ways;
	}
}
