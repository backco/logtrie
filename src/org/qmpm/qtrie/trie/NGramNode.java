package org.qmpm.qtrie.trie;

public abstract class NGramNode<T> extends QNode<T> {

	private int count = 0;

	protected NGramNode(T label, QNode<T> parent, QTrie<T> trie) {

		this(label, parent, trie, false);
	}

	protected NGramNode(T label, QNode<T> parent, QTrie<T> trie, boolean isRoot) {

		super(parent, trie, isRoot);
	}

	void addToCount(int c) {
		this.count += c;
	}

	public int getCount() {
		return this.count;
	}

}