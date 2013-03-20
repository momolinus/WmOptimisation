package org.athmis.wmoptimisation.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter fetches all files which contain "_content" in the file name.
 *
 * @author Marcus Bleil, http://www.ing-bleil.de, 31.12.2012
 *
 */
public final class ChangeSetContentFileFilter implements FileFilter {
	public static final String CHANGE_SET_CONTENT_LABEL = "_content";

	@Override
	public boolean accept(File pathname) {
		return pathname.getName().contains(CHANGE_SET_CONTENT_LABEL);
	}
}