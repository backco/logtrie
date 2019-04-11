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
import org.qmpm.qtrie.labelers.LongLabeler;

public class QLongTrie extends QTrie<Long> {

	public class QLongNode extends QNode<Long> {

		long parentEdgeLabel;

		protected QLongNode(Long label, QNode<Long> parent, QTrie<Long> trie) {
			this(label, parent, trie, false);
		}

		protected QLongNode(Long label, QNode<Long> parent, QTrie<Long> trie, boolean isRoot) {

			super(parent, trie, isRoot);
			this.parentEdgeLabel = label;
		}

		@Override
		protected boolean labelEqualsSub(Long l1, Long l2) {
			long i1 = l1;
			long i2 = l2;
			return i1 == i2;
		}

		@Override
		public Long getParentEdgeLabel() {

			return this.parentEdgeLabel;
		}

	}

	@Override
	protected Node<Long>[] createNodeArray(int size) {

		return new QLongNode[size];
	}

	@Override
	public void setRoot(Long rootActivity, String rootName, QNode<Long> rootParent) {

		this.root = new QLongNode(rootActivity, rootParent, this, true);
	}

	@Override
	public QNode<Long> createNode(Long label, QNode<Long> parent) throws Exception {

		return new QLongNode(label, parent, this);
	}

	@Override
	protected Labeler<Long> createLabeler() {

		return new LongLabeler();
	}

}