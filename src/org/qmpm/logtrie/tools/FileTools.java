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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.qmpm.logtrie.exceptions.FileLoadException;

public class FileTools {
	
	public static List<String> findFiles(List<String> logPathParam, String extension) throws FileLoadException {
		
		String[] pathArray = logPathParam.toArray(new String[logPathParam.size()]);
		
		return findFiles(pathArray, extension);
	}
 	
	public static List<String> findFiles(String[] logPathParam, String extension) throws FileLoadException {
		
		List<String> retLogs = new ArrayList<>();
		
		for (String logPath : logPathParam) {
			retLogs.addAll(findFiles(logPath, extension));
		}
		
		if (retLogs.isEmpty()) {
			System.out.println("No files found in (check for escape characters):");
			for (String path : logPathParam) {
				System.out.println(path);
			}
		}
		
		return retLogs;
	}
	
	public static List<String> findFiles(String logPathParam, String extension) throws FileLoadException {
		
		File f = new File(logPathParam);
		String logPath = f.toString();
		List<String> retFiles = new ArrayList<>();
		
		if (f.isDirectory()) {
			return loadDir(logPathParam, extension);
		
		} else if (f.isFile() ) {
		
			if (logPath.toLowerCase().endsWith("xes")) {
				retFiles.add(logPath);
			} 
		} 
		
		return retFiles;
	}

	private static List<String> loadDir(String logDirPath, String extension) throws FileLoadException {

		List<String> retFiles = new ArrayList<>();
		File[] files = new File(logDirPath).listFiles();
		
		if (files != null) {
			for (File f :files) {
				retFiles.addAll(findFiles(f.toString(), extension));
			}
		}
		
		return retFiles;
	}
}
