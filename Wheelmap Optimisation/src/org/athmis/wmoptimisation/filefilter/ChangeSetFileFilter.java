package org.athmis.wmoptimisation.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter omits all files, which contain "_content" in name.
 *
 * @author Marcus Bleil, http://www.ing-bleil.de, 31.12.2012
 *
 */
public final class ChangeSetFileFilter implements FileFilter {
	@Override
	public boolean accept(File pathname) {
		return !pathname.getName().contains("_content");
	}
}