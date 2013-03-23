/**
 * 
 */
package org.athmis.wmoptimisation.algos;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.versuche.ChangeSetZipContentData;
import org.athmis.wmoptimisation.versuche.ReadFromZip;

/**
 * @author Marcus
 * 
 */
public class AlgorithmRunner {

	private static Logger LOGGER = Logger.getLogger(AlgorithmRunner.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChangeSetZipContentData changes, optimizedChanges;
		String zipFileName;
		ChangeSetGenerator changeSetGenerator;

		// configure Log4j
		BasicConfigurator.configure();

		zipFileName = "roald-linus-2012.zip";

		changeSetGenerator = new SimpleChangeSetGenerator();

		try {
			changes = ReadFromZip.readOsmChangeContent(zipFileName);

			LOGGER.info("read file '" + zipFileName + "' with " + changes.changeSetsSize()
					+ " changesets");

			optimizedChanges = changeSetGenerator.createOptimizedChangeSets(changes);

			LOGGER.info("... successful finished");

		} catch (IOException e) {
			LOGGER.fatal("can't read file " + zipFileName, e);
		}

	}

}
