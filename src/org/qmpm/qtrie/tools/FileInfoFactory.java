/*
 * 	LogTrie - an efficient data structure and CLI for XES event logs and other sequential data
 *
 * 	Author: Christoffer Olling Back	<www.christofferback.com>
 *
 * 	Copyright (C) 2018 University of Copenhagen
 *
 *	This file is part of LogTrie.
 *
 *	LogTrie is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	LogTrie is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with LogTrie.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.qmpm.qtrie.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileInfoFactory {

	@SuppressWarnings("unchecked")
	public static <T extends Collection<? extends List<?>>> FileInfo<T> build(String path, boolean createFile) {

		if (path.toLowerCase().endsWith("xes")) {
			return (FileInfo<T>) new XLogFile(path, createFile);
		}

		return null;
	}

	public static <T extends Collection<? extends List<?>>> List<FileInfo<T>> partition(FileInfo<T> fi, int k)
			throws Exception {

		List<FileInfo<T>> result = new ArrayList<>();

		for (Collection<? extends List<?>> elem : MathTools.partition(fi.getLoadedFile(), k)) {
			FileInfo<T> filePart = fi.clone();
			filePart.setLoadedFile(elem);
			result.add(filePart);
		}

		return result;
	}
}
