/**
 * created at 12.09.2014 (12:09:53)
 */
package examples;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.athmis.wmoptimisation.changeset.OsmChange;
import org.athmis.wmoptimisation.fetch_changesets.FetchingChangeSetsToolbox;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * @author M. Comp. Sc. Marcus Bleil<br>
 *         TU Berlin<br>
 *         Arbeits-, Ingenieur- und Organisationspsychologie<br>
 *         Marchstr. 12, 10587 Berlin<br>
 *         marcus.bleil@tu-berlin.de<br>
 *         http://www.aio.tu-berlin.de
 */
public class OneError {

	private static Serializer SERIALIZER;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		SERIALIZER = new Persister();
	}

	@Ignore
	@Test
	public void test_that_stream_fails_sometimes() {
		try {
			String apiCall;
			URL url;

			apiCall = FetchingChangeSetsToolbox.createApiCall(9062501l, false);
			url = new URL(apiCall);

			OsmChange osmChange = SERIALIZER.read(OsmChange.class, url.openStream());
			assertNotNull(osmChange);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Ignore
	@Test
	public void test_that_stream_to_string_never_fails() {
		try {
			String apiCall;
			URL url;

			apiCall = FetchingChangeSetsToolbox.createApiCall(9062501l, false);
			url = new URL(apiCall);

			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder builder = new StringBuilder();

			reader.lines().forEach(line -> {
				builder.append(line);
				builder.append("\n");
			});

			OsmChange osmChange = SERIALIZER.read(OsmChange.class, builder.toString());
			assertNotNull(osmChange);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
