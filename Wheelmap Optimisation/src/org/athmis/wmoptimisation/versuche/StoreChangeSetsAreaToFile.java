package org.athmis.wmoptimisation.versuche;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.athmis.wmoptimisation.changeset.ChangeSet;
import org.athmis.wmoptimisation.filefilter.ChangeSetFileFilter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class StoreChangeSetsAreaToFile {

	/**
	 * Stores the area of a set of changesets to one text file.
	 *
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		File[] changeSetFiles;
		Serializer serializer;
		List<Double> areas = new ArrayList<Double>();

		serializer = new Persister();

		// read all changesets files in given folder
		changeSetFiles = new File("roald_linus").listFiles(new ChangeSetFileFilter());

		try {
			BufferedWriter changeSetsAreas = new BufferedWriter(new FileWriter("results/roald_linus.txt"));

			// loop over all content files
			for (File changeset : changeSetFiles) {

				ChangeSet changeSet = serializer.read(ChangeSet.class, changeset);

				System.out.println(changeset.getName() + " gespeichert");

				areas.add(Double.valueOf(changeSet.getArea()));

				changeSetsAreas.append(String.format("%f", Double.valueOf(changeSet.getArea())));
				changeSetsAreas.newLine();
			}

			changeSetsAreas.close();
			System.out.println("success for " + areas.size() + " changesets");

		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
}
