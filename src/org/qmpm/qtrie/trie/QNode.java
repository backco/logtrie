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
import java.util.List;

import org.qmpm.qtrie.trie.Trie.Node;

public abstract class QNode<T> implements Node<T> {

	private List<Node<T>> children = new ArrayList<>();
	private byte properties = 0b0000_0001;
	private int visits = 0;
	private int endVisits = 0;
	private int depth = -1;
	protected QNode<T> parent = null;

	/*
	 * ABSTRACT METHODS
	 */
	protected abstract boolean labelEqualsSub(T l1, T l2);

	@Override
	public abstract T getParentEdgeLabel();

	protected QNode(QNode<T> parent, QTrie<T> trie) {
		this(parent, trie, false);
	}

	protected QNode(QNode<T> parent, QTrie<T> trie, boolean isRoot) {

		if (isRoot) { // TODO: Fix this weirdness

			this.setIsRoot(true);
			this.depth = 0;

		} else {

			// this.ID = trie.getSize();
			this.parent = parent;
			parent.addChild(this);
			this.depth = this.getParent().getDepth() + 1;
			// trie.addNode(this);
		}

		int depth = this.getDepth();

		if (depth > trie.getLongestBranch()) {
			trie.setLongestBranch(depth);
		}

		trie.incrementSize();

	}

	/*
	 * @Override public int getID() { return this.ID; }
	 */

	@Override
	public void decrementVisits() {
		this.visits--;
	}

	@Override
	public void decrementEndVisits() {
		this.endVisits--;
	}

	@Override
	public Node<T> getChild(T label) {

		List<Node<T>> children = this.getChildren();

		if (children == null) {
			return null;
		} else {

			for (Node<T> c : this.getChildren()) {
				if (((QNode<T>) c).labelEquals(c.getParentEdgeLabel(), label)) {
					return c;
				}
			}

			return null;
		}
	}

	@Override
	public List<Node<T>> getChildren() {
		return this.children;
	}

	@Override
	public int getDepth() {
		return this.depth;
		// return this.isRoot() ? 0 : this.getParent().getDepth() + 1;
	}

	@Override
	public boolean getIsLeaf() {

		return (this.properties & 0b000_0001) == 0b000_0001;
	}

	@Override
	public boolean getIsEnd() {
		return (this.properties & 0b000_0010) == 0b000_0010;
	}

	@Override
	public boolean isRoot() {
		return (this.properties & 0b000_0100) == 0b000_0100;
	}

	@Override
	public boolean getFlag() {
		return (this.properties & 0b000_1000) == 0b000_1000;
	}

	@Override
	public void setIsLeaf(boolean b) {

		if (b) {
			this.properties = (byte) (this.properties | 0b000_0001);
		} else {
			this.properties = (byte) (this.properties & 0b111_1110);
		}
	}

	@Override
	public void setIsEnd(boolean b) {

		if (b) {
			this.properties = (byte) (this.properties | 0b000_0010);
		} else {
			this.properties = (byte) (this.properties & 0b111_1101);
		}
	}

	@Override
	public void setIsRoot(boolean b) {

		if (b) {
			this.properties = (byte) (this.properties | 0b000_0100);
		} else {
			this.properties = (byte) (this.properties & 0b111_1011);
		}
	}

	@Override
	public void setFlag(boolean b) {

		if (b) {
			this.properties = (byte) (this.properties | 0b000_1000);
		} else {
			this.properties = (byte) (this.properties & 0b111_0111);
		}
	}

	@Override
	public String getName() {
		return this.isRoot() ? QTrie.ROOT_NAME : "node" + this.hashCode();
	}

	@Override
	public List<T> getChildEdgeLabels() {

		List<T> result = new ArrayList<>();

		for (Node<T> c : this.getChildren()) {
			result.add(c.getParentEdgeLabel());
		}

		return result;
	}

	@Override
	public Node<T> getParent() {
		return this.parent;
	}

	@Override
	public int getVisits() {
		return this.visits;
	}

	@Override
	public int getEndVisits() {
		return this.endVisits;
	}

	@Override
	public void incrementVisits() {
		this.visits++;
	}

	@Override
	public void incrementEndVisits() {
		this.endVisits++;
	}

	@Override
	public String toString() {

		String parent = "N/A";

		if (!this.isRoot()) {
			parent = String.valueOf(this.getParent().getParentEdgeLabel());
		}

		String output = "";
		output += "Depth   : " + this.getDepth() + System.getProperty("line.separator");
		output += "Name    : " + this.getName() + System.getProperty("line.separator");
		output += "Visits  : " + this.getVisits() + System.getProperty("line.separator");
		output += "Parent  : " + parent + System.getProperty("line.separator");
		output += "Edge Label: " + this.getParentEdgeLabel() + System.getProperty("line.separator");
		output += "Children: " + this.getChildren() + System.getProperty("line.separator");
		output += System.getProperty("line.separator");

		return output;
	}

	@Override
	public void addChild(Node<T> node) {

		if (this.children == null) {
			this.children = new ArrayList<>();
		}

		this.children.add(node);
	}

	public boolean labelEquals(T l1, T l2) {

		if (l1 == null & l2 == null) {
			return true;
		} else if (l1 == null ^ l2 == null) {
			return false;
		} else {
			return this.labelEqualsSub(l1, l2);
		}
	}

	/*
	 * @Override public Object getAttribute(String key) { if (this.attributes !=
	 * null) { if (this.attributes.containsKey(key)) { return
	 * this.attributes.get(key); } } return null; }
	 */

	/*
	 * @Override public boolean hasAttribute(String key) { return
	 * this.attributes.containsKey(key); }
	 *
	 *
	 * @Override public void setAttribute(String key, Object value) { if
	 * (this.attributes == null) { this.attributes = new HashMap<>(); }
	 * this.attributes.put(key, value); }
	 */
}
