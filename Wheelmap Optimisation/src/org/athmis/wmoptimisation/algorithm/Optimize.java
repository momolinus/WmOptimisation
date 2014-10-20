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
package org.athmis.wmoptimisation.algorithm;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.algorithm.areaguard.AreaGuardChangeSetGenerator;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;

// XXX better documentation needed for "algorithm finding strategy"

/**
 * @author Marcus
 */
public class Optimize {

	private final static Logger LOGGER = Logger.getLogger(Optimize.class);

	public static void main(String[] args) {

		// configure Log4j
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);

		try {
			Optimize.run(args);

			System.exit(1);
		}
		catch (IOException | ParseException e) {

			e.printStackTrace();

			System.exit(1);
		}
	}

	public static void run(String[] args) throws IOException, ParseException {

		ChangeSetGenerator simpleGenerator, minimizeAreaGenerator;
		OptimizationResult simpleResult, minimizeAreaResult;

		simpleGenerator = new SimpleChangeSetGenerator();
		simpleResult = runChangeSetGenerator(simpleGenerator, "wheelchair_visitor-2010.zip");
		System.out.println(simpleResult.oneRowHeader());
		System.out.println(simpleResult.toOneRow());

		minimizeAreaGenerator = new AreaGuardChangeSetGenerator(0.001);
		minimizeAreaResult =
			runChangeSetGenerator(minimizeAreaGenerator, "wheelchair_visitor-2010.zip");
		System.out.println(minimizeAreaResult.toOneRow());

		BufferedWriter writer = Files.newBufferedWriter(Paths.get("wheel-2-2010.csv"));

		writer.append(simpleResult.getOriginalChangesTable());
		writer.newLine();
		writer.append(simpleResult.getOptimizedChangesTable());
		writer.newLine();
		writer.append(minimizeAreaResult.getOptimizedChangesTable());
		writer.close();

		LOGGER.info("finished");
	}

	private static OptimizationResult runChangeSetGenerator(ChangeSetGenerator generator,
															String fileName) throws IOException {
		OsmChangeContent changeContent, optimizedContent;

		OptimizationResult optimizationResult =
			new OptimizationResult(fileName, generator.getName());

		changeContent = OsmChangeContent.createOsmChangeContentFromZip(fileName);
		optimizationResult.setOriginalChanges(changeContent.getChangeSetsAsStrTable("original",
																					false));

		optimizationResult.setMeanAreaSource(changeContent.getMeanArea());
		optimizationResult.setNoChangeSetsSource(changeContent.getNoChangeSets());
		optimizationResult.setNumberNodesSource(changeContent.getNodes());

		optimizedContent = generator.createOptimizedChangeSets(changeContent);
		optimizationResult.appendOptimizedChanges(optimizedContent
				.getChangeSetsAsStrTable(generator.getName(), false));

		optimizationResult.setMeanAreaOptimized(optimizedContent.getMeanArea());
		optimizationResult.setNoChangeSetsOptimized(optimizedContent.getNoChangeSets());
		optimizationResult.setNumberNodesOptimized(optimizedContent.getNodes());

		optimizationResult.setChangesHeader(changeContent.getChangeSetsAsStrTableHeader());

		return optimizationResult;
	}
}
