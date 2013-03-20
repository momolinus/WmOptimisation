package org.athmis.wmoptimisation.changeset;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "way", strict = false)
public class Way {

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

	public long getChangeset() {
		return changeset;
	}

	public long getId() {
		return id;
	}

	public List<Nd> getNodes() {
		return nodes;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public long getUid() {
		return uid;
	}

	public String getUser() {
		return user;
	}

	public int getVersion() {
		return version;
	}
}
