package org.qmpm.logtrie.tools;

import java.io.File;
import java.util.List;

import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.exceptions.FileLoadException;
import org.qmpm.logtrie.exceptions.LabelTypeException;

public class XESTools {

	public static XLog loadXES(String logPath) throws FileLoadException{
		
		File f = new File(logPath);

		if (f.isFile() ) {
		
			if (logPath.toLowerCase().endsWith("xes")) {
			
				if (canParse(logPath)) {
				
					XLog log = load(logPath).get(0); 
					return log;

				} else throw new FileLoadException("ERROR: (cannot parse file!): " + logPath);
			
			} else throw new FileLoadException("ERROR: (wrong extension!): " + logPath);
		
		} else throw new FileLoadException("ERROR: (not a file!): " + logPath); 
	}
	
	public static List<XLog> load(String path) {
		
		File file = read(path);
		List<XLog> xLogs = null;
		XesXmlParser xesXmlParser = new XesXmlParser();
		XesXmlGZIPParser xesXmlGZIPParser = new XesXmlGZIPParser();
		
		if (xesXmlParser.canParse(file)) {
		
			xLogs = parse(file, xesXmlParser);
		
		} else if (xesXmlGZIPParser.canParse(file)) {
		
			xLogs = parse(file, xesXmlGZIPParser);
		
		} else System.err.println("File format can't be parsed!");
		
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

	// TODO: Improve XEvent parsing
	public static String xEventName(XEvent event) throws LabelTypeException {
		
		XAttributeMap xaMap = event.getAttributes();
		
		if (xaMap.containsKey("concept:name")) {
			XAttribute xa = xaMap.get("concept:name");
			return xa.toString();
		} else throw new LabelTypeException("ERROR: (cannot find 'concept:name' entry for XEvent");
	}
}
