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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.qmpm.logtrie.elementlabel.ElementLabel;
import org.qmpm.logtrie.elementlabel.LabelFactory;
import org.qmpm.logtrie.elementlabel.StringLabel;
import org.qmpm.logtrie.exceptions.LabelTypeException;
//import org.qmpm.logtrie.exceptions.ProcessTransitionException;
import org.qmpm.logtrie.exceptions.NodeNotFoundException;

import com.google.common.collect.HashBiMap;

public class TrieImpl implements Trie {

	final static ElementLabel ROOT_ACTIVITY = new StringLabel("ROOT");
	final static String ROOT_NAME = "root";
	private int attemptedInsertions = 0;
	private HashBiMap<String, String> activityAbbrevMap = HashBiMap.create();
	private Map<String, Object> attributes = new HashMap<>();
	private Map<Integer, Integer> breadthMap = new HashMap<>();
	private Set<ElementLabel> elementLabels = new HashSet<>();
	private List<Node> endNodeSet = new ArrayList<>();
	private int longestBranch = 0;
	private Set<Node> nodeSet = new HashSet<>();
	protected Node root;
	private final Node ROOT_PARENT = null;
	private int size = 0;
	private Trie associatedTrie = null;
	protected Map<String, ?> encodingScheme = null;

	public TrieImpl() {
		this.setRoot(ROOT_ACTIVITY, ROOT_NAME, this.ROOT_PARENT);
	}

	@Override
	public <T> void addElementLabel(T s) throws LabelTypeException {
		this.elementLabels.add(LabelFactory.build(s, this.getEncodingScheme()));
	}

	@Override
	public void addElementLabels(Set<?> s) throws LabelTypeException {
		for (Object l : s) {
			this.elementLabels.add(LabelFactory.build(l, this.encodingScheme));
		}
	}

	@Override
	public void addNode(Node node) {
		this.nodeSet.add(node);
	}

	@Override
	public List<Node> cloneNodes(List<Node> original) {

		List<Node> clone = new ArrayList<>();

		for (Node node : original) {
			clone.add(node);
		}

		return clone;
	}

	@Override
	public Node createNode(ElementLabel label, Node parent) throws Exception {
		return new NodeImpl(label, parent, this);
	}

	@Override
	public String draw() {

		System.out.println("Trie " + this.hashCode());
		System.out.println("longestBranch: " + this.getLongestBranch());
		String out = "";
		List<Node> frontier = new ArrayList<>();
		List<Node> newFrontier = new ArrayList<>();
		frontier.add(this.getRoot());

		for (int i = 0; i < this.getLongestBranch(); i++) {

			System.out.println("i: " + i);

			out = i + ": ";
			newFrontier.clear();

			for (Node node : frontier) {
				newFrontier.addAll(node.getChildren().values());
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

	@Override
	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}

	@Override
	public int getAttemptedInsertions() {

		return this.attemptedInsertions;
	}

	@Override
	public Set<ElementLabel> getElementLabels() throws Exception {

		if (this.elementLabels.isEmpty()) {
			throw new Exception("Prefix tree is empty. It has probably not been built yet.");
		}
		return this.elementLabels;
	}

	@Override
	public List<Node> getEndNodeSet() {
		return this.endNodeSet;
	}

	public String getLabelAbbrev(String abbrev) {
		return this.activityAbbrevMap.get(abbrev);
	}

	public HashBiMap<String, String> getLabelAbbrevMap() {
		return this.activityAbbrevMap;
	}

	@Override
	public Set<Node> getLeafNodeSet() {

		Set<Node> leafNodes = new HashSet<>();

		for (Node node : this.getNodeSet()) {
			if (node.getIsLeaf()) {
				leafNodes.add(node);
			}
		}

		return leafNodes;
	}

	@Override
	public int getLongestBranch() {
		return this.longestBranch;
	}

	public Set<Node> getNodeSet() {
		return this.nodeSet;
	}

	@Override
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

	@Override
	public Node getRoot() {
		return this.root;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
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

		System.out.println("TotalEndVisits: " + totalEndVisits);

		return totalEndVisits;
	}

	@Override
	public int getTotalVisits(boolean includeRoot) {

		int totalVisits = 0;
		Set<Node> nodeSetTmp = new HashSet<>();
		nodeSetTmp.addAll(this.nodeSet);

		if (!includeRoot) {
			nodeSetTmp.remove(this.getRoot());
		}
		for (Node node : nodeSetTmp) {
			totalVisits += node.getVisits();
		}

		return totalVisits;
	}

	@Override
	public List<ElementLabel> getVisitingPrefix(Node node) {

		Node tmpNode = node;
		List<ElementLabel> prefix = new ArrayList<>();

		// If node is root node, return empty prefix
		if (node.getIsRoot()) {
			return prefix;
		}

		// Retrace trie to reconstruct prefix (it will be in reverse order)
		while (!tmpNode.getIsRoot()) {
			prefix.add(tmpNode.getParentEdgeLabel());
			tmpNode = tmpNode.getParent();
		}

		Collections.reverse(prefix);

		return prefix;
	}

	public int getWidestLevel() {

		int level = 0;
		int max = 0;

		for (Integer l : this.breadthMap.keySet()) {
			if (this.breadthMap.get(l) > max) {
				max = this.breadthMap.get(l);
				level = l;
			}
		}

		return level;
	}

	@Override
	public boolean hasAttribute(String key) {
		return this.attributes.containsKey(key);
	}

	@Override
	public void incrementSize() {
		this.size++;
	}

	@Override
	public Node insert(List<?> sequence, boolean flatten) throws LabelTypeException {
		this.attemptedInsertions++;
		Node lastNode = this.getRoot();
		Map<ElementLabel, ? extends Node> children = this.getRoot().getChildren();
		lastNode.incrementVisits();

		// System.out.println("inserting: " + XESTools.xTraceToString((XTrace)
		// sequence));
		// System.out.println("sequence.size: " + sequence.size());
		for (int i = 0; i < sequence.size(); i++) {
			ElementLabel label = null;
			label = LabelFactory.build(sequence.get(i), this.encodingScheme);
			this.addElementLabel(label);

			Node node;

			if (children.containsKey(label)) {
				node = children.get(label);
				if (i == sequence.size() - 1) {
					if (node.getIsEnd() && flatten) {
						this.remove(node);
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
					node = this.createNode(label, lastNode);
				} catch (Exception e) {

					this.remove(lastNode);
					return null;
				}
				node.getParent().setIsLeaf(false);
				Integer breadth = 1;

				if (this.breadthMap.containsKey(node.getDepth())) {
					breadth = this.breadthMap.get(node.getDepth());
				}

				this.breadthMap.put(node.getDepth(), breadth);

				if (i == sequence.size() - 1) {
					node.setIsEnd(true);
					node.incrementEndVisits();
					this.endNodeSet.add(node);
					this.toDoAtEndOfInsert(node);
				}
			}

			node.incrementVisits();
			lastNode = node;
			children = node.getChildren();
		}

		return lastNode;
	}

	@Override
	@SuppressWarnings("unused")
	public void kill() {

		for (Node n : this.nodeSet) {
			n = null;
		}
	}

	@Override
	public double medianBranchLength() {

		int i = 0;
		double[] lengths = new double[this.endNodeSet.size()];

		for (Node n : this.endNodeSet) {
			lengths[i++] = n.getDepth();
		}

		return new Median().evaluate(lengths);
	}

	public void putActivityAbbrev(String activity) {
		this.activityAbbrevMap.put("act" + this.activityAbbrevMap.size(), activity);
	}

	@Override
	public List<List<ElementLabel>> rebuildSequences(boolean flatten) {

		List<List<ElementLabel>> retSeqs = new ArrayList<>();

		for (Node endNode : this.getEndNodeSet()) {
			List<ElementLabel> seq = this.getVisitingPrefix(endNode);
			for (int i = 0; i < (flatten ? 1 : endNode.getEndVisits()); i++) {
				retSeqs.add(seq);
			}
		}

		return retSeqs;
	}

	@Override
	public void remove(List<?> sequence) throws LabelTypeException, NodeNotFoundException {

		Node endNode = this.search(sequence);
		this.remove(endNode);
	}

	@Override
	public void remove(Node endNode) {

		Node lastNode = endNode;
		boolean finished = endNode.getIsRoot();

		while (!finished) {

			lastNode.decrementVisits();
			Node node = lastNode.getParent();

			if (lastNode.getIsLeaf() && lastNode.getVisits() == 1) {
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

	@Override
	public Node search(List<?> sequence) throws LabelTypeException, NodeNotFoundException {

		Node root = this.getRoot();
		Map<ElementLabel, ? extends Node> children = root.getChildren();
		Node node = root;

		for (int i = 0; i < sequence.size(); i++) {

			ElementLabel label = null;
			label = LabelFactory.build(sequence.get(i), this.encodingScheme);

			if (children.containsKey(label)) {
				node = children.get(label);
				children = node.getChildren();
			} else {
				throw new NodeNotFoundException("Could not find " + label + " in children: " + children.keySet());
			}
		}

		return node;
	}

	@Override
	public <V> void setAttribute(String key, V value) {
		this.attributes.put(key, value);
	}

	@Override
	public void setLongestBranch(int length) {
		this.longestBranch = length;
	}

	@Override
	public void setRoot(ElementLabel rootActivity, String rootName, Node rootParent) {

		this.root = new NodeImpl(rootActivity, rootParent, this);
		this.root.setName(rootName);
		// this.root.setParent((XESLogNode) rootParent);
	}

	protected void toDoAtEndOfInsert(Node node) {
	}

	// Print out tree using depth-first search
	private String toString(Node n, String prefix, boolean lastSibling) {
		StringBuilder subTree = new StringBuilder();
		subTree.append(System.lineSeparator() + prefix);
		subTree.append(n.getIsRoot() ? "" : lastSibling ? "└── " : "├── ");
		subTree.append(n.getParentEdgeLabel().toString() + " : " + n.getVisits()
				+ (n.getIsEnd() ? "(E " + n.getEndVisits() + ")" : ""));
		prefix += n.getIsRoot() ? "" : lastSibling ? "    " : "│   ";

		int c = 1;
		for (ElementLabel childLabel : n.getChildEdgeLabels()) {

			Node child = n.getChildren().get(childLabel);
			boolean isLast = c++ == n.getChildEdgeLabels().size() ? true : false;
			subTree.append(this.toString(child, prefix, isLast));
		}
		return subTree.toString();
	}

	@Override
	public String toString() {
		return this.toString(this.getRoot(), "", true);
	}

	@Override
	public void setAssociatedTrie(Trie t) {

		this.associatedTrie = t;
	}

	@Override
	public Trie getAssociatedTrie() {

		return this.associatedTrie;
	}

	@Override
	public Map<String, ?> getEncodingScheme() {
		return this.encodingScheme;
	}

	@Override
	public void setEncodingScheme(Map<String, ?> encScheme) {
		this.encodingScheme = encScheme;
	}

}
