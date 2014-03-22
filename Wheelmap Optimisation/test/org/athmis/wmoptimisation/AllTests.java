package org.athmis.wmoptimisation;

import org.athmis.wmoptimisation.algorithm.SimpleChangeSetGeneratorTest;
import org.athmis.wmoptimisation.algorithm.SimpleChangeSetGeneratorTest2;
import org.athmis.wmoptimisation.changeset.ChangeSetTest;
import org.athmis.wmoptimisation.changeset.FetchChangeSetsTest;
import org.athmis.wmoptimisation.changeset.NodeAndApiTest;
import org.athmis.wmoptimisation.osmserver.OsmServerAddChangesTest;
import org.athmis.wmoptimisation.osmserver.OsmServerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ChangeSetTest.class, FetchChangeSetsTest.class, NodeAndApiTest.class,
	OsmServerAddChangesTest.class, OsmServerTest.class, SimpleChangeSetGeneratorTest.class,
	SimpleChangeSetGeneratorTest2.class })
public class AllTests {

}
