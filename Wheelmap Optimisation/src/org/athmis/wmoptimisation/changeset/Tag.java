package org.athmis.wmoptimisation.changeset;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "tag")
public class Tag {

	// "required = false" is needed, because id is for my local database, OsmAPi
	// sends no id
	@Attribute(required = false)
	private long id;

	@Attribute(name = "k")
	private String key;

	@Attribute(name = "v")
	private String value;

	public long getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
