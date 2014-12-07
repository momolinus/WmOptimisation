package org.athmis.wmoptimisation.changeset;

public class OsmChangeUpdateAble extends OsmChange {

	// TODO inspect next sprint: kommentieren und prüfen, added nur zur modified liste, da die
	// Wheelmap die ersten Jahre nur modifizieren konnte, die Methode wird nur so gebraucht (vgl.
	// Suche) -> also kommentieren
	public void addChange(Change change) {

		if (getChangeSetId() != -1) {
			if (change.getChangeset() != getChangeSetId()) {
				throw new IllegalArgumentException(
						"it is not permitted to add changes with different changeset ids, this chnageset id = "
							+ getChangeSetId() + ", not permitted changeset id of change = "
							+ change.getChangeset());
			}
		}

		NodeContainer container;
		container = new NodeContainer();
		// FIXME hier muss eine Kopie rein, da ein Node/Way mehrfach geändert werden kann und dann
		// immer wieder eine neue ChangesetId bekommt -> mit Test prüfen und dann ändern
		container.addChange(makeCopy(change));

		modified.add(container);
	}

	// TODO in Toolkit, ist durch copy n' paste entstanden
	private Change makeCopy(Change change) {
		if (change.isWay()) {
			throw new IllegalArgumentException("cant' work on ways in simulation");
		}
		else {
			if (change instanceof Node) {
				Node node = new Node((Node) change);
				return node;
			}
			else {
				throw new IllegalArgumentException("can't work on type "
					+ change.getClass().getName() + " in simulation");
			}
		}
	}
}
