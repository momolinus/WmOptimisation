package org.athmis.wmoptimisation.osmserver;

import java.awt.geom.Rectangle2D;
import java.util.Calendar;

import org.athmis.wmoptimisation.changeset.ChangeSet;

/**
 * A OsmServer object simulates an <a
 * href="http://wiki.openstreetmap.org/wiki/API_v0.6">OSM Server</a>. Most
 * methods has a parameter for calling time. This prevents, that any clock must
 * be running in any thread; in other words 'the time ist simulated', too.
 * 
 * @author Marcus
 * 
 */
public class OsmServer {

	public OsmServer() {
	}

	/**
	 * Closes the changeset for given id.
	 * 
	 * @param id
	 *            changesets id
	 * @param closeTime
	 *            the closing time
	 * @return <code>true</code> if changeset was open, <code>false</code> if
	 *         changeset still was closed.
	 */
	public boolean closeChangeSet(Integer id, Calendar closeTime) {
		return false;
	}

	/**
	 * Creates a changeset and keeps it's state in memory.
	 * 
	 * @param creationTime
	 *            in the simulation clients set the creation time, so no clock
	 *            must run in any thread
	 * @return the unique (during one run) id of the changeset
	 */
	public Integer createChangeSet(Calendar creationTime) {
		return null;
	}

	// XXX für eine ungültige id könnte man eine andere Exception nehmen
	/**
	 * Expands the given changesets bounding box.
	 * 
	 * @param id
	 *            changesets id
	 * @param boundingBox
	 *            the new bounding box
	 * @param now
	 *            the time of this request
	 * @return <code>true</code> if was successful, false if changeset was
	 *         closed
	 * @throws IllegalArgumentException
	 *             in case of illegal bounding box or illegal id
	 */
	public boolean expandBoundingBox(Integer id, Rectangle2D boundingBox, Calendar now) {
		return false;
	}

	/**
	 * Returns the changeset open state.
	 * 
	 * @param id
	 *            the id of changeset
	 * @param the
	 *            actual time
	 * @return <code>true</code> if the changeset ist open
	 */
	public boolean isChangeSetOpen(Integer id, Calendar now) {
		return false;

	}

	// TODO für welche Parameter ist die IllegalArgumentException sinnvoll?
	/**
	 * Returns the changeset with given id.
	 * 
	 * @param id
	 *            the if of the changeset weich should be returned
	 * @return the changeset with given id
	 * @throws IllegalArgumentException
	 */
	public ChangeSet getChangeSet(Integer id) {
		return null;
	}
}
