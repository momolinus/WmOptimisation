/**
 * created at 17.01.2015 (15:03:16)
 */
package org.athmis.wmoptimisation.analyse;

import java.io.IOException;
import java.util.Map;

import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;

/**
 * @author Marcus
 */
public class ShowAndStoreMinDistances {

	public static void main(String[] args) {
		OsmChangeContent changeContent;

		try {
			changeContent =
				OsmChangeContent.createOsmChangeContentFromZip("wheelchair_visitor-2010.zip");
			Map<Long, Double> distances = changeContent.getNoChangesPerChangeset();
			// double maxNum = 0;

			// Map<Long, Double> distances = changeContent.getMinChangeDistance();
			distances.forEach((id, d) -> {
				if (d < Double.MAX_VALUE) {
					System.out.println(id + ":\t" + d);
					// maxNum = Math.max(maxNum, d);
				}
			});
		}
		catch (IOException e) {

			e.printStackTrace();
			System.exit(1);
		}

	}

}
