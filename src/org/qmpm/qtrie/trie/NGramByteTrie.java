package org.qmpm.qtrie.trie;

import org.qmpm.qtrie.labelers.ByteLabeler;
import org.qmpm.qtrie.labelers.Labeler;

public class NGramByteTrie extends NGramTrie<Byte> {

	public class NGramIntNode extends NGramNode<Byte> {

		byte parentEdgeLabel;

		protected NGramIntNode(Byte label, QNode<Byte> parent, QTrie<Byte> trie) {
			this(label, parent, trie, false);
		}

		protected NGramIntNode(Byte label, QNode<Byte> parent, QTrie<Byte> trie, boolean isRoot) {
			super(label, parent, trie, isRoot);
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

	public NGramByteTrie() {
		super();
	}

	public NGramByteTrie(Labeler<Byte> labeler) {
		super(labeler);
	}

	@Override
	public QNode<Byte> createNode(Byte label, QNode<Byte> parent) throws Exception {

		return new NGramIntNode(label, parent, this);
	}

	@Override
	protected Labeler<Byte> createLabeler() {

		return new ByteLabeler();
	}

	@Override
	protected Node<Byte>[] createNodeArray(int size) {

		return new NGramIntNode[size];
	}

	@Override
	public void setRoot(Byte rootActivity, String rootName, QNode<Byte> rootParent) {

		this.root = new NGramIntNode(rootActivity, rootParent, this, true);
	}

}
