/*
Copyright Marcus Bleil, Oliver Rudzik, Christoph B�nte 2012

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

Wheelmap Optimization ist Freie Software: Sie k�nnen es unter den Bedingungen
der GNU General Public License, wie von der Free Software Foundation,
Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

Wheelmap Optimization wird in der Hoffnung, dass es n�tzlich sein wird, aber
OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
Siehe die GNU General Public License f�r weitere Details.

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.athmis.wmoptimisation.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter omits all files, which contain "_content" in name.
 *
 *  Bleil, http://www.ing-bleil.de, 31.12.2012
 *
 */
public final class ChangeSetFileFilter implements FileFilter {
	@Override
	public boolean accept(File pathname) {
		return !pathname.getName().contains("_content");
	}
}