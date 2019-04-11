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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.qmpm.logtrie.elementlabel.ElementLabel;
import org.qmpm.logtrie.elementlabel.LabelFactory;
import org.qmpm.logtrie.exceptions.LabelTypeException;
//import org.qmpm.logtrie.exceptions.ProcessTransitionException;
import org.qmpm.logtrie.exceptions.NodeNotFoundException;

import com.google.common.collect.HashBiMap;

public class TrieImpl implements Trie {

	final Integer ROOT_ACTIVITY = null;// new StringLabel("ROOT");
	final static String ROOT_NAME = "root";
	private int attemptedInsertions = 0;
	private HashBiMap<String, String> activityAbbrevMap = HashBiMap.create();
	private Map<String, Object> attributes = new HashMap<>();
	private Map<Integer, Integer> breadthMap = new HashMap<>();
	private Set<Object> alphabet = new HashSet<>();
	private List<Node> endNodeSet = new ArrayList<>();
	private int longestBranch = 0;
	protected Node root;
	private final Node ROOT_PARENT = null;
	private int size = 0;
	private Trie associatedTrie = null;
	// protected Map<ElementLabel, Integer> encodingScheme = null;
	private int alphabetSize = -1;

	public TrieImpl() {
		// this.setRoot(ROOT_ACTIVITY, ROOT_NAME, this.ROOT_PARENT);
		// System.out.println("TrieImpl()");
	}

	/*
	 * @Override public void addElementLabel(ElementLabel l) throws
	 * LabelTypeException { this.elementLabels.add(l); }
	 *
	 * @Override public void addElementLabels(Set<?> s) throws LabelTypeException {
	 * for (Object l : s) { this.elementLabels.add(LabelFactory.build(l,
	 * this.encodingScheme)); } }
	 */

	@Override
	public List<Node> cloneNodes(List<Node> original) {

		List<Node> clone = new ArrayList<>();

		for (Node node : original) {
			clone.add(node);
		}

		return clone;
	}

	@Override
	public Node createNode(Object label, Node parent) throws Exception {
		// System.out.println(++this.createdNodes);
		// System.out.println("TrieImpl.createNode()");
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

			out = i + ": ";
			newFrontier.clear();

			for (Node node : frontier) {
				newFrontier.addAll(node.getChildren());
				for (Node child : node.getChildren()) {
					out += String.valueOf(child.getParentEdgeLabel()) + "(" + child.getVisits() + "),";
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
	public int getAlphabetSize() {
		return this.alphabetSize;
	}

	@Override
	public void setAlphabetSize(int size) {
		this.alphabetSize = size;
	}

	@Override
	public Set<Object> getAlphabet() throws Exception {

		return this.alphabet;

		/*
		 * if (this.alphabet.isEmpty()) {
		 *
		 * Set<Object> result = new HashSet<>(); LinkedList<Node> children = new
		 * LinkedList<>(); LinkedList<Node> tmp = new LinkedList<>();
		 *
		 * children.addAll(this.getRoot().getChildren());
		 *
		 * while (!children.isEmpty()) {
		 *
		 * for (Node c : children) {
		 *
		 * result.add(c.getParentEdgeLabel()); tmp.addAll(c.getChildren()); }
		 * children.clear(); children.addAll(tmp); tmp.clear(); }
		 *
		 * return result; } else { return this.alphabet; }
		 */
	}

	@Override
	public List<Node> getEndNodeSet() {

		LinkedList<Node> endNodes = new LinkedList<>();
		LinkedList<Node> children = new LinkedList<>();
		LinkedList<Node> tmp = new LinkedList<>();

		children.addAll(this.getRoot().getChildren());

		while (!children.isEmpty()) {

			for (Node c : children) {

				if (c.getIsEnd()) {
					endNodes.addLast(c);
				}
				tmp.addAll(c.getChildren());
			}
			children.clear();
			children.addAll(tmp);
			tmp.clear();
		}

		return endNodes;
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

		Node[] nodeSet = this.getNodes(true);

		for (Node n : nodeSet) {
			if (n == null) {
				System.out.println("null");
			}
		}

		int i = 0;

		for (Node node : nodeSet) {

			if (node == null) {
				System.out.println(i + " / " + nodeSet.length);
				System.out.println(i + " / " + this.getSize());
			}

			if (node.getIsLeaf()) {
				leafNodes.add(node);
			}
			i++;
		}

		return leafNodes;
	}

	@Override
	public int getLongestBranch() {
		return this.longestBranch;
	}

	@Override
	public List<Node> getNodeSetOld(boolean includeRoot) {

		List<Node> result = new ArrayList<>();
		result.add(this.getRoot());

		int beg = 0;
		int end = 2;

		while (beg != end) {
			for (int i = beg; i < end; i++) {
				List<Node> children = result.get(i).getChildren();
				result.addAll(children);
			}
			beg = end;
			end = result.size();
		}

		if (!includeRoot) {
			result.remove(0);
		}

		// System.out.println("Initilizing array took " + 100 * (double) sub0 / total +
		// "% of the time");
		// System.out.println("AddAll took " + 100 * (double) sub1 / total + "% of the
		// time");
		// System.out.println("Converting to List took " + 100 * (double) sub2 / total +
		// "% of the time");

		return result;
	}

	@Override
	public Node[] getNodes(boolean includeRoot) {

		int size = this.getSize();

		Node[] result = new Node[includeRoot ? size : size - 1];

		List<Node> children = this.getRoot().getChildren();

		for (int i = 0; i < children.size(); i++) {
			result[i] = children.get(i);
		}

		int beg = 0;
		int end = children.size();
		int res = end;

		while (res < size - 1) {

			for (int i = beg; i < end; i++) {

				children = result[i].getChildren();

				for (Node c : children) {
					result[res++] = c;
				}
			}

			beg = end;
			end = res;
		}

		if (includeRoot) {
			result[size - 1] = this.getRoot();
		}

		return result;
	}

	@Override
	public Node getRoot() {

		if (this.root == null) {
			this.setRoot(this.ROOT_ACTIVITY, ROOT_NAME, this.ROOT_PARENT);
		}
		return this.root;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public int getTotalEndVisits(boolean includeRoot) {

		int totalEndVisits = includeRoot ? this.getRoot().getEndVisits() : 0;

		LinkedList<Node> children = new LinkedList<>();
		LinkedList<Node> tmp = new LinkedList<>();

		children.addAll(this.getRoot().getChildren());

		while (!children.isEmpty()) {

			for (Node c : children) {

				if (c.getIsEnd()) {
					totalEndVisits += c.getEndVisits();
				}
				tmp.addAll(c.getChildren());
			}
			children.clear();
			children.addAll(tmp);
			tmp.clear();
		}

		return totalEndVisits;
	}

	@Override
	public int getTotalVisits(boolean includeRoot) {

		int totalVisits = includeRoot ? this.getRoot().getVisits() : 0;

		LinkedList<Node> children = new LinkedList<>();
		LinkedList<Node> tmp = new LinkedList<>();

		children.addAll(this.getRoot().getChildren());

		while (!children.isEmpty()) {

			for (Node c : children) {

				totalVisits += c.getVisits();
				tmp.addAll(c.getChildren());
			}
			children.clear();
			children.addAll(tmp);
			tmp.clear();
		}

		return totalVisits;
	}

	@Override
	public List<Object> getVisitingPrefix(Node node) {

		Node tmpNode = node;
		List<Object> prefix = new ArrayList<>();

		// If node is root node, return empty prefix
		if (node.isRoot()) {
			return prefix;
		}

		// Retrace trie to reconstruct prefix (it will be in reverse order)
		while (!tmpNode.isRoot()) {
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

	private void decrementSize() {
		this.size--;
	}

	@Override
	public Node insert(List<?> sequence, boolean flatten) throws LabelTypeException {

		this.addToAlphabet(sequence);

		this.attemptedInsertions++;
		Node lastNode = this.getRoot();
		Node node = this.getRoot();
		int len = sequence.size();

		lastNode.incrementVisits();

		for (int i = 0; i < len; i++) {

			int label = sequence.get(i) instanceof Integer ? (int) sequence.get(i)
					: LabelFactory.intEncoding(sequence.get(i));
			// this.addElementLabel(label);

			Node child = node.getChild(label);

			if (child != null) {
				node = child;

				if (i == len - 1) {
					if (node.getIsEnd() && flatten) {
						this.remove(node);
					} else {
						node.setIsEnd(true);
						node.incrementEndVisits();
					}
				}
			} else {
				try {
					node = this.createNode(label, lastNode);
				} catch (Exception e) {
					e.printStackTrace();
					this.remove(lastNode);
					return null;
				}
				node.getParent().setIsLeaf(false);
				Integer breadth = 1;

				if (this.breadthMap.containsKey(i + 1)) {
					breadth = this.breadthMap.get(i + 1);
				}

				this.breadthMap.put(i + 1, breadth);

				if (i == sequence.size() - 1) {
					node.setIsEnd(true);
					node.incrementEndVisits();
					this.endNodeSet.add(node);
					this.toDoAtEndOfInsert(node);
				}
			}

			node.incrementVisits();
			lastNode = node;
			// children = node.getChildren();
		}
		return lastNode;
	}

	@Override
	@SuppressWarnings("unused")
	public void kill() {

		for (Node n : this.getNodes(true)) {
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
			List<ElementLabel> seq = new ArrayList<>();
			for (Object i : this.getVisitingPrefix(endNode)) {
				seq.add(LabelFactory.labelEncoding((int) i));
			}
			for (int i = 0; i < (flatten ? 1 : endNode.getEndVisits()); i++) {
				retSeqs.add(seq);
			}
		}

		return retSeqs;
	}

	@Override
	public void remove(List<Object> sequence) throws LabelTypeException, NodeNotFoundException {

		Node endNode = this.search(sequence);
		this.remove(endNode);
	}

	@Override
	public void remove(Node endNode) {

		Node lastNode = endNode;
		boolean finished = endNode.isRoot();

		while (!finished) {

			lastNode.decrementVisits();
			Node node = lastNode.getParent();

			if (lastNode.getIsLeaf() && lastNode.getVisits() == 1) {
				// TODO: node.removeChild(lastNode);
				node.getChildren().remove(lastNode.getParentEdgeLabel());
				this.decrementSize();
			}

			if (node.isRoot()) {
				node.decrementVisits();
				finished = true;
			}

			lastNode = node;
		}
	}

	@Override
	public Node search(List<Object> sequence) throws LabelTypeException {

		Node root = this.getRoot();
		Node node = root;

		for (int i = 0; i < sequence.size(); i++) {

			int label = sequence.get(i) instanceof Integer ? (int) sequence.get(i)
					: LabelFactory.intEncoding(sequence.get(i));

			// Node child = node.getChild(label);

			Node child = node.getChild(label);

			if (child != null) {
				node = child;
			} else {
				return null;
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
	public void setRoot(Object rootActivity, String rootName, Node rootParent) {

		this.root = new NodeImpl(rootActivity, rootParent, this);
		// this.root.setName(rootName);
		// this.root.setParent((XESLogNode) rootParent);
	}

	protected void toDoAtEndOfInsert(Node node) {
	}

	// Print out tree using depth-first search
	private String toString(Node n, String prefix, boolean lastSibling) {

		StringBuilder subTree = new StringBuilder("");
		subTree.append(System.lineSeparator() + prefix);
		subTree.append(n.isRoot() ? "" : lastSibling ? "└── " : "├── ");
		int l = n.getParentEdgeLabel() == null ? -1 : (int) n.getParentEdgeLabel();
		String name = n.isRoot() ? " (ROOT) " : " (" + n.getName() + ") ";
		subTree.append(String.valueOf(LabelFactory.labelEncoding(l)) + name + " : " + n.getVisits()
				+ (n.getIsEnd() ? " (E" + n.getEndVisits() + ")" : ""));
		prefix += n.isRoot() ? "" : lastSibling ? "    " : "│   ";

		int c = 1;
		for (Node child : n.getChildren()) {

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

	/*
	 * @Override public Map<ElementLabel, Integer> getEncodingScheme() { return
	 * this.encodingScheme; }
	 *
	 * @Override public void setEncodingScheme(Map<ElementLabel, Integer> encScheme)
	 * { this.encodingScheme = encScheme; }
	 *
	 *
	 */
	private Set<Node> getNodesAt(List<Integer> depths, Node n, int counter) {

		Set<Node> result = new HashSet<>();

		if (depths.contains(counter)) {
			result.add(n);
		}

		if (counter < depths.get(depths.size() - 1)) {

			for (Node c : n.getChildren()) {
				result.addAll(this.getNodesAt(depths, c, counter + 1));
			}
		}

		return result;
	}

	@Override
	public Set<Node> getNodesAt(int depth) {
		List<Integer> depths = new ArrayList<>();
		depths.add(depth);
		return this.getNodesAt(depths);
	}

	@Override
	public Set<Node> getNodesAt(List<Integer> depths) {
		depths.sort((x, y) -> Integer.compare(x, y));
		return this.getNodesAt(depths, this.getRoot(), 0);
	}

	@Override
	public void setAllFlags(boolean b) {

		LinkedList<Node> current = new LinkedList<>();
		current.add(this.getRoot());

		while (!current.isEmpty()) {
			LinkedList<Node> children = new LinkedList<>();
			for (Node n : current) {
				n.setFlag(b);
				List<Node> theseChildren = n.getChildren();
				for (Node c : theseChildren) {
					children.addLast(c);
				}
			}
			current = children;
		}
	}

	@Override
	public void setAllFlagsOld(boolean b) {

		List<Node> current = new ArrayList<>();
		current.add(this.getRoot());

		while (!current.isEmpty()) {
			List<Node> children = new ArrayList<>();
			for (Node n : current) {
				n.setFlag(b);
				children.addAll(n.getChildren());
			}
			current = children;
		}
	}

	@Override
	public void addToAlphabet(Collection<?> allActivities) {
		this.alphabet.addAll(allActivities);
	}
}