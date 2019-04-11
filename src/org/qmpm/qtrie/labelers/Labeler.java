package org.qmpm.qtrie.labelers;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.qmpm.qtrie.exceptions.LabelTypeException;
import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;
import org.qmpm.qtrie.tools.XESTools;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class Labeler<L> {

	protected BiMap<String, L> map = HashBiMap.create();

	abstract L newValue(String o) throws ValueOutOfBoundsException;

	public L get(Object o) throws ValueOutOfBoundsException {

		String key = this.toString(o);

		if (this.map.containsKey(key)) {
			return this.map.get(key);
		} else {
			L val = this.newValue(key);
			this.map.put(key, val);
			return val;
		}
	}

	public String getInverse(L label) throws Exception {

		if (this.map.inverse().containsKey(label)) {
			return this.map.inverse().get(label);
		} else {
			System.out.println(this.map);
			throw new Exception("Label (" + label + ") not assigned to object");
		}
	}

	public String toString(Object o) {

		if (o == null) {
			return "NULL";
		} else if (o instanceof String) {
			return (String) o;
		} else if (o instanceof XEvent) {
			try {
				XEvent x = (XEvent) o;
				return XESTools.xEventName(x);
			} catch (LabelTypeException e) {
				e.printStackTrace();
				return "ERROR: converting XEvent to string (" + o.hashCode() + ")";
			}
		} else if (o instanceof Transition) {
			Transition t = (Transition) o;
			return t.getLabel().contains("tau") ? "tau-" + t.getId().toString() : t.getLabel();
		} else {
			return o.toString();
		}
	}

}
