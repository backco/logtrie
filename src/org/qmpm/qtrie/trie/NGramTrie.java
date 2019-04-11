package org.qmpm.qtrie.trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.qmpm.qtrie.exceptions.NodeNotFoundException;
import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;
import org.qmpm.qtrie.labelers.Labeler;

public abstract class NGramTrie<T> extends QTrie<T> {

	private List<Integer> nValues = new ArrayList<>();
	private Map<Integer, Integer> totalNGramMap = new HashMap<>();

	public NGramTrie(Labeler<T> labeler) {
		super(labeler);
	}

	public NGramTrie() {
		super();
	}

	@Override
	public abstract QNode<T> createNode(T label, QNode<T> parent) throws Exception;

	// TODO: Better solution for tries with direct edge labeling
	@Override
	protected T getLabel(Object o) throws ValueOutOfBoundsException {
		return (T) o;
	}

	public Node<T> addNGram(List<T> nGram, int count) throws NodeNotFoundException, ValueOutOfBoundsException {

		Node<T> node = this.search(nGram);

		if (node == null) {
			try {
				node = this.insert(nGram, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		((NGramNode<T>) node).addToCount(count);
		return node;
	}

	public int build(Trie<T> t) throws NodeNotFoundException, ValueOutOfBoundsException {

		int totalNGrams = 0;

		for (int i = t.getLongestBranch(); i > 0; i--) {
			totalNGrams += this.build(t, i);
		}

		return totalNGrams;
	}

	public int build(Trie<T> t, int n) throws NodeNotFoundException, ValueOutOfBoundsException {

		this.nValues.add(n);
		// Set<Node<T>> nodeSet = this.nodeSetMap.get(n) == null ? new HashSet<>() :
		// this.nodeSetMap.get(n);
		int totalNGrams = 0;

		// Reset visit flags on all nodes for next block
		// t.getRoot().setFlag(false);
		t.setAllFlags(false);
		List<Node<T>> leafNodes = t.getLeafNodes();

		for (Node<T> leafNode : leafNodes) {

			// If node depth is less than k, then the prefix is too short to find any
			// k-blocks

			int depth = leafNode.getDepth();

			if (depth >= n) {

				Node<T> currentEndNode = leafNode;

				LinkedList<T> block = null;

				Node<T> leadingNode = currentEndNode;

				for (int i = 0; i < n - 1; i++) {
					leadingNode = leadingNode.getParent();
				}

				while (leadingNode.getDepth() > 0) {

					// Flag denotes whether node has already been counted, once this is reached,
					// stop moving further up the trie

					if (!currentEndNode.getFlag()) {

						if (block == null) {
							block = this.blockBuilder(n, currentEndNode);
						} else {
							T label = leadingNode.getParentEdgeLabel();
							block.addFirst(label);
							block.removeLast();
						}
						this.addNGram(block, currentEndNode.getVisits());

						// nodeSet.add(node);

						totalNGrams += currentEndNode.getVisits();
						currentEndNode.setFlag(true);
					} else {
						break;
					}
					leadingNode = leadingNode.getParent();
					currentEndNode = currentEndNode.getParent();
				}
			}

			// Update progress of associated Metric object, check for timeout or error
			/*
			 * this.updateProgress((double) ++progress / (total +
			 * nGramTrie.getEndNodeSet().size())); if (this.getOutcome() !=
			 * Outcome.CONTINUE) { return this.getOutcome(); }
			 */
		}
		// this.nodeSetMap.put(n, nodeSet);
		// Reset visit flags on all nodes for next block
		// t.getRoot().setFlag(false);
		t.setAllFlags(false);

		this.totalNGramMap.put(n, totalNGrams);
		return totalNGrams;
	}

	private LinkedList<T> blockBuilder(int n, Node<T> currentNode) {

		LinkedList<T> block = new LinkedList<>(); // Arrays.asList(new Object[n]);

		for (int q = n - 1; q >= 0; q--) {

			T element = currentNode.getParentEdgeLabel();
			block.add(element);
			currentNode = currentNode.getParent();
		}

		Collections.reverse(block);

		return block;
	}

	/*
	 * @Override public Set<Node<T>> getNodesAt(int depth) { return
	 * this.nodeSetMap.get(depth); }
	 */

	public List<Integer> getNValues() {
		return this.nValues;
	}

	public int totalCounts(int n) {
		int total = 0;
		for (Node<T> node : this.getNodesAt(n)) {
			total += ((NGramNode<T>) node).getCount();
		}
		return total;
	}
	// Print out tree using depth-first search
	/*
	 * private String toString(Node n, String prefix, boolean lastSibling) {
	 *
	 * int count = -1;
	 *
	 * if (n instanceof NGramNode) { count = ((NGramNode) n).getCount(); }
	 *
	 * StringBuilder subTree = new StringBuilder("");
	 * subTree.append(System.lineSeparator() + prefix); subTree.append(n.isRoot() ?
	 * "" : lastSibling ? "└── " : "├── "); int l = n.getParentEdgeLabel() == null ?
	 * -1 : (int) n.getParentEdgeLabel();
	 * subTree.append(LabelFactory.labelEncoding(l) + " : " + n.getVisits() +
	 * (n.getIsEnd() ? " (COUNT: " + count + ")" : "")); prefix += n.isRoot() ? "" :
	 * lastSibling ? "    " : "│   ";
	 *
	 * int c = 1; for (Object childLabel : n.getChildEdgeLabels()) {
	 *
	 * Node child = n.getChild(childLabel); boolean isLast = c++ ==
	 * n.getChildEdgeLabels().size() ? true : false;
	 * subTree.append(this.toString(child, prefix, isLast)); } return
	 * subTree.toString(); }
	 *
	 * @Override public String toString() {
	 *
	 * // System.out.println("this.getRoot(): " + this.getRoot());
	 *
	 * return this.toString(this.getRoot(), "", true); }
	 */
}
