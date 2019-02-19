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

package org.qmpm.logtrie.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.output.NullOutputStream;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;
import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.exceptions.FileLoadException;
import org.qmpm.logtrie.exceptions.LabelTypeException;

public class XESTools {

	public static XLog loadXES(String logPath, boolean sortByTimeStamp) throws FileLoadException{
		
		File f = new File(logPath);

		if (f.isFile() ) {
		
			if (logPath.toLowerCase().endsWith("xes")) {
			
				if (canParse(logPath)) {
				
					XLog log = load(logPath).get(0);
					
					/*
					if (sortByTimeStamp) {
						sortByTimeStamp(log);
					}
					*/
					
					return log;

				} else throw new FileLoadException("ERROR: (cannot parse file!): " + logPath);
			
			} else throw new FileLoadException("ERROR: (wrong extension!): " + logPath);
		
		} else throw new FileLoadException("ERROR: (not a file!): " + logPath); 
	}
	
	private static List<XLog> load(String path) throws FileLoadException {
		
		PrintStream ps = System.out;
		System.setOut(new PrintStream(new NullOutputStream()));
		System.setErr(new PrintStream(new NullOutputStream()));
		
		File file = read(path);
		List<XLog> xLogs = null;
		XesXmlParser xesXmlParser = new XesXmlParser();
		XesXmlGZIPParser xesXmlGZIPParser = new XesXmlGZIPParser();
		
		if (xesXmlParser.canParse(file)) {
		
			xLogs = parse(file, xesXmlParser);
		
		} else if (xesXmlGZIPParser.canParse(file)) {
		
			xLogs = parse(file, xesXmlGZIPParser);
		
		} else throw new FileLoadException("File format can't be parsed!");
		
		System.setOut(ps);
		System.setErr(ps);
		
		return xLogs;
	}
	
	public static boolean canParse(String path) {
		
		File file = read(path);
		XesXmlParser xesXmlParser = new XesXmlParser();
		XesXmlGZIPParser xesXmlGZIPParser = new XesXmlGZIPParser();
		
		if (xesXmlParser.canParse(file)) {
		
			return true;
		
		} else if (xesXmlGZIPParser.canParse(file)) {
		
			return true;
			
		} else return false;
	
	}

	private static File read(String path) {
	
		File file = null;
		
		try {
			file = new File(path);
		} catch (NullPointerException e) {
			System.err.println("Problem loading file: " + path);
			e.printStackTrace();
			System.exit(1);
		}
		
		return file;
	}
	
	private static List<XLog> parse(File file, XesXmlParser parser) {
		
		List<XLog> xLogs = null;
		
		try {
			xLogs = parser.parse(file);
		} catch (Exception e) {
			Framework.permitOutput();
			System.err.println("Unexpected problem parsing file");
			e.printStackTrace();
			System.exit(1);
		}
		
		return xLogs;
	}
	
	private static List<XLog> parse(File file, XesXmlGZIPParser parser) {
		
		List<XLog> xLogs = null;
		
		try {
			xLogs = parser.parse(file);
		} catch (Exception e) {
			System.err.println("Unexpected problem parsing file");
			e.printStackTrace();
			System.exit(1);
		}
		
		return xLogs;
	}
	
	public static String xTraceToString(final XTrace trace) throws LabelTypeException {
		
		String traceAsString = "$";
		
		for (XEvent event : trace) {
			if (event != null) {
				traceAsString += xEventName(event);
			}
		}
		
		return traceAsString;
	}
	
	public static String xTraceID(XTrace trace) throws LabelTypeException {
		
		XAttributeMap xaMap = trace.getAttributes();

		if (xaMap.containsKey("concept:name")) {
			XAttribute xa = xaMap.get("concept:name");
			return xa.toString();
		} else throw new LabelTypeException("ERROR: (cannot find 'concept:name' entry for XTrace");
	}

	// TODO: Improve XEvent parsing
	public static String xEventName(XEvent event) throws LabelTypeException {
		
		XAttributeMap xaMap = event.getAttributes();
		
		if (xaMap.containsKey("concept:name")) {
			XAttribute xa = xaMap.get("concept:name");
			return xa.toString();
		} else throw new LabelTypeException("ERROR: (cannot find 'concept:name' entry for XEvent");
	}
	
	public static String xTraceTimeStamp(XTrace trace) throws LabelTypeException {
		
		XAttributeMap xaMap = trace.get(0).getAttributes();

		if (xaMap.containsKey("time:timestamp")) {
			XAttribute xa = xaMap.get("time:timestamp");
			return xa.toString();
		} else throw new LabelTypeException("ERROR: (cannot find 'time:timestamp' entry for XEvent");
	}

	
	public static void saveFile(XLog log, String path) throws FileNotFoundException, IOException {
		
		File f = new File(path);
		
		f.getParentFile().mkdirs();
		f.createNewFile();
		
		XesXmlSerializer xesSerial = new XesXmlSerializer();
		xesSerial.serialize(log, new FileOutputStream(f));
	}
	
	public static void saveFile(XLog log, File f) throws FileNotFoundException, IOException {
		
		f.getParentFile().mkdirs();
		f.createNewFile();
		
		XesXmlSerializer xesSerial = new XesXmlSerializer();
		xesSerial.serialize(log, new FileOutputStream(f));
	}
	
	public static boolean isSorted(XTrace t) {
		
		for (int i=0; i<t.size()-1; i++) {
			if (0 < LocalDateTime.parse(t.get(i).getAttributes().get("time:timestamp").toString(),  DateTimeFormatter.ISO_OFFSET_DATE_TIME).compareTo(
					LocalDateTime.parse(t.get(i+1).getAttributes().get("time:timestamp").toString(),  DateTimeFormatter.ISO_OFFSET_DATE_TIME))) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isSorted(XLog l) {
		
		for (XTrace t : l) {
			if (!isSorted(t)) {
				return false;
			}
		}

		for (int i=0; i<l.size()-1; i++) {
			if (0 < LocalDateTime.parse(l.get(i).get(0).getAttributes().get("time:timestamp").toString(),  DateTimeFormatter.ISO_OFFSET_DATE_TIME).compareTo(
					LocalDateTime.parse(l.get(i+1).get(0).getAttributes().get("time:timestamp").toString(),  DateTimeFormatter.ISO_OFFSET_DATE_TIME))) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void sortByTimeStamp (XTrace t) {
		
		t.sort((x,y) ->(LocalDateTime.parse( ((XEvent) x).getAttributes().get("time:timestamp").toString(),  DateTimeFormatter.ISO_OFFSET_DATE_TIME).compareTo(
						LocalDateTime.parse( ((XEvent) y).getAttributes().get("time:timestamp").toString(),  DateTimeFormatter.ISO_OFFSET_DATE_TIME))));
	}
	
	public static void sortByTimeStamp (XLog l) {
		
		for (XTrace t : l) {
			sortByTimeStamp(t);
		}
		
		l.sort((x,y) ->(LocalDateTime.parse( ((XTrace) x).get(0).getAttributes().get("time:timestamp").toString(),  DateTimeFormatter.ISO_OFFSET_DATE_TIME).compareTo(
						LocalDateTime.parse( ((XTrace) y).get(0).getAttributes().get("time:timestamp").toString(),  DateTimeFormatter.ISO_OFFSET_DATE_TIME))));
	}
	
	public static Set<String> getAllActivities(XLog l) {
		
		Set<String> result = new HashSet<>();
		
		for (XTrace t : l) {
			for (XEvent e : t) {
				result.add(e.getAttributes().get("concept:name").toString());
			}
		}
		
		return result;
	}
}