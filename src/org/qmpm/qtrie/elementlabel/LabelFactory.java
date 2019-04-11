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

package org.qmpm.qtrie.elementlabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.qmpm.qtrie.exceptions.LabelTypeException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class LabelFactory {

	/*
	 * public static <T> ElementLabel build(T element) throws LabelTypeException {
	 * return build(element, null); }
	 */

	static Map<Object, ElementLabel> labelMap = new HashMap<>();
	static BiMap<ElementLabel, Integer> lblIntMap = HashBiMap.create();

	// abstract long getMinimum();

	// abstract long getMaximum();

	public static ElementLabel build(Object element, Map<ElementLabel, Integer> map) throws LabelTypeException {
		return build(element);
	}

	public static ElementLabel build(Object element) throws LabelTypeException {

		if (element instanceof ElementLabel) {

			ElementLabel label = (ElementLabel) element;

			if (!lblIntMap.containsKey(label)) {
				lblIntMap.put(label, lblIntMap.size());
			}
			return label;

		}

		/*
		 * else if (element instanceof Integer) {
		 *
		 * System.out.println("INTEGER!");
		 *
		 * ElementLabel label = new IntegerLabel((Integer) element);
		 *
		 * if (!lblIntMap.containsKey(label)) { lblIntMap.put(label, lblIntMap.size());
		 * } return label; }
		 */
		/*
		 * else if (element instanceof XEvent) {
		 *
		 * return new XEventStrLbl((XEvent) element);
		 *
		 * }
		 */
		else if (element instanceof String) {

			ElementLabel label = new StringLabel((String) element);

			if (!lblIntMap.containsKey(label)) {
				lblIntMap.put(label, lblIntMap.size());
			}
			return label;

		} else if (element instanceof Transition) {

			// TODO: Disentangle this

			ElementLabel label = new PNTransitionLabel((Transition) element);

			if (!lblIntMap.containsKey(label)) {
				lblIntMap.put(label, lblIntMap.size());
			}
			return label;

		} else if (element instanceof XEvent) {

			if (labelMap.containsKey(element)) {
				return labelMap.get(element);
			} else {

				ElementLabel label = new XEventStrLbl((XEvent) element);

				if (!lblIntMap.containsKey(label)) {
					lblIntMap.put(label, lblIntMap.size());
				}
				labelMap.put(element, label);
				return label;
			}

		} else {

			throw new LabelTypeException("ERROR: Element of type " + element.getClass().getName()
					+ " cannot be converted to an ElementLabel");

		}
	}

	public static Integer intEncoding(ElementLabel l) {

		if (!lblIntMap.containsKey(l)) {
			try {
				build(l, null);
			} catch (LabelTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
		return lblIntMap.get(l);
	}

	public static Integer intEncoding(Object l) {

		if (!lblIntMap.containsKey(l)) {
			try {
				ElementLabel label = build(l, null);
				return intEncoding(label);
			} catch (LabelTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
		return lblIntMap.get(l);
	}

	public static ElementLabel labelEncoding(int i) {

		return lblIntMap.inverse().get(i);
	}

	public static List<ElementLabel> labelEncoding(List<Integer> seq) {

		List<ElementLabel> result = new ArrayList<>();

		for (int i : seq) {
			result.add(labelEncoding(i));
		}

		return result;
	}

	public static Integer size() {
		return lblIntMap.size();
	}

	public static Map<ElementLabel, Integer> getEncoding() {
		return lblIntMap;
	}

	public static String toString(List<Integer> seq) {

		String result = "";

		for (int i : seq) {
			result += labelEncoding(i).toString();
		}

		return result;
	}
}
