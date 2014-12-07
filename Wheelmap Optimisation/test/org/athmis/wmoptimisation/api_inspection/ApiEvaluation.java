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
package org.athmis.wmoptimisation.api_inspection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.athmis.wmoptimisation.changeset.OsmApiChangeSetsResult;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

// XXX change to an unit test, see "Clean Code inspect the api"
public class ApiEvaluation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Find changesets that were closed after T1 and created before T2
		String t1Beforet2, t1Aftert2;
		URL url;
		OsmApiChangeSetsResult apiResult;
		Serializer serializer;
		int noChangeSets;
		// Find changesets that were closed after T1 and created before T2

		// closed after 10. but created before 27. <= could not be
		t1Beforet2 = "http://api.openstreetmap.org/api/0.6/changesets?";
		t1Beforet2 += "display_name=wheelmap_visitor&time=2012-04-10T01:57:12Z,2012-04-27T01:57:12Z&closed=true";

		// closed after 27. and created before 24. <= could also not be
		t1Aftert2 = "http://api.openstreetmap.org/api/0.6/changesets?";
		t1Aftert2 += "display_name=wheelmap_visitor&time=2012-04-27T01:57:12Z,2012-04-10T01:57:12Z&closed=true";

		serializer = new Persister();

		try {
			url = new URL(t1Beforet2);
			apiResult = serializer.read(OsmApiChangeSetsResult.class,
					url.openStream());
			noChangeSets = apiResult.getNumberChangesets();
			System.out.println(noChangeSets
					+ " closed after 24. but created before 28.");

			serializer.write(apiResult, System.out);
			System.out.println();

			url = new URL(t1Aftert2);
			apiResult = serializer.read(OsmApiChangeSetsResult.class,
					url.openStream());
			noChangeSets = apiResult.getNumberChangesets();
			System.out.println(noChangeSets
					+ " closed after 28. and created before 24.");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
