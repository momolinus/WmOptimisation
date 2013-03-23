package org.athmis.wmoptimisation.osmserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.athmis.wmoptimisation.changeset.Change;
import org.athmis.wmoptimisation.changeset.ChangeSet;

public class OsmServer {

	private List<ChangeSet> createdChangeSet;
	private Map<ChangeSet, Change> changes;
	private int changeSetId;

	public OsmServer() {
		createdChangeSet = new ArrayList<>();
		changeSetId = 0;
	}

	/**
	 * 
	 * @param creationTime
	 * @return
	 */
	public ChangeSet createChangeSet(Calendar creationTime) {
		ChangeSet changeSet;

		return null;
	}

	public boolean isOpenFor(ChangeSet changeSetInUse, Change change) {
		int changeSetSize;
		
		if (!createdChangeSet.contains(changeSetInUse))
			throw new IllegalStateException("client uses changest not created by server");

		
		
		return false;
	}
}
