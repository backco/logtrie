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

package org.qmpm.logtrie.core;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.output.NullOutputStream;
import org.qmpm.logtrie.enums.MetricLabel;
import org.qmpm.logtrie.trie.AbstractTrieMediator;
import org.qmpm.logtrie.ui.CLI;
import org.qmpm.logtrie.ui.LambdaOption;

public class Framework {
	
	private static PrintStream ps = System.out;
	protected static AbstractTrieMediator trieMediator;
	protected static CommandLine cmd;
	private static boolean quiet = false;
	private static boolean verbose = false;
	
	public static void run(CLI cli, String[] args, AbstractTrieMediator tm) {
		
		trieMediator = tm;
		
		cmd = cli.generateCommandLine(cli.generateOptions(), args);
				
		if (cmd.getArgList().isEmpty()) {
		
			cli.printHelp(cli.generateOptions());
			System.exit(1);
		}
		
		Set<Option> processedOpts = new HashSet<>();
		
		for (Option o : cmd.getOptions()) {
			if (!processedOpts.contains(o)) {
				((LambdaOption) o).doOperation();
				processedOpts.add(o);
			}
		}
		
		addFiles(cmd.getArgList());
		
		if (!verbose) setQuiet(true);
		
		trieMediator.run();
	}

	protected static void addFiles(List<String> files) {

		System.out.println("Adding files...");
		
		try {
			trieMediator.addFiles(files);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addMetric(MetricLabel m, Option o) {

		String[] args = cmd.getOptionValues(o.getLongOpt());
		
		if (args.length < o.getArgs()) {
			
			System.out.println("Option " + o.getArgName() + " requires " + o.getArgs() + " arguments. Received: " + args);
			System.exit(1);
		}
		
		for (int i=0; i<args.length; i += o.getArgs()) {
			
			String[] subArgs = Arrays.copyOfRange(args, i, i + o.getArgs());
			trieMediator.addMetric(m, subArgs);
		}
	}
	
	public static void addMetric(MetricLabel m) {
		trieMediator.addMetric(m, new String[0]);
	}
	
	public static void permitOutput() {
			System.setOut(ps);
	}
	
	public static void setQuiet(boolean b) {
		quiet = b;
		if (b) {
			System.setOut(new PrintStream(new NullOutputStream()));
			System.setErr(new PrintStream(new NullOutputStream()));
		} else {
			System.setOut(ps);
			System.setErr(ps);			
		}
	}
	
	public static void resetQuiet() {
		setQuiet(quiet);
	}
	
	public static void showTime(boolean b) {
		trieMediator.showTime(b);
	}
	
	public static void showProgress(boolean b) {
		trieMediator.showProgress(b);
	}
	
	public static void setTimeout(Option o) {
	
		String[] args = cmd.getOptionValues(o.getLongOpt());
		
		try {
			long seconds = Long.parseLong(args[0]); 
			trieMediator.setTimeout(TimeUnit.SECONDS.toNanos(seconds));
		} catch (NumberFormatException e) {
			System.out.println("Unexpected value for timeout option: " + Arrays.toString(args) + ". Using default.");
		}
	}
	
	public static void setSigDigits(Option o) {
		
		String[] args = cmd.getOptionValues(o.getLongOpt());
		
		try {
			int digits = Integer.parseInt(args[0]);
			trieMediator.setSigDigits(digits);
		} catch (NumberFormatException e) {
			System.out.println("Unexpected value for significant digits option: " + Arrays.toString(args)+ ". Using default.");
		}
	}

	public static void setPartition(Option o) {
		
		String[] args = cmd.getOptionValues(o.getLongOpt());
		
		try {
			int k = Integer.parseInt(args[0]);
			trieMediator.setPartition(k);
		} catch (NumberFormatException e) {
			System.out.println("Unexpected value for partition option: " + Arrays.toString(args)+ ". Using default.");
		}
	}
	
	public static void setFlatten(boolean b) {
		
		trieMediator.setFlatten(b);
	}
	
	public static void setVerbose(boolean b) {
		
		trieMediator.setVerbose(b);
	}
}