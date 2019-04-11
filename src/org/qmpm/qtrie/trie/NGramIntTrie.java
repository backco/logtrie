package org.qmpm.qtrie.trie;

import org.qmpm.qtrie.labelers.IntLabeler;
import org.qmpm.qtrie.labelers.Labeler;

public class NGramIntTrie extends NGramTrie<Integer> {

	public class NGramIntNode extends NGramNode<Integer> {

		int parentEdgeLabel;

		protected NGramIntNode(Integer label, QNode<Integer> parent, QTrie<Integer> trie) {
			this(label, parent, trie, false);
		}

		protected NGramIntNode(Integer label, QNode<Integer> parent, QTrie<Integer> trie, boolean isRoot) {
			super(label, parent, trie, isRoot);
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

	public NGramIntTrie() {
		super();
	}

	public NGramIntTrie(Labeler<Integer> labeler) {
		super(labeler);
	}

	@Override
	public QNode<Integer> createNode(Integer label, QNode<Integer> parent) throws Exception {

		return new NGramIntNode(label, parent, this);
	}

	@Override
	protected Labeler<Integer> createLabeler() {

		return new IntLabeler();
	}

	@Override
	protected Node<Integer>[] createNodeArray(int size) {

		return new NGramIntNode[size];
	}

	@Override
	public void setRoot(Integer rootActivity, String rootName, QNode<Integer> rootParent) {

		this.root = new NGramIntNode(rootActivity, rootParent, this, true);
	}

}
