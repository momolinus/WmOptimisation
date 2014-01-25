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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * A Node object stores a position and tags. It is the basic object in OSM
 * database. Also it has reference to a {@linkplain ChangeSet} id.
 */
@Root(name = "node", strict = false)
public class Node implements Change {

	/**
	 * Returns the center of Berlin, just for test purpose.
	 * 
	 * @return center of Berlin as a node
	 */
	public static Change getBerlin() {
		Change result = new Node(121212, 52.515905, 13.378588, "2010-1-1T12:00:00Z", 1, true);
		return result;
	}

	/**
	 * Returns the center of Berlin, just for test purpose.
	 * 
	 * @return center of Berlin as a node
	 */
	public static Node getBerlinAsNode() {
		Node result = new Node(121212, 52.515905, 13.378588, "2010-1-1T12:00:00Z", 1, true);
		return result;
	}

	@Attribute
	private long changeset;

	@Attribute
	private long id;

	@Attribute
	private double lat;

	@Attribute
	private double lon;

	// leave "required=false" because it could be there is a node without tags
	@ElementList(inline = true, required = false)
	private List<Tag> tags = new ArrayList<Tag>();

	@Attribute
	private String timestamp;

	@Attribute
	private String user;

	@Attribute
	private int version;

	@Attribute
	private boolean visible;

	public Node() {
	}

	// TODO timestamp parsen und prüfen
	public Node(long id, double lat, double lon, String timestamp) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.timestamp = timestamp;
		this.version = 1;
		this.visible = true;
		this.user = "default constructor";
	}

	// TODO timestamp parsen und prüfen
	public Node(long id, double lat, double lon, String timestamp, int version, boolean visible) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.timestamp = timestamp;
		this.version = version;
		this.visible = visible;
		this.user = "default constructor";
	}

	/**
	 * makes a deep copy
	 * 
	 * @param this will be a deep copy of given node
	 */
	public Node(Node node) {
		this.id = node.id;
		this.lat = node.lat;
		this.lon = node.lon;
		this.timestamp = node.timestamp;
		this.version = node.version;
		this.visible = node.visible;
		this.user = node.user;
		this.changeset = node.changeset;

		for (Tag t : node.tags) {
			tags.add(new Tag(t));
		}
	}

	@Override
	public int compareTo(Change other) {
		return getCreatedAt().compareTo(other.getCreatedAt());
	}

	@Override
	public long getChangeset() {
		return changeset;
	}

	@Override
	public Calendar getCreatedAt() {
		return ChangeSetToolkit.osmToCal(timestamp);
	}

	@Override
	public long getId() {
		return id;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	/**
	 * Returns a list with it's points, list has no back references to this.
	 */
	public List<Point2D> getPoints() {
		List<Point2D> result;

		result = new ArrayList<>();
		result.add(new Point2D.Double(lat, lon));
		return result;
	}

	/**
	 * Returns an unmodifiable list with the tags.
	 * 
	 * @return unmodifiable list with the tags
	 */
	public List<Tag> getTags() {
		return Collections.unmodifiableList(tags);
	}

	@Override
	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public String getUser() {
		return user;
	}

	public int getVersion() {
		return version;
	}

	public boolean isVisible() {
		return visible;
	}

	/**
	 * @return all ways <code>false</code>
	 */
	@Override
	public boolean isWay() {
		return false;
	}

	@Override
	public void setChangeset(long changeSetId) {
		this.changeset = changeSetId;
	}

	@Override
	public String verbose() {
		StringBuilder msg = new StringBuilder();

		msg.append("Node [id = " + id + ", ");
		msg.append("created = " + ChangeSetToolkit.FORMATTER.format(getCreatedAt().getTime()) + "]");

		return msg.toString();
	}
}
