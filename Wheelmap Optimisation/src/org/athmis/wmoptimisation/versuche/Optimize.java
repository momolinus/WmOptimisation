package org.athmis.wmoptimisation.versuche;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.athmis.wmoptimisation.algos.ChangeSetGenerator;
import org.athmis.wmoptimisation.algos.SimpleChangeSetGenerator;

public class Optimize {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// configure Log4j
		BasicConfigurator.configure();
		
		try {
			ChangeSetGenerator generator;
			ChangeSetZipContentData changesFromZip, optimizedChangeSet;

			generator = new SimpleChangeSetGenerator();
			changesFromZip = ReadFromZip.readOsmChangeContent("roald-linus-2010.zip");
			optimizedChangeSet = generator.createOptimizedChangeSets(changesFromZip);

			System.out.println(optimizedChangeSet.getAreasAsCSV("flaechen"));
			System.out.println("... finished");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
