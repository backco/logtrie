/*
 * 	LogTrie - an efficient data structure /and CLI for XES event logs and other sequential data
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
import java.util.LinkedHashMap;
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
import org.qmpm.logtrie.tools.XESTools;
import org.qmpm.logtrie.trie.Trie.Node;
import org.qmpm.logtrie.ui.ProgObsThread;
import org.qmpm.logtrie.ui.ProgressObserver;

public abstract class AbstractTrieMediator {

	private class LoadFile {
	}

	// TODO: Separate output into own class

	public class TrieAttributes<T extends Collection<? extends List<?>>> {

		public static final String METRICS = "METRICS";
		public String[] metricTypes = { METRICS };
		public Outcome buildOutcome = Outcome.DEFAULT;
		Collection<? extends List<?>> collection = null;
		public FileInfo<T> file;
		List<Metric> metrics = new ArrayList<>();
		String name;
		Trie trie;
		String info = "";
		Map<String, ?> encodingScheme = new HashMap<>();

		public TrieAttributes(Trie t, Collection<? extends List<?>> c, String n, FileInfo<T> f) {
			this.trie = t;
			this.collection = c;
			this.name = n;
			this.file = f;
		}

		public TrieAttributes(Trie t, String n, FileInfo<T> f) {
			this.trie = t;
			this.name = n;
			this.file = f;
		}

		public Outcome getBuildOutcome() {
			return this.buildOutcome;
		}

		public void addMetric(Metric m) {
			this.metrics.add(m);
		}

		public List<Metric> getMetrics() {
			return this.metrics;
		}

		public List<Metric> getMetrics(String labelClassName) {

			List<Metric> result = new ArrayList<>();

			for (Metric m : this.metrics) {
				if (m.getLabel().getClass().getSimpleName().equals(labelClassName)) {
					result.add(m);
				}
			}

			return result;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String n) {
			this.name = n;
		}

		protected String[] getMetricTypes() {
			return this.metricTypes;
		}

		public FileInfo<T> getFile() {
			return this.file;
		}

		public void setInfo(String i) {
			this.info = i;
		}

		public String getInfo() {
			return this.info;
		}

	}

	private final String BUILDINGTRIE = "BUILDING TRIE...";
	private final String LOADINGFILE = "LOADING FILE...";
	private final String READINGFILES = "READING FILES...";
	private final String SEARCHINGFILES = "SEARCHING FOR FILES...";
	protected final String CVPARTITIONING = "CROSS-VALIDATION (partitioning log)...";
	protected final String MINING = "MINING...";
	protected String preLabel = "";
	protected List<FileInfo<? extends Collection<? extends List<?>>>> files = new ArrayList<>();
	protected Trie filePathTrie = new TrieImpl();
	protected ProgressObserver progObs = new ProgressObserver();
	protected boolean showProgress = false;
	private boolean showTime = false;
	protected int sigDigs = 3;
	protected long timeout = Long.MAX_VALUE; // 292 years
	protected List<Trie> tries = new ArrayList<>();
	private Map<MetricLabel, List<List<String>>> metrics = new LinkedHashMap<>();
	private boolean flatten = false;
	protected boolean verbose = false;
	protected List<Trie> trieIterList = new ArrayList<>();

	public abstract TrieAttributes<? extends Collection<? extends List<?>>> getTrieAttributes(Trie t);

	protected abstract <T extends Collection<? extends List<?>>> void setTrieAttributes(Trie t, TrieAttributes<?> ta);

	protected abstract <T extends Collection<? extends List<?>>> boolean beforeTrieBuild(
			ListIterator<Trie> trieIterator, Trie t, int current, int total, String labelFormat);

	public abstract void setupTries();

	protected abstract String getLastMetric();

	protected abstract boolean needToBuildTries();

	public <T extends Collection<? extends List<?>>> void addFile(FileInfo<T> fi) throws LabelTypeException {

		// TODO: Move this to run(), build from a path saved in TrieAttributes
		/*
		 * try { filePathTrie.insert(pathAsRevList(f), false); } catch
		 * (ProcessTransitionException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		this.files.add(fi);
	}

	public <T extends Collection<? extends List<?>>> void addFile(String path) throws LabelTypeException {

		FileInfo<T> fi = FileInfoFactory.build(path, true);
		this.addFile(fi);
	}

	public <T extends Collection<? extends List<?>>> void addFiles(List<String> paths) throws Exception {

		LoadFile placeHolder = new LoadFile();
		this.progObs.register(placeHolder);
		ProgObsThread loadProgThread = new ProgObsThread(this.progObs, placeHolder, this.showProgress);

		loadProgThread.setPreLabel(this.preLabel);
		loadProgThread.setEndLabel(this.SEARCHINGFILES);
		loadProgThread.setCurrent(1);
		loadProgThread.setTotal(paths.size());
		loadProgThread.start();

		// TODO: add compatibility for CSV and other extensions
		List<String> xesFiles = FileTools.findFiles(paths, "xes");

		loadProgThread.setEndLabel(this.READINGFILES);
		loadProgThread.setTotal(xesFiles.size());

		for (String path : xesFiles) {

			FileInfo<T> fi = FileInfoFactory.build(path, true);
			this.addFile(fi);
		}

		this.progObs.setFinished(placeHolder, true);

		try {
			loadProgThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addMetric(MetricLabel m, String[] args) {

		List<List<String>> argsList = new ArrayList<>();

		if (this.metrics.containsKey(m)) {
			argsList = this.metrics.get(m);
		}

		argsList.add(Arrays.asList(args));
		this.metrics.put(m, argsList);
	}

	public <S> void buildTrie(Trie t, boolean flatten) {
		this.buildTrie(t, flatten, -1, -1);
	}

	public void buildTrie(Trie t, boolean flatten, int current, int total) {

		TrieAttributes<? extends Collection<? extends List<?>>> ta = this.getTrieAttributes(t);

		if (this.verbose) {
			System.out.println("Building trie for: " + ta.name);
		}

		Collection<? extends List<?>> col = ta.collection;

		if (col == null) {
			System.out.println(t);
			this.loadFile(t, this.preLabel, current);
			col = ta.collection;
			if (col == null) {
				ta.buildOutcome = Outcome.ERROR;
				return;
			}
		}

		int counter = 0;

		this.progObs.register(t);
		this.progObs.setTimeout(t, this.timeout);

		ProgObsThread progThread = new ProgObsThread(this.progObs, t, this.showProgress);

		progThread.setPreLabel(this.preLabel);
		progThread.setEndLabel(this.BUILDINGTRIE);
		progThread.setCurrent(current);
		progThread.setTotal(total);
		progThread.start();

		for (List<?> sequence : col) {

			if (this.verbose) {
				String seqStr = "!!!";
				try {
					seqStr = XESTools.xTraceToString((XTrace) sequence);
				} catch (LabelTypeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("inserting sequence " + (counter + 1) + " of " +
				// col.size() + " in trie "
				// + t.hashCode() + ": " + seqStr);
				// System.out.println(t);
			}

			try {
				t.insert(sequence, flatten);
			} catch (LabelTypeException e1) {

				System.out.println("   INSERT FAILED: LabelTypeException");

				e1.printStackTrace();

				this.getTrieAttributes(t).buildOutcome = Outcome.ERROR;
				this.progObs.setFinished(t, true);

				try {
					progThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				return;
			} catch (ProcessTransitionException e2) {

				// if (this.verbose) {
				System.out.println("   INSERT FAILED: ProcessTransitionException");
				// }

				// getTrieAttributes(t).buildOutcome = Outcome.ERROR;
				// progObs.setFinished(t, true);
			}

			this.progObs.updateProgress(t, (double) ++counter / col.size());

			if (this.progObs.timeout(t)) {

				this.getTrieAttributes(t).buildOutcome = Outcome.TIMEOUT;
				this.progObs.setFinished(t, true);

				try {
					progThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				return;
			}
		}

		this.getTrieAttributes(t).buildOutcome = Outcome.SUCCESS;
		this.progObs.setFinished(t, true);

		try {
			progThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (this.verbose) {
			System.out.println("Done building trie " + t.hashCode() + ".It represents " + t.getTotalEndVisits(false)
					+ " sequences");
		}
	}

	public void buildTries(boolean flatten) {

		if (this.verbose) {
			System.out.println("Building tries...");
		}

		int current = 1;
		int total = this.tries.size();
		for (Trie t : this.tries) {
			if (this.getTrieAttributes(t).buildOutcome == Outcome.DEFAULT) {
				this.buildTrie(t, flatten, current++, total);
			}
		}
	}

	public boolean containsMetric(MetricLabel m) {
		return this.metrics.keySet().contains(m);
	}

	public void run() {

		this.setupTries();

		this.updateAllMetrics();

		int fLen = this.longestNameFiles();
		int counter = 1;
		// trieIterList.addAll(tries);

		ListIterator<Trie> trieIterator = new ArrayList<Trie>().listIterator();
		for (Trie s : this.tries) {
			trieIterator.add(s);
		}

		String headerFormat = "%-" + (fLen + 11) + "s | ";
		for (MetricLabel m : this.metrics.keySet()) {
			headerFormat += "%" + Math.max(m.toString().length(), this.sigDigs + 3) + "s | ";
		}
		List<String> metStrings = new ArrayList<>();
		metStrings.add("         | FILENAME");
		for (MetricLabel m : this.metrics.keySet()) {
			metStrings.add(m.toString());
		}
		String header = String.format(headerFormat, metStrings.toArray());
		Framework.permitOutput();
		System.out.println(this.showProgress ? "\r" + header : header);
		Framework.resetQuiet();

		for (Trie t : this.tries) {

			// System.out.println(trieIterator.hasNext());

			// System.out.println("trieIterator size: " + Iterators.size(trieIterator));

			if (this.verbose) {
				System.out.println("tries.size(): " + this.tries.size());
			}

			// Trie t = trieIterator.previous();

			// System.out.println("trieIterator.previous(): " + t.hashCode());

			TrieAttributes<? extends Collection<? extends List<?>>> ta = this.getTrieAttributes(t);

			String[] resultSoFar = new String[this.metrics.keySet().size() + 1];
			for (int i = 0; i < resultSoFar.length; i++) {
				resultSoFar[i] = "";
			}

			// String format = "%-" + fLen + "s | " + resultSoFar + "%-" + mLen + "s : %12s
			// %s %s";
			this.preLabel = String.format(headerFormat, (Object[]) resultSoFar); // "%-" + fLen + "s | %-" + mLen + "s
																					// :";

			// Load file if not yet loaded
			this.loadFile(t, this.preLabel, counter);

			this.beforeTrieBuild(trieIterator, t, counter, this.tries.size(), this.preLabel);
			// Try to build trie
			if (this.needToBuildTries() && this.getTrieAttributes(t).buildOutcome == Outcome.DEFAULT) {

				if (this.verbose) {
					System.out.println("BUILDING TRIE...");
				}
				this.buildTrie(t, this.flatten, counter, this.tries.size());

				Outcome o = this.getTrieAttributes(t).buildOutcome;

				if (o != Outcome.SUCCESS) {
					for (Metric met : ta.getMetrics(this.getLastMetric())) {
						met.setResult(o);
					}
				}
			}

			resultSoFar[0] = (ta.file.isSorted() ? "SORTED   | " : "UNSORTED | ") + ta.getName();

			// List<Metric> sortedMetrics = ta.getMetrics(getLastMetric());
			// sortedMetrics.sort( (Metric m1, Metric m2) ->
			// m1.toString().compareTo(m2.toString() ) );

			int i = 1;

			if (this.verbose) {
				System.out.println("COMPUTING METRICS");
			}

			for (Metric m : ta.getMetrics(this.getLastMetric())) {

				if (this.verbose) {
					System.out.println(m.getLabel().toString());
				}

				// Compute metric if nothing else went wrong
				if (m.getResult() == Outcome.DEFAULT.toString()) {

					ProgObsThread progThread = new ProgObsThread(this.progObs, m, this.showProgress);
					MetricThread metThread = new MetricThread(m, t);

					// progThread.setBarWidth(mLen-2);
					progThread.setCurrent(counter);
					progThread.setTotal(this.tries.size());

					progThread.setPreLabel(this.preLabel);
					progThread.start();

					metThread.start();

					try {
						metThread.join();
						progThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				resultSoFar[i++] = m.getResult();
				this.preLabel = String.format(headerFormat, (Object[]) resultSoFar);
			}

			String result = this.preLabel + ta.getInfo();
			Framework.permitOutput();
			System.out.println(this.showProgress ? "\r" + result : result);
			Framework.resetQuiet();
			counter++;
		}
	}

	public void drawTries() {

		for (Trie t : this.tries) {
			t.draw();
		}
	}

	// TODO: find identical tries
	// - structurally isomorphic vs. label-identical

	public Collection<Collection<Trie>> findDuplicateTries() {
		return null;
	}

	private <S> String formatName(Trie t) {

		String result = "";
		Node n = this.filePathTrie.getRoot();
		FileInfo<? extends Collection<? extends List<?>>> fi = this.getTrieAttributes(t).file;

		/*
		 * List<String> pathRevList = new ArrayList<>(); pathRevList.add("-" +
		 * getTrieAttributes(t).getInfo());
		 * pathRevList.addAll(pathAsRevList(fi.getFile()));
		 */

		List<String> revPath = this.pathAsRevList(fi.getFile());

		for (String s : revPath) {

			ElementLabel l = null;

			try {
				l = LabelFactory.build(s.equals(fi.getFile().getName()) ? fi.getName() : s, null);
			} catch (LabelTypeException e1) {
				e1.printStackTrace();
			}

			n = n.getChildren().get(l);
			result = File.separator + l.toString() + result;

			if (n.getVisits() < 2) {
				break;
			}

			if (n.getChildren().size() == 0) {
				return "~" + File.separator + revPath.get(0);
			}
		}

		return "~" + result;
	}

	private void formatNames() {

		for (Trie t : this.tries) {
			try {
				this.filePathTrie.insert(this.pathAsRevList(this.getTrieAttributes(t).getFile().getFile()), false);
			} catch (LabelTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProcessTransitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (Trie t : this.tries) {
			this.getTrieAttributes(t).name = this.formatName(t);
		}
	}

	public List<FileInfo<? extends Collection<? extends List<?>>>> getFiles() {

		return this.files;
	}

	public List<Trie> getTries() {
		return this.tries;
	}

	public Map<MetricLabel, List<List<String>>> getMetrics(String labelClassName) {

		Map<MetricLabel, List<List<String>>> result = new HashMap<>();

		for (MetricLabel m : this.metrics.keySet()) {
			if (m.getClass().getSimpleName().equals(labelClassName)) {
				result.put(m, this.metrics.get(m));
			}
		}

		return result;
	}

	public <S> void loadFile(Trie t) {

		if (this.getTrieAttributes(t).collection == null && this.getTrieAttributes(t).buildOutcome == Outcome.DEFAULT) {

			TrieAttributes<? extends Collection<? extends List<?>>> ta = this.getTrieAttributes(t);
			this.getTrieAttributes(t).collection = ta.file.getLoadedFile();
		}
	}

	public <S> void loadFile(Trie t, int num) {

		this.loadFile(t, "", num);
	}

	public <S> void loadFile(Trie t, String preLabel, int num) {

		// TrieAttributes<? extends Collection<? extends List<?>>> ta =
		// getTrieAttributes(t);

		if (this.getTrieAttributes(t).collection == null && this.getTrieAttributes(t).buildOutcome == Outcome.DEFAULT) {

			LoadFile placeHolder = new LoadFile();
			this.progObs.register(placeHolder);
			ProgObsThread loadProgThread = new ProgObsThread(this.progObs, placeHolder, this.showProgress);

			loadProgThread.setPreLabel(preLabel);
			loadProgThread.setEndLabel(this.LOADINGFILE);
			loadProgThread.setCurrent(num);
			loadProgThread.setTotal(this.tries.size());
			loadProgThread.start();

			this.loadFile(t);
			this.progObs.setFinished(placeHolder, true);

			try {
				loadProgThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadFiles() {

		for (Trie t : this.tries) {
			this.loadFile(t);
		}
	}

	public int longestNameFiles() {

		this.formatNames();

		int max = 0;

		for (Trie t : this.tries) {

			int len = this.getTrieAttributes(t).getName().length();

			if (len > max) {
				max = len;
			}
		}

		return Math.max(max + 14, 1);
	}

	public int longestNameMetric() {

		int max = 0;

		for (Trie t : this.tries) {

			int l = this.longestNameMetric(this.getTrieAttributes(t).getMetrics());

			if (l > max) {
				max = l;
			}
		}

		return Math.max(max, 1);
	}

	public int longestNameMetric(List<Metric> list) {

		int max = 0;
		List<String> strings = new ArrayList<>();

		strings.add(this.LOADINGFILE);
		strings.add(this.BUILDINGTRIE);
		strings.add(this.READINGFILES);
		strings.add(this.SEARCHINGFILES);
		strings.add(this.CVPARTITIONING);
		strings.add(this.MINING);

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

		while (par != null) {

			String name = par.getName();

			// Hack for root directories
			for (File r : File.listRoots()) {
				if (par.equals(r)) {
					name = par.getPath().substring(0, par.getPath().length() - 1);
				}
			}

			path.add(name);
			par = par.getParentFile();
		}

		return path;
	}

	public void printOriginalSequences() {

		System.err.println("ORIGINAL INPUT REBUILT FROM TRIE");

		for (Trie t : this.tries) {

			System.err.println("Name: " + this.getTrieAttributes(t).name);
			List<List<ElementLabel>> seqs = t.rebuildSequences(false);

			for (List<ElementLabel> seq : seqs) {
				System.err.println(Arrays.toString(seq.toArray()));
			}
		}
	}

	public void removeMetrics(String labelClassName) {

		Set<MetricLabel> toRemove = new HashSet<>();

		for (MetricLabel m : this.metrics.keySet()) {
			if (m.getClass().getSimpleName().equals(labelClassName)) {
				toRemove.add(m);
			}
		}

		for (MetricLabel m : toRemove) {
			this.metrics.remove(m);
		}
	}

	public void setSigDigits(int significantDigits) {
		this.sigDigs = significantDigits;
	}

	public void setTimeout(long nanoSeconds) {
		this.timeout = nanoSeconds;
	}

	public void showProgress(boolean b) {
		this.showProgress = b;
	}

	public void showTime(boolean b) {
		this.setShowTime(b);
	}

	public void setVerbose(boolean b) {

		this.verbose = b;
	}

	public void updateAllMetrics() {

		for (Trie t : this.tries) {

			TrieAttributes<? extends Collection<? extends List<?>>> ta = this.getTrieAttributes(t);

			for (MetricLabel l : this.metrics.keySet()) {

				for (List<String> args : this.metrics.get(l)) {

					String[] argsArray = new String[args.size()];

					for (int i = 0; i < args.size(); i++) {
						argsArray[i] = args.get(i);
					}

					Metric m = l.delegate(argsArray);

					m.registerProgObs(this.progObs);
					m.setTimeout(this.timeout);

					if (m.getSigDigs() < 0) {
						m.setSigDigits(this.sigDigs);
					}
					ta.addMetric(m);
				}
			}
		}
	}

	public void setFlatten(boolean b) {
		this.flatten = b;
	}

	/**
	 * @return the showTime
	 */
	public boolean isShowTime() {
		return this.showTime;
	}

	/**
	 * @param showTime the showTime to set
	 */
	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}
}
