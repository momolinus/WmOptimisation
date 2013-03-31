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
