package org.qmpm.logtrie.elementlabel;

public class StringLabel extends ElementLabel {

	String element;
	String label;
	
	public StringLabel(String s) {

		element = s;
		label = s;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public String customToString() {
		return label;
	}

}
