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

package org.qmpm.qtrie.ui;

import java.util.HashMap;
import java.util.Map;

public class ProgressObserver {
	
	private Map<Object, Double> progMap = new HashMap<>();
	private Map<Object, Boolean> finishedMap = new HashMap<>();
	private Map<Object, Long> timeStartMap = new HashMap<>();
	private Map<Object, Long> timeOutMap = new HashMap<>();
	
	public ProgressObserver() {
	}
	
	public void updateProgress(Object o, Double prog) {
		progMap.put(o,  prog);
	}
	
	public Double getProgress(Object o) {
		return progMap.get(o);
	}
	
	public void setFinished(Object o, boolean b) {
		finishedMap.put(o, (Boolean) b);
	}
	
	public boolean getFinished(Object o) {
		return finishedMap.get(o);
	}
	
	public void register(Object o) {
		progMap.put(o,  0.0);
		finishedMap.put(o, false);
	}
	
	public boolean isRegistered(Object o ) {
		return progMap.containsKey(o);
	}

	public boolean contains(Object o) {
		return (progMap.containsKey(o) && finishedMap.get(o));
	}
	
	@Override
	public String toString() {
		String out = "ProgressObserver:\n";
		out += "\nprogMap:\n";
		for (Object key : progMap.keySet()) {
			out += "   " + key.getClass().getSimpleName() + "(" + key.hashCode() + "): " + progMap.get(key) + "\n";
		}
		out += "\nfinishedMap\n";
		for (Object key : finishedMap.keySet()) {
			out += "   " + key.getClass().getSimpleName() + "(" + key.hashCode() + "): " + finishedMap.get(key) + "\n";
		}
		
		return out;
	}
	
	public boolean timeout(Object o) {

		if (timeOutMap.containsKey(o)) {
			long elapsed = System.nanoTime() - timeStartMap.get(o);
			return (timeOutMap.get(o) - elapsed < 0);
			
		} else return false;
	}
	
	public void startTimer(Object o) {
		timeStartMap.put(o, System.nanoTime());
	}
	
	public long getTimeElapsed(Object o) {
		return System.nanoTime() - timeStartMap.get(o);
	}
	
	public void setTimeout(Object o, long nanoSeconds) {
		timeOutMap.put(o, nanoSeconds);
	}
}
