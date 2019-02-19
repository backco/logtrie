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
import java.util.List;

public interface FileInfo<T extends Collection<? extends List<?>>> {
	
	public void append(FileInfo<T> fi2);
	public File getFile();
	public Collection<? extends List<?>> getLoadedFile();
	public void setLoadedFile(Collection<? extends List<?>> loadedFile) throws Exception; 
	public boolean fileLoaded();
	public void cutDownFile(int i, int j);
	public FileInfo<T> clone();
	public boolean rename(String newPath);
	public String getID();
	public void setID(String ID);
	public String getName();
	public void saveAs(String path) throws FileNotFoundException, IOException;
	public void shuffle();
	public void sort();
	public boolean isSorted();
}
