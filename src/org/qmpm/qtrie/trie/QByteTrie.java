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

import org.qmpm.qtrie.labelers.ByteLabeler;
import org.qmpm.qtrie.labelers.Labeler;

public class QByteTrie extends QTrie<Byte> {

	public class QByteNode extends QNode<Byte> {

		byte parentEdgeLabel;

		protected QByteNode(Byte label, QNode<Byte> parent, QTrie<Byte> trie) {
			this(label, parent, trie, false);
		}

		protected QByteNode(Byte label, QNode<Byte> parent, QTrie<Byte> trie, boolean isRoot) {

			super(parent, trie, isRoot);
			this.parentEdgeLabel = label;
		}

		@Override
		protected boolean labelEqualsSub(Byte l1, Byte l2) {
			byte i1 = l1;
			byte i2 = l2;
			return i1 == i2;
		}

		@Override
		public Byte getParentEdgeLabel() {

			return this.parentEdgeLabel;
		}

	}

	@Override
	protected Node<Byte>[] createNodeArray(int size) {

		return new QByteNode[size];
	}

	@Override
	public void setRoot(Byte rootActivity, String rootName, QNode<Byte> rootParent) {

		this.root = new QByteNode(rootActivity, rootParent, this, true);
	}

	@Override
	public QNode<Byte> createNode(Byte label, QNode<Byte> parent) throws Exception {

		return new QByteNode(label, parent, this);
	}

	@Override
	protected Labeler<Byte> createLabeler() {

		return new ByteLabeler();
	}

}