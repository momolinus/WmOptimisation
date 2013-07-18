/*
Copyright Marcus Bleil, Oliver Rudzik, Christoph B�nte 2012

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

Wheelmap Optimization ist Freie Software: Sie k�nnen es unter den Bedingungen
der GNU General Public License, wie von der Free Software Foundation,
Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

Wheelmap Optimization wird in der Hoffnung, dass es n�tzlich sein wird, aber
OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
Siehe die GNU General Public License f�r weitere Details.

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.athmis.wmoptimisation.changeset;

import java.text.ParseException;
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

	public Node(long id, double lat, double lon, String timestamp, int version, boolean visible) {
		super();
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.timestamp = timestamp;
		this.version = version;
		this.visible = visible;
		this.user = "default constructor";
	}

	@Override
	public long getChangeset() {
		return changeset;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLon() {
		return lon;
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

	@Override
	public Calendar getCreatedAt() throws ParseException {
		return ChangeSetToolkit.osmToCal(timestamp);
	}

	@Override
	public int compareTo(Object other) {
		Calendar meTimeStamp, otherTimeStamp;
		try {
			meTimeStamp = ChangeSetToolkit.osmToCal(timestamp);
			otherTimeStamp = ChangeSetToolkit.osmToCal(((Node) other).timestamp);

			return meTimeStamp.compareTo(otherTimeStamp);
		} catch (ParseException pe) {
			throw new RuntimeException("timestamp of this node is wrong initialized", pe);
		}
	}

	@Override
	public void setChangeset(long changeSetId) {
		this.changeset = changeSetId;
	}
}
