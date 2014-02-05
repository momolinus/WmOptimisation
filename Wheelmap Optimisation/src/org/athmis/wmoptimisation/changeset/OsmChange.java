/* Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012 This file is part
 * of Wheelmap Optimization. Wheelmap Optimization is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Wheelmap Optimization is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with
 * Athmis. If not, see <http://www.gnu.org/licenses/>. Diese Datei ist Teil von
 * Wheelmap Optimization. Wheelmap Optimization ist Freie Software: Sie können
 * es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder
 * späteren veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 * Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details. Sie sollten eine
 * Kopie der GNU General Public License zusammen mit diesem Programm erhalten
 * haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>. */
package org.athmis.wmoptimisation.changeset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * An OsmChange object acts for the root element of OsmAPI response on changed
 * nodes and ways. It contains two {@linkplain NodeContainer} lists: one for
 * changed OSM-objects and one for new created OSM-objects.
 */
@Root(name = "osmChange", strict = false)
public class OsmChange {

	/**
	 * <strong>important note:</strong> this id is <strong>not</strong> returned
	 * by OSM API, planned for using in database, but development of database
	 * version is canceled
	 */
	@Attribute(required = false)
	private long id;
	@Attribute
	private double version;
	@Attribute
	private String generator;

	// "required=false" is important for empty changes or missing creations
	@ElementList(entry = "create", inline = true, required = false)
	private List<NodeContainer> created = new ArrayList<NodeContainer>();
	// "required=false" is important for empty changes or missing modifications
	@ElementList(entry = "modify", inline = true, required = false)
	private List<NodeContainer> modified = new ArrayList<NodeContainer>();

	public String getGenerator() {
		return generator;
	}

	/**
	 * Returns an id useful for storing in databases. It is not returned by OSM
	 * API, if using ist must be set.
	 * 
	 * @deprecated not in use anymore, was planed for using in database, but
	 *             developing of database version is canceled
	 * @return the id
	 */
	@Deprecated
	public long getId() {
		return id;
	}

	public List<NodeContainer> getNodesCreate() {
		return Collections.unmodifiableList(created);
	}

	public List<NodeContainer> getNodesModify() {
		return Collections.unmodifiableList(modified);
	}

	public int getNumberCreated() {
		return created.size();
	}

	public int getNumberModified() {
		return modified.size();
	}

	public double getVersion() {
		return version;
	}

	public Collection<Change> getChanges() {
		Collection<Change> allChanges;

		allChanges = new ArrayList<>();

		for (NodeContainer container : created) {
			if (container.getNode() != null)
				allChanges.add(container.getNode());

			if (container.getWay() != null)
				allChanges.add(container.getWay());
		}

		for (NodeContainer container : modified) {
			if (container.getNode() != null)
				allChanges.add(container.getNode());

			if (container.getWay() != null)
				allChanges.add(container.getWay());
		}

		return allChanges;
	}

	// TODO Prüfen ob die Referenzen o.k. sind
	/**
	 * Returns the nodes of this OsmChange object. Map could be empty. Key value
	 * is the id of the changeset.
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

	public void addChange(Change change) {
		NodeContainer container;

		container = new NodeContainer();
		container.addChange(change);

		// FIXME muss noch richtig gemacht werden
		modified.add(container);
	}
}
