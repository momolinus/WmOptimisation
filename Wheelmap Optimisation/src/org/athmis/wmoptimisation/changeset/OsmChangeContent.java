/* Copyright Marcus Bleil, Oliver Rudzik, Christoph B�nte 2012 This file is part
 * of Wheelmap Optimization. Wheelmap Optimization is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Wheelmap Optimization is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with
 * Athmis. If not, see <http://www.gnu.org/licenses/>. Diese Datei ist Teil von
 * Wheelmap Optimization. Wheelmap Optimization ist Freie Software: Sie k�nnen
 * es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder
 * sp�teren ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.
 * Wheelmap Optimization wird in der Hoffnung, dass es n�tzlich sein wird, aber
 * OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License f�r weitere Details. Sie sollten eine
 * Kopie der GNU General Public License zusammen mit diesem Programm erhalten
 * haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>. */
package org.athmis.wmoptimisation.changeset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.filefilter.ChangeSetContentFileFilter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * A ChangeSetZipContentData contains changesets (as {@linkplain ChangeSet}
 * objects in a map) and their changes ({@linkplain OsmChange}).
 */
public class OsmChangeContent {

	private static final DateFormat formatter = new SimpleDateFormat();
	private final static Logger LOGGER = Logger.getLogger(OsmChangeContent.class);

	/**
	 * Reads a zip file with changesets files and changeset content files
	 * 
	 * @param zipFileName
	 * @return changesets and changesets content object
	 * @throws IOException
	 *             on error reading file, like parse errors
	 */
	public static OsmChangeContent readOsmChangeContent(String zipFileName) throws IOException {
		Serializer serializer;
		OsmChangeContent result;

		serializer = new Persister();
		result = new OsmChangeContent();

		// note: ZipFile implements AutoCloseable
		try (ZipFile changeSetsZip =
			new ZipFile(Paths.get(zipFileName).toFile(), ZipFile.OPEN_READ)) {
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

					}
					catch (Exception e) {
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

					}
					catch (Exception e) {
						throw new IOException("can't read changeset file '" + zipEntry.getName()
							+ "' from zip-file '" + zipFileName + "', reason: ", e);
					}

				}
			}

		}
		catch (IOException e) {
			throw new IOException("can't read OsmChange file '" + zipFileName + "' from zip-file '"
				+ zipFileName + "', reason: ", e);

		}
		System.out.println();
		return result;
	}

	private List<OsmChange> changes;

	private Map<Long, ChangeSet> changeSets;

	/**
	 * Constructs an empty ChangeSetZipContentData object. It has an empty map
	 * for {@linkplain ChangeSet} and an empty list for {@linkplain OsmChange}.
	 */
	public OsmChangeContent() {
		changeSets = new HashMap<>();
		changes = new ArrayList<>();
	}

	/**
	 * Stores given changeset in internal map with changesets id as key and the
	 * changeset object as value.
	 * 
	 * @param changeSet
	 * @return <code>null</code> if no value for changeset id was stored, or
	 *         previous stored changeset, which usually seem to be an error
	 */
	public ChangeSet add(ChangeSet changeSet) {
		return changeSets.put(Long.valueOf(changeSet.getId()), changeSet);
	}

	// XXX wenn OsmChange mal equals() unterst�tzt kann gepr�ft werden, ob das
	// object schon gespeichert war
	/**
	 * Adds given OsmChange object.
	 * 
	 * @param changeContent
	 *            will be stored to internal list
	 */
	public void add(OsmChange changeContent) {
		changes.add(changeContent);
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
		ChangeSet changeSetForStoring;

		changeSetForStoring = fetchOrStoreAndFetchChangeset(changeSet);

		validateIsStoringPossible(change, changeSetForStoring);

		setChangeAsStored(change, changeSetForStoring);

		if (changes.size() == 0) {
			changes.add(new OsmChange());
		}

		// add change to last OsmChange
		changes.get(changes.size() - 1).addChange(change);
	}

	/**
	 * Returns changesets as table.
	 * 
	 * @return a changeset table with id, user, closed time, open time in hours,
	 *         area
	 * @throws ParseException
	 *             in case of syntax error in date or time string of OSM raw
	 *             data
	 */
	public String asTable() {
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

			if (chSet.isOpen()) {
				Calendar future = Calendar.getInstance();
				future.set(Calendar.YEAR, 2099);
				table.append(String.format("%tF", future));
			}
			else
				table.append(String.format("%tF", chSet.getClosed()));

			table.append(";");

			if (chSet.isOpen()) {
				table.append(String.format("%.12f", 100.0));
			}
			else
				table.append(String.format("%.12f", chSet.getOpenTimeInHours()));

			table.append(";");
			table.append(String.format("%.12f", chSet.getArea()));

			if (chs.hasNext())
				table.append("\n");
		}

		return table.toString();
	}

	public void closeAllChangeSets() {

		for (ChangeSet changeSet : changeSets.values()) {
			if (changeSet.isOpen()) {
				changeSet.closeNow();
			}
		}

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
	 * Returns the areas of all changesets as a table.
	 * 
	 * @param header
	 *            used as header for the result table
	 * @return areas as table string
	 */
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

	public List<Double> getAreas() {
		List<Double> areas;

		areas = new ArrayList<>();
		for (ChangeSet changeSet : changeSets.values()) {
			areas.add(Double.valueOf(changeSet.getArea()));
		}

		return areas;
	}

	public int size() {
		return changes.size() + changeSets.size();
	}

	@Override
	public String toString() {
		double meanArea;
		int changesNum;

		meanArea = ChangeSetToolkit.meanArea(changeSets.values());

		changesNum = 0;
		for (OsmChange changesContent : changes) {
			changesNum += changesContent.getNumberCreated();
			changesNum += changesContent.getNumberModified();
		}

		return "contains " + changeSets.entrySet().size() + " changesets with mean area = "
			+ Double.toString(meanArea) + " and " + changesNum + " changes";
	}

	public String verbose() throws ParseException {
		StringBuilder result = new StringBuilder();

		for (OsmChange c : changes) {
			for (Change ch : c.getChanges()) {
				result.append(ch.getChangeset() + "\t"
					+ formatter.format(ch.getCreatedAt().getTime()));
				result.append("\n");
			}
		}

		for (Entry<Long, ChangeSet> changeset : changeSets.entrySet()) {
			result.append(changeset.getKey().toString() + "\t"
				+ formatter.format(changeset.getValue().getCreated().getTime()));
			result.append("\n");
		}

		return result.toString();
	}

	/**
	 * Looks for given changeset (by its is), if found will be returned, else it
	 * will be stored and returned. Changesets id will used for searching.
	 * 
	 * @param changeSet
	 *            will be searched or will be stored as new one
	 * @return the stored changeset (compared on id) or the given changeset,
	 *         which will be stored as a new one
	 */
	private ChangeSet fetchOrStoreAndFetchChangeset(ChangeSet changeSet) {
		ChangeSet changeSetForStoring;
		long changesetId;

		changesetId = changeSet.getId();
		if (changeSets.containsKey(Long.valueOf(changesetId))) {
			changeSetForStoring = changeSets.get(Long.valueOf(changesetId));
		}
		else {
			changeSetForStoring = changeSet;
			changeSets.put(Long.valueOf(changeSetForStoring.getId()), changeSetForStoring);
		}
		return changeSetForStoring;
	}

	/**
	 * Sets the changeset id of given change, meaning change is stored to
	 * changeset.
	 * 
	 * @param change
	 *            will be "stored" to given changeset
	 * @param changeSetForStoring
	 *            "stores" given change
	 */
	private void setChangeAsStored(Change change, ChangeSet changeSetForStoring) {
		change.setChangeset(changeSetForStoring.getId());
		changeSetForStoring.updateArea(change);
	}

	/**
	 * Validates the storing.
	 * 
	 * @param change
	 *            should be stored in given change set
	 * @param changeSetForStoring
	 *            should store given change
	 * @throws IllegalArgumentException
	 *             if changes could not be stored in given change set
	 */
	private void validateIsStoringPossible(Change change, ChangeSet changeSetForStoring) {

		Calendar changeCreated, changeSetCreated;
		int ageDiff;

		changeCreated = change.getCreatedAt();
		changeSetCreated = changeSetForStoring.getCreated();

		ageDiff = changeCreated.compareTo(changeSetCreated);

		if (ageDiff < 0) {
			throw new IllegalArgumentException("change " + change.verbose()
				+ " is older than change set " + changeSetForStoring.verbose()
				+ ", can't store change");
		}

		if (TimeUnit.MILLISECONDS.toHours(ageDiff) >= 24) {
			throw new IllegalArgumentException("change " + change.verbose()
				+ "is >= 24 younger than change set " + changeSetForStoring.verbose()
				+ ", can't store change");
		}

		// FIXME missing test for 50 000 changesets
	}
}