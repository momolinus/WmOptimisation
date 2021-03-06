/*
 * Copyright Marcus Bleil, Oliver Rudzik, Christoph B�nte 2012 This file is part of Wheelmap
 * Optimization. Wheelmap Optimization is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. Wheelmap Optimization is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with Athmis. If not, see <http://www.gnu.org/licenses/>. Diese Datei ist Teil von
 * Wheelmap Optimization. Wheelmap Optimization ist Freie Software: Sie k�nnen es unter den
 * Bedingungen der GNU General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder sp�teren ver�ffentlichten Version, weiterverbreiten
 * und/oder modifizieren. Wheelmap Optimization wird in der Hoffnung, dass es n�tzlich sein wird,
 * aber OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite Gew�hrleistung der
 * MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public License f�r
 * weitere Details. Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.athmis.wmoptimisation.changeset;

import java.awt.geom.Point2D;
import java.util.*;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * A Node object stores a position and tags. It is the basic object in OSM database. Also it has
 * reference to a {@linkplain ChangeSet} id.
 */
@Root(name = "node", strict = false)
public class Node implements Change {

	public static double getBbox(Node node1, Node node2) {

		double deltaLat, deltaLon;

		deltaLat = Math.abs(node1.lat - node2.lat);
		deltaLon = Math.abs(node1.lon - node2.lon);
		return deltaLat * deltaLon;
	}

	/**
	 * Returns the center of Berlin, just for test purpose.
	 *
	 * @return center of Berlin as a node
	 */
	public static Node getBerlin() {

		Node result = new Node(121212, 52.515905, 13.378588, "2010-1-1T12:00:00Z", 1, true);
		return result;
	}

	/**
	 * Returns the center of Berlin, just for test purpose.
	 *
	 * @return center of Berlin as a node
	 */
	public static Node getBerlinAsNode() {

		return getBerlin();
	}

	// XXX add a comment
	public static Node getDifferentNode(Node node, double lat, double lon) {

		Calendar nodeTime = node.getCreatedAt();
		nodeTime.add(Calendar.MINUTE, 5);
		Node result = new Node(node.id + 1, lat, lon, ChangeSetToolkit.calToOsm(nodeTime), 1, true);

		return result;
	}

	public static Node getMovedNode(Node node, double deltaLat, double deltaLon) {

		Calendar nodeTime = node.getCreatedAt();
		nodeTime.add(Calendar.MINUTE, 5);
		Node result = new Node(node.id + 1, node.lat + deltaLat, node.lon + deltaLon,
			ChangeSetToolkit.calToOsm(nodeTime), 1, true);

		return result;
	}

	/**
	 * Method constructs a new {@link Change} from given Change but with different creation time of
	 * given minutes.
	 *
	 * @param change
	 *            this change position used for new created change
	 * @param minutes
	 *            the amount of minutes will be added to the given change creation time using for
	 *            the new change
	 * @return a new Change instance at same position but with different creation time
	 */
	public static Node later(Change change, int minutes) {

		Calendar nodeTime = change.getCreatedAt();
		nodeTime.add(Calendar.MINUTE, minutes);

		Node result = new Node(change.getId() + System.currentTimeMillis(), change.getLat(),
			change.getLon(), ChangeSetToolkit.calToOsm(nodeTime), 1, true);

		return result;
	}

	// TODO document next sprint
	/**
	 * @param node
	 * @param minutes
	 * @param lat
	 * @param lon
	 * @return
	 */
	public static Node getMovedNode(Node node, int minutes, double lat, double lon) {

		Node result = new Node(node);
		Calendar createTime;

		result.lat += lat;
		result.lon += lon;

		createTime = ChangeSetToolkit.osmToCal(result.timestamp);
		createTime.add(Calendar.MINUTE, minutes);
		result.timestamp = ChangeSetToolkit.calToOsm(createTime);
		result.id = System.nanoTime();
		return result;
	}

	/**
	 * Returns a node with given lat and lon, id = 1, version = 1 and visible = true.
	 *
	 * @param lat
	 *            latitude in degree
	 * @param lon
	 *            longitude in degree
	 * @return node with given lat and lon
	 */
	public static Node getNode(double lat, double lon) {

		Node result =
			new Node(1, lat, lon, ChangeSetToolkit.calToOsm(Calendar.getInstance()), 1, true);
		return result;
	}

	/**
	 * Returns two nodes, with a little difference in time and index.
	 *
	 * @param distance
	 *            will be distance from second node
	 * @return two nodes
	 */
	public static List<Node> getNodes(double distance) {

		Node result = new Node(121212, 52.515905, 13.378588, "2010-1-1T12:00:00Z", 1, true);
		Node result2 = new Node(121213, 52.515905 - distance, 13.378588 - distance,
			"2010-1-1T12:05:00Z", 1, true);
		return new ArrayList<>(Arrays.asList(result, result2));
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

	public Node() {}

	// TODO next+1 sprint: timestamp parsen und pr�fen, geht einfach und dann exception werfen
	public Node(long id, double lat, double lon, String timestamp) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.timestamp = timestamp;
		this.version = 1;
		this.visible = true;
		this.user = "default constructor";
	}

	// TODO next+1 sprint timestamp parsen und pr�fen, geht einfach und dann exception werfen
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
	 * @param this
	 *            will be a deep copy of given node
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

		for (Tag tag : node.tags) {
			tags.add(new Tag(tag));
		}
	}

	@Override
	public int compareTo(Change other) {

		return getCreatedAt().compareTo(other.getCreatedAt());
	}

	/**
	 * Returns a {@linkplain Point2D} with x = lon and y = lat.
	 *
	 * @return a {@linkplain Point2D} with x = lon and y = lat
	 */
	public Point2D getArea() {

		return new Point2D.Double(lon, lat);
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

	@Override
	public double getLat() {

		return lat;
	}

	public Point2D getLatLon() {

		return new Point2D.Double(lat, lon);
	}

	@Override
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

	public void setId(long id) {

		this.id = id;
	}

	@Override
	public String toString() {

		return "Node [changeset=" + changeset + ", id=" + id + ", timestamp=" + timestamp
			+ ", user=" + user + "]";
	}

	@Override
	public String verbose() {

		StringBuilder msg = new StringBuilder();

		msg.append("Node [id = " + id + ", ");
		msg.append("created = " + ChangeSetToolkit.FORMATTER.format(getCreatedAt().getTime())
			+ "]");

		return msg.toString();
	}
}
