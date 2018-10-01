package org.qmpm.logtrie.elementlabel;

import org.deckfour.xes.model.XEvent;
import org.qmpm.logtrie.exceptions.LabelTypeException;
import org.qmpm.logtrie.tools.XESTools;

public class XEventStrLbl extends ElementLabel {
	
	private XEvent event;
	private String label;
	
	XEventStrLbl(XEvent e) throws LabelTypeException {
		
		event = e;
		label = XESTools.xEventName(e);
	}
	
	public XEvent getElement() {
		return event;
	}

	public String getLabel() {
		return label;
	}
		
	public String customToString() {
		return label;
	}

}