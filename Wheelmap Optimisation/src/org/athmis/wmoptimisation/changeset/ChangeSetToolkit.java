/*
Copyright Marcus Bleil, Oliver Rudzik, Christoph B�nte 2012

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

Wheelmap Optimization ist Freie Software: Sie k�nnen es unter den Bedingungen
der GNU General Public License, wie von der Free Software Foundation,
Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

Wheelmap Optimization wird in der Hoffnung, dass es n�tzlich sein wird, aber
OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
Siehe die GNU General Public License f�r weitere Details.

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.athmis.wmoptimisation.changeset;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.simpleframework.xml.Transient;

public class ChangeSetToolkit {

	/**
	 * format conversation of osm date format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 */
	@Transient
	public final static DateFormat OSM_DATE_TO_JAVA = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	private ChangeSetToolkit() {

	}

	public static Calendar osmToCal(String dateString) throws ParseException {
		Calendar result = GregorianCalendar.getInstance();
		result.setTime(OSM_DATE_TO_JAVA.parse(dateString));
		return result;
	}

	public static String calToOsm(Calendar date) {
		String result;
		result = OSM_DATE_TO_JAVA.format(date.getTime());
		return result;
	}
}
