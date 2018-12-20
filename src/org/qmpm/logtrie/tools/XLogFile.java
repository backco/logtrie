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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.exceptions.FileLoadException;
import org.qmpm.logtrie.exceptions.LabelTypeException;

import com.google.common.io.Files;

public class XLogFile implements FileInfo<XLog> {

	private XFactory xFactory = new XFactoryBufferedImpl(); 
	private File file;
	private String ID = "";
	private XLog log = null;
	private String path;
	private boolean createFileOnDisk = false;
	
	public XLogFile(String filePath, boolean createFileOnDisk) {
		
		file = new File(filePath);
		path = filePath;
		this.createFileOnDisk = createFileOnDisk;
	}

	public void append(FileInfo<XLog> fi2) {
		
		if (log == null) {
			log = xFactory.createLog();
		}
		
		XLog loadedFile = (XLog) fi2.getLoadedFile();
		
		for (XTrace trace : loadedFile) {

			log.add(trace);
		}
	}
	
	public FileInfo<XLog> clone() {
		
		return clone("-clone", false);
	}
	
	public FileInfo<XLog> clone(boolean createFile) {
		
		return clone("-clone", createFile);
	}

	public FileInfo<XLog> clone(String ext, boolean createFile) {
		
		XLogFile result = new XLogFile(path, createFile);
		
		result.file = new File(file.getAbsolutePath() + ext);
		result.log = (XLog) log.clone();

		return result;
	}

	public void cutDownFile(int n, int m) {
		
		XLog newLog = (XLog) log.clone();
		newLog.clear();
		
		for (int i = n; i < m; i++) {
			newLog.add(log.get(i));
		}
		log = newLog;
	}
	
	public boolean fileLoaded() {
		return (log != null);
	}

	@Override
	public File getFile() {
		return file;
	}
	
	@Override
	public String getID() {
		return ID;
	}
	
	@Override
	public XLog getLoadedFile() {

		if (log == null) {
		
			try {
				log = XESTools.loadXES(path, true);
			} catch (FileLoadException e) {
				
				System.out.println("XLogFile()...file does not exist, creating new file");
				
				log = xFactory.createLog();
				
				if (createFileOnDisk) {
				
					file.getParentFile().mkdirs();
					
					try {
						file.createNewFile();
					} catch (IOException e1) {
						Framework.permitOutput();
						System.out.println("Failed to create file: " + file.getAbsolutePath() + ". Aborting...");
						e1.printStackTrace();
						Framework.resetQuiet();
						System.exit(1);
					}
				}
			}
		}
		
		return log;
	}
	
	@Override
	public String getName() {
		return file.getName() + getID();
	}

	public String getPath() {
		return path;
	}
	
	
	public void loadFile() {
		
		try {
			log = XESTools.loadXES(path, true);
		} catch (FileLoadException e) {
			e.printStackTrace();
		}
	}

	public boolean rename(String newPath) {
		System.out.println("Renaming file to " + newPath);
		path = newPath;
		return file.renameTo(new File(newPath));
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public void setLoadedFile(Collection<? extends List<? extends Object>> loadedFile) throws Exception {
		
		if (loadedFile instanceof XLog) {
			log = (XLog) loadedFile;
		} else throw new Exception("setLoadedFile: bad parameter type (" + loadedFile.getClass().toGenericString() + ")");
	}

	@Override
	public void saveAs(String newPath) throws FileNotFoundException, IOException {
		System.out.println("Saving file as " + newPath);
		rename(newPath);
		XESTools.saveFile(log, file);
	}
	
	public String toString() {
		
		String out = "";
		out += "name: " + getName() + "\n";
		out += "path: " + path + "\n";
		for (XTrace trace : log) {
			try {
				out += XESTools.xTraceToString(trace) + "\n";
			} catch (LabelTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return out;
	}

	@Override
	public void shuffle() {
		
		if (log != null) {
			Collections.shuffle(log);
		}
		
	}

	@Override
	public void sort() {
		
		XESTools.sortByTimeStamp(getLoadedFile());
	}

	@Override
	public boolean isSorted() {
		
		return XESTools.isSorted(getLoadedFile());
	}
}
