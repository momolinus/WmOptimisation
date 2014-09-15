/* Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012 This file is part of Wheelmap
 * Optimization. Wheelmap Optimization is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. Wheelmap Optimization is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with Athmis. If not, see <http://www.gnu.org/licenses/>. Diese Datei ist Teil von
 * Wheelmap Optimization. Wheelmap Optimization ist Freie Software: Sie können es unter den
 * Bedingungen der GNU General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version, weiterverbreiten
 * und/oder modifizieren. Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird,
 * aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public License für
 * weitere Details. Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>. */
package org.athmis.wmoptimisation.fetch_changesets;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.*;
import org.athmis.wmoptimisation.filefilter.ChangeSetContentFileFilter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * The FetchChanges class (toolkit class) provides methods for fetching changesets for given date
 * and osm user. Uses <a href="http://wiki.openstreetmap.org/wiki/API_v0.6">OSM API v0.6</a>, more
 * detailed <a href=
 * "http://wiki.openstreetmap.org/wiki/API_v0.6#Query:_GET_.2Fapi.2F0.6.2Fchangesets" >Query: GET
 * /api/0.6/changesets</a>.
 */
public final class FetchingChangeSetsToolbox {

	/**
	 * the call for downloading changes for a given changeset id, see <a href=
	 * "http://wiki.openstreetmap.org/wiki/API_v0.6#Download:_GET_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fdownload"
	 * >Download: GET /api/0.6/changeset/#id/download</a>
	 */
	public static final String GET_CHANGE_SET_DOWNLOAD = "http://api.openstreetmap.org/"
		+ "api/0.6/changeset/%s/download";

	/**
	 * the test call for downloading changes for a given changeset id, see <a href=
	 * "http://wiki.openstreetmap.org/wiki/API_v0.6#Download:_GET_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fdownload"
	 * >Download: GET /api/0.6/changeset/#id/download</a>
	 */
	public static final String GET_CHANGE_SET_DOWNLOAD_DEV = "http://api06.dev.openstreetmap.org/"
		+ "api/0.6/changeset/%s/download";

	/**
	 * the public user name of <a href="wheelmap.org">wheelmap</a>
	 */
	public static final String WHEELMAP_VISITOR = "wheelmap_visitor";

	/**
	 * the serializer used for serializing the OSM API response (which is in XML) to the classes of
	 * this project
	 */
	private static final Serializer API_RESPONSE_SERIALIZER = new Persister();

	/**
	 * set to true, for using the test OSM API, set to false using the live-API
	 */
	private static boolean develop = true;

	/**
	 * the call for changeset, which closed after given time (live-API)
	 */
	private static final String GET_CHANGE_SETS_CLOSED_AFTER = "http://api.openstreetmap.org/"
		+ "api/0.6/changesets?display_name=%s&time=%s&closed=true";

	/**
	 * the call for changeset, which closed after given time (test-API)
	 */
	private static final String GET_CHANGE_SETS_CLOSED_AFTER_DEV =
		"http://api06.dev.openstreetmap.org/"
			+ "api/0.6/changesets?display_name=%s&time=%s&closed=true";

	/**
	 * the call for changeset, which closed and created after given time (live-API)
	 */
	private static final String GET_CHANGE_SETS_FOR_TIME_PERIOD = "http://api.openstreetmap.org/"
		+ "api/0.6/changesets?display_name=%s&time=%s,%s&closed=true";

	/**
	 * the call for changeset, which closed and created after given time (live-API)
	 */
	private static final String GET_CHANGE_SETS_FOR_TIME_TIME_PERIOD_DEV =
		"http://api06.dev.openstreetmap.org/"
			+ "api/0.6/changesets?display_name=%s&time=%s,%s&closed=true";

	/**
	 * a logger for test and develop purpose
	 */
	private static final Logger LOGGER = Logger.getLogger(FetchingChangeSetsToolbox.class);

	/**
	 * the default serializer, use for all serializing and deserializing tasks
	 */
	private static final Serializer SERIALIZER = new Persister();

	/**
	 * constant for two days in hours
	 */
	private static final int TWO_DAYS_IN_HOURS = 48;

	/**
	 * @param apiCall
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String apiCallResult(String apiCall) throws MalformedURLException, IOException {
		URL url;
		url = new URL(apiCall);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		StringBuilder builder = new StringBuilder();
		reader.lines().forEach(line -> {
			builder.append(line);
			builder.append("\n");
		});

		return builder.toString();
	}

	/**
	 * Build the OSM API call for fetching the changes referring to a given changeset id.
	 *
	 * @param changeSetId
	 *            the if of the changeset which changes should be downloaded
	 * @param develop
	 *            <code>true</code> means use the OSM test API, <code>false</code> means using the
	 *            OSM live API
	 * @return the API call
	 */
	public static String createApiCall(Long changeSetId, boolean develop) {
		if (develop) {
			return String.format(GET_CHANGE_SET_DOWNLOAD_DEV, changeSetId.toString());
		}
		else {
			return String.format(GET_CHANGE_SET_DOWNLOAD, changeSetId.toString());
		}
	}

	/**
	 * Fetches the changesets for given dates and returns a map with id of changeset as key and the
	 * changeset as value.
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
			}
			else {
				oldestCreationDate.add(Calendar.HOUR, 2 * (-TWO_DAYS_IN_HOURS));
			}
		}

		return Collections.unmodifiableMap(result);
	}

	/**
	 * Fetches all the changes (and their content) for a given changeset id.
	 *
	 * @param changeSetId
	 *            the if of the changeset
	 * @param develop
	 *            <code>true</code> means use the OSM test API, <code>false</code> means using the
	 *            OSM live API
	 * @return an {@link OsmChange} object, which contains all the changes for the given changeset
	 * @throws IOException
	 *             if the call returns no valid values, compare to <a href=
	 *             "http://wiki.openstreetmap.org/wiki/API_v0.6#Download:_GET_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fdownload"
	 *             >Error codes</a>
	 */
	public static OsmChange fetchContent(Long changeSetId, boolean develop) throws IOException {
		String apiCall;
		OsmChange result = null;

		apiCall = createApiCall(changeSetId, develop);

		try {
			result = SERIALIZER.read(OsmChange.class, apiCallResult(apiCall));
		}
		catch (Exception e) {

			throw new IOException("error with api call '" + apiCall + "', message: "
				+ e.getMessage(), e);

		}
		return result;
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
																									throws IOException,
																									Exception {
		return fetch100Youngest(createdDate, user);
	}

	/**
	 * Searches for the oldest changeset in given result. An empty result is not permitted.
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
	 * Returns the OSM-API v0.6 call for fetching almost 100 actual changesets closed after given
	 * time and created before given time. Time period is at maximum:
	 * <code>[(closed time - 24h)..(created time + 24h)]</code>
	 *
	 * @param develop
	 *            the develop
	 * @return the OSM-API v0.6 call as string, replace user, T1 and T2 with strings (%s)
	 * @see <a
	 *      href="http://wiki.openstreetmap.org/wiki/API_v0.6#Query:_GET_.2Fapi.2F0.6.2Fchangesets">OSM
	 *      API v0.6</a>
	 */
	public static String getApiCallForPeriod(boolean develop) {
		if (develop) {
			return GET_CHANGE_SETS_FOR_TIME_TIME_PERIOD_DEV;
		}
		else {
			return GET_CHANGE_SETS_FOR_TIME_PERIOD;
		}
	}

	/**
	 * an example for using methods of this class, storing is commented out, more about storing you
	 * finde here: {@linkplain StoreChangeSets#storeWithContentToFolder(Map, String, boolean)}
	 *
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		Map<Long, ChangeSet> changeSets;
		String user;

		// configure Log4j
		BasicConfigurator.configure();

		try {
			// it's me :-), replace by your OSM account
			user = "roald-linus";

			user = WHEELMAP_VISITOR;

			// use the live-OSM-API, since May 2013 my live user works no more
			// on the test API
			develop = false;

			// fetch the changesets in a given time period
			// pay attention to the confusing month indexing set by the Java
			// Calendar API
			changeSets =
				fetchChanges(user, new GregorianCalendar(2012, 11, 31), new GregorianCalendar(2012,
						1, 1));

			// print out some parameters of the fetch result
			LOGGER.info(changeSets.size() + " changesets fetched for " + user + " in "
				+ (develop ? "DEVELOP" : "live") + " mode");

			// omit the storing, if you want to to store the result remove
			// comments, but first take a look at JavaDoc of
			// StoreChangeSets.storeWithContent(..) !!!

			storeWithContent(changeSets, "wheelchair-2012.zip", develop);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		LOGGER.info("successfully completed");
	}

	/**
	 * Stores given map of changesets and their content to given zip file. The changeset will be
	 * stored to a file "[changesetId].xml", representing an {@linkplain ChangeSet} object.
	 * <p>
	 * All the changes of the changeset will be stored to a file "[changesetId]_content.xml",
	 * representing an {@linkplain OsmChange} object.
	 * <p>
	 * The root folder will be the name of the given zip file reduced the extension ".zip".
	 * <p>
	 * The content for given changeset will be fetched from OSM server, <strong>so the call of this
	 * method could take a longer time</strong>. For monitoring the progress the
	 * {@linkplain #LOGGER} is used. Any main class should configure the logger, the easiest way is
	 * to call <code>BasicConfigurator.configure();</code> (compare to the Log4j ApiDoc).
	 * <p>
	 *
	 * @param changeSets
	 *            stored as file in given zip file, it's content also stored as file in given zip
	 *            file
	 * @param zipFileName
	 *            the name of the zip file, where changesets will be stored
	 * @param develop
	 *            with <code>true</code> the developer OSM API will be used, else the live OSM API
	 *            will be used
	 * @throws IOException
	 *             if the call returns no valid values
	 */
	public static void storeWithContent(Map<Long, ChangeSet> changeSets, String zipFileName,
										boolean develop) throws IOException {

		String folder, zip;

		if (!zipFileName.endsWith(".zip")) {
			zip = zipFileName + ".zip";
		}
		else {
			zip = zipFileName;
		}

		folder = zip.replace(".zip", "");

		try (ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(zip))) {

			for (Entry<Long, ChangeSet> changeSet : changeSets.entrySet()) {
				try {
					ZipEntry folderForChangeSetEntry;
					OsmChange changeSetContent;

					// store the changeset file
					folderForChangeSetEntry =
						new ZipEntry(folder + "/" + changeSet.getKey() + ".xml");
					zipFile.putNextEntry(folderForChangeSetEntry);

					SERIALIZER.write(changeSet.getValue(), zipFile);
					zipFile.closeEntry();

					// store the changesets content
					changeSetContent = fetchContent(changeSet.getKey(), develop);
					folderForChangeSetEntry =
						new ZipEntry(folder + "/" + changeSet.getKey()
							+ ChangeSetContentFileFilter.CHANGE_SET_CONTENT_LABEL + ".xml");
					zipFile.putNextEntry(folderForChangeSetEntry);
					SERIALIZER.write(changeSetContent, zipFile);

					zipFile.closeEntry();

					LOGGER.info("change set 'id=" + changeSet.getKey() + "' stored to zip file");
				}
				catch (Exception e) {
					LOGGER.warn("can't store content and changeset for id = " + changeSet.getKey()
						+ " from " + changeSet.getValue().getCreatedAt()
						+ ", changeset will be omitted", e);
				}
			}

			// note: don't call close, should be done by surrounding "try with
			// resources"
			zipFile.finish();

		}
		catch (IOException e) {
			throw new IOException("can't create zip file '" + zipFileName + "', reason: ", e);
		}
	}

	/**
	 * Stores given map of changesets and their content to given folder. The changeset will be
	 * stored to a file "[changesetId].xml", representing an {@linkplain ChangeSet} object.
	 * <p>
	 * all the changes of the changeset will be stored to one file "[changesetId]_content.xml",
	 * representing an {@linkplain OsmChange} object.
	 * <p>
	 * The content for given changeset will be fetched from OSM server, <strong>so the call of this
	 * method could take a longer time</strong>. For monitoring the progress the
	 * {@linkplain #LOGGER} is used. Any main class should configure the logger, the easiest way is
	 * to call <code>BasicConfigurator.configure();</code> (compare to the Log4j ApiDoc).
	 * <p>
	 *
	 * @param changeSets
	 *            stored as file ("[changesetId].xml") to given folder
	 * @param folderName
	 *            where the changesets and its changes will be stored
	 * @param develop
	 *            with <code>true</code> the developer OSM API will be used, else the live OSM API
	 *            will be used
	 * @throws IOException
	 *             if the call returns no valid values
	 */
	public static void storeWithContentToFolder(Map<Long, ChangeSet> changeSets, String folderName,
												boolean develop) throws IOException {

		try {
			if (!(new File(folderName)).isDirectory()) {
				if (!new File(folderName).mkdir()) {
					throw new IOException("can't create folder '" + folderName + "'");
				}
			}

			for (Entry<Long, ChangeSet> changeSet : changeSets.entrySet()) {

				OsmChange content;
				String fileName = folderName + "/" + changeSet.getKey().toString() + ".xml";
				try {
					// store the changeset file
					SERIALIZER.write(changeSet.getValue(), new File(fileName));

				}
				catch (Exception e) {
					throw new IOException("can't store changeset 'id=" + changeSet.getKey()
						+ "'  '" + fileName + "', reason: ", e);
				}

				// store the changeset content
				content = fetchContent(changeSet.getKey(), develop);
				String contentFileName =
					folderName + "/" + changeSet.getKey().toString()
						+ ChangeSetContentFileFilter.CHANGE_SET_CONTENT_LABEL + ".xml";
				try {
					SERIALIZER.write(content, new File(contentFileName));
				}
				catch (Exception e) {
					throw new IOException("can't put content for changeset 'id="
						+ changeSet.getKey() + "'  file '" + contentFileName + "', reason: ", e);
				}

				LOGGER.info("change set 'id=" + changeSet.getKey() + "' stored");
			}

		}
		catch (IOException e) {
			throw new IOException("can't store to folder '" + folderName + "', reason: ", e);
		}
	}

	/**
	 * Fetches the first 100 changesets which are older then createdDate set by object construction.
	 *
	 * @param createdDate
	 *            the created date
	 * @param user
	 *            the user
	 * @return a map with changesets as value and their id's as key. Size could be within 0 and 100.
	 * @throws IOException
	 *             network error
	 * @throws Exception
	 *             error on parsing the API result
	 */
	private static Map<Long, ChangeSet> fetch100Youngest(Calendar createdDate, String user)
																							throws IOException,
																							Exception {
		String apiCall;
		OsmApiChangeSetsResult apiResult;

		apiCall =
			String.format(	getApiCallForClosedAfterGivenTime(develop), user,
							ChangeSetToolkit.OSM_DATE_TO_JAVA.format(createdDate.getTime()));

		apiResult =
			API_RESPONSE_SERIALIZER.read(OsmApiChangeSetsResult.class, apiCallResult(apiCall));

		return apiResult.asMap();
	}

	/**
	 * Fetches at most 100 changesets which are at maximum 48 hours older then given createdDate.
	 * The closedDate will be set to createdDate - 48 hours
	 * <p>
	 *
	 * @param createdDate
	 *            fetched changesets will be older then createdDate
	 * @param user
	 *            the user
	 * @return a map with changesets als values and their id's as key, size is within 0 and 100,
	 *         where 100 ist set as border by the OSM-API, so if size == 100 you should reduce the
	 *         next createdDate at 1 hour for skipping the time window one hour back an get really
	 *         all changesets
	 * @throws IOException
	 *             network error
	 * @throws Exception
	 *             error on parsing the result
	 * @see <a
	 *      href="http://wiki.openstreetmap.org/wiki/API_v0.6#Query:_GET_.2Fapi.2F0.6.2Fchangesets">Osm
	 *      Api06 wiki for Query: GET /api/0.6/changesets</a>
	 */
	private static Map<Long, ChangeSet> fetchOlderChangeSets(Calendar createdDate, String user)
																								throws IOException,
																								Exception {
		Calendar closed, created;
		OsmApiChangeSetsResult apiResult;
		String apiCall;
		String createdT2, closedT1;
		created = (Calendar) createdDate.clone();
		closed = (Calendar) created.clone();
		closed.add(Calendar.HOUR, -TWO_DAYS_IN_HOURS);

		assert closed.before(created) : "closed date must be before created date, otherwise you don't have a time window";

		createdT2 = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(created.getTime());
		closedT1 = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(closed.getTime());

		apiCall = String.format(getApiCallForPeriod(develop), user, closedT1, createdT2);

		apiResult =
			API_RESPONSE_SERIALIZER.read(OsmApiChangeSetsResult.class, apiCallResult(apiCall));

		return apiResult.asMap();
	}

	/**
	 * Returns OSM-API v0.6 call for fetching almost 100 actual changesets closed after given time.
	 * The oldest could (closed time - 24h). * @param devolop with <code>true</code> the OSM
	 * developer API is used, else the live API
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
		}
		else {
			return GET_CHANGE_SETS_CLOSED_AFTER;
		}
	}

	/**
	 * it's private to prevent instantiation.
	 */
	private FetchingChangeSetsToolbox() {}
}
