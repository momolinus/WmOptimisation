package org.athmis.wmoptimisation.analyse;

import java.io.IOException;

import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContent;

public class BuildTableFromZip {

	public static void main(String[] args) {

		try {
			OsmChangeContent changeContent;

			/* changeContent =
			 * OsmChangeContent.createOsmChangeContentFromZip("wheelchair_visitor-2010.zip");
			 * changeContent =
			 * OsmChangeContent.createOsmChangeContentFromZip("wheelchair_visitor-2010.zip");
			 * changeContent =
			 * OsmChangeContent.createOsmChangeContentFromZip("wheelchair_visitor-2010.zip"); */

			// wheelchair-2012.zip geht nicht
			// roald-linus-2011.zip geht auch
			// wheelchair_visitor-2010.zip geht
			changeContent = OsmChangeContent.createOsmChangeContentFromZip("roald-linus-2011.zip");

			System.out.println(changeContent.getChangeSetsAsStrTable("original", true));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
