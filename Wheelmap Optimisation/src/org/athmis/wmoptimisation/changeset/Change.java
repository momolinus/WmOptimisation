package org.athmis.wmoptimisation.changeset;

import java.text.ParseException;
import java.util.Calendar;

public interface Change {
	public abstract String getUser();

	public abstract String getTimestamp();

	public abstract double getLon();

	public abstract double getLat();

	public abstract long getId();

	public abstract long getChangeset();

	public abstract Calendar getCreatedAt() throws ParseException;
}
