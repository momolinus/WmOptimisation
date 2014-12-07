/**
 *
 */
package org.athmis.wmoptimisation.changeset;

import java.util.Calendar;

/**
 * The CangeSetUpdateAble class is an modifiable extension for {@linkplain ChangeSet} class. It
 * provides methods for closing and enlarging a changeset.
 *
 * @author Marcus Bleil
 */
public class ChangeSetUpdateAble extends ChangeSet {

	private int bBoxUpdates = 0;
	private boolean original = false;

	/**
	 * {@inheritDoc}
	 */
	public ChangeSetUpdateAble(ChangeSet changeSet) {
		super(changeSet);

		// FIXME kommentieren, das gilt nur für Roh-Daten
		/* if (changeSet.getBoundingBoxSquareDegree() > 0) { bBoxUpdates = 2; } if (changeSet.g) */
		original = true;
	}

	// TODO wird in der Simulation verwendet und sollte nur da verwendet werden
	/**
	 * {@inheritDoc}
	 */
	public ChangeSetUpdateAble(String calToOsm, long index, boolean open) {
		super(calToOsm, index, open);
	}

	/**
	 * Closes the changeSet object at given time.
	 *
	 * @param closingTime
	 *            will became the closing time
	 */
	public void close(Calendar closingTime) {
		open = false;
		closedAt = ChangeSetToolkit.calToOsm(closingTime);
	}

	/**
	 * Closes a changeset, by setting close time = create time + 23:59
	 */
	public void closeNow() {
		Calendar openTime;

		open = false;
		openTime = ChangeSetToolkit.osmToCal(createdAt);
		openTime.add(Calendar.HOUR, 23);
		openTime.add(Calendar.MINUTE, 59);
	}

	/**
	 * Updates the bounding box with given change. Meaning setting the max latitude and max
	 * longitude using old max values and the new latitude and longitude from given change.
	 *
	 * @param change
	 *            given change latitude and longitude are used for updating the bounding box
	 */
	public void updateBoundingBox(Change change) {
		double lat, lon;

		bBoxUpdates++;

		lat = change.getLat();
		lon = change.getLon();

		maxLatitude = Math.max(maxLatitude, lat);
		minLatitude = Math.min(minLatitude, lat);

		maxLongitude = Math.max(maxLongitude, lon);
		minLongitude = Math.min(minLongitude, lon);

		// calculateArea();

		if (!Double.isFinite(getBoundingBoxSquareDegree())) {
			throw new IllegalArgumentException("error updating bounding box with change "
				+ change.toString());
		}
	}

	@Override
	public double getBoundingBoxSquareDegree() {
		if (original) {
			return super.getBoundingBoxSquareDegree();
		}
		else {

			if (bBoxUpdates == 0) {
				return -1.0;
			}
			else if (bBoxUpdates == 1) {
				return 0.0;
			}
			else {
				return super.getBoundingBoxSquareDegree();
			}
		}
	}

	public void setUser(String user) {
		this.user = user;
	}
}
