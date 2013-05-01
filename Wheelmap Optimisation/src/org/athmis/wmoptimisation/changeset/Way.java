/*
Copyright Marcus Bleil, Oliver Rudzik, Christoph Bünte 2012

This file is part of Wheelmap Optimization.

Wheelmap Optimization is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Wheelmap Optimization is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Athmis. If not, see <http://www.gnu.org/licenses/>.

Diese Datei ist Teil von Wheelmap Optimization.

Wheelmap Optimization ist Freie Software: Sie können es unter den Bedingungen
der GNU General Public License, wie von der Free Software Foundation,
Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
veröffentlichten Version, weiterverbreiten und/oder modifizieren.

Wheelmap Optimization wird in der Hoffnung, dass es nützlich sein wird, aber
OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
Siehe die GNU General Public License für weitere Details.

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.athmis.wmoptimisation.changeset;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "way", strict = false)
public class Way implements Change {

	@Attribute
	private long changeset;

	// you could have one node more times in the database, because it could be
	// modified by different changesets
	@Attribute
	private long id;

	// leave "required=false" because it could be there is a way without tags
	@ElementList(inline = true, required = false)
	private List<Nd> nodes = new ArrayList<Nd>();

	// leave "required=false" because it could be there is a way without tags
	@ElementList(inline = true, required = false)
	private List<Tag> tags = new ArrayList<Tag>();

	@Attribute
	private String timestamp;

	@Attribute
	private long uid;

	@Attribute
	private String user;

	@Attribute
	private int version;

	@Override
	public long getChangeset() {
		return changeset;
	}

	@Override
	public long getId() {
		return id;
	}

	public List<Nd> getNodes() {
		return nodes;
	}

	public List<Tag> getTags() {
		return tags;
	}

	@Override
	public String getTimestamp() {
		return timestamp;
	}

	public long getUid() {
		return uid;
	}

	@Override
	public String getUser() {
		return user;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public double getLon() {
		return 0;
	}

	@Override
	public double getLat() {
		return 0;
	}

	@Override
	public Calendar getCreatedAt() throws ParseException {
		return ChangeSetToolkit.osmToCal(timestamp);
	}

	@Override
	public int compareTo(Object other) {
		Calendar meTimeStamp, otherTimeStamp;
		try {
			meTimeStamp = ChangeSetToolkit.osmToCal(timestamp);
			otherTimeStamp = ChangeSetToolkit.osmToCal(((Way) other).timestamp);

			return meTimeStamp.compareTo(otherTimeStamp);
		} catch (ParseException pe) {
			throw new RuntimeException("timestamp of this way is wrong initialized", pe);
		}
	}

	@Override
	public void setChangeset(long changeSetId) {
		this.changeset = changeSetId;
	}
}
