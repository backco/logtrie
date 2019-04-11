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
