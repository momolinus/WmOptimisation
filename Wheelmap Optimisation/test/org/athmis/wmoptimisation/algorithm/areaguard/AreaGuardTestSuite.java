package org.athmis.wmoptimisation.algorithm.areaguard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AreaGuardForSizeAndNeighborTest.class,
	AreaGuardSizeAndNeighborChangesetGeneratorTest.class, AreaGuardTest.class,
	AreaGuardToolBoxTest.class, AreaTest.class, MinimizeAreaSelfChangeSetGenartorTest.class })
public class AreaGuardTestSuite {

}
