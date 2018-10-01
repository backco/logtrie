package org.qmpm.logtrie.elementlabel;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.qmpm.logtrie.exceptions.LabelTypeException;

public class LabelFactory {

	public static <T> ElementLabel build(T element) throws LabelTypeException {
		if (element instanceof ElementLabel) {
			
			return (ElementLabel) element;
		
		} else if (element instanceof XEvent) {
		
			return new XEventStrLbl((XEvent) element);
		
		} else if (element instanceof String) {
		
			return new StringLabel((String) element);
		
		} else if (element instanceof Transition) {
		
			// TODO: Disentangle this
			return new PNTransitionLabel((Transition) element);
		
		} else {
			
			throw new LabelTypeException("ERROR: Element of type " + element.getClass().getName() + " cannot be converted to an ElementLabel");
		
		}
	}
}
