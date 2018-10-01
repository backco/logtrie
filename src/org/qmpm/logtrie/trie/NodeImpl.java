package org.qmpm.logtrie.trie;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.qmpm.logtrie.elementlabel.ElementLabel;
import org.qmpm.logtrie.trie.Trie.Node;

public class NodeImpl implements Node {
	
	private Map<String, Object> attributes = new HashMap<>();
	private Map<ElementLabel, Node> children = new HashMap<>();
	private boolean isEnd = false; // Denotes whether this node correspond to the end of a sequence? (Regardless of whether it is a leaf)
	private boolean isLeaf = true;
	private boolean isRoot = false;
	private boolean flag = false;
	private int visits = 0;
	private int endVisits = 0;
	private int depth = -1;
	private int ID;
	private ElementLabel parentEdgeLabel = null; // Name of edge label from parent to child
	private String name = "";
	protected Node parent = null;
	
	protected NodeImpl(ElementLabel parentEdgeLabel, Node parent, Trie trie) {
		
		this.parentEdgeLabel = parentEdgeLabel;
		
		if ((parentEdgeLabel == TrieImpl.ROOT_ACTIVITY)) { // TODO: Fix this weirdness

			this.name = TrieImpl.ROOT_NAME;
			this.isRoot = true;
			this.depth = 0;
		
		} else {
			
			this.name = "node" + trie.getSize();
			this.ID = trie.getSize();
			this.parent = parent;
			parent.addChild(parentEdgeLabel, this);
			this.depth = getParent().getDepth()+1;
		}

		trie.incrementSize();
		trie.addNode(this);
		
		if (this.depth > trie.getLongestBranch()) {
			trie.setLongestBranch(this.depth);
		}
	}
	
	public int getID() {
		return this.ID;
	}
	
	public void decrementVisits() {
		this.visits--;
	}

	public void decrementEndVisits() {
		this.endVisits--;
	}
	
	public Map<ElementLabel, Node> getChildren() {
		return this.children;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public boolean getFlag() {
		return this.flag;
	}
	
	public boolean getIsEnd() {
		return this.isEnd;
	}
	
	public boolean getIsLeaf() {
		return this.isLeaf;
	}
	
	public boolean getIsRoot() {
		return this.isRoot;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Set<ElementLabel> getChildEdgeLabels() {
		return this.getChildren().keySet();
	}
	
	public Node getParent() {
		return this.parent;
	}
	
	public int getVisits() {
		return this.visits;
	}

	public int getEndVisits() {
		return this.endVisits;
	}
	
	public void incrementVisits() {
		this.visits++;
	}

	public void incrementEndVisits() {
		this.endVisits++;
	}
	
	public void setFlag(boolean value) {
		this.flag = value;
	}
	
	public void setIsEnd(boolean isEnd ) {
		this.isEnd = isEnd;
	}
	
	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	public void setIsRoot(boolean isRoot) {
		this.isRoot = true;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		
		String parent = "N/A";

		if (!this.getIsRoot()) {
			parent = this.getParent().getParentEdgeLabel().toString();
		}
		
		String output = "";
		output += "Depth   : " + this.depth + System.getProperty("line.separator");
		output += "Name    : " + this.getName() + System.getProperty("line.separator");
		output += "Visits  : " + this.getVisits() + System.getProperty("line.separator");
		output += "Parent  : " + parent + System.getProperty("line.separator");
		output += "Edge Label: " + this.parentEdgeLabel + System.getProperty("line.separator");
    	output += "Children: " + this.getChildren().keySet() + System.getProperty("line.separator");
    	output += System.getProperty("line.separator");
    	
    	return output;
	}
	
	public void addChild(ElementLabel edgeLabel, Node node) {
		this.children.put(edgeLabel, node);
	}

	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}

	public ElementLabel getParentEdgeLabel() {
		return this.parentEdgeLabel;
	}

	public boolean hasAttribute(String key) {
		return this.attributes.containsKey(key);
	}

	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}

	public void setParentEdgeLabel(ElementLabel label) {
		this.parentEdgeLabel = label;
	}
}
