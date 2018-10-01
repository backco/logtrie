package org.qmpm.logtrie.enums;

import org.qmpm.logtrie.metrics.Metric;

public interface MetricLabel {
	
	public Metric delegate(String[] args);
	public String shortDescription();
	public String labelType();
}
