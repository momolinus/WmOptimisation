package org.athmis.wmoptimisation.changeset;

import org.athmis.wmoptimisation.fetch_changesets.FetchingChangeSetsToolbox;

public class OsmChangeUpdateAble extends OsmChange {

	// TODO inspect next sprint: kommentieren und prüfen, added nur zur modified liste, da die
	// Wheelmap die ersten Jahre nur modifizieren konnte, die Methode wird nur so gebraucht (vgl.
	// Suche) -> also kommentieren
	public void addChange(Change change) {

		if (getChangeSetId() != -1) {
			if (change.getChangeset() != getChangeSetId()) {
				throw new IllegalArgumentException(
						"it is not permitted to add changes with different changeset ids, this chnageset id = "
							+ getChangeSetId() + ", not permitted changeset id of change = " + change.getChangeset());
			}
		}

		NodeContainer container;
		container = new NodeContainer();
		container.addChange(FetchingChangeSetsToolbox.makeCopy(change));

		modified.add(container);
	}
}
