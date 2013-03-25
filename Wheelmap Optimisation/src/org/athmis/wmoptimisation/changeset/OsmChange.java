package org.athmis.wmoptimisation.changeset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * An OsmChange object acts for the root element of OsmAPI response on changed
 * nodes. It contains two {@linkplain NodeContainer} collections, one for
 * changed {@linkplain Node}s and one for new created {@linkplain Node}s.
 * 
 * @author Marcus
 * 
 */
@Root(name = "osmChange", strict = false)
public class OsmChange {

	// "required=false" is important for empty changes or missing creations
	@ElementList(entry = "create", inline = true, required = false)
	private List<NodeContainer> created = new ArrayList<NodeContainer>();

	@Attribute
	private String generator;

	/**
	 * <strong>important note:</strong> this id is <strong>not</strong> returned
	 * by OSM API, planned for using in database, but development of database
	 * version is canceled
	 */
	@Attribute(required = false)
	private long id;

	// "required=false" is important for empty changes or missing modifications
	@ElementList(entry = "modify", inline = true, required = false)
	private List<NodeContainer> modified = new ArrayList<NodeContainer>();

	@Attribute
	private double version;

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
}
