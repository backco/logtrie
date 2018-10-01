package org.qmpm.logtrie.tools;

public class FileInfoFactory {
	
	public static FileInfo build(String path) {
		
		if (path.toLowerCase().endsWith("xes")) {
			return new XLogFile(path);
		}
		
		return null;
	}
}
