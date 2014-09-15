/**
 * created at 12.09.2014 (12:25:10)
 */
package examples;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.athmis.wmoptimisation.changeset.OsmChange;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class OsmChangeTest {

	private static String wrongSet;
	private final static String WRONG_SET_FILE_NAME = "changeset9277511.txt";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		InputStream wrongSetInputStream;

		wrongSetInputStream = OsmChangeTest.class.getResourceAsStream(WRONG_SET_FILE_NAME);

		if (wrongSetInputStream == null) {
			throw new IOException("can't open file " + WRONG_SET_FILE_NAME);
		}

		BufferedReader wrongSetReader;
		wrongSetReader = new BufferedReader(new InputStreamReader(wrongSetInputStream));

		StringBuilder wrongSetStringBuilder;
		wrongSetStringBuilder = new StringBuilder();

		wrongSetReader.lines().forEach(line -> {
			wrongSetStringBuilder.append(line);
			wrongSetStringBuilder.append("\n");
		});

		wrongSet = wrongSetStringBuilder.toString();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {}

	@Test
	public void test() {
		Serializer serializer = new Persister();

		try {
			serializer.read(OsmChange.class, wrongSet);

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
