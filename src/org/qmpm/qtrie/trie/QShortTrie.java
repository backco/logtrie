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

import org.qmpm.qtrie.labelers.Labeler;
import org.qmpm.qtrie.labelers.ShortLabeler;

public class QShortTrie extends QTrie<Short> {

	public class QShortNode extends QNode<Short> {

		short parentEdgeLabel;

		protected QShortNode(Short label, QNode<Short> parent, QTrie<Short> trie) {
			this(label, parent, trie, false);
		}

		protected QShortNode(Short label, QNode<Short> parent, QTrie<Short> trie, boolean isRoot) {

			super(parent, trie, isRoot);
			this.parentEdgeLabel = label;
		}

		@Override
		protected boolean labelEqualsSub(Short l1, Short l2) {
			short i1 = l1;
			short i2 = l2;
			return i1 == i2;
		}

		@Override
		public Short getParentEdgeLabel() {

			return this.parentEdgeLabel;
		}

	}

	@Override
	protected Node<Short>[] createNodeArray(int size) {

		return new QShortNode[size];
	}

	@Override
	public void setRoot(Short rootActivity, String rootName, QNode<Short> rootParent) {

		this.root = new QShortNode(rootActivity, rootParent, this, true);
	}

	@Override
	public QNode<Short> createNode(Short label, QNode<Short> parent) throws Exception {

		return new QShortNode(label, parent, this);
	}

	@Override
	protected Labeler<Short> createLabeler() {

		return new ShortLabeler();
	}

}