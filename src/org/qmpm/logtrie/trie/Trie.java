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

package org.qmpm.logtrie.trie;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.qmpm.logtrie.elementlabel.ElementLabel;
import org.qmpm.logtrie.exceptions.LabelTypeException;
import org.qmpm.logtrie.exceptions.ProcessTransitionException;

public interface Trie {
	
	interface Node {
		
		void addChild(ElementLabel edgeLabel, Node node);
		void decrementEndVisits();
		void decrementVisits();
		public Object getAttribute(String key);
		public Set<ElementLabel> getChildEdgeLabels();
		public Map<ElementLabel, ? extends Node> getChildren();
		public int getDepth();
		public int getEndVisits();
		public int getID();
		public boolean getIsEnd();
		public boolean getIsLeaf();
		public boolean getIsRoot();
		public String getName();
		public Node getParent();
		public ElementLabel getParentEdgeLabel();
		public int getVisits();
		public boolean hasAttribute(String key);
		void incrementEndVisits();
		void incrementVisits();
		void setAttribute(String key, Object value);
		void setIsEnd(boolean isEnd);
		void setIsLeaf(boolean isLeaf);
		void setName(String name);
	}
	
	 
	 void addNode(Node n);
	 List<Node> cloneNodes(List<Node> original);
	 Node createNode(ElementLabel edgeLabel, Node parent) throws Exception;
	 String draw();
	 Object getAttribute(String key);
	 Set<ElementLabel> getElementLabels();
	 List<Node> getEndNodeSet();
	 Set<Node> getLeafNodeSet();
	 int getLongestBranch();
	 Set<Node> getNodeSet(boolean includeRoot);
	 Node getRoot();
	 int getSize();
	 int getTotalEndVisits(boolean includeRoot);
	 int getTotalVisits(boolean includeRoot);
	 List<ElementLabel> getVisitingPrefix(Node node);
	 boolean hasAttribute(String key);
	 void incrementSize();
	 Node insert(List<? extends Object> sequence, boolean flatten) throws LabelTypeException,ProcessTransitionException;
	 void kill();	
	 double medianBranchLength();
	 List<List<ElementLabel>> rebuildSequences();
	 void remove(List<Object> sequence) throws LabelTypeException;
	 void remove(Node endNode);
	 Node search(List<Object> sequence) throws LabelTypeException;
     <V> void setAttribute(String key, V value);
     void setLongestBranch(int length);
	 void setRoot(ElementLabel rootActivity, String rootName, Node rootParent);
	 String toString();
}
