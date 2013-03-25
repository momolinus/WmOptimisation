package org.athmis.wmoptimisation.changeset;

import java.io.StringWriter;

import org.athmis.wmoptimisation.changeset.Node;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class NodeAndApiTest {

	@Test
	public void testLong() {
		Node node;
		Serializer serializer;
		StringWriter stringWriter;

		serializer = new Persister();
		node = new Node(2164999418L, 47.1, 3.5, "2013-02-21T07:55:08Z", 1, true);
		stringWriter = new StringWriter();

		try {
			// serializer.write(node, System.out);
			serializer.write(node, stringWriter);
			serializer.read(Node.class, stringWriter.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
