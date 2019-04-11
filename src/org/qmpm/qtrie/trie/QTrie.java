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

package org.qmpm.qtrie.trie;

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
import org.qmpm.qtrie.exceptions.LabelTypeException;
import org.qmpm.qtrie.exceptions.NodeNotFoundException;
import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;
import org.qmpm.qtrie.labelers.Labeler;

import com.google.common.collect.HashBiMap;

public abstract class QTrie<T> implements Trie<T> {

	T ROOT_ACTIVITY;// new StringLabel("ROOT");
	final static String ROOT_NAME = "root";
	private int attemptedInsertions = 0;
	private HashBiMap<String, String> activityAbbrevMap = HashBiMap.create();
	private Map<String, Object> attributes = new HashMap<>();
	private Map<Integer, Integer> breadthMap = new HashMap<>();
	private Set<T> alphabet = new HashSet<>();
	private List<Node<T>> endNodeSet = new ArrayList<>();
	private int longestBranch = 0;
	protected QNode<T> root;
	private final QNode<T> ROOT_PARENT = null;
	private int size = 0;
	private Trie<?> associatedTrie = null;
	// protected Map<ElementLabel, Integer> encodingScheme = null;
	private int alphabetSize = -1;
	protected Labeler<T> labeler;

	/*
	 * ABSTRACT METHODS
	 */
	protected abstract Labeler<T> createLabeler();

	// protected abstract T getLabel(Object o) throws ValueOutOfBoundsException;

	// protected abstract String labelToString(T label) throws Exception;

	protected abstract Node<T>[] createNodeArray(int size);

	public abstract void setRoot(T rootActivity, String rootName, QNode<T> rootParent);

	public abstract QNode<T> createNode(T label, QNode<T> parent) throws Exception;

	/*
	 * IMPLEMENTED METHODS
	 */
	public QTrie() {
		this(null);
	}

	public QTrie(Labeler<T> l) {
		this.labeler = l == null ? this.createLabeler() : l;
		try {
			this.ROOT_ACTIVITY = this.labeler.get(null);
		} catch (ValueOutOfBoundsException e) {
			e.printStackTrace();
			this.ROOT_ACTIVITY = null;
		}
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
	public List<Node<T>> cloneNodes(List<Node<T>> original) {

		List<Node<T>> clone = new ArrayList<>();

		for (Node<T> node : original) {
			clone.add(node);
		}

		return clone;
	}

	@Override
	public String draw() {

		System.out.println("Trie " + this.hashCode());
		System.out.println("longestBranch: " + this.getLongestBranch());
		String out = "";
		List<Node<T>> frontier = new ArrayList<>();
		List<Node<T>> newFrontier = new ArrayList<>();
		frontier.add(this.getRoot());

		for (int i = 0; i < this.getLongestBranch(); i++) {

			out = i + ": ";
			newFrontier.clear();

			for (Node<T> node : frontier) {
				newFrontier.addAll(node.getChildren());
				for (Node<T> child : node.getChildren()) {
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
	public Set<T> getAlphabet() throws Exception {

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
	public List<Node<T>> getEndNodes() {

		LinkedList<Node<T>> endNodes = new LinkedList<>();
		LinkedList<Node<T>> children = new LinkedList<>();
		LinkedList<Node<T>> tmp = new LinkedList<>();

		children.addAll(this.getRoot().getChildren());

		while (!children.isEmpty()) {

			for (Node<T> c : children) {

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
	public List<Node<T>> getLeafNodes() {

		LinkedList<Node<T>> leafNodes = new LinkedList<>();
		LinkedList<Node<T>> children = new LinkedList<>();
		LinkedList<Node<T>> tmp = new LinkedList<>();

		children.addAll(this.getRoot().getChildren());

		while (!children.isEmpty()) {

			for (Node<T> c : children) {

				if (c.getIsLeaf()) {
					leafNodes.addLast(c);
				}
				tmp.addAll(c.getChildren());
			}
			children.clear();
			children.addAll(tmp);
			tmp.clear();
		}

		return leafNodes;
	}

	@Override
	public int getLongestBranch() {
		return this.longestBranch;
	}

	@Override
	public Node<T>[] getNodes(boolean includeRoot) {

		int size = this.getSize();

		Node<T>[] result = this.createNodeArray(includeRoot ? size : size - 1);
		// Node<T>[] result = new Node[includeRoot ? size : size - 1];

		List<Node<T>> children = this.getRoot().getChildren();

		for (int i = 0; i < children.size(); i++) {
			result[i] = children.get(i);
		}

		int beg = 0;
		int end = children.size();
		int res = end;

		while (res < size - 1) {

			for (int i = beg; i < end; i++) {

				children = result[i].getChildren();

				for (Node<T> c : children) {
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
	public Node<T> getRoot() {

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

		LinkedList<Node<T>> children = new LinkedList<>();
		LinkedList<Node<T>> tmp = new LinkedList<>();

		children.addAll(this.getRoot().getChildren());

		while (!children.isEmpty()) {

			for (Node<T> c : children) {

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

		LinkedList<Node<T>> children = new LinkedList<>();
		LinkedList<Node<T>> tmp = new LinkedList<>();

		children.addAll(this.getRoot().getChildren());

		while (!children.isEmpty()) {

			for (Node<T> c : children) {

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
	public List<T> getVisitingPrefix(Node<T> node) {

		Node<T> tmpNode = node;
		List<T> prefix = new ArrayList<>();

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
	public Node<T> insert(List<?> sequence, boolean flatten) throws ValueOutOfBoundsException {

		this.attemptedInsertions++;
		QNode<T> lastNode = (QNode<T>) this.getRoot();
		QNode<T> node = (QNode<T>) this.getRoot();
		int len = sequence.size();

		lastNode.incrementVisits();

		for (int i = 0; i < len; i++) {

			T label = this.getLabel(sequence.get(i));
			// int label = sequence.get(i) instanceof Integer ? (int) sequence.get(i) :
			// LabelFactory.intEncoding(sequence.get(i));
			// this.addElementLabel(label);

			QNode<T> child = label == null ? null : (QNode<T>) node.getChild(label);

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
					this.addToAlphabet(label);
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

	protected T getLabel(Object o) throws ValueOutOfBoundsException {
		return this.labeler.get(o);
	}

	protected String labelToString(T label) throws Exception {
		Object o = this.labeler.getInverse(label);
		return this.labeler.toString(o);
	}

	@Override
	@SuppressWarnings("unused")
	public void kill() {

		for (Node<T> n : this.getNodes(true)) {
			n = null;
		}
	}

	@Override
	public double medianBranchLength() {

		int i = 0;
		double[] lengths = new double[this.endNodeSet.size()];

		for (Node<T> n : this.endNodeSet) {
			lengths[i++] = n.getDepth();
		}

		return new Median().evaluate(lengths);
	}

	public void putActivityAbbrev(String activity) {
		this.activityAbbrevMap.put("act" + this.activityAbbrevMap.size(), activity);
	}

	/*
	 * @Override public List<List<ElementLabel>> rebuildSequences(boolean flatten) {
	 *
	 * List<List<ElementLabel>> retSeqs = new ArrayList<>();
	 *
	 * for (Node endNode : this.getEndNodeSet()) { List<ElementLabel> seq = new
	 * ArrayList<>(); for (Object i : this.getVisitingPrefix(endNode)) {
	 * seq.add(LabelFactory.labelEncoding((int) i)); } for (int i = 0; i < (flatten
	 * ? 1 : endNode.getEndVisits()); i++) { retSeqs.add(seq); } }
	 *
	 * return retSeqs; }
	 */

	@Override
	public void remove(List<?> sequence) throws ValueOutOfBoundsException, NodeNotFoundException {

		Node<T> endNode = this.search(sequence);
		this.remove(endNode);
	}

	@Override
	public void remove(Node<T> endNode) {

		Node<T> lastNode = endNode;
		boolean finished = endNode.isRoot();

		while (!finished) {

			lastNode.decrementVisits();
			Node<T> node = lastNode.getParent();

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
	public Node<T> search(List<?> sequence) throws ValueOutOfBoundsException {

		Node<T> root = this.getRoot();
		Node<T> node = root;

		for (int i = 0; i < sequence.size(); i++) {

			T label = this.getLabel(sequence.get(i));
			// int label = sequence.get(i) instanceof Integer ? (int) sequence.get(i) :
			// LabelFactory.intEncoding(sequence.get(i));

			// Node child = node.getChild(label);

			Node<T> child = node.getChild(label);

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

	protected void toDoAtEndOfInsert(Node<T> node) {
	}

	// Print out tree using depth-first search
	private String toString(Node<T> n, String prefix, boolean lastSibling) throws LabelTypeException {

		StringBuilder subTree = new StringBuilder("");
		subTree.append(System.lineSeparator() + prefix);
		subTree.append(n.isRoot() ? "" : lastSibling ? "└── " : "├── ");
		T label = n.getParentEdgeLabel();
		String lblString;// = label == null ? "null" : this.labeler.toString(o);
		try {
			lblString = this.labelToString(label);
		} catch (Exception e) {
			lblString = label.toString();
			e.printStackTrace();
		}

		String name = n.isRoot() ? " (ROOT) " : " (" + n.getName() + ") ";
		subTree.append(lblString + name + " : " + n.getVisits() + (n.getIsEnd() ? " (E" + n.getEndVisits() + ")" : ""));
		prefix += n.isRoot() ? "" : lastSibling ? "    " : "│   ";

		int c = 1;
		for (Node<T> child : n.getChildren()) {

			boolean isLast = c++ == n.getChildEdgeLabels().size() ? true : false;
			subTree.append(this.toString(child, prefix, isLast));
		}
		return subTree.toString();
	}

	@Override
	public Labeler<T> getLabeler() {
		return this.labeler;
	}

	@Override
	public String toString() {
		try {
			return this.toString(this.getRoot(), "", true);
		} catch (LabelTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ERROR: LabelTypeException";
		}
	}

	@Override
	public void setAssociatedTrie(Trie<?> t) {

		this.associatedTrie = t;
	}

	@Override
	public Trie<?> getAssociatedTrie() {

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
	private Set<Node<T>> getNodesAt(List<Integer> depths, Node<T> n, int counter) {

		Set<Node<T>> result = new HashSet<>();

		if (depths.contains(counter)) {
			result.add(n);
		}

		if (counter < depths.get(depths.size() - 1)) {

			for (Node<T> c : n.getChildren()) {
				result.addAll(this.getNodesAt(depths, c, counter + 1));
			}
		}

		return result;
	}

	@Override
	public Set<Node<T>> getNodesAt(int depth) {
		List<Integer> depths = new ArrayList<>();
		depths.add(depth);
		return this.getNodesAt(depths);
	}

	@Override
	public Set<Node<T>> getNodesAt(List<Integer> depths) {
		depths.sort((x, y) -> Integer.compare(x, y));
		return this.getNodesAt(depths, this.getRoot(), 0);
	}

	@Override
	public void setAllFlags(boolean b) {

		LinkedList<Node<T>> current = new LinkedList<>();
		current.add(this.getRoot());

		while (!current.isEmpty()) {
			LinkedList<Node<T>> children = new LinkedList<>();
			for (Node<T> n : current) {
				n.setFlag(b);
				List<Node<T>> theseChildren = n.getChildren();
				for (Node<T> c : theseChildren) {
					children.addLast(c);
				}
			}
			current = children;
		}
	}

	@Override
	public void setAllFlagsOld(boolean b) {

		List<Node<T>> current = new ArrayList<>();
		current.add(this.getRoot());

		while (!current.isEmpty()) {
			List<Node<T>> children = new ArrayList<>();
			for (Node<T> n : current) {
				n.setFlag(b);
				children.addAll(n.getChildren());
			}
			current = children;
		}
	}

	@Override
	public void addToAlphabet(Collection<T> allActivities) {
		this.alphabet.addAll(allActivities);
	}

	@Override
	public void addToAlphabet(T activities) {
		this.alphabet.add(activities);
	}

	@Override
	public Node<T> createNode(T label, Node<T> parent) throws Exception {

		return this.createNode(label, (QNode<T>) parent);
	}
}