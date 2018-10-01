package org.qmpm.logtrie.ui;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public interface CLI {
	
	public Options generateOptions();
	public CommandLine generateCommandLine(Options options, String[] args);
	public void printHelp(Options options);
}