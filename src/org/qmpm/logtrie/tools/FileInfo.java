package org.qmpm.logtrie.tools;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface FileInfo {
	
	public File getFile();
	public Collection<? extends List<? extends Object>> getLoadedFile();
	public boolean fileLoaded();
}
