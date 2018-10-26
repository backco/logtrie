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

package org.qmpm.logtrie.tools;

import java.util.HashSet;
import java.util.Set;

public class FileInfoFactory {
	
	public static FileInfo build(String path) {
		
		if (path.toLowerCase().endsWith("xes")) {
			return new XLogFile(path);
		}
		
		return null;
	}
	
	public static Set<FileInfo> partition(FileInfo fi, int k) {
		
		Set<FileInfo> result = new HashSet<FileInfo>();
		
		int i = Math.floorDiv(fi.getLoadedFile().size(), k);
		int r = fi.getLoadedFile().size() % k;
		
		for (int j=0; j<k; j++) {
			
			FileInfo filePart = fi.clone();
			
			filePart.cutDownFile(j * i, j * i + i + (r-- > 0 ? 1 : 0));
			result.add(filePart);
		}
		
		return result;
	}
}
