/* Copyright Marcus Bleil, Oliver Rudzik, Christoph B�nte 2012 This file is part of Wheelmap
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
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>. */
package org.athmis.wmoptimisation.changeset;

import java.awt.geom.Rectangle2D;
import java.text.*;
import java.time.*;
import java.util.*;

/**
 * Helper class to work with change sets.
 */
public final class ChangeSetToolkit {

	/**
	 * format conversation of osm date format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 */
	public final static DateFormat OSM_DATE_TO_JAVA = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private ChangeSetToolkit() {

	}

	// TODO inspect next sprint: nach FindBug ist das nicht threadsafe, pr�fen
	/**
	 * @param dateString
	 * @return
	 * @throws IllegalArgumentException
	 *             in case of error on parsing given date string
	 */
	public static Calendar osmToCal(String dateString) {
		Calendar result = GregorianCalendar.getInstance();
		try {
			result.setTime(OSM_DATE_TO_JAVA.parse(dateString));
		}
		catch (ParseException e) {
			throw new IllegalArgumentException("can't convert " + String.valueOf(dateString)
				+ " to Calendar object", e);
		}
		return result;
	}

	/**
	 * Converts a {@linkplain Calendar} object to an OSM data string.
	 * <p>
	 * Note: millisecond will be lost, so two {@linkplain Calendar} which differs on millisecond
	 * could be equal as OSM Strings (and vice versa).
	 *
	 * @param date
	 *            the date for conversion
	 * @return given date as OSM date string
	 */
	public static String calToOsm(Calendar date) {
		String result;
		result = OSM_DATE_TO_JAVA.format(date.getTime());
		return result;
	}

	// TODO document next sprint: klar machen, dass es nur die die areas > 0 nimmt
	/**
	 * Calculates the mean area as the mean of given changesets bounding boxes.
	 *
	 * @param changeSets
	 *            a list with changesets, where the bounding boxes will be used
	 * @return mean area as the mean of given changesets bounding boxes or {@link Double#NaN} if
	 *         changeSets has no elements
	 * @throws IllegalArgumentException
	 *             if changeSets is <code>null</code>
	 */
	public static double meanArea(Collection<ChangeSetUpdateAble> changeSets) {
		double result = 0;
		int areas = 0;

		if (changeSets == null) {
			throw new IllegalArgumentException("null as changeSets is nnot permitted");
		}

		if (changeSets.size() == 0) {
			return Double.NaN;
		}

		for (ChangeSet changeSet : changeSets) {
			if (changeSet.getBoundingBoxSquareDegree() > 0) {
				result += changeSet.getBoundingBoxSquareDegree();
				areas++;
			}
		}

		if (areas > 0) {
			return result / areas;
		}
		else {
			return 0;
		}
	}

	public static double getDistance(Change c1, Change c2) {
		double deltaLat, deltaLon;

		deltaLat = Math.abs(c1.getLat() - c2.getLat());
		deltaLon = Math.abs(c1.getLon() - c2.getLon());
		return Math.sqrt(deltaLat * deltaLon);
	}

	/**
	 * @param node
	 * @param area
	 * @return a new created area with max lat/lon of both given parameters, where x of area is lon
	 *         and y is lat
	 * @throws UnsupportedOperationException
	 *             because logic is to complex actually
	 */
	public static Rectangle2D updateArea(Node node, Rectangle2D area) {
		throw new UnsupportedOperationException("method still missing");
	}

	public static String localDateToOsm(LocalDate localDate) {
		ZonedDateTime zonedDateTime;

		zonedDateTime = ZonedDateTime.of(localDate, LocalTime.of(0, 0), ZoneId.systemDefault());
		Date date;
		date = Date.from(Instant.from(zonedDateTime));
		return OSM_DATE_TO_JAVA.format(date);
	}
}
