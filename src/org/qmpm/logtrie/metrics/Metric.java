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

package org.qmpm.logtrie.metrics;

import org.qmpm.logtrie.enums.MetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.exceptions.LabelTypeException;
import org.qmpm.logtrie.exceptions.NodeTypeException;
import org.qmpm.logtrie.tools.MathTools;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.ui.ProgressObserver;

public abstract class Metric {
	
	protected ProgressObserver progObs = new ProgressObserver();
	protected Double value = null;
	protected long startTime = 0;
	public long timeout = Long.MAX_VALUE; // Measured in nanoseconds. Will run for 292 years by default.
	protected String result = Outcome.DEFAULT.toString();
	private Outcome outcome = Outcome.CONTINUE;
	private int sigDigs = 8;
	private MetricLabel label;
	
	public abstract Outcome doComputation(Trie t) throws LabelTypeException, NodeTypeException;
	public abstract void processArgs(String[] args); // add throws for bad arg input
	public abstract MetricLabel getLabel();
	public abstract String parametersAsString();
	
	public Metric() {
		label = getLabel();
	}

	public Metric(String[] args) {
		
		label = getLabel();
		processArgs(args);
	}
	
	public Double getValue() {
		return value;
	}
	
	public Double formattedValue() {
		return MathTools.round(getValue(), sigDigs);
	}
	
	public void registerProgObs(ProgressObserver po) {

		progObs = po;
		progObs.register(this);
		progObs.setTimeout(this, timeout);
	}
	
	public void updateProgress(double prog) {
		
		if (progObs == null) {
			
			outcome = Outcome.CONTINUE;
		
		} else {
		
			progObs.updateProgress(this, prog);
			
			if (progObs.timeout(this)) {
				outcome = Outcome.TIMEOUT;
			} else {
				outcome = Outcome.CONTINUE;
			}
		}
	}
	
	protected void finished() {
		
		if (progObs != null) {
		
			progObs.setFinished(this, true);
		}
	}
	
	public void compute(Trie t) {
		
		//Outcome outcome;
		
		try {
			outcome = doComputation(t);
		} catch (LabelTypeException e) {
			outcome = Outcome.ERROR;
			e.printStackTrace();
			System.exit(1);
		} catch (NodeTypeException e) {
			outcome = Outcome.ERROR;
			e.printStackTrace();
			System.exit(1);
		}
		
		finished();
		
		switch(outcome) {
		case SUCCESS:	setResult(value == null ? Outcome.SUCCESS.toString() : formattedValue().toString());	break;
		case TIMEOUT:	setResult(Outcome.TIMEOUT.toString()); break;
		case ERROR:		setResult(Outcome.ERROR.toString()); break;
		default: result = "UNHANDLED"; break;
		}
	}
	
	public Outcome getOutcome() {
		return outcome;
	}
	
	public ProgressObserver getProgressObserver() {
		return progObs;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setTimeout(long nanoSeconds) {
		timeout = nanoSeconds;
		progObs.setTimeout(this, timeout);
	}
	
	public void setSigDigits(int significantDigits) {
		sigDigs = significantDigits;
	}

	public void setOutcome(Outcome o) {
		outcome = o;
	}

	public void setResult(Outcome r) {
		result = r.toString();
	}
	
	public void setResult(String r) {
		result = r;
	}
	
	@Override
	public String toString() {
		String p = parametersAsString();
		return label.shortDescription() + (p == "" ? "" : " (" + p + ")");
	}
}
