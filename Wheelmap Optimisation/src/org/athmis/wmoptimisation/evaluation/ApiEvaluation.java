package org.athmis.wmoptimisation.evaluation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.athmis.wmoptimisation.changeset.OsmApiChangeSetsResult;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

//TODO das kommt in UNIT Test nach dem Buch CC oder Effective Java "die Api" kennenlernen
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
