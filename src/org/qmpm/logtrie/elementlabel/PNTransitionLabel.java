package org.qmpm.logtrie.elementlabel;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class PNTransitionLabel extends ElementLabel {

	Transition transition;
	String label;
	
	public PNTransitionLabel(Transition t) {

		transition = t;
		label = t.getLabel().contains("tau") ? "tau-" + t.getId().toString() : t.getLabel();
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public String customToString() {
		return label;
	}

}
