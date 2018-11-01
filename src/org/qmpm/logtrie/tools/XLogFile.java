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
import java.util.Collection;
import java.util.List;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.qmpm.logtrie.exceptions.FileLoadException;

public class XLogFile implements FileInfo<XLog> {

	private File file;
	private String ID = "";
	private XLog log = null;
	private String path;;
	
	public XLogFile(String filePath) {
		
		file = new File(filePath);
		path = filePath;
	}
	
	public void append(FileInfo<XLog> fi2) {
		
		XLog loadedFile = (XLog) fi2.getLoadedFile();
		
		for (XTrace trace : loadedFile) {
			log.add(trace);
		}
	}

	public FileInfo<XLog> clone() {
		
		XLogFile result = new XLogFile(path);
		
		result.file = new File(file.getAbsolutePath());
		result.log = log;

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
		
		if (log == null) loadFile();
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
			log = XESTools.loadXES(path);
		} catch (FileLoadException e) {
			e.printStackTrace();
		}
	}

	public boolean rename(String newPath) {
		
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
	public FileInfo shallowCopy() {
		XLogFile result = new XLogFile(path);
		result.setID(ID);
		result.log = new XFactoryBufferedImpl().createLog();
		
		return result;
	}
}
