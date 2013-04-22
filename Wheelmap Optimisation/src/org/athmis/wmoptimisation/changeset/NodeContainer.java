package org.athmis.wmoptimisation.changeset;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * A NodeContainer object stores one Node object. It has no representation in
 * OsmAPI, it is necessary for Simple-XML serialization framework.
 * 
 * @author Marcus
 * 
 */
// very important, because common users like me work on relations, but
// whelmap_visitor should not do so
@Root(strict = false)
public class NodeContainer {

	@Attribute(required = false)
	private long id;

	@Element(required = false)
	private Node node;

	@Element(required = false)
	private Way way;

	public long getId() {
		return id;
	}

	public Node getNode() {
		return node;
	}

	public Way getWay() {
		return way;
	}

	public void addChange(Change change) {

		if (change instanceof Way) {
			this.way = (Way) change;
		} else if (change instanceof Node) {
			this.node = (Node) change;
		} else {
			throw new IllegalArgumentException("can't add change '" + change.toString()
					+ "' to this NodeContainer");
		}

		this.id = change.getId();
	}
}
