package org.qmpm.logtrie.metrics;

import org.qmpm.logtrie.trie.Trie;

public class MetricThread extends Thread {
	
	Metric metric;
	Trie trie;
	long timeout = Long.MAX_VALUE;	// Measured in nanoseconds. Will run for 292 years by default.
	
	public MetricThread(Metric m, Trie t) {
		
		metric = m;
		trie = t;
	}

	public MetricThread(Metric m, Trie t, long timeout) {
		
		this(m, t);
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		metric.compute(trie);
	}
}
