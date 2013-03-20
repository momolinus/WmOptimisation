/**
 * 
 */
package org.athmis.wmoptimisation.versuche;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.changeset.OsmChange;
import org.athmis.wmoptimisation.filefilter.ChangeSetContentFileFilter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * @author Marcus
 * 
 */
public class ReadFromZip {

	private static Logger LOGGER = Logger.getLogger(ReadFromZip.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// configure Log4j
		BasicConfigurator.configure();
		ChangeSetZipContentData changes;
		String zipFileName;
		zipFileName = "changesets.zip";

		try {
			changes = readOsmChangeContent(zipFileName);
			System.out.println(changes.getAreasForR());
			LOGGER.info(changes.size() + " OsmChange objects extracted");
		} catch (IOException e) {
			LOGGER.error("error reading zip file '" + zipFileName + "'", e);
		}
	}

	public static ChangeSetZipContentData readOsmChangeContent(String zipFileName)
			throws IOException {
		Serializer serializer;
		ChangeSetZipContentData result;

		serializer = new Persister();
		result = new ChangeSetZipContentData();
		;

		// note: ZipFile implements AutoCloseable
		try (ZipFile changeSetsZip = new ZipFile(Paths.get(zipFileName).toFile(), ZipFile.OPEN_READ)) {
			int changesCounter = 0, changeSetsCounter = 0;
			for (ZipEntry zipEntry : Collections.list(changeSetsZip.entries())) {

				if (zipEntry.getName()
						.contains(ChangeSetContentFileFilter.CHANGE_SET_CONTENT_LABEL)) {
					InputStream changeSetContentStream;
					OsmChange changeSetContent;

					changeSetContentStream = changeSetsZip.getInputStream(zipEntry);
					try {
						changeSetContent = serializer.read(OsmChange.class, changeSetContentStream);
						result.add(changeSetContent);

						System.out.print(".");
						changesCounter++;
						if (changesCounter % 80 == 0)
							System.out.println();

					} catch (Exception e) {
						throw new IOException("can't read OsmChange file '" + zipEntry.getName()
								+ "' from zip-file '" + zipFileName + "', reason: ", e);
					}
				}
				// should be a changeset file
				else {
					InputStream changeSetStream;
					ChangeSet changeSet;

					changeSetStream = changeSetsZip.getInputStream(zipEntry);
					try {
						changeSet = serializer.read(ChangeSet.class, changeSetStream);
						if (result.add(changeSet) != null) {
							LOGGER.info("changeSet 'id=" + changeSet.getId()
									+ "' was stored before");
						}

						System.out.print(".");
						changeSetsCounter++;
						if (changeSetsCounter % 80 == 0)
							System.out.println();

					} catch (Exception e) {
						throw new IOException("can't read changeset file '" + zipEntry.getName()
								+ "' from zip-file '" + zipFileName + "', reason: ", e);
					}

				}
			}

		} catch (IOException e) {
			throw new IOException("can't read OsmChange file '" + zipFileName + "' from zip-file '"
					+ zipFileName + "', reason: ", e);

		}
		System.out.println();
		return result;
	}
}
