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

package org.qmpm.logtrie.ui;

import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.tools.TimeTools;

public class ProgObsThread extends Thread {
	
	private Object obj;
	private ProgressObserver progObs;
	private String preLabel = "";
	private String endLabel = "";
	private Integer total = 0;
	private Integer current = 0;
	//private int barWidth = 40;
	private long timeElapsed = 0;
	private boolean showProgress = false;
	private boolean labelChange = false;
	
	public ProgObsThread(ProgressObserver po, Object o, boolean showProgress) {
		
		obj = o;
		progObs = po;
		this.showProgress = showProgress;
	}
	
	public void run() {
		
		Framework.permitOutput();
		
		int clrLen = 0;
		
		long startTime = System.nanoTime();
		long elapsed = 0;
		
		while (!progObs.getFinished(obj) && !progObs.timeout(obj)) {
			
			double percent = progObs.getProgress(obj)*100;
			int places = (int) Math.ceil(Math.max(1, Math.log10(total)));
			
			elapsed = (System.nanoTime() - startTime);
						
			String formatPercent = "%7.3f";
			String formatFile = (total == 0) ? "" : "FILE %" + places + "d of %" + places + "d";

			String printPercent = String.format(formatPercent, percent);
			String printFile = String.format(formatFile, current, total);
			String printTime = TimeTools.nanoToHourMinSecMilli(elapsed);
			
			if (labelChange && showProgress) {
				System.out.print("\r" + new String(new char[clrLen]).replace('\0', ' '));
				labelChange = false;
			}
			String out = "\r" + preLabel + " [" + printPercent + " % ]  [" + printTime + "] " + printFile + " - " + endLabel;
			
			if (showProgress)	System.out.print(out);
			
			clrLen = out.length()+20;
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Clear line
		if (showProgress)	System.out.print("\r" + new String(new char[clrLen]).replace('\0', ' '));
		
		timeElapsed = elapsed;
		
		Framework.resetQuiet();
	}
	
	public ProgressObserver getProgressObserver() {
		return progObs;
	}
	
	public long getTimeElapsedNano() {
		return timeElapsed;
	}
	
	public void setEndLabel(String s) {
		endLabel = s;
		labelChange = true;
	}

	public void setPreLabel(String s) {
		preLabel = s;
		labelChange = true;
	}
	
	public void setCurrent(int c) {
		current = c;
	}
	
	public void setTotal(int t) {
		total = t;
	}
	
	@SuppressWarnings("unused")
	private static String bar(int prog, int max) {
		
		String bar = "[";
		
		for (int i=0; i<prog; i++) {
			bar += "=";
		}
		if (prog != max) {
			bar += ">";
		}
		for (int i=0; i<max-prog-1; i++) {
			bar += " ";
		}

		bar += "] ";
		
		return bar;
	}
	
	public void start() {
		startTimer(obj);
		super.start();
	}
	
	public void startTimer(Object o) {
		progObs.startTimer(o);
	}
}
