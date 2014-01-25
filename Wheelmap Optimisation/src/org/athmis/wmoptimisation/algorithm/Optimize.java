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
import java.text.ParseException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.changeset.OsmChangeContent;

public class Optimize {

	private final static Logger LOGGER = Logger.getLogger(Optimize.class);

	public static void run(String[] args) throws IOException, ParseException {

		ChangeSetGenerator generator;
		OsmChangeContent changesFromZip, optimizedChangeSet;

		generator = new SimpleChangeSetGenerator();

		// changesFromZip =
		// ChangeSetZipContentData.readOsmChangeContent("olr-2010-2012.zip");

		// changesFromZip =
		// ChangeSetZipContentData.readOsmChangeContent("testdaten2.zip");
		// changesFromZip =
		// ChangeSetZipContentData.readOsmChangeContent("wheelchair_3x_samples.zip");
		// changesFromZip =
		// ChangeSetZipContentData.readOsmChangeContent("einfach3x.zip");

		changesFromZip = OsmChangeContent
				.readOsmChangeContent("wheelmap_visitor-2010-2012.zip");

		LOGGER.info("try to optimize changes: " + changesFromZip.toString());

		optimizedChangeSet = generator.createOptimizedChangeSets(changesFromZip);

		// LOGGER.info("optimized changes: " + optimizedChangeSet.verbose());

		LOGGER.info("optimized changes: " + optimizedChangeSet.toString());

		LOGGER.info("finished");
	}

	public static void main(String[] args) {
		// configure Log4j
		BasicConfigurator.configure();

		try {
			Optimize.run(args);

			System.exit(1);

		} catch (IOException | ParseException e) {

			e.printStackTrace();

			System.exit(1);
		}
	}
}
