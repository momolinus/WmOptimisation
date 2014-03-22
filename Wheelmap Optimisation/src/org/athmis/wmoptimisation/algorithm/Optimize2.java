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
package org.athmis.wmoptimisation.algorithm;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.OsmChangeContent;

/**
 * @author Marcus
 */
public class Optimize2 {

	private final static Logger LOGGER = Logger.getLogger(Optimize2.class);

	public static void run(String[] args) throws IOException, ParseException {

		ChangeSetGenerator generator;
		String fileName;
		
		fileName = "wheelmap_visitor-2010-2012.zip";
		generator = new MinimizeAreaChangeSetGenartor();

		runChangeSetGenerator(generator, fileName);
	}

	private static void runChangeSetGenerator(ChangeSetGenerator generator, String fileName)
		throws IOException {
		OsmChangeContent changesFromZip, optimizedChangeSet;
		
		LOGGER.info("now working on " + fileName);
		changesFromZip = OsmChangeContent.readOsmChangeContent(fileName);
		LOGGER.info("try to optimize changes: " + changesFromZip.toString());
		LOGGER.info("mean nodes area size = " + changesFromZip.getMeanAreaOfChangeSetsForNodes());

		LOGGER.info("*** starting optimiztion ***");

		optimizedChangeSet = generator.createOptimizedChangeSets(changesFromZip);
		LOGGER.info("optimized changes: " + optimizedChangeSet.toString());
		LOGGER.info("optimized mean nodes area size = "
			+ optimizedChangeSet.getMeanAreaOfChangeSetsForNodes());
		LOGGER.info("finished with " + fileName);

		LOGGER.info("***--------------------------***");

		LOGGER.info("finished");
	}

	public static void main(String[] args) {
		// configure Log4j
		BasicConfigurator.configure();

		try {
			Optimize2.run(args);

			System.exit(1);

		}
		catch (IOException | ParseException e) {

			e.printStackTrace();

			System.exit(1);
		}
	}
}
