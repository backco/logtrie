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

package org.qmpm.logtrie.elementlabel;

import java.util.Map;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.qmpm.logtrie.exceptions.LabelTypeException;
import org.qmpm.logtrie.tools.XESTools;

public final class LabelFactory {

	/*
	public static <T> ElementLabel build(T element) throws LabelTypeException {
		return build(element, null);
	}
	*/
	
	public static <S> ElementLabel build(Object element, Map<String, S> encodingScheme) throws LabelTypeException {
		if (element instanceof ElementLabel) {
			
			return (ElementLabel) element;
		
		} 
		/*
		else if (element instanceof XEvent) {
		
			return new XEventStrLbl((XEvent) element);
		
		}
		*/ 
		else if (element instanceof String) {
			return new StringLabel((String) element);
		
		} else if (element instanceof Transition) {
		
			// TODO: Disentangle this
			return new PNTransitionLabel((Transition) element);
		
		} else if (element instanceof XEvent) {
			
			if (encodingScheme == null) return new XEventStrLbl((XEvent) element);
 			
			String name = XESTools.xEventName((XEvent) element);
			
			return new XEventIntLbl<S>((XEvent) element, encodingScheme.get(name));			
			
		} else {
			
			throw new LabelTypeException("ERROR: Element of type " + element.getClass().getName() + " cannot be converted to an ElementLabel");
		
		}
	}
}
