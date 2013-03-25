/**
 * created at 17.07.2012
 */
package org.athmis.wmoptimisation.fetch_changesets;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.changeset.ChangeSetToolkit;
import org.athmis.wmoptimisation.changeset.OsmApiChangeSetsResult;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * A FetchChanges object fetches changesets for given date and osm user. Uses <a
 * href="http://wiki.openstreetmap.org/wiki/API_v0.6">OSM API v0.6</a>.
 */
public class FetchChangeSets {

	/**
	 * the public user name of <a href="wheelmap.org">wheelmap</a>
	 */
	public static final String WHEELMAP_VISITOR = "wheelmap_visitor";

	/** The Constant API_RESPONSE_SERIALIZER. */
	private static final Serializer API_RESPONSE_SERIALIZER = new Persister();

	/** The develop. */
	private static boolean develop = true;

	/** The Constant GET_CHANGE_SETS_CLOSED_AFTER. */
	private static final String GET_CHANGE_SETS_CLOSED_AFTER = "http://api.openstreetmap.org/"
			+ "api/0.6/changesets?display_name=%s&time=%s&closed=true";

	/** The Constant GET_CHANGE_SETS_CLOSED_AFTER_DEV. */
	private static final String GET_CHANGE_SETS_CLOSED_AFTER_DEV = "http://api06.dev.openstreetmap.org/"
			+ "api/0.6/changesets?display_name=%s&time=%s&closed=true";

	/** The Constant GET_CHANGE_SETS_FOR_TIME_PERIOD. */
	private static final String GET_CHANGE_SETS_FOR_TIME_PERIOD = "http://api.openstreetmap.org/"
			+ "api/0.6/changesets?display_name=%s&time=%s,%s&closed=true";

	/** The Constant GET_CHANGE_SETS_FOR_TIME_TIME_PERIOD_DEV. */
	private static final String GET_CHANGE_SETS_FOR_TIME_TIME_PERIOD_DEV = "http://api06.dev.openstreetmap.org/"
			+ "api/0.6/changesets?display_name=%s&time=%s,%s&closed=true";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(FetchChangeSets.class);

	/** The Constant TWO_DAYS_IN_HOURS. */
	private static final int TWO_DAYS_IN_HOURS = 48;

	/**
	 * Fetches the changesets for given dates and returns a map with id of
	 * changeset as key and the changeset as value.
	 * 
	 * @param user
	 *            the user
	 * @param youngerBorder
	 *            the created date
	 * @param olderBorder
	 *            the closed date
	 * @return a unmodifiable map with the changesets
	 * @throws Exception
	 *             in case of errors parsing the API result
	 */
	public static Map<Long, ChangeSet> fetchChanges(String user, Calendar youngerBorder,
			Calendar olderBorder) throws Exception {
		Calendar oldestCreationDate;
		Map<Long, ChangeSet> result;

		result = new HashMap<>();

		oldestCreationDate = (Calendar) youngerBorder.clone();

		// go back in time until the oldest changeset ist older than
		// olderBorder
		while (oldestCreationDate.after(olderBorder)) {
			Map<Long, ChangeSet> olderChangeSets;

			olderChangeSets = fetchOlderChangeSets(oldestCreationDate, user);
			result.putAll(olderChangeSets);

			if (olderChangeSets.size() >= 100) {
				oldestCreationDate.add(Calendar.HOUR, -1);
			} else {
				oldestCreationDate.add(Calendar.HOUR, 2 * (-TWO_DAYS_IN_HOURS));
			}
		}

		return Collections.unmodifiableMap(result);
	}

	/**
	 * Fetch youngest change sets.
	 * 
	 * @param createdDate
	 *            the created date
	 * @param user
	 *            the user
	 * @return the map
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws Exception
	 *             the exception
	 */
	public static Map<Long, ChangeSet> fetchYoungestChangeSets(Calendar createdDate, String user)
			throws IOException, Exception {
		return fetch100Youngest(createdDate, user);
	}

	/**
	 * Searches for the oldest changeset in given result. An empty result is not
	 * permitted.
	 * 
	 * @param changeSets
	 *            will be searched for oldest changeset, must contain changesets
	 * @return the date of creation of the oldest result
	 * @throws ParseException
	 *             if the date string could not be parsed
	 */
	public static GregorianCalendar findOldestChangeset(Map<Long, ChangeSet> changeSets)
			throws ParseException {
		List<ChangeSet> changeSetsList;
		ChangeSet oldestSet;
		GregorianCalendar oldest;

		changeSetsList = new LinkedList<ChangeSet>(changeSets.values());
		Collections.sort(changeSetsList);
		oldestSet = changeSetsList.get(0);
		oldest = new GregorianCalendar();
		oldest.setTime(ChangeSetToolkit.OSM_DATE_TO_JAVA.parse(oldestSet.getCreatedAt()));

		LOGGER.info(changeSetsList.size() + " changesets fetched, oldest one created at "
				+ oldestSet.getCreatedAt());

		return oldest;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		Map<Long, ChangeSet> changeSets;
		String user;

		// configure Log4j
		BasicConfigurator.configure();

		try {
			user = "olr";
			develop = false;

			changeSets = fetchChanges(user, new GregorianCalendar(2012, 11, 31),
					new GregorianCalendar(2011, 0, 1));

			LOGGER.info(changeSets.size() + " changesets fetched for " + user + " in "
					+ (develop ? "DEVELOP" : "live") + " mode");

			StoreChangeSets.storeWithContent(changeSets, "olr-2010-2012.zip", develop);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		LOGGER.info("successfully completed");
	}

	/**
	 * Fetches the first 100 changesets which are older then createdDate set by
	 * object construction.
	 * 
	 * @param createdDate
	 *            the created date
	 * @param user
	 *            the user
	 * @return a map with changesets as value and their id's as key. Size could
	 *         be within 0 and 100.
	 * @throws IOException
	 *             network error
	 * @throws Exception
	 *             error on parsing the API result
	 */
	private static Map<Long, ChangeSet> fetch100Youngest(Calendar createdDate, String user)
			throws IOException, Exception {
		String apiCall;
		URL url;
		OsmApiChangeSetsResult apiResult;

		apiCall = String.format(getApiCallForClosedAfterGivenTime(develop), user,
				ChangeSetToolkit.OSM_DATE_TO_JAVA.format(createdDate.getTime()));

		url = new URL(apiCall);

		apiResult = API_RESPONSE_SERIALIZER.read(OsmApiChangeSetsResult.class, url.openStream());

		return apiResult.asMap();
	}

	/**
	 * Fetches at most 100 changesets which are at maximum 48 hours older then
	 * given createdDate. The closedDate will be set to createdDate - 48 hours
	 * <p>
	 * 
	 * @param createdDate
	 *            fetched changesets will be older then createdDate
	 * @param user
	 *            the user
	 * @return a map with changesets als values and their id's as key, size is
	 *         within 0 and 100, where 100 ist set as border by the OSM-API, so
	 *         if size == 100 you should reduce the next createdDate at 1 hour
	 *         for skipping the time window one hour back an get really all
	 *         changesets
	 * @throws IOException
	 *             network error
	 * @throws Exception
	 *             error on parsing the result
	 * @see <a
	 *      href="http://wiki.openstreetmap.org/wiki/API_v0.6#Query:_GET_.2Fapi.2F0.6.2Fchangesets">Osm
	 *      Api06 wiki for Query: GET /api/0.6/changesets</a>
	 */
	private static Map<Long, ChangeSet> fetchOlderChangeSets(Calendar createdDate, String user)
			throws IOException, Exception {
		Calendar closed, created;
		OsmApiChangeSetsResult apiResult;
		String apiCall;
		String createdT2, closedT1;
		URL url;

		created = (Calendar) createdDate.clone();
		closed = (Calendar) created.clone();
		closed.add(Calendar.HOUR, -TWO_DAYS_IN_HOURS);

		assert closed.before(created) : "closed date must be before created date, otherwise you don't have a time window";

		createdT2 = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(created.getTime());
		closedT1 = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(closed.getTime());

		apiCall = String.format(getApiCallForPeriod(develop), user, closedT1, createdT2);

		url = new URL(apiCall);
		apiResult = API_RESPONSE_SERIALIZER.read(OsmApiChangeSetsResult.class, url.openStream());

		return apiResult.asMap();
	}

	/**
	 * Returns OSM-API v0.6 call for fetching almost 100 actual changesets
	 * closed after given time. The oldest could (closed time - 24h). * @param
	 * devolop with <code>true</code> the OSM developer API is used, else the
	 * live API
	 * 
	 * @param develop
	 *            the develop
	 * @return the OSM-API v0.6 call as string, replace user and T1 with strings
	 * @see <a
	 *      href="http://wiki.openstreetmap.org/wiki/API_v0.6#Query:_GET_.2Fapi.2F0.6.2Fchangesets">OSM
	 *      API v0.6</a>
	 */
	private static String getApiCallForClosedAfterGivenTime(boolean develop) {
		if (develop) {
			return GET_CHANGE_SETS_CLOSED_AFTER_DEV;
		} else {
			return GET_CHANGE_SETS_CLOSED_AFTER;
		}
	}

	/**
	 * Returns the OSM-API v0.6 call for fetching almost 100 actual changesets
	 * closed after given time and created before given time. Time period is at
	 * maximum: <code>[(closed time - 24h)..(created time + 24h)]</code>
	 * 
	 * @param develop
	 *            the develop
	 * @return the OSM-API v0.6 call as string, replace user, T1 and T2 with
	 *         strings (%s)
	 * @see <a
	 *      href="http://wiki.openstreetmap.org/wiki/API_v0.6#Query:_GET_.2Fapi.2F0.6.2Fchangesets">OSM
	 *      API v0.6</a>
	 */
	private static String getApiCallForPeriod(boolean develop) {
		if (develop) {
			return GET_CHANGE_SETS_FOR_TIME_TIME_PERIOD_DEV;
		} else {
			return GET_CHANGE_SETS_FOR_TIME_PERIOD;
		}
	}

	/**
	 * it's private to prevent instantiation.
	 */
	private FetchChangeSets() {
	}
}
