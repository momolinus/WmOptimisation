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
package org.athmis.wmoptimisation.algorithm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.changeset.OsmChange;
import org.athmis.wmoptimisation.filefilter.ChangeSetContentFileFilter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * A ChangeSetZipContentData contains changesets (as {@linkplain ChangeSet}
 * objects in a map) and their changes ({@linkplain OsmChange}).
 * 
 * @author Marcus Bleil<br>
 *         http://www.marcusbleil.de
 * 
 */
public class ChangeSetZipContentData {

	static Logger LOGGER = Logger.getLogger(ChangeSetZipContentData.class);

	private List<OsmChange> changes;
	private Map<Long, ChangeSet> changeSets;

	public ChangeSetZipContentData() {
		changeSets = new HashMap<>();
		changes = new ArrayList<>();
	}

	public ChangeSet add(ChangeSet changeSet) {
		return changeSets.put(Long.valueOf(changeSet.getId()), changeSet);
	}

	// XXX wenn OsmChange mal equals() unterstützt kann geprüft werden, ob das
	// object schon gespeichert war
	public void add(OsmChange changeContent) {
		changes.add(changeContent);
	}

	public int size() {
		return changes.size() + changeSets.size();
	}

	public String getAreasAsCSV(String header) {
		StringBuilder result;
		result = new StringBuilder();

		result.append(header);
		result.append("\n");

		Iterator<ChangeSet> chs = changeSets.values().iterator();
		while (chs.hasNext()) {
			result.append(String.format("%.12f", chs.next().getArea()));
			if (chs.hasNext())
				result.append("\n");
		}

		return result.toString();
	}

	/**
	 * 
	 * @return
	 * @throws ParseException
	 *             in case of syntax error in date or time string of OSM raw
	 *             data
	 */
	public String asTable() throws ParseException {
		StringBuilder table;
		table = new StringBuilder();

		table.append("id;user;closed;opentime;area");
		table.append("\n");

		Iterator<ChangeSet> chs = changeSets.values().iterator();
		while (chs.hasNext()) {
			ChangeSet chSet = chs.next();

			table.append(chSet.getId());
			table.append(";");
			table.append(chSet.getUser());
			table.append(";");
			table.append(String.format("%tF", chSet.getClosed()));
			table.append(";");
			table.append(String.format("%.12f", chSet.getOpenTimeInHours()));
			table.append(";");
			table.append(String.format("%.12f", chSet.getArea()));

			if (chs.hasNext())
				table.append("\n");
		}

		return table.toString();

	}

	public List<Change> getAllChanges() {
		List<Change> allChanges;

		allChanges = new ArrayList<>();

		for (OsmChange osmChange : changes) {
			allChanges.addAll(osmChange.getChanges());
		}

		return allChanges;

	}

	/**
	 * Adds the given change to given changeset. Stores both objects. It Could
	 * be, that given changeset is still stored, then it will not be stored
	 * again as copy or so.
	 * 
	 * @param change
	 *            stores this new change
	 * @param changeSet
	 *            used for storing the change, stored also to this object
	 */
	public void addChangeForChangeSet(Change change, ChangeSet changeSet) {
		long changesetId;
		ChangeSet destChange;

		changesetId = changeSet.getId();
		if (changeSets.containsKey(Long.valueOf(changesetId))) {
			destChange = changeSets.get(Long.valueOf(changesetId));
		} else {
			destChange = changeSet;
			changeSets.put(Long.valueOf(destChange.getId()), destChange);
		}

		change.setChangeset(destChange.getId());

		if (changes.size() == 0) {
			changes.add(new OsmChange());
		}

		changes.get(changes.size() - 1).addChange(change);
	}

	public static ChangeSetZipContentData readOsmChangeContent(String zipFileName)
			throws IOException {
		Serializer serializer;
		ChangeSetZipContentData result;

		serializer = new Persister();
		result = new ChangeSetZipContentData();

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
