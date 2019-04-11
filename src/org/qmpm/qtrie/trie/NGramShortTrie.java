package org.qmpm.qtrie.trie;

import org.qmpm.qtrie.labelers.Labeler;
import org.qmpm.qtrie.labelers.ShortLabeler;

public class NGramShortTrie extends NGramTrie<Short> {

	public class NGramIntNode extends NGramNode<Short> {

		short parentEdgeLabel;

		protected NGramIntNode(Short label, QNode<Short> parent, QTrie<Short> trie) {
			this(label, parent, trie, false);
		}

		protected NGramIntNode(Short label, QNode<Short> parent, QTrie<Short> trie, boolean isRoot) {
			super(label, parent, trie, isRoot);
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

	public NGramShortTrie() {
		super();
	}

	public NGramShortTrie(Labeler<Short> labeler) {
		super(labeler);
	}

	@Override
	public QNode<Short> createNode(Short label, QNode<Short> parent) throws Exception {

		return new NGramIntNode(label, parent, this);
	}

	@Override
	protected Labeler<Short> createLabeler() {

		return new ShortLabeler();
	}

	@Override
	protected Node<Short>[] createNodeArray(int size) {

		return new NGramIntNode[size];
	}

	@Override
	public void setRoot(Short rootActivity, String rootName, QNode<Short> rootParent) {

		this.root = new NGramIntNode(rootActivity, rootParent, this, true);
	}

}
