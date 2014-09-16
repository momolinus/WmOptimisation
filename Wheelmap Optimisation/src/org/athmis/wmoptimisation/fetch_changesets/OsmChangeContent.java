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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.*;
import org.athmis.wmoptimisation.filefilter.ChangeSetContentFileFilter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * A OsmChangeContent contains changesets (as {@linkplain ChangeSet} objects in a map) and their
 * changes ({@linkplain OsmChange}).
 * <p>
 * The first implementation of this class was only used to be filled with changes from a given zip
 * file. This zip-file contained changes/edits from real osm users, which fetched with OSM Api 0.6.
 */
public class OsmChangeContent {

	private static final DateFormat FORMATTER = new SimpleDateFormat();

	private final static Logger LOGGER = Logger.getLogger(OsmChangeContent.class);

	private final static Serializer SERIALIZER = new Persister();

	/**
	 * Reads a zip file with changesets files and changeset content files
	 *
	 * @param zipFileName
	 * @return changesets and changesets content object
	 * @throws IOException
	 *             on error reading file, like parse errors
	 */
	public static OsmChangeContent createOsmChangeContentFromZip(String zipFileName)
																					throws IOException {

		OsmChangeContent changeContent;
		changeContent = new OsmChangeContent();

		// note: ZipFile implements AutoCloseable
		try (ZipFile changeSetsZip =
			new ZipFile(Paths.get(zipFileName).toFile(), ZipFile.OPEN_READ)) {

			readZipFile(zipFileName, changeContent, changeSetsZip);

		}
		catch (IOException e) {
			throw new IOException("can't read OsmChange file '" + zipFileName + "' from zip-file '"
				+ zipFileName + "', reason: ", e);
		}

		return changeContent;
	}

	public static OsmChangeContent readOsmChangeContent(String[] ids) throws Exception {

		OsmChangeContent changeContent;
		changeContent = new OsmChangeContent();

		for (String id : ids) {
			Path changeSetPath, changesPath;

			changeSetPath = Paths.get(id + ".xml");
			assertThatPathExists(changeSetPath);

			changesPath =
				Paths.get(id + ChangeSetContentFileFilter.CHANGE_SET_CONTENT_LABEL + ".xml");
			assertThatPathExists(changesPath);

			ChangeSet changeSet = SERIALIZER.read(ChangeSet.class, changeSetPath.toFile());
			changeContent.add(new CangeSetUpdateAble(changeSet));

			OsmChange changes = SERIALIZER.read(OsmChange.class, changesPath.toFile());
			changeContent.add(changes);
		}

		return changeContent;
	}

	// XXX anders formatieren
	private static void addChangeSetFromZipFile(OsmChangeContent result, InputStream chnageSetStream)
																										throws Exception {

		ChangeSet changeSet;

		changeSet = SERIALIZER.read(ChangeSet.class, chnageSetStream);

		if (result.add(new CangeSetUpdateAble(changeSet)) != null) {
			// TODO prüfen ob das sein kann und wenn ja, kommentieren warum
			LOGGER.warn("changeSet 'id=" + changeSet.getId() + "' was stored before");
		}
	}

	private static void addChangesFromZipFile(OsmChangeContent changeContent,
												InputStream changesStream) throws Exception {

		OsmChange changeSetContent;

		changeSetContent = SERIALIZER.read(OsmChange.class, changesStream);
		changeContent.add(changeSetContent);

	}

	/**
	 * @throws IOException
	 *             if given path not exists
	 */
	private static void assertThatPathExists(Path changeSet) throws IOException {
		if (!Files.exists(changeSet)) {
			throw new IOException("can't find file " + changeSet.toString());
		}

	}

	private static void readZipFile(String zipFileName, OsmChangeContent changeContent,
									ZipFile zipFile) throws IOException {

		for (ZipEntry zipEntry : Collections.list(zipFile.entries())) {

			try {

				InputStream zipEntryStream = zipFile.getInputStream(zipEntry);

				if (zipEntry.getName()
						.contains(ChangeSetContentFileFilter.CHANGE_SET_CONTENT_LABEL)) {
					addChangesFromZipFile(changeContent, zipEntryStream);
				}
				else {
					addChangeSetFromZipFile(changeContent, zipEntryStream);
				}

			}
			catch (Exception e) {
				throw new IOException("can't read entry " + zipEntry.getName() + " from zip-file "
					+ zipFile.getName() + ", reason: " + e.getLocalizedMessage(), e);
			}
		}
	}

	private List<OsmChange> changes;
	/**
	 * a map with all changesets as value and changesets id as key
	 */
	private Map<Long, CangeSetUpdateAble> changeSets;

	private String header = "missing";

	/**
	 * Constructs an empty OsmChangeContent object. It has an empty map for {@linkplain ChangeSet}
	 * and an empty list for {@linkplain OsmChange}.
	 */
	public OsmChangeContent() {
		changeSets = new HashMap<>();
		changes = new ArrayList<>();
	}

	/**
	 * Stores given changeset in internal map with changesets id as key and the changeset object as
	 * value.
	 *
	 * @param changeSet
	 * @return <code>null</code> if no value for changeset id was stored, or previous stored
	 *         changeset, which usually seem to be an error
	 */
	public ChangeSet add(CangeSetUpdateAble changeSet) {
		return changeSets.put(Long.valueOf(changeSet.getId()), changeSet);
	}

	// XXX wenn OsmChange mal equals() unterstützt kann geprüft werden, ob das
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

	// TODO ist das dir richtige Stelle, um zu prüfen, ob das Change gespeichert
	// werden darf?
	/**
	 * Adds the given change to given changeset. Stores both objects. It Could be, that given
	 * changeset is still stored, then it will not be stored again as copy or so.
	 *
	 * @param change
	 *            stores this new change
	 * @param changeSet
	 *            used for storing the change, stored also to this object
	 * @throws IllegalArgumentException
	 *             if change could not be stored to given chnageSet; reasons: both differ in age
	 *             more than 24 hours, this has 50,000 changes, changeset was not used for more than
	 *             one hour ore changeset is not open
	 */
	public void addChangeForChangeSet(Change change, CangeSetUpdateAble changeSet) {
		CangeSetUpdateAble changeSetForStoring;

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
	 * @return a changeset table with id, user, closed time, open time in hours, area
	 * @throws ParseException
	 *             in case of syntax error in date or time string of OSM raw data
	 */
	public String asTable() {
		StringBuilder table;
		table = new StringBuilder();

		table.append("id;user;closed;opentime;area");
		table.append("\n");

		Iterator<CangeSetUpdateAble> chs = changeSets.values().iterator();

		while (chs.hasNext()) {
			ChangeSet chSet = chs.next();

			// changeset id
			table.append(chSet.getId());
			table.append(";");

			// changeset user
			table.append(chSet.getUser());
			table.append(";");

			// changeset open state
			if (chSet.isOpen()) {
				Calendar future = Calendar.getInstance();
				future.set(Calendar.YEAR, 2099);
				table.append(String.format("%tF", future));
			}
			else {
				table.append(String.format("%tF", chSet.getClosed()));
			}
			table.append(";");

			// changeset open time in hours
			if (chSet.isOpen()) {
				table.append(String.format("%.12f", 100.0));
			}
			else {
				table.append(String.format("%.12f", chSet.getOpenTimeInHours()));
			}
			table.append(";");

			// changeset area
			table.append(String.format("%.12f", chSet.getBoundingBoxSquareDegree()));

			if (chs.hasNext()) {
				table.append("\n");
			}
		}

		return table.toString();
	}

	public void closeAllChangeSets() {

		for (CangeSetUpdateAble changeSet : changeSets.values()) {
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
	 * Returns a copy of all ways this object contains.
	 *
	 * @return copy of all ways this object contains, could be empty but not <code>null</code>
	 */
	public List<Way> getAllWays() {
		List<Way> ways;

		ways = new ArrayList<>();

		for (OsmChange change : changes) {
			for (Way way : change.getWays()) {
				ways.add(way);
			}
		}

		return ways;
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

		Iterator<CangeSetUpdateAble> chs = changeSets.values().iterator();
		while (chs.hasNext()) {
			result.append(String.format("%.12f", chs.next().getBoundingBoxSquareDegree()));
			if (chs.hasNext()) {
				result.append("\n");
			}
		}

		return result.toString();
	}

	public String getChangeSetsAsStrTableHeader() {

		return header;
	}

	public Table<Long, String, String> getChangeSets(String algorithmus) {
		Table<Long, String, String> changeSetsTable;

		changeSetsTable = HashBasedTable.create();

		for (CangeSetUpdateAble changeSet : changeSets.values()) {
			double noChanges = 0;

			changeSetsTable.put(changeSet.getId(), "user", changeSet.getUser());
			changeSetsTable.put(changeSet.getId(), "algorithm", algorithmus);
			changeSetsTable.put(changeSet.getId(), "area",
								Double.toString(changeSet.getBoundingBoxSquareDegree()));

			for (OsmChange change : changes) {
				if (change.getChangeSetId() == changeSet.getId()) {
					noChanges += change.getNumber();
				}
			}

			changeSetsTable.put(changeSet.getId(), "no_changes", Double.toString(noChanges));
		}

		return changeSetsTable;
	}

	public String getChangeSetsAsStrTable(String algorithmus, boolean withHeader) {
		StringBuilder tableBuilder = new StringBuilder();

		Table<Long, String, String> table = getChangeSets(algorithmus);

		String tempHeader;
		tempHeader = "changesetId";
		for (String column : table.columnKeySet()) {
			tempHeader += ";" + column;
		}

		if (withHeader) {
			tableBuilder.append(tempHeader + "\n");
		}

		header = tempHeader;

		for (Long changeId : table.rowKeySet()) {

			tableBuilder.append(Long.toString(changeId));

			for (Entry<String, String> row : table.row(changeId).entrySet()) {
				tableBuilder.append(";" + row.getValue());
			}

			tableBuilder.append("\n");
		}

		return tableBuilder.toString();
	}

	/**
	 * Method return a list with the area (in square degree °x°) of the changesets stored in this
	 * OsmChangeContent object.
	 *
	 * @return a list with the area (in square degree °x°) of the changesets
	 */
	public List<Double> getBoundingBoxesSquareDegree() {
		List<Double> boundingBoxes;

		boundingBoxes = new ArrayList<>();
		for (ChangeSet changeSet : changeSets.values()) {
			boundingBoxes.add(Double.valueOf(changeSet.getBoundingBoxSquareDegree()));
		}

		return boundingBoxes;
	}

	public double getMeanArea() {
		double areasSum = 0;
		int noAreas = 0;

		for (ChangeSet changeSet : changeSets.values()) {
			if (changeSet.getBoundingBoxSquareDegree() > 0) {
				noAreas++;
				areasSum += changeSet.getBoundingBoxSquareDegree();
			}
		}

		if (noAreas == 0) {
			return Double.NaN;
		}
		else {
			return areasSum / noAreas;
		}
	}

	public int getNoChangeSets() {
		return changeSets.values().size();
	}

	public int getNodes() {
		int nodes = 0;
		for (OsmChange change : changes) {
			nodes += change.getNumberNodes();
		}
		return nodes;
	}

	public int size() {
		return changes.size() + changeSets.size();
	}

	/**
	 * Returns the number of changesets this contains, the mean area of the changesets and the
	 * number of changes/edits stored in this changesets.
	 */
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
					+ FORMATTER.format(ch.getCreatedAt().getTime()));
				result.append("\n");
			}
		}

		for (Entry<Long, CangeSetUpdateAble> changeset : changeSets.entrySet()) {
			result.append(changeset.getKey().toString() + "\t"
				+ FORMATTER.format(changeset.getValue().getCreated().getTime()));
			result.append("\n");
		}

		return result.toString();
	}

	/**
	 * Looks for given changeset (by its id), if found will be returned, else it will be stored and
	 * returned. Changesets id will used for searching.
	 *
	 * @param changeSet
	 *            will be searched or will be stored as new one
	 * @return the stored changeset (compared on id) or the given changeset, which will be stored as
	 *         a new one
	 */
	private CangeSetUpdateAble fetchOrStoreAndFetchChangeset(CangeSetUpdateAble changeSet) {
		CangeSetUpdateAble changeSetForStoring;
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
	 * Sets the changeset id of given change, meaning change is stored to changeset.
	 *
	 * @param change
	 *            will be "stored" to given changeset
	 * @param changeSetForStoring
	 *            "stores" given change
	 */
	private void setChangeAsStored(Change change, CangeSetUpdateAble changeSetForStoring) {
		change.setChangeset(changeSetForStoring.getId());
		changeSetForStoring.updateBoundingBox(change);
	}

	/**
	 * Validates the storing.
	 *
	 * @param change
	 *            should be stored in given change set
	 * @param changeSetForStoring
	 *            should store given change
	 * @throws IllegalArgumentException
	 *             if change could not be stored to given changeSet; reasons: both differ in age
	 *             more than 24 hours, changeset has 50,000 changes or changeset was not used for
	 *             more than one hour
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

		// if (changeSetForStoring.)

		// FIXME missing test for 50 000 changesets, wenn implementiert, dann
		// muss der Test aus Infinitest ausgeschlossen werden
	}

}
