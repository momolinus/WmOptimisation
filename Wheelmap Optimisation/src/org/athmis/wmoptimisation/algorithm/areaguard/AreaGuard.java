package org.athmis.wmoptimisation.algorithm.areaguard;

import org.athmis.wmoptimisation.changeset.Change;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public abstract class AreaGuard {

	protected Multimap<Long, Area> edges;
	protected final double maxBboxEdge;

	public AreaGuard(final double maxBboxEdge) {
		if (maxBboxEdge > 0.0) {
			this.maxBboxEdge = maxBboxEdge;
			edges = ArrayListMultimap.create();
		}
		else {
			throw new IllegalArgumentException("max bounding box must be > 0.0");
		}
	}

	public final void addUpdatedItem(Long changeSetId, Change updatedItem) {
		boolean success;

		success = edges.put(changeSetId, new Area(updatedItem));

		if (!success) {
			throw new IllegalStateException("can't add item " + String.valueOf(updatedItem)
				+ " to changeset id " + String.valueOf(changeSetId));
		}
	}

	public abstract boolean isNextBoxToLarge(Long changeSetId, Change updatedItem);

}