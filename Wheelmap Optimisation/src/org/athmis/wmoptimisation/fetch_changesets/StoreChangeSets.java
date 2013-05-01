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
package org.athmis.wmoptimisation.fetch_changesets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.changeset.OsmChange;
import org.athmis.wmoptimisation.filefilter.ChangeSetContentFileFilter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * 
 * @author Marcus Bleil<br>
 *         http://www.marcusbleil.de
 * 
 */
public class StoreChangeSets {

	/**
	 * the call for downloading changes for a given changeset id, see <a href=
	 * "http://wiki.openstreetmap.org/wiki/API_v0.6#Download:_GET_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fdownload"
	 * >Download: GET /api/0.6/changeset/#id/download</a>
	 */
	public static final String GET_CHANGE_SET_DOWNLOAD = "http://api.openstreetmap.org/"
			+ "api/0.6/changeset/%s/download";

	/**
	 * the test call for downloading changes for a given changeset id, see <a
	 * href=
	 * "http://wiki.openstreetmap.org/wiki/API_v0.6#Download:_GET_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fdownload"
	 * >Download: GET /api/0.6/changeset/#id/download</a>
	 */
	public static final String GET_CHANGE_SET_DOWNLOAD_DEV = "http://api06.dev.openstreetmap.org/"
			+ "api/0.6/changeset/%s/download";

	/**
	 * the default logger, use for developing and monitoring tasks
	 */
	private static final Logger LOGGER = Logger.getLogger(StoreChangeSets.class);

	/**
	 * the default serializer, use for all serializing and deserializing tasks
	 */
	private static final Serializer SERIALIZER = new Persister();

	/**
	 * Fetches all the changes (and their content) for a given changeset id.
	 * 
	 * @param changeSetId
	 *            the if of the changeset
	 * @param develop
	 *            <code>true</code> means use the OSM test API,
	 *            <code>false</code> means using the OSM live API
	 * @return an {@link OsmChange} object, which contains all the changes for
	 *         the given changeset
	 * @throws IOException
	 *             if the call returns no valid values, compare to <a href=
	 *             "http://wiki.openstreetmap.org/wiki/API_v0.6#Download:_GET_.2Fapi.2F0.6.2Fchangeset.2F.23id.2Fdownload"
	 *             >Error codes</a>
	 */
	public static OsmChange fetchContent(Long changeSetId, boolean develop) throws IOException {
		String apiCall;
		URL url;
		OsmChange result = null;

		apiCall = createApiCall(changeSetId, develop);
		try {
			url = new URL(apiCall);
			result = SERIALIZER.read(OsmChange.class, url.openStream());
		} catch (Exception e) {
			throw new IOException("error with api call '" + apiCall + "'", e);
		}
		return result;
	}

	/**
	 * Stores given map of changesets and their content to given zip file. The
	 * changeset will be stored to a file "[changesetId].xml", representing an
	 * {@linkplain ChangeSet} object.
	 * <p>
	 * All the changes of the changeset will be stored to a file
	 * "[changesetId]_content.xml", representing an {@linkplain OsmChange}
	 * object.
	 * <p>
	 * The root folder will be the name of the given zip file reduced the
	 * extension ".zip".
	 * <p>
	 * The content for given changeset will be fetched from OSM server,
	 * <strong>so the call of this method could take a longer time</strong>. For
	 * monitoring the progress the {@linkplain #LOGGER} is used. Any main class
	 * should configure the logger, the easiest way is to call
	 * <code>BasicConfigurator.configure();</code> (compare to the Log4j
	 * ApiDoc).
	 * <p>
	 * 
	 * @param changeSets
	 *            stored as file in given zip file, it's content also stored as
	 *            file in given zip file
	 * @param zipFileName
	 *            the name of the zip file, where changesets will be stored
	 * @param develop
	 *            with <code>true</code> the developer OSM API will be used,
	 *            else the live OSM API will be used
	 * @throws IOException
	 *             if the call returns no valid values
	 */
	public static void storeWithContent(Map<Long, ChangeSet> changeSets, String zipFileName,
			boolean develop) throws IOException {

		String folder, zip;

		if (!zipFileName.endsWith(".zip")) {
			zip = zipFileName + ".zip";
		} else {
			zip = zipFileName;
		}

		folder = zip.replace(".zip", "");

		try (ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(zip))) {

			for (Entry<Long, ChangeSet> changeSet : changeSets.entrySet()) {
				ZipEntry entry;
				OsmChange content;

				// store the changeset file
				entry = new ZipEntry(folder + "/" + changeSet.getKey() + ".xml");
				zipFile.putNextEntry(entry);
				try {
					SERIALIZER.write(changeSet.getValue(), zipFile);
					zipFile.closeEntry();
				} catch (Exception e) {
					throw new IOException("can't put changeset 'id=" + changeSet.getKey()
							+ "' to zip file '" + zipFileName + "', reason: ", e);
				}

				// store the changeset content
				content = fetchContent(changeSet.getKey(), develop);
				entry = new ZipEntry(folder + "/" + changeSet.getKey()
						+ ChangeSetContentFileFilter.CHANGE_SET_CONTENT_LABEL + ".xml");
				zipFile.putNextEntry(entry);
				try {
					SERIALIZER.write(content, zipFile);
					zipFile.closeEntry();
				} catch (Exception e) {
					throw new IOException("can't put content for changeset 'id="
							+ changeSet.getKey() + "' to zip file '" + zipFileName + "', reason: ",
							e);
				}

				LOGGER.info("change set 'id=" + changeSet.getKey() + "' stored to zip file");
			}

			// note: don't call close, should be done by surrounding "try with
			// resources"
			zipFile.finish();

		} catch (IOException e) {
			throw new IOException("can't create zip file '" + zipFileName + "', reason: ", e);
		}
	}

	/**
	 * Stores given map of changesets and their content to given folder. The
	 * changeset will be stored to a file "[changesetId].xml", representing an
	 * {@linkplain ChangeSet} object.
	 * <p>
	 * all the changes of the changeset will be stored to one file
	 * "[changesetId]_content.xml", representing an {@linkplain OsmChange}
	 * object.
	 * <p>
	 * The content for given changeset will be fetched from OSM server,
	 * <strong>so the call of this method could take a longer time</strong>. For
	 * monitoring the progress the {@linkplain #LOGGER} is used. Any main class
	 * should configure the logger, the easiest way is to call
	 * <code>BasicConfigurator.configure();</code> (compare to the Log4j
	 * ApiDoc).
	 * <p>
	 * 
	 * @param changeSets
	 *            stored as file ("[changesetId].xml") to given folder
	 * @param folderName
	 *            where the changesets and its changes will be stored
	 * @param develop
	 *            with <code>true</code> the developer OSM API will be used,
	 *            else the live OSM API will be used
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

				} catch (Exception e) {
					throw new IOException("can't store changeset 'id=" + changeSet.getKey()
							+ "'  '" + fileName + "', reason: ", e);
				}

				// store the changeset content
				content = fetchContent(changeSet.getKey(), develop);
				String contentFileName = folderName + "/" + changeSet.getKey().toString()
						+ ChangeSetContentFileFilter.CHANGE_SET_CONTENT_LABEL + ".xml";
				try {
					SERIALIZER.write(content, new File(contentFileName));
				} catch (Exception e) {
					throw new IOException("can't put content for changeset 'id="
							+ changeSet.getKey() + "'  file '" + contentFileName + "', reason: ", e);
				}

				LOGGER.info("change set 'id=" + changeSet.getKey() + "' stored");
			}

		} catch (IOException e) {
			throw new IOException("can't store to folder '" + folderName + "', reason: ", e);
		}
	}

	/**
	 * Build the OSM API call for fetching the changes referring to a given
	 * changeset id.
	 * 
	 * @param changeSetId
	 *            the if of the changeset which changes should be downloaded
	 * @param develop
	 *            <code>true</code> means use the OSM test API,
	 *            <code>false</code> means using the OSM live API
	 * @return the API call
	 */
	private static String createApiCall(Long changeSetId, boolean develop) {
		if (develop)
			return String.format(GET_CHANGE_SET_DOWNLOAD_DEV, changeSetId.toString());
		else
			return String.format(GET_CHANGE_SET_DOWNLOAD, changeSetId.toString());
	}

	/**
	 * no instance useful, class provides only helper methods
	 */
	private StoreChangeSets() {

	}
}
