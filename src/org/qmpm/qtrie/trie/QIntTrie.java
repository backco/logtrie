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

import org.qmpm.qtrie.labelers.IntLabeler;
import org.qmpm.qtrie.labelers.Labeler;

public class QIntTrie extends QTrie<Integer> {

	public class QIntNode extends QNode<Integer> {

		int parentEdgeLabel;

		protected QIntNode(Integer label, QNode<Integer> parent, QTrie<Integer> trie) {
			this(label, parent, trie, false);
		}

		protected QIntNode(Integer label, QNode<Integer> parent, QTrie<Integer> trie, boolean isRoot) {

			super(parent, trie, isRoot);
			this.parentEdgeLabel = label;
		}

		@Override
		protected boolean labelEqualsSub(Integer l1, Integer l2) {
			int i1 = l1;
			int i2 = l2;
			return i1 == i2;
		}

		@Override
		public Integer getParentEdgeLabel() {

			return this.parentEdgeLabel;
		}

	}

	@Override
	protected Node<Integer>[] createNodeArray(int size) {

		return new QIntNode[size];
	}

	@Override
	public void setRoot(Integer rootActivity, String rootName, QNode<Integer> rootParent) {

		this.root = new QIntNode(rootActivity, rootParent, this, true);
	}

	@Override
	public QNode<Integer> createNode(Integer label, QNode<Integer> parent) throws Exception {

		return new QIntNode(label, parent, this);
	}

	@Override
	protected Labeler<Integer> createLabeler() {

		return new IntLabeler();
	}

}