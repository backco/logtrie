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

import org.deckfour.xes.model.XEvent;
import org.qmpm.qtrie.exceptions.LabelTypeException;

public class XEventIntLbl<S> extends ElementLabel {
	
	private XEvent event;
	private S label;
	
	XEventIntLbl(XEvent e, S i) throws LabelTypeException {
		
		event = e;
		label = i;
	}
	
	public XEvent getElement() {
		return event;
	}

	public S getLabel() {
		return label;
	}
		
	public String customToString() {
		return label.toString();
	}

}