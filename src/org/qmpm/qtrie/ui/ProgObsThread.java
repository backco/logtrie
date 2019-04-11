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

import org.qmpm.qtrie.core.Framework;
import org.qmpm.qtrie.tools.TimeTools;

public class ProgObsThread extends Thread {

	private Object obj;
	private ProgressObserver progObs;
	private String preLabel = "";
	private String endLabel = "";
	private Integer total = 0;
	private Integer current = 0;
	// private int barWidth = 40;
	private long timeElapsed = 0;
	private boolean showProgress = false;
	private boolean labelChange = false;

	public ProgObsThread(ProgressObserver po, Object o, boolean showProgress) {

		this.obj = o;
		this.progObs = po;
		this.showProgress = showProgress;
	}

	@Override
	public void run() {

		Framework.permitOutput();

		int clrLen = 0;

		long startTime = System.nanoTime();
		long elapsed = 0;

		double percent;
		int places;

		String formatPercent;
		String formatFile;
		String printPercent;
		String printFile;
		String printTime;
		String out;

		while (!this.progObs.getFinished(this.obj) && !this.progObs.timeout(this.obj)) {

			percent = this.progObs.getProgress(this.obj) * 100;
			places = (int) Math.ceil(Math.max(1, this.total.toString().length()));

			elapsed = System.nanoTime() - startTime;

			formatPercent = "%7.3f";
			formatFile = this.total == 0 ? "" : "FILE %" + places + "d of %" + places + "d";

			printPercent = String.format(formatPercent, percent);
			printFile = String.format(formatFile, this.current, this.total);
			printTime = TimeTools.nanoToHourMinSecMilli(elapsed);

			if (this.labelChange && this.showProgress) {
				System.out.print("\r" + new String(new char[clrLen]).replace('\0', ' '));
				this.labelChange = false;
			}
			out = "\r" + this.preLabel + " [" + printPercent + " % ]  [" + printTime + "] " + printFile + " - "
					+ this.endLabel;

			if (this.showProgress) {
				System.out.print(out);
			}

			clrLen = out.length() + 20;

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Clear line
		if (this.showProgress) {
			System.out.print("\r" + new String(new char[clrLen]).replace('\0', ' '));
		}

		this.timeElapsed = elapsed;

		Framework.resetQuiet();
	}

	public ProgressObserver getProgressObserver() {
		return this.progObs;
	}

	public long getTimeElapsedNano() {
		return this.timeElapsed;
	}

	public void setEndLabel(String s) {
		this.endLabel = s;
		this.labelChange = true;
	}

	public void setPreLabel(String s) {
		this.preLabel = s;
		this.labelChange = true;
	}

	public void setCurrent(int c) {
		this.current = c;
	}

	public void setTotal(int t) {
		this.total = t;
	}

	@SuppressWarnings("unused")
	private static String bar(int prog, int max) {

		String bar = "[";

		for (int i = 0; i < prog; i++) {
			bar += "=";
		}
		if (prog != max) {
			bar += ">";
		}
		for (int i = 0; i < max - prog - 1; i++) {
			bar += " ";
		}

		bar += "] ";

		return bar;
	}

	@Override
	public void start() {
		this.startTimer(this.obj);
		super.start();
	}

	public void startTimer(Object o) {
		this.progObs.startTimer(o);
	}
}
