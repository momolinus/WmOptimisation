/* Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012 This file is part
 * of Wheelmap Optimization. Wheelmap Optimization is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Wheelmap Optimization is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with
 * Athmis. If not, see <http://www.gnu.org/licenses/>. Diese Datei ist Teil von
 * Wheelmap Optimization. Wheelmap Optimization ist Freie Software: Sie können
 * es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder
 * späteren veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 * Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details. Sie sollten eine
 * Kopie der GNU General Public License zusammen mit diesem Programm erhalten
 * haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>. */
package org.athmis.wmoptimisation.changeset;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

/**
 * A ChangeSet object is an OSM-Changeset, which is a container for changes on
 * {@linkplain Node}s or {@linkplain Way}s. The nodes or ways has a reference to
 * their changeset id as an attribute.
 * <p>
 * This class is used to fetch the changesets from the osm server. Although it
 * seems that ChangeSet is a data container, itself doe's not store the
 * changes/edits (like nodes ore ways). But the changes/edits stores the ids of
 * their changeset. If a data container is needed in programming context use
 * {@linkplain OsmChangeContent} instead.
 * <p>
 * From <a
 * href="http://wiki.openstreetmap.org/wiki/API_v0.6#Changesets_2">OSM-Wiki</a>:
 * <blockquote cite="http://wiki.openstreetmap.org/wiki/API_v0.6#Changesets_2">
 * To avoid stale open changesets a mechanism is implemented to automatically
 * close changesets upon one of the following three conditions:
 * <ul>
 * <li>More than 50.000 edits on a single changeset</li>
 * <li>The changeset has been open for more than 24 hours</li>
 * <li>There have been no changes/API calls related to a changeset in 1 hour
 * (i.e. idle timeout)</li>
 * </ul>
 * </blockquote>
 */
@Root(name = "changeset", strict = false)
public class ChangeSet implements Comparable<ChangeSet> {

	@Attribute(required = false)
	private double area;

	@Attribute(name = "closed_at", required = false)
	protected String closedAt;

	@Attribute(name = "max_lat", required = false)
	protected double maxLatitude = -Double.MAX_VALUE;

	@Attribute(name = "max_lon", required = false)
	protected double maxLongitude = -Double.MAX_VALUE;

	@Attribute(name = "min_lat", required = false)
	protected double minLatitude = Double.MAX_VALUE;

	@Attribute(name = "min_lon", required = false)
	protected double minLongitude = Double.MAX_VALUE;

	@Attribute(name = "open", required = false)
	protected boolean open;

	@ElementList(inline = true, required = false)
	private List<Tag> tags = new ArrayList<Tag>();

	@Attribute(name = "user")
	private String user;

	// leave visibility "protected" for test purpose
	@Attribute(name = "created_at", required = false)
	protected String createdAt;

	/**
	 * corresponding to the id from OSM API call
	 */
	// leave visibility "protected" for test purpose
	@Attribute(name = "id")
	protected long id;

	/**
	 * default constructor needed for simple framework
	 */
	public ChangeSet() {

	}

	public ChangeSet(String createdAt, long id, boolean open) {
		this.createdAt = createdAt;
		this.id = id;
		this.open = open;
	}

	/**
	 * Constructor for a deep copy of given changeset.
	 * 
	 * @param changeSet
	 *            its state will be used to construct a new ChangeSet object (a
	 *            deep copy)
	 */
	public ChangeSet(ChangeSet changeSet) {
		this.area = changeSet.area;

		for (Tag tag : changeSet.tags) {
			tags.add(new Tag(tag));
		}

		this.user = changeSet.user;
		this.closedAt = changeSet.closedAt;
		this.createdAt = changeSet.createdAt;
		this.id = changeSet.id;
		this.maxLatitude = changeSet.maxLatitude;
		this.maxLongitude = changeSet.maxLongitude;
		this.minLatitude = changeSet.minLatitude;
		this.minLongitude = changeSet.minLongitude;
		this.open = changeSet.open;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if one created date has wrong format
	 * @see ChangeSetToolkit#OSM_DATE_TO_JAVA
	 */
	@Override
	public int compareTo(ChangeSet arg0) {
		Date meCreated;
		Date otherCreated;

		try {
			otherCreated = ChangeSetToolkit.OSM_DATE_TO_JAVA.parse(arg0.createdAt);
			meCreated = ChangeSetToolkit.OSM_DATE_TO_JAVA.parse(createdAt);
		}
		catch (ParseException e) {
			throw new IllegalArgumentException("created date has wrong format", e);
		}

		return meCreated.compareTo(otherCreated);
	}

	/**
	 * Returns the bounding box of the changeset in °*° (in words:
	 * "square degree").
	 * 
	 * @return the bounding box of the changeset, {@link Double#isInfinite()} if
	 *         changeset has no {@linkplain Change}s
	 */
	public double getBoundingBoxSquareDegree() {

		// note: no matter calculate the area always, it's fast enough
		calculateArea();

		return area;
	}

	public Calendar getClosed() {
		Calendar result = closedAt();
		return result;
	}

	public String getClosedAt() {
		return closedAt;
	}

	public Calendar getCreated() {
		Calendar result = createdAt();
		return result;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public long getId() {
		return id;
	}

	/**
	 * Returns the open hours, decimal place is hours fraction, e.g. 0.5 means
	 * 30 min.
	 * 
	 * @return the open hours of the changeset, maximum should be 24 h
	 */
	public double getOpenTimeInHours() {
		double openTime;
		long openTimeInMillis;

		openTimeInMillis = closedAt().getTimeInMillis() - createdAt().getTimeInMillis();
		openTime = TimeUnit.MILLISECONDS.toHours(openTimeInMillis);

		// the tail less then 1 hour divided through 1 hour in ms (cast is o.k.,
		// because openTime has no decimal place here)
		openTime +=
			((openTimeInMillis - TimeUnit.HOURS.toMillis((long) openTime)))
				/ (double) TimeUnit.HOURS.toMillis(1);

		return openTime;
	}

	public List<Tag> getTags() {
		return Collections.unmodifiableList(tags);
	}

	public String getUser() {
		return user;
	}

	public boolean isOpen() {
		return open;
	}

	public String verbose() {
		StringBuilder msg = new StringBuilder();

		msg.append("ChangeSet [id = " + id + ", ");
		msg.append("created = " + ChangeSetToolkit.FORMATTER.format(getCreated().getTime()) + "]");

		return msg.toString();
	}

	private Calendar closedAt() {
		Calendar result = GregorianCalendar.getInstance();

		Date closed;
		try {
			if (closedAt == null)
				throw new IllegalStateException("closedAt is null, id = " + String.valueOf(id)
					+ ", user = " + String.valueOf(user));

			closed = ChangeSetToolkit.OSM_DATE_TO_JAVA.parse(closedAt);
			result.setTime(closed);
		}
		catch (ParseException e) {
			throw new IllegalStateException("error with parsing closed date "
				+ String.valueOf(closedAt) + ", message: " + e.getLocalizedMessage(), e);
		}

		return result;
	}

	private Calendar createdAt() {
		Calendar result = GregorianCalendar.getInstance();
		Date created;
		try {
			if (createdAt == null)
				throw new IllegalStateException("createdAt is null, id = " + String.valueOf(id)
					+ ", user = " + String.valueOf(user));

			created = ChangeSetToolkit.OSM_DATE_TO_JAVA.parse(createdAt);
			result.setTime(created);
		}
		catch (ParseException e) {
			throw new IllegalStateException("error with parsing closed date "
				+ String.valueOf(closedAt) + ", message: " + e.getLocalizedMessage(), e);
		}

		return result;
	}

	/**
	 * Calculates the area/bounding box from {@linkplain #maxLatitude},
	 * {@linkplain #minLatitude}, {@linkplain #maxLongitude} and
	 * {@linkplain ChangeSet#minLongitude}; uses {@linkplain Math#abs(double)}
	 * when calculates the difference in latitude and longitude.
	 */
	@Commit
	protected void calculateArea() {
		double width, heigth;

		heigth = Math.abs(maxLatitude - minLatitude);
		width = Math.abs(maxLongitude - minLongitude);

		area = heigth * width;
	}
}
