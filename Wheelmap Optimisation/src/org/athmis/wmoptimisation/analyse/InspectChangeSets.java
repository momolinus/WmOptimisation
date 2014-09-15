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
package org.athmis.wmoptimisation.analyse;

import java.io.IOException;
import java.util.*;

import org.athmis.wmoptimisation.changeset.Way;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;

/**
 * @author Marcus Bleil, created at2014-09-15
 */
public class InspectChangeSets {

	public static String describeWays(String zipFile) {
		String result;

		try {
			OsmChangeContent changes;
			List<Way> ways;
			changes = OsmChangeContent.createOsmChangeContentFromZip(zipFile);

			ways = changes.getAllWays();

			if (ways.size() > 0) {
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(zipFile + " ways:(chnageset id:\ttimestamp)\n");

				for (Way way : ways) {
					strBuilder.append(way.getChangeset() + ":\t" + way.getTimestamp() + "\n");
				}

				result = strBuilder.toString();
			}
			else {
				result = zipFile + " contains no ways";
			}
		}
		catch (IOException e) {
			result =
				"error reading file " + String.valueOf(zipFile) + ": " + e.getLocalizedMessage();
		}
		return result;
	}

	/**
	 * some samples
	 *
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		String wheelchair2012 = "C:/Users/Marcus/Desktop/Testdaten Wheelmap/wheelchair-2012.zip";

		String description = describeWays(wheelchair2012);

		System.out.println(description);
	}
}
