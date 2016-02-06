package org.athmis.wmoptimisation;

import org.athmis.wmoptimisation.algorithm.SimpleChangeSetGeneratorTest;
import org.athmis.wmoptimisation.algorithm.SimpleChangeSetGeneratorTest2;
import org.athmis.wmoptimisation.algorithm.areaguard.*;
import org.athmis.wmoptimisation.changeset.*;
import org.athmis.wmoptimisation.fetch_changesets.OsmChangeContentTest;
import org.athmis.wmoptimisation.osmserver.OsmServerAddChangesTest;
import org.athmis.wmoptimisation.osmserver.OsmServerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("deprecation")
@RunWith(Suite.class)
@SuiteClasses({ ChangeSetTest.class, FetchChangeSetsTest.class, NodeAndApiTest.class, OsmServerAddChangesTest.class,
	OsmServerTest.class, SimpleChangeSetGeneratorTest.class, SimpleChangeSetGeneratorTest2.class,
	AreaGuardForSizeAndNeighborTest.class, AreaGuardSizeAndNeighborChangesetGeneratorTest.class, AreaGuardTest.class,
	AreaGuardToolBoxTest.class, AreaTest.class, MinimizeAreaSelfChangeSetGenartorTest.class,
	ChangeSetUpdateAbleTest.class, FetchChangeSetsTest.class, NdTest.class, OsmChangeUpdateAbleTest.class,
	OsmChangeContentTest.class })
public class AllTests {

}
