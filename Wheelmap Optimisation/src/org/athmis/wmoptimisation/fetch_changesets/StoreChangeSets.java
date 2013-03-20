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

public class StoreChangeSets {

	private static final Logger LOGGER = Logger.getLogger(StoreChangeSets.class);

	private static final Serializer SERIALIZER = new Persister();

	public static final String GET_CHANGE_SET_DOWNLOAD = "http://api.openstreetmap.org/"
			+ "api/0.6/changeset/%s/download";
	public static final String GET_CHANGE_SET_DOWNLOAD_DEV = "http://api06.dev.openstreetmap.org/"
			+ "api/0.6/changeset/%s/download";

	/**
	 * Stores given map of changesets and their content to given zip file.
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

	public static OsmChange fetchContent(Long key, boolean develop) throws IOException {
		String apiCall;
		URL url;
		OsmChange result = null;

		apiCall = createApiCall(key, develop);
		try {
			url = new URL(apiCall);
			result = SERIALIZER.read(OsmChange.class, url.openStream());
		} catch (Exception e) {
			throw new IOException("error with api call '" + apiCall + "'", e);
		}
		return result;
	}

	private static String createApiCall(Long key, boolean develop) {
		if (develop)
			return String.format(GET_CHANGE_SET_DOWNLOAD_DEV, key.toString());
		else
			return String.format(GET_CHANGE_SET_DOWNLOAD, key.toString());
	}

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
}
