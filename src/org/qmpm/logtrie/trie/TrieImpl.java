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

import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.qmpm.logtrie.elementlabel.ElementLabel;
import org.qmpm.logtrie.elementlabel.LabelFactory;
import org.qmpm.logtrie.elementlabel.StringLabel;
import org.qmpm.logtrie.exceptions.LabelTypeException;
import org.qmpm.logtrie.exceptions.ProcessTransitionException;

import com.google.common.collect.HashBiMap;

public class TrieImpl implements Trie {
	
	final static ElementLabel ROOT_ACTIVITY = new StringLabel("N/A");
	final static String ROOT_NAME = "root";
	private HashBiMap<String, String> activityAbbrevMap = HashBiMap.create();
	private Map<String, Object> attributes = new HashMap<>();
	private Map<Integer, Integer> breadthMap = new HashMap<>();
	private Collection<? extends List<? extends Object>> collection = null;
	private Set<ElementLabel> elementLabels = new HashSet<>();
	private List<Node> endNodeSet = new ArrayList<>();
	private int longestBranch = 0;
	private Set<Node> nodeSet = new HashSet<>();
	protected Node root;
	private final Node ROOT_PARENT = null;
	private int size = 0;
	
	public TrieImpl() {
		setRoot(ROOT_ACTIVITY, ROOT_NAME, ROOT_PARENT);
	}

	public TrieImpl(Collection<? extends List<? extends Object>> collection) {
		this();
		this.collection = collection;
	}

	public void addNode(Node node) {
		this.nodeSet.add(node);
	}
	
	public List<Node> cloneNodes(List<Node> original) {
		
		List<Node> clone = new ArrayList<>();
		
		for (Node node : original) {
			clone.add(node);
		}
		
		return clone;
	}
	
	public Node createNode(ElementLabel label, Node parent) throws Exception {
		return new NodeImpl(label, parent, this);
	}

	@Override
	public String draw() {
		
		String out = "";
		List<Node> frontier = new ArrayList<>();
		List<Node> newFrontier = new ArrayList<>();
		frontier.add(this.getRoot());
		
		for (int i=0; i<this.getLongestBranch(); i++) {
			out = i + ": ";
			newFrontier.clear();
		
			for (Node node : frontier) {
				newFrontier.addAll((Collection<? extends Node>) node.getChildren().values());
				for (Node child : node.getChildren().values()) {
					out += child.getParentEdgeLabel().toString() + "(" + child.getVisits() + "),";
				}
				out += "  ";
			}		
			
			frontier.clear();
			frontier.addAll(newFrontier);
		}
		
		return out;
	}
	
	public void endNodeTodo() {
	}

	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}

	public Collection<? extends List<? extends Object>> getCollection() {
		return collection;
	}
	
	@Override
	public Set<ElementLabel> getElementLabels() {
		return elementLabels;
	}
	
	public List<Node> getEndNodeSet() {
		return this.endNodeSet;
	}
	
	public String getLabelAbbrev(String abbrev) {
		return this.activityAbbrevMap.get(abbrev);
	}
	
	public HashBiMap<String, String> getLabelAbbrevMap() {
		return this.activityAbbrevMap;
	}

	public Set<Node> getLeafNodeSet() {
		
		Set<Node> leafNodes = new HashSet<Node>();
		
		for (Node node : this.getNodeSet()) {
			if (node.getIsLeaf()) {
				leafNodes.add(node);
			}
		}
		
		return leafNodes;
	}
	
	public int getLongestBranch() {
		return this.longestBranch;
	}
	
	public Set<Node> getNodeSet() {
		return this.nodeSet;
	}
	
	public Set<Node> getNodeSet(boolean includeRoot) {
		
		if (includeRoot) {
			return this.nodeSet;
		} else {
			Set<Node> result = new HashSet<>();
			result.addAll(this.nodeSet);
			result.remove(this.getRoot());
			return result;
		}
	}
	
	public Node getRoot() {
		return this.root;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getTotalEndVisits(boolean includeRoot) {
		
		int totalEndVisits = 0;
		Set<Node> nodeSetTmp = new HashSet<>();
		nodeSetTmp.addAll(this.endNodeSet);
		
		if (!includeRoot) { 
			nodeSetTmp.remove(this.getRoot());
		}
		
		for (Node node : nodeSetTmp) {
			totalEndVisits += node.getEndVisits();
		}
		
		return totalEndVisits;
	}
	
	public int getTotalVisits(boolean includeRoot) {
		
		int totalVisits = 0;
		Set<Node> nodeSetTmp = new HashSet<>();
		nodeSetTmp.addAll(this.nodeSet);
		
		if (!includeRoot) 
			nodeSetTmp.remove(this.getRoot());
		for (Node node : nodeSetTmp) {
			totalVisits += node.getVisits();
		}
		
		return totalVisits;
	}
	
	public List<ElementLabel> getVisitingPrefix(Node node) {
		
		Node tmpNode = node;
		List<ElementLabel> prefix = new ArrayList<>();
		
		// If node is root node, return empty prefix
		if (node.getIsRoot()) {
			return prefix;
		}
		
		// Retrace trie to reconstruct prefix (it will be in reverse order)
		while (!(tmpNode.getIsRoot())) {
			prefix.add(tmpNode.getParentEdgeLabel());
			tmpNode = tmpNode.getParent();
		}

		Collections.reverse(prefix);
		
		return prefix;
	}
	
    public int getWidestLevel() {
		
    	int level = 0;
		int max = 0;
		
		for (Integer l : breadthMap.keySet()) {
			if (breadthMap.get(l) > max) {
				max = breadthMap.get(l);
				level = l;
			}
		}
		
		return level;
	}

	public boolean hasAttribute(String key) {
		return this.attributes.containsKey(key);
	}

	public void incrementSize() {
		this.size++;
	}

	public Node insert(List<? extends Object> sequence, boolean flatten) throws LabelTypeException {
		
		Node lastNode = this.getRoot();
		Map<ElementLabel, ? extends Node> children = this.getRoot().getChildren();
		lastNode.incrementVisits();
		
		for (int i=0; i<sequence.size(); i++) {
			
			ElementLabel label = null;
			label = LabelFactory.build(sequence.get(i));
			
			elementLabels.add(label);
			Node node;

			if (children.containsKey(label)) {
				node = children.get(label);
				if (i==sequence.size()-1) {
					if (node.getIsEnd() && flatten) {
						remove(node);
					} else {
						node.setIsEnd(true);
						node.incrementEndVisits();
						if (!this.endNodeSet.contains(node)) {
							this.endNodeSet.add(node);
						}
					}
				}
			} else {
				try {
					node = createNode(label, lastNode);
				} catch (Exception e) {
					remove(lastNode);
					return null;
				}
				node.getParent().setIsLeaf(false);
				Integer breadth = 1;

				if (breadthMap.containsKey(node.getDepth())) {
					breadth = breadthMap.get(node.getDepth());
				}
				
				breadthMap.put(node.getDepth(), breadth);
				
				if (i==sequence.size()-1) {
					node.setIsEnd(true);
					node.incrementEndVisits();
					this.endNodeSet.add(node);
					toDoAtEndOfInsert(node);
				}
			}
		
			node.incrementVisits();
			lastNode = node;
			children = node.getChildren();
		}
		
		return lastNode;
	}

	@SuppressWarnings("unused")
	public void kill() {
		
		for (Node n : nodeSet ) {
			n = null;
		}
	}
	
    public double medianBranchLength() {
		
		int i = 0;
		double[] lengths = new double[endNodeSet.size()];
		
		for (Node n : endNodeSet) {
			lengths[i++] = (double) n.getDepth();
		}
		
		return new Median().evaluate(lengths);
	}
	
	public void putActivityAbbrev(String activity) {
    	this.activityAbbrevMap.put("act" + this.activityAbbrevMap.size(), activity);
    }
    
	public List<List<ElementLabel>> rebuildSequences() {
		
		List<List<ElementLabel>> retSeqs = new ArrayList<>();
		
		for (Node endNode : this.getEndNodeSet()) {
			retSeqs.add(getVisitingPrefix(endNode));
		}
		
		return retSeqs;
	}
	
    public void remove(List<Object> sequence) throws LabelTypeException {
		
    	Node endNode = search(sequence);
		remove(endNode);
	}

    public void remove(Node endNode) {
    	
    	Node lastNode = endNode;
    	boolean finished = endNode.getIsRoot();
    	
    	while (!finished) {

    		lastNode.decrementVisits();
    		Node node = lastNode.getParent();
    		
    		if (lastNode.getIsLeaf() && (lastNode.getVisits()==1)) {
    			node.getChildren().remove(lastNode.getParentEdgeLabel());
    			this.nodeSet.remove(lastNode);
    		}
    		
    		if (node.getIsRoot()) {
    			node.decrementVisits();
    			finished = true;
    		}
    		
    		lastNode = node;
    	}
    }

	public Node search(List<Object> sequence) throws LabelTypeException{
    	
		Node root = getRoot();
        Map<ElementLabel, ? extends Node> children = root.getChildren(); 
        Node node = root;
        
        for(int i=0; i<sequence.size(); i++){
        	
        	ElementLabel label = null;
       		label = LabelFactory.build(sequence.get(i));

       		if(children.containsKey(label)){
                node = children.get(label);
                children = node.getChildren();
            } else {
                return null;
            }
        }
        
        return node;
    }

	public <V> void setAttribute(String key, V value) {
		this.attributes.put(key, value);
	}

	public void setLongestBranch(int length) {
		this.longestBranch = length;
	}

	public void setRoot(ElementLabel rootActivity, String rootName, Node rootParent) {
		
		this.root = new NodeImpl(rootActivity, rootParent, this);
    	this.root.setName(rootName);
    	//this.root.setParent((XESLogNode) rootParent);
	}

	protected void toDoAtEndOfInsert(Node node) {
	}

	@Override
    // Print out tree, depth-first
    public String toString() {
    	
		Node root = getRoot();
    	String output = "";
    	int counter = 0;
    	output += "Level: " + counter  + System.getProperty("line.separator");
    	output += "Label: " + root.getParentEdgeLabel() + System.getProperty("line.separator");
    	output += System.getProperty("line.separator");
    	output += toString(root, output, counter+1);
    	
    	return output;
    }
	
	public String toString(Node node,  String outputIn,  int counterIn) {
    	
		String output = "";
    	
		for (Node childNode : node.getChildren().values()) {
    		output += "Level	: " + counterIn + System.getProperty("line.separator");
    		output += "Name		: " + childNode.getName() + System.getProperty("line.separator");
    		output += "Visits	: " + childNode.getVisits() + System.getProperty("line.separator");
    		output += "Parent	: " + childNode.getParent().getName() + System.getProperty("line.separator");
    		output += "Label	: " + childNode.getParentEdgeLabel() + System.getProperty("line.separator");
        	output += "Children	: " + childNode.getChildren().keySet() + System.getProperty("line.separator");
        	output += System.getProperty("line.separator");
        	output += toString(childNode, output, counterIn+1);
        }

    	return output;
    }
}
