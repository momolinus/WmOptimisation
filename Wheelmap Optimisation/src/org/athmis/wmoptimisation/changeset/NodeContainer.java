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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * A NodeContainer object stores one {@linkplain Node} and one {@linkplain Way} object. It has no
 * representation in OsmAPI, it is necessary for Simple-XML serialization framework.
 * <p>
 * note: strict = false is very important, because common users work on relations, but
 * wheelmap_visitor should not do so
 */
@Root(strict = false)
public class NodeContainer {

	@Attribute(required = false)
	private long id;

	@Element(required = false)
	private Node node;

	@Element(required = false)
	private Way way;

	/**
	 * Adds a change to this container. Only {@linkplain Way}s or {@linkplain Node}s allowed.
	 *
	 * @param change
	 *            will be added to this container
	 * @throws IllegalArgumentException
	 *             if the type of change is not {@linkplain Way} or {@linkplain Node}
	 */
	public void addChange(Change change) {

		if (change instanceof Way) {
			this.way = (Way) change;
		}
		else if (change instanceof Node) {
			this.node = (Node) change;
		}
		else {
			throw new IllegalArgumentException("can't add change '" + change.toString()
				+ "' to this NodeContainer");
		}

		this.id = change.getId();
	}

	public long getId() {
		return id;
	}

	/**
	 * Returns the node of this container.
	 *
	 * @return the node of this container, could be <code>null</code>
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Returns the way of this container.
	 *
	 * @return the way of this container, could be null
	 */
	public Way getWay() {
		return way;
	}
}
