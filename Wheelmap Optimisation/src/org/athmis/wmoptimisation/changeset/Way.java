package org.athmis.wmoptimisation.changeset;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "way", strict = false)
public class Way implements Change {

	@Attribute
	private long changeset;

	// you could have one node more times in the database, because it could be
	// modified by different changesets
	@Attribute
	private long id;

	// leave "required=false" because it could be there is a way without tags
	@ElementList(inline = true, required = false)
	private List<Nd> nodes = new ArrayList<Nd>();

	// leave "required=false" because it could be there is a way without tags
	@ElementList(inline = true, required = false)
	private List<Tag> tags = new ArrayList<Tag>();

	@Attribute
	private String timestamp;

	@Attribute
	private long uid;

	@Attribute
	private String user;

	@Attribute
	private int version;

	@Override
	public long getChangeset() {
		return changeset;
	}

	@Override
	public long getId() {
		return id;
	}

	public List<Nd> getNodes() {
		return nodes;
	}

	public List<Tag> getTags() {
		return tags;
	}

	@Override
	public String getTimestamp() {
		return timestamp;
	}

	public long getUid() {
		return uid;
	}

	@Override
	public String getUser() {
		return user;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public double getLon() {
		return 0;
	}

	@Override
	public double getLat() {
		return 0;
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
			otherTimeStamp = ChangeSetToolkit.osmToCal(((Way) other).timestamp);

			return meTimeStamp.compareTo(otherTimeStamp);
		} catch (ParseException pe) {
			throw new RuntimeException("timestamp of this way is wrong initialized", pe);
		}
	}

	@Override
	public void setChangeset(long changeSetId) {
		this.changeset = changeSetId;
	}
}
