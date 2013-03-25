package org.athmis.wmoptimisation.changeset;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "node", strict = false)
public class Node implements Change {

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
		// FIXME was ist das ???
		this.visible = true;
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
}
