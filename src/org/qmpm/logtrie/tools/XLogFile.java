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
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.exceptions.FileLoadException;
import org.qmpm.logtrie.exceptions.LabelTypeException;

public class XLogFile implements FileInfo<XLog> {

	private XFactory xFactory = new XFactoryBufferedImpl();
	private File file;
	private String ID = "";
	private XLog log = null;
	private String path;
	private boolean createFileOnDisk = false;

	public XLogFile(String filePath, boolean createFileOnDisk) {

		this.file = new File(filePath);
		this.path = filePath;
		this.createFileOnDisk = createFileOnDisk;
	}

	@Override
	public void append(FileInfo<XLog> fi2) {

		if (this.log == null) {
			this.log = this.xFactory.createLog();
		}

		XLog loadedFile = (XLog) fi2.getLoadedFile();

		for (XTrace trace : loadedFile) {

			this.log.add(trace);
		}
	}

	@Override
	public FileInfo<XLog> clone() {

		return this.clone("-clone", false);
	}

	public FileInfo<XLog> clone(boolean createFile) {

		return this.clone("-clone", createFile);
	}

	public FileInfo<XLog> clone(String ext, boolean createFile) {

		XLogFile result = new XLogFile(this.path, createFile);

		result.file = new File(this.file.getAbsolutePath() + ext);
		result.log = (XLog) this.log.clone();

		return result;
	}

	@Override
	public void cutDownFile(int n, int m) {

		XLog newLog = (XLog) this.log.clone();
		newLog.clear();

		for (int i = n; i < m; i++) {
			newLog.add(this.log.get(i));
		}
		this.log = newLog;
	}

	@Override
	public boolean fileLoaded() {
		return this.log != null;
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public String getID() {
		return this.ID;
	}

	@Override
	public XLog getLoadedFile() {

		if (this.log == null) {

			try {
				this.log = XESTools.loadXES(this.path, true);
			} catch (FileLoadException e) {

				System.out.println("XLogFile()...file does not exist, creating new file");

				this.log = this.xFactory.createLog();

				if (this.createFileOnDisk) {

					this.file.getParentFile().mkdirs();

					try {
						this.file.createNewFile();
					} catch (IOException e1) {
						Framework.permitOutput();
						System.out.println("Failed to create file: " + this.file.getAbsolutePath() + ". Aborting...");
						e1.printStackTrace();
						Framework.resetQuiet();
						System.exit(1);
					}
				}
			}
		}

		return this.log;
	}

	@Override
	public String getName() {
		return this.file.getName() + this.getID();
	}

	public String getPath() {
		return this.path;
	}

	public void loadFile() {

		try {
			this.log = XESTools.loadXES(this.path, true);
		} catch (FileLoadException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean rename(String newPath) {
		System.out.println("Renaming file to " + newPath);
		this.path = newPath;
		return this.file.renameTo(new File(newPath));
	}

	@Override
	public void setID(String ID) {
		this.ID = ID;
	}

	@Override
	public void setLoadedFile(Collection<? extends List<?>> loadedFile) throws Exception {

		XLog newLog = this.xFactory.createLog();

		for (List<?> trace : loadedFile) {
			newLog.add((XTrace) trace);
			/*
			 * if (trace instanceof XTrace) { throw new Exception(
			 * "setLoadedFile: bad parameter type (" +
			 * loadedFile.getClass().toGenericString() + ")"); } else { newLog.add((XTrace)
			 * trace); }
			 */
		}

		this.log = newLog;
		/*
		 * if (loadedFile instanceof XLog) { this.log = (XLog) loadedFile; } else {
		 * throw new Exception("setLoadedFile: bad parameter type (" +
		 * loadedFile.getClass().toGenericString() + ")"); }
		 */
	}

	@Override
	public void saveAs(String newPath) throws FileNotFoundException, IOException {
		System.out.println("Saving file as " + newPath);
		this.rename(newPath);
		XESTools.saveFile(this.log, this.file);
	}

	@Override
	public String toString() {

		String out = "";
		out += "name: " + this.getName() + "\n";
		out += "path: " + this.path + "\n";
		for (XTrace trace : this.log) {
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

		if (this.log != null) {
			Collections.shuffle(this.log);
		}

	}

	@Override
	public void sort() {

		XESTools.sortByTimeStamp(this.getLoadedFile());
	}

	@Override
	public boolean isSorted() {

		return XESTools.isSorted(this.getLoadedFile());
	}

	public void setXLog(XLog log) {
		this.log = log;
	}

	public XFactory getXFactory() {

		return this.xFactory;
	}
}
