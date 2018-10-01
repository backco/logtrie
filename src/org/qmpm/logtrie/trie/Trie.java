package org.qmpm.logtrie.trie;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.qmpm.logtrie.elementlabel.ElementLabel;
import org.qmpm.logtrie.exceptions.LabelTypeException;

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
	 Node createNode(ElementLabel edgeLabel, Node parent);
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
	 Node insert(List<? extends Object> sequence, boolean flatten) throws LabelTypeException;
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
