package org.qmpm.logtrie.tools;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.qmpm.logtrie.exceptions.FileLoadException;

public class XLogFile implements FileInfo {

	private File file;
	private String path;
	private XLog log = null;
	
	public XLogFile(String filePath) {
		
		file = new File(filePath);
		path = filePath;
	}
	
	@Override
	public File getFile() {
		return file;
	}


	public String getPath() {
		return path;
	}
	
	public void loadFile() {
		
		try {
			log = XESTools.loadXES(path);
		} catch (FileLoadException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Collection<? extends List<? extends Object>> getLoadedFile() {
		
		if (log == null) loadFile();
		return log;
	}
	
	public boolean fileLoaded() {
		return (log != null);
	}

}
