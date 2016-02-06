/*
 * Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012 This file is part of Wheelmap
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
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.athmis.wmoptimisation.algorithm;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.athmis.wmoptimisation.algorithm.areaguard.AreaGuardSizeAndNeighborChangesetGenerator;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;

/**
 * The Optimize class runs the algorithm for optimization and stores the result to a file.
 *
 * @author @author Marcus Bleil, http://www.marcusbleil.de
 */
public class Optimize {

	private final static Logger LOGGER = Logger.getLogger(Optimize.class);

	public static void main(String[] args) {

		// configure Log4j
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);

		try {
			Optimize.run(args);

			System.exit(0);
		}
		catch (IOException | ConfigurationException e) {

			e.printStackTrace();

			System.exit(1);
		}
	}

	public static void run(String[] args) throws IOException, ConfigurationException {

		ChangeSetGenerator generator_10km, generator_50km, generator_2km, humanExample;
		OptimizationResult result_10km, result_50km, result_2km, humanExampleResult;

		humanExample = new SimpleChangeSetGenerator();

		// 10km: 52,4798529 - 52,4725339 = 0,0073
		generator_2km = new AreaGuardSizeAndNeighborChangesetGenerator(0.0073 / 5.0);
		generator_10km = new AreaGuardSizeAndNeighborChangesetGenerator(0.0073);
		generator_50km = new AreaGuardSizeAndNeighborChangesetGenerator(0.0073 * 5.0);

		LOGGER.info("starting simulation");

		try (BufferedWriter writer = Files.newBufferedWriter(buildFileName())) {

			humanExampleResult = runChangeSetGenerator(humanExample, "roald-linus-2011.zip");
			result_2km = runChangeSetGenerator(generator_2km, "wheelchair_visitor-2010.zip");
			result_10km = runChangeSetGenerator(generator_10km, "wheelchair_visitor-2010.zip");
			result_50km = runChangeSetGenerator(generator_50km, "wheelchair_visitor-2010.zip");

			writer.append(humanExampleResult.getChangesHeader());
			writer.newLine();

			writer.append(humanExampleResult.getOriginalChangesTable());
			writer.newLine();
			writer.append(result_2km.getOriginalChangesTable());
			writer.newLine();

			writer.append(result_2km.getOptimizedChangesTable());
			writer.newLine();
			writer.append(result_10km.getOptimizedChangesTable());
			writer.newLine();
			writer.append(result_50km.getOptimizedChangesTable());

			writer.close();
		}
		catch (IOException e) {
			throw new IOException(e);
		}
		catch (ConfigurationException e1) {
			throw new ConfigurationException(e1);
		}

		LOGGER.info("finished");
	}

	private static Path buildFileName() throws ConfigurationException {
		PropertiesConfiguration config = new PropertiesConfiguration("ressources/file-counter.txt");
		int counter = config.getInt("counter");
		counter++;
		config.setProperty("counter", counter);
		config.save();

		return Paths.get("optimization_" + counter + ".csv");
	}

	private static OptimizationResult runChangeSetGenerator(ChangeSetGenerator generator,
		String fileName) throws IOException {
		OsmChangeContent changeContent, optimizedContent;

		OptimizationResult optimizationResult =
			new OptimizationResult(fileName, generator.getName());

		changeContent = OsmChangeContent.createOsmChangeContentFromZip(fileName);
		LOGGER.info("read zip file " + fileName);
		optimizationResult
			.setOriginalChangesAsTable(changeContent.getChangeSetsAsStrTable("original", false));
		LOGGER.info("stored original data to content object");

		optimizationResult.setMeanAreaSource(changeContent.getMeanArea());
		optimizationResult.setNoChangeSetsSource(changeContent.getNoChangeSets());
		optimizationResult.setNumberNodesSource(changeContent.getNodes());

		optimizedContent = generator.createOptimizedChangeSets(changeContent);
		LOGGER.info("optimized changesets with generator " + generator.getName());
		optimizationResult.appendOptimizedChanges(optimizedContent
			.getChangeSetsAsStrTable(generator.getName(), false));

		optimizationResult.setMeanAreaOptimized(optimizedContent.getMeanArea());
		optimizationResult.setNoChangeSetsOptimized(optimizedContent.getNoChangeSets());
		optimizationResult.setNumberNodesOptimized(optimizedContent.getNodes());

		optimizationResult.setChangesHeader(changeContent.getChangeSetsAsStrTableHeader());

		return optimizationResult;
	}
}
