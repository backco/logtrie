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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.qmpm.qtrie.exceptions.NodeNotFoundException;
import org.qmpm.qtrie.exceptions.ProcessTransitionException;
import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;
import org.qmpm.qtrie.labelers.Labeler;

public interface Trie<T> {

	interface Node<T> {

		void addChild(Node<T> node);

		void decrementEndVisits();

		void decrementVisits();

		// Object getAttribute(String key);

		List<T> getChildEdgeLabels();

		List<Node<T>> getChildren();

		int getDepth();

		int getEndVisits();

		// int getID();

		boolean getIsEnd();

		boolean getIsLeaf();

		boolean isRoot();

		String getName();

		Node<T> getParent();

		T getParentEdgeLabel();

		int getVisits();

		// boolean hasAttribute(String key);

		void incrementEndVisits();

		void incrementVisits();

		// void setAttribute(String key, Object value);

		void setFlag(boolean b);

		boolean getFlag();

		void setIsEnd(boolean isEnd);

		void setIsLeaf(boolean isLeaf);

		void setIsRoot(boolean isRoot);

		Node<T> getChild(T i);
	}

	List<Node<T>> cloneNodes(List<Node<T>> original);

	String draw();

	int getAttemptedInsertions();

	Object getAttribute(String key);

	Set<T> getAlphabet() throws Exception;

	List<Node<T>> getEndNodes();

	List<Node<T>> getLeafNodes();

	Set<Node<T>> getNodesAt(List<Integer> depths);

	Set<Node<T>> getNodesAt(int depth);

	int getLongestBranch();

	Node<T>[] getNodes(boolean includeRoot);

	Node<T> getRoot();

	int getSize();

	int getTotalEndVisits(boolean includeRoot);

	int getTotalVisits(boolean includeRoot);

	List<T> getVisitingPrefix(Node<T> node);

	boolean hasAttribute(String key);

	Node<T> insert(List<?> sequence, boolean flatten) throws ProcessTransitionException, ValueOutOfBoundsException;

	void kill();

	double medianBranchLength();

	// List<List<ElementLabel>> rebuildSequences(boolean flatten);

	void remove(List<?> sequence) throws NodeNotFoundException, ValueOutOfBoundsException;

	void remove(Node<T> endNode);

	Node<T> search(List<?> sequence) throws NodeNotFoundException, ValueOutOfBoundsException;

	<V> void setAttribute(String key, V value);

	void setLongestBranch(int length);

	@Override
	String toString();

	void setAssociatedTrie(Trie<?> t);

	Trie<?> getAssociatedTrie();

//	void addint(int l) throws LabelTypeException;

//	void addints(Set<?> s) throws LabelTypeException;

//	Map<ElementLabel, T> getEncodingScheme();

//	void setEncodingScheme(Map<ElementLabel, T> encScheme);

	void setAlphabetSize(int size);

	int getAlphabetSize();

	void setAllFlagsOld(boolean b);

	void setAllFlags(boolean b);

	void incrementSize();

	void addToAlphabet(Collection<T> activities);

	void addToAlphabet(T activity);

	Node<T> createNode(T label, Node<T> parent) throws Exception;

	Labeler<T> getLabeler();
}
