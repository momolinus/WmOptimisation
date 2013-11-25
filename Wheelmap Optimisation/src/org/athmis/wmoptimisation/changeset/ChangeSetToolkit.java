/*
Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012

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

Wheelmap Optimization ist Freie Software: Sie können es unter den Bedingungen
der GNU General Public License, wie von der Free Software Foundation,
Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
veröffentlichten Version, weiterverbreiten und/oder modifizieren.

Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird, aber
OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
Siehe die GNU General Public License für weitere Details.

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.athmis.wmoptimisation.changeset;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

/**
 * Helper class to work with change sets.
 */
public class ChangeSetToolkit {

	/**
	 * format conversation of osm date format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 */
	public final static DateFormat OSM_DATE_TO_JAVA = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private ChangeSetToolkit() {

	}

	/**
	 * 
	 * @param dateString
	 * @return
	 * 
	 * @throws IllegalArgumentException
	 *             in case of error on parsing given date string
	 */
	public static Calendar osmToCal(String dateString) {
		Calendar result = GregorianCalendar.getInstance();
		try {
			result.setTime(OSM_DATE_TO_JAVA.parse(dateString));
		} catch (ParseException e) {
			throw new IllegalArgumentException("can't convert " + String.valueOf(dateString)
					+ " to Calendar object", e);
		}
		return result;
	}

	public static String calToOsm(Calendar date) {
		String result;
		result = OSM_DATE_TO_JAVA.format(date.getTime());
		return result;
	}

	public static double meanArea(Collection<ChangeSet> changeSets) {
		double result = 0;

		if (changeSets == null)
			throw new IllegalArgumentException("null as changeSets is nnot permitted");

		if (changeSets.size() == 0)
			return Double.NaN;

		for (ChangeSet changeSet : changeSets) {
			result += changeSet.getArea();
		}

		return result / changeSets.size();
	}
}
