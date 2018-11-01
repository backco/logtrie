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

package org.qmpm.logtrie.trie;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XTrace;
import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.elementlabel.ElementLabel;
import org.qmpm.logtrie.elementlabel.LabelFactory;
import org.qmpm.logtrie.enums.MetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.exceptions.LabelTypeException;
import org.qmpm.logtrie.exceptions.ProcessTransitionException;
import org.qmpm.logtrie.metrics.Metric;
import org.qmpm.logtrie.metrics.MetricThread;
import org.qmpm.logtrie.tools.FileInfo;
import org.qmpm.logtrie.tools.FileInfoFactory;
import org.qmpm.logtrie.tools.FileTools;
import org.qmpm.logtrie.tools.TimeTools;
import org.qmpm.logtrie.tools.XESTools;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.trie.TrieImpl;
import org.qmpm.logtrie.trie.Trie.Node;
import org.qmpm.logtrie.ui.ProgObsThread;
import org.qmpm.logtrie.ui.ProgressObserver;

import com.google.common.collect.Iterators;

public abstract class AbstractTrieMediator {
	
	private class LoadFile {}
	
	// TODO: Separate output into own class
	
	public class TrieAttributes<T extends Collection<? extends List<? extends Object>>> {
		
		public static final String METRICS = "METRICS";
		public String[] metricTypes = {METRICS};
		public Outcome buildOutcome = Outcome.DEFAULT;
		Collection<? extends List<? extends Object>> collection = null;
		public FileInfo<T> file;
		List<Metric> metrics = new ArrayList<>();
		String name;
		Trie trie;
		
		public TrieAttributes(Trie t, Collection<? extends List<? extends Object>> c, String n, FileInfo<T> f) {
			trie = t; collection = c; name = n; file = f;
		}
		
		public TrieAttributes(Trie t, String n, FileInfo<T> f) {
			trie = t; name = n; file = f;
		}
	
		public Outcome getBuildOutcome() {
			return buildOutcome;
		}
				
		public void addMetric(Metric m) {
			metrics.add(m);
		}
		
		public List<Metric> getMetrics() {
			return metrics;
		}
		
		public List<Metric> getMetrics(String labelClassName) {
			
			List<Metric> result = new ArrayList<>();
			
			for (Metric m : metrics) {
				if (m.getLabel().getClass().getSimpleName().equals(labelClassName)) {
					result.add(m);
				}
			}
			
			return result;
		}
		
		public String getName() {
			return name;
		}

		protected String[] getMetricTypes() {
			return metricTypes;
		}
	
		public FileInfo<T> getFile() {
			return file;
		}

	}
	
	private final String BUILDINGTRIE = "BUILDING TRIE...";
	private final String LOADINGFILE = "LOADING FILE...";
	protected List<FileInfo<? extends Collection<? extends List<? extends Object>>>> files = new ArrayList<>();
	protected Trie filePathTrie = new TrieImpl();
	protected ProgressObserver progObs = new ProgressObserver();
	protected boolean showProgress = false;
	private boolean showTime = false;
	protected int sigDigs = 3;	
	protected long timeout = Long.MAX_VALUE; // 292 years
	protected List<Trie> tries = new ArrayList<>();
	private Map<MetricLabel, List<List<String>>> metrics = new HashMap<>();
	private boolean flatten = false;
	private boolean verbose = false;
	private int partitionLog = 1;
	protected List<Trie> trieIterList = new ArrayList<Trie>();
	
	public abstract TrieAttributes<? extends Collection<? extends List<? extends Object>>> getTrieAttributes(Trie t);
	protected abstract <T extends Collection<? extends List<? extends Object>>> void setTrieAttributes(Trie t, TrieAttributes<T> ta);
	protected abstract <T extends Collection<? extends List<? extends Object>>> boolean beforeTrieBuild(ListIterator<Trie> trieIterator, Trie t, int current, int total, String labelFormat);
	public abstract void setupTries();
	protected abstract String getLastMetric();
	
	public <T extends Collection<? extends List<? extends Object>>> void addFile(FileInfo<T> fi) throws LabelTypeException {
		
		File f = fi.getFile();
		List<String> pathAsList = pathAsRevList(f);
		
		if (partitionLog > 1) {
			
			int k = 1;
			
			for (FileInfo<T> fiPart : FileInfoFactory.partition(fi, partitionLog)) {
				
				fiPart.setID(" - " + String.valueOf(k++) + "/" + partitionLog);				
				List<String> pathAsListK = new ArrayList<>();
				pathAsListK.addAll(pathAsList);
				pathAsListK.set(0, pathAsListK.get(0) + fiPart.getID());
				try {
					filePathTrie.insert(pathAsListK, false);
				} catch (ProcessTransitionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				files.add(fiPart);
			}
		} else {
			
			try {
				filePathTrie.insert(pathAsRevList(f), false);
			} catch (ProcessTransitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			files.add(fi);
		}
	}
	
	public <T extends Collection<? extends List<? extends Object>>> void addFile(String path) throws LabelTypeException {
		
		FileInfo<T> fi = FileInfoFactory.build(path);
		addFile(fi);
	}	
	
	public <T extends Collection<? extends List<? extends Object>>> void addFiles(List<String> paths) throws Exception {
		
		// TODO: add compatibility for CSV and other extensions
		List<String> xesFiles = FileTools.findFiles(paths, "xes");
		
		for (String path : xesFiles) {

			FileInfo<T> fi = FileInfoFactory.build(path);
			addFile(fi);
		}			
	}
	
	public void addMetric(MetricLabel m, String[] args) {
		
		List<List<String>> argsList = new ArrayList<>();

		if (metrics.containsKey(m)) {
			argsList = metrics.get(m); 
		}
		
		argsList.add(Arrays.asList(args));
		metrics.put(m, argsList);
	}
	
	public void buildTrie(Trie t, boolean flatten) {
		buildTrie(t, flatten, null, null);
	}
	
	public void buildTrie(Trie t, boolean flatten, Integer current, Integer total) {
		
		System.out.println("buildTrie()");
		
		if (verbose) {
			System.out.println("Building trie for: " + getTrieAttributes(t).name);
		}
		
		Collection<? extends List<? extends Object>> col = getTrieAttributes(t).collection;

		String preLabelFormat = "%-" + longestNameFiles() + "s | %-" + longestNameMetric() + "s :";
		
		if (col == null) {
			
			loadFile(t, preLabelFormat, current);
			col = getTrieAttributes(t).collection;
			
			if (col == null) {
				getTrieAttributes(t).buildOutcome = Outcome.ERROR;
				return;
			}
		}
		
		int counter = 0;
		
		progObs.register(t);	
		progObs.setTimeout(t, timeout);
		
		ProgObsThread progThread = new ProgObsThread(progObs, t, showProgress);
		
		progThread.setPreLabel(String.format(preLabelFormat, getTrieAttributes(t).getName(), "BUILDING TRIE..."));
		progThread.setCurrent(current);
		progThread.setTotal(total);
		progThread.start();
		
		for (List<? extends Object> sequence : col) {
			
			if (verbose)
				try {
					System.out.println("inserting sequence " + (counter+1) + " of " + col.size() + ": " + XESTools.xTraceToString((XTrace) sequence));
				} catch (LabelTypeException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
			
			try {
				t.insert(sequence, flatten);
			} catch (LabelTypeException e1) {
				
				e1.printStackTrace();
				
				getTrieAttributes(t).buildOutcome = Outcome.ERROR;
				progObs.setFinished(t, true);
				
				try {
					progThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				return;
			} catch (ProcessTransitionException e2) {
				
				if (verbose) System.out.println("   INSERT FAILED");
				
				//getTrieAttributes(t).buildOutcome = Outcome.ERROR;
				//progObs.setFinished(t, true);
			}
			
			progObs.updateProgress(t, (double) ++counter / col.size());
			
			if (progObs.timeout(t)) {
				
				getTrieAttributes(t).buildOutcome = Outcome.TIMEOUT;
				progObs.setFinished(t, true);
				
				try {
					progThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				return;
			}
		}
		
		getTrieAttributes(t).buildOutcome = Outcome.SUCCESS;
		progObs.setFinished(t, true);
		
		try {
			progThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void buildTries(boolean flatten) {
		
		if (verbose) {
			System.out.println("Building tries...");
		}
		
		int current = 1;
		int total = tries.size();
		for (Trie t : tries) {
			if (getTrieAttributes(t).buildOutcome == Outcome.DEFAULT) {
				buildTrie(t, flatten, current++, total);
			}
		}
	}
	
	public void run() {
		
		System.out.println("In run()");
		System.out.println("verbose: " + verbose);
		
		setupTries();
		
		updateAllMetrics();
		
		int fLen = longestNameFiles();
		int counter = 1;
		
		//trieIterList.addAll(tries);
		ListIterator<Trie> trieIterator = new ArrayList<Trie>().listIterator();
		for (Trie s : tries) {
			trieIterator.add(s);
		}
		
		while (trieIterator.hasPrevious()) {
			
			//System.out.println(trieIterator.hasNext());
			//System.out.println("trieIterator size: " + Iterators.size(trieIterator));
			
			Trie t = trieIterator.previous();
			
			TrieAttributes<? extends Collection<? extends List<? extends Object>>> ta = getTrieAttributes(t);
			int mLen = longestNameMetric();
			String format = "%-" + fLen + "s | %-" + mLen + "s : %12s  %s";
			String preLabelFormat = "%-" + fLen + "s | %-" + mLen + "s :";
			
			// Load file if not yet loaded
			loadFile(t, preLabelFormat, counter);
			
			//System.err.println("Before BeforeTrieBuild");
			if (!beforeTrieBuild(trieIterator, t, counter, tries.size(), preLabelFormat)) {
				continue;
			}
			
			// Try to build trie
			if (getTrieAttributes(t).buildOutcome == Outcome.DEFAULT) {

				buildTrie(t, flatten, counter, tries.size());
				
				Outcome o = getTrieAttributes(t).buildOutcome;
				
				if (o != Outcome.SUCCESS) {
					for (Metric met : ta.getMetrics(getLastMetric())) {
						met.setResult(o);
					}
				}
			}
						
			for (Metric m : ta.getMetrics(getLastMetric())) {

				// Compute metric if nothing else went wrong
				long elapsed = 0;
				if (m.getResult() == Outcome.DEFAULT.toString()) {
					
					ProgObsThread progThread = new ProgObsThread(progObs, m, showProgress);
					MetricThread metThread = new MetricThread(m, t);
					
					//progThread.setBarWidth(mLen-2);
					progThread.setCurrent(counter);
					progThread.setTotal(tries.size());
					
					progThread.setPreLabel(String.format(preLabelFormat, ta.getName(), m.toString()));
					progThread.start();
					
					metThread.start();
					
					try {
						metThread.join();
						progThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					elapsed = progThread.getTimeElapsedNano();
				}
				
				String time = !showTime ? "" : "[" + TimeTools.nanoToHourMinSecMilli(elapsed) + "]";
				String out = String.format(format, ta.getName(), m.toString(), m.getResult(), time);

				Framework.permitOutput();
				System.out.println("\r" + out);
				Framework.resetQuiet();
			}
			counter++;
		}
	}
	
	// TODO: find identical tries 
	// 			- structurally isomorphic vs. label-identical

	public void drawTries() {

		for (Trie t : tries) {
			t.draw();
		}
	}
	
	public Collection<Collection<Trie>> findDuplicateTries() {
		return null;
	}

	private String formatName(Trie t) {
		
		String result = "";
		Node n = filePathTrie.getRoot();
		FileInfo<? extends Collection<? extends List<? extends Object>>> fi = getTrieAttributes(t).file;
		
		for (String s : pathAsRevList(fi.getFile())) {
		
			ElementLabel l = null;
			
			try {
				l = LabelFactory.build( s.equals(fi.getFile().getName()) ? fi.getName() : s);
			} catch (LabelTypeException e1) {
				e1.printStackTrace();
			}
			
			n = n.getChildren().get(l);
			result = File.separator + l.toString() + result;

			if (n.getVisits() < 2) break;
		}
		
		return "~" + result;
	}

	private void formatNames() {
		
		for (Trie t : tries) {
			getTrieAttributes(t).name = formatName(t);
		}
	}
	
	public List<FileInfo<? extends Collection<? extends List<? extends Object>>>> getFiles() {
		
		return files;
	}

	public List<Trie> getTries() {
		return tries;
	}
	
	public Map<MetricLabel, List<List<String>>> getMetrics(String labelClassName) {
		
		Map<MetricLabel, List<List<String>>> result = new HashMap<>();
		
		for (MetricLabel m : metrics.keySet()) {
			if (m.getClass().getSimpleName().equals(labelClassName)) {
				result.put(m, metrics.get(m));
			}
		}
		
		return result;
	}
	
	public void loadFile(Trie t) {
		
		if ((getTrieAttributes(t).collection == null) && getTrieAttributes(t).buildOutcome == Outcome.DEFAULT) {
		
			TrieAttributes<? extends Collection<? extends List<? extends Object>>> ta = getTrieAttributes(t);
			getTrieAttributes(t).collection = ta.file.getLoadedFile();
		}
	}
	
	public void loadFile(Trie t, int num) {
		loadFile(t, "", num);
	}
	
	public void loadFile(Trie t, String preLabelFormat, int num) {
		
		TrieAttributes<? extends Collection<? extends List<? extends Object>>> ta = getTrieAttributes(t);
		
		if ((getTrieAttributes(t).collection == null) && getTrieAttributes(t).buildOutcome == Outcome.DEFAULT) {
			
			LoadFile placeHolder = new LoadFile();
			progObs.register(placeHolder);
			ProgObsThread loadProgThread = new ProgObsThread(progObs, placeHolder, showProgress);

			loadProgThread.setPreLabel(String.format(preLabelFormat, ta.getName(), "LOADING FILE..."));
			loadProgThread.setCurrent(num);
			loadProgThread.setTotal(tries.size());
			loadProgThread.start();
			
			loadFile(t);
			progObs.setFinished(placeHolder, true);
			
			try {
				loadProgThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadFiles() {
		
		for (Trie t : tries) {
			loadFile(t);
		}
	}
	
	public int longestNameFiles() {
		
		formatNames();
		
		int max = 0;
		
		for (Trie t : tries) {
		
			int len = getTrieAttributes(t).getName().length();
			
			if (len > max) {
				max = len;
			}
		}
		
		return Math.max(max, 1);
	}
	
	public int longestNameMetric() {
		
		int max = 0;
		
		for (Trie t : tries) {
		
			int l = longestNameMetric(getTrieAttributes(t).getMetrics());
			
			if (l > max) {
				max = l;
			}
		}
		
		return Math.max(max, 1);
	}
	
	public int longestNameMetric(List<Metric> list) {
		
		int max = 0;
		List<String> strings = new ArrayList<>();
		
		strings.add(LOADINGFILE);
		strings.add(BUILDINGTRIE);
		
		for (Metric m : list) {
			strings.add(m.toString());
		}
		
		for (String s : strings) {
		
			int len = s.length();
			
			if (len > max) {
				max = len;
			}
		}
		
		return max;
	}
	
	protected List<String> pathAsRevList(File f) {
		
		List<String> path = new ArrayList<>();
		path.add(f.getName());
		File par = f.getParentFile();
		

		
		while ((par != null) ) {
			
			String name = par.getName();
			
			// Hack for root directories
			for (File r : File.listRoots()) {
				if (par.equals(r)) {
					name = par.getPath().substring(0, par.getPath().length()-1);
				}
			}
			
			path.add(name);
			par = par.getParentFile();
		}
		
		return path;
	}
	
	public void printOriginalSequences() {
		
		System.err.println("ORIGINAL INPUT REBUILT FROM TRIE");
		
		for (Trie t : tries) {
		
			System.err.println("Name: " + getTrieAttributes(t).name);
			List<List<ElementLabel>> seqs = t.rebuildSequences();
			
			for (List<ElementLabel> seq : seqs) {
				System.err.println(Arrays.toString(seq.toArray()));
			}
		}
	}
	
	public void removeMetrics(String labelClassName) {
		
		Set<MetricLabel> toRemove = new HashSet<MetricLabel>();
		
		for (MetricLabel m : metrics.keySet()) {
			if (m.getClass().getSimpleName().equals(labelClassName)) {
				toRemove.add(m);
			}
		}
		
		for (MetricLabel m : toRemove) {
			metrics.remove(m);
		}
	}

	public void setSigDigits(int significantDigits) {
		sigDigs = significantDigits;
	}

	public void setTimeout(long nanoSeconds) {
		timeout = nanoSeconds;
	}
	
	public void showProgress(boolean b) {
		showProgress = b;
	}
	
	public void showTime(boolean b) {
		showTime = b;
	}

	public void setVerbose(boolean b) {
		
		verbose = b;
	}


	public void updateAllMetrics() {
		
		for (Trie t : tries) {
			
			TrieAttributes<? extends Collection<? extends List<? extends Object>>> ta = getTrieAttributes(t);
			System.err.println("      Trie t: " + ta.getName());

			for (MetricLabel l : metrics.keySet()) {
				
				for (List<String> args : metrics.get(l)) {
				
					String[] argsArray = new String[args.size()];
					
					for (int i=0; i<args.size(); i++) {
						argsArray[i] = args.get(i);
					}

					Metric m = l.delegate(argsArray);
					
					m.registerProgObs(progObs);
					m.setTimeout(timeout);
					m.setSigDigits(sigDigs);
					
					ta.addMetric(m);
				}
			}
		}
	}
		
	public void setFlatten(boolean b) {
		flatten = b;
	}

	public void setPartition(int k) {
		partitionLog = Math.abs(k);
	}
}