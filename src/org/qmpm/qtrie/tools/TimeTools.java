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

import java.util.concurrent.TimeUnit;

public class TimeTools {
	
	public static String nanoToHourMinSecMilli(long nanoSeconds) {
		
		long seconds = TimeUnit.NANOSECONDS.toSeconds(nanoSeconds);
		int hours = (int) (seconds / 3600);
		int min = (int) ((seconds % 3600) / 60);
		int sec = (int) (seconds) % 60;
		int millis = (int) TimeUnit.NANOSECONDS.toMillis(nanoSeconds) % 1000;
	
		String formatTime = "%02d:%02d:%02d:%03d";
		return String.format(formatTime, hours, min, sec, millis);
	}
}
