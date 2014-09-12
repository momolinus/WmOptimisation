/**
 * created at 12.09.2014 (09:53:37)
 */
package org.athmis.wmoptimisation.changeset;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
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
public class NdTest {

	private String failedChangeset;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		failedChangeset = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		failedChangeset +=
			"<osmChange version=\"0.6\" generator=\"OpenStreetMap server\" copyright=\"OpenStreetMap and contributors\" attribution=\"http://www.openstreetmap.org/copyright\" license=\"http://opendatacommons.org/licenses/odbl/1-0/\">";
		failedChangeset += "<modify>";
		failedChangeset +=
			"<way id=\"232575179\" changeset=\"19017419\" timestamp=\"2013-11-20T16:45:18Z\" version=\"2\" visible=\"true\" user=\"wheelmap_visitor\" uid=\"290680\">";
		failedChangeset += "<nd ref=\"332625443\"/>";
		failedChangeset += "<nd ref=\"332625352\"/>";
		failedChangeset += "<nd ref=\"332625351\"/>";
		failedChangeset += "<nd ref=\"332625350\"/>";
		failedChangeset += "<nd ref=\"332625349\"/>";
		failedChangeset += "<nd ref=\"332625356\"/>";
		failedChangeset += "<nd ref=\"2409197118\"/>";
		failedChangeset += "<nd ref=\"332625443\"/>";
		failedChangeset += "<tag k=\"amenity\" v=\"parking\"/>";
		failedChangeset += "<tag k=\"name\" v=\"Vanemuise ülemine\"/>";
		failedChangeset += "<tag k=\"wheelchair\" v=\"yes\"/>";
		failedChangeset += "</way>";
		failedChangeset += "</modify>";
		failedChangeset += "</osmChange>";
	}

	@Test
	public void test() {
		Serializer serializer = new Persister();

		try {
			OsmChange change = serializer.read(OsmChange.class, failedChangeset);

			assertThat(change.getChanges(), hasSize(1));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
