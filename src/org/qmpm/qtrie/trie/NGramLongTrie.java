package org.qmpm.qtrie.trie;

import org.qmpm.qtrie.labelers.Labeler;
import org.qmpm.qtrie.labelers.LongLabeler;

public class NGramLongTrie extends NGramTrie<Long> {

	public class NGramIntNode extends NGramNode<Long> {

		long parentEdgeLabel;

		protected NGramIntNode(Long label, QNode<Long> parent, QTrie<Long> trie) {
			this(label, parent, trie, false);
		}

		protected NGramIntNode(Long label, QNode<Long> parent, QTrie<Long> trie, boolean isRoot) {
			super(label, parent, trie, isRoot);
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

	public NGramLongTrie() {
		super();
	}

	public NGramLongTrie(Labeler<Long> labeler) {
		super(labeler);
	}

	@Override
	public QNode<Long> createNode(Long label, QNode<Long> parent) throws Exception {

		return new NGramIntNode(label, parent, this);
	}

	@Override
	protected Labeler<Long> createLabeler() {

		return new LongLabeler();
	}

	@Override
	protected Node<Long>[] createNodeArray(int size) {

		return new NGramIntNode[size];
	}

	@Override
	public void setRoot(Long rootActivity, String rootName, QNode<Long> rootParent) {

		this.root = new NGramIntNode(rootActivity, rootParent, this, true);
	}

}
