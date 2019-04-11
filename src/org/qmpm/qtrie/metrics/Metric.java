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

package org.qmpm.qtrie.metrics;

import org.qmpm.qtrie.enums.MetricLabel;
import org.qmpm.qtrie.enums.Outcome;
import org.qmpm.qtrie.exceptions.FileLoadException;
import org.qmpm.qtrie.exceptions.LabelTypeException;
import org.qmpm.qtrie.exceptions.NodeTypeException;
import org.qmpm.qtrie.trie.Trie;
import org.qmpm.qtrie.ui.ProgressObserver;

public abstract class Metric {

	protected ProgressObserver progObs = new ProgressObserver();
	protected Double value = null;
	protected long startTime = 0;
	public long timeout = Long.MAX_VALUE; // Measured in nanoseconds. Will run for 292 years by default.
	private String result = Outcome.DEFAULT.toString();
	private Outcome outcome = Outcome.CONTINUE;
	protected int sigDigs = -1;
	private MetricLabel label;
	private String[] args = null;

	protected abstract <T> Outcome doComputation(Trie<T> t) throws LabelTypeException, NodeTypeException, FileLoadException;

	public abstract void processArgs(String[] args); // add throws for bad arg input

	public abstract MetricLabel getLabel();

	public abstract String parametersAsString();

	public Metric() {
		this.label = this.getLabel();
	}

	public Metric(String[] args) {

		this.label = this.getLabel();
		this.args = args;
		this.processArgs(args);
	}

	public Double getValue() {
		return this.value;
	}

	/*
	 * public Double formattedValue() {
	 *
	 * return MathTools.round(getValue(), sigDigs); }
	 */

	public void registerProgObs(ProgressObserver po) {

		this.progObs = po;
		this.progObs.register(this);
		this.progObs.setTimeout(this, this.timeout);
	}

	public void updateProgress(double prog) {

		if (this.progObs == null) {

			this.outcome = Outcome.CONTINUE;

		} else {

			this.progObs.updateProgress(this, prog);

			if (this.progObs.timeout(this)) {
				this.outcome = Outcome.TIMEOUT;
			} else {
				this.outcome = Outcome.CONTINUE;
			}
		}
	}

	protected void finished() {

		if (this.progObs != null) {

			this.progObs.setFinished(this, true);
		}
	}

	public <T> void compute(Trie<T> t) {

		// Outcome outcome;

		try {
			this.outcome = this.doComputation(t);
		} catch (LabelTypeException e) {
			this.outcome = Outcome.ERROR;
			e.printStackTrace();
			System.exit(1);
		} catch (NodeTypeException e) {
			this.outcome = Outcome.ERROR;
			e.printStackTrace();
			System.exit(1);
		} catch (FileLoadException e) {
			this.outcome = Outcome.ERROR;
			e.printStackTrace();
			System.exit(1);
		}

		this.finished();

		switch (this.outcome) {
		case SUCCESS:
			this.setResult(this.value == null ? Outcome.SUCCESS.toString() : String.format("%." + (this.sigDigs < 0 ? 0 : this.sigDigs) + "f", this.value));
			break;
		case TIMEOUT:
			this.setResult(Outcome.TIMEOUT.toString());
			break;
		case ERROR:
			this.setResult(Outcome.ERROR.toString());
			break;
		default:
			System.out.println("Outcome: " + this.outcome + " is unhandled");
			this.result = "UNHANDLED";
			break;
		}
	}

	public Outcome getOutcome() {
		return this.outcome;
	}

	public ProgressObserver getProgressObserver() {
		return this.progObs;
	}

	public String getResult() {
		return this.result;
	}

	public int getSigDigs() {
		return this.sigDigs;
	}

	public void setTimeout(long nanoSeconds) {
		this.timeout = nanoSeconds;
		this.progObs.setTimeout(this, this.timeout);
	}

	public void setSigDigits(int significantDigits) {

		if (this.sigDigs < 0) {
			this.sigDigs = significantDigits; // allows inherited classes to enforce immutable significant digit value
		}
	}

	public void setOutcome(Outcome o) {
		this.outcome = o;
	}

	public void setResult(Outcome r) {
		this.result = r.toString();
	}

	public void setResult(String r) {
		this.result = r;
	}

	@Override
	public String toString() {
		String p = this.parametersAsString();
		return this.label.shortDescription() + (p == "" ? "" : " (" + p + ")");
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public String[] getArgs() {
		return this.args;
	}

}