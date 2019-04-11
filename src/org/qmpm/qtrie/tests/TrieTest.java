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

package org.qmpm.qtrie.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qmpm.qtrie.exceptions.NodeNotFoundException;
import org.qmpm.qtrie.exceptions.ProcessTransitionException;
import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;
import org.qmpm.qtrie.tools.MathTools;
import org.qmpm.qtrie.trie.QIntTrie;
import org.qmpm.qtrie.trie.Trie;
import org.qmpm.qtrie.trie.Trie.Node;

class TrieTest {

	static Trie<Integer> trieNew = new QIntTrie();
	static Trie<Integer> trieOld = new QIntTrie();
	static List<List<String>> insertSequences = new ArrayList<>();
	static List<List<String>> searchSequences = new ArrayList<>();
	static int sequences = 10;
	static int maxLen = 5;
	static long sum = 0;

	@BeforeAll
	static void setupBeforeClass() {

		trieNew.setAlphabetSize(52);
		trieOld.setAlphabetSize(52);

		// String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String alphabet = "ab";

		for (int i = 0; i < sequences; i++) {

			List<String> seq = new ArrayList<>();

			long max = Math.round(Math.random() * maxLen) + 1;
			sum += max;

			for (int j = 0; j < maxLen; j++) {
				int k = Math.round((float) Math.random() * (alphabet.length() - 1));
				seq.add(String.valueOf(alphabet.charAt(k)));
			}

			insertSequences.add(seq);
		}

		searchSequences.addAll(insertSequences.subList(0, sequences / 2));

		for (int i = 0; i < sequences / 2; i++) {

			List<String> seq = new ArrayList<>();

			long max = Math.round(Math.random() * maxLen) + 1;

			for (int j = 0; j < max; j++) {
				int k = Math.round((float) Math.random() * (alphabet.length() - 1));
				seq.add(String.valueOf(alphabet.charAt(k)));
			}

			searchSequences.add(seq);
		}

	}

	/*
	 * @Test public void testSetLeaf() {
	 *
	 * Node n; try { n = trieNew.createNode(null, null);
	 *
	 * n.setIsLeaf(true); assertTrue(n.getIsLeaf()); n.setIsLeaf(false);
	 * assertTrue(!n.getIsLeaf());
	 *
	 * } catch (Exception e) { e.printStackTrace(); fail(""); } }
	 *
	 * @Test public void testSetEnd() {
	 *
	 * Node n; try { n = trieNew.createNode(null, null);
	 *
	 * n.setIsEnd(true); assertTrue(n.getIsEnd()); n.setIsEnd(false);
	 * assertTrue(!n.getIsEnd());
	 *
	 * } catch (Exception e) { e.printStackTrace(); fail(""); } }
	 *
	 * @Test public void testSetRoot() {
	 *
	 * Node n; try { n = trieNew.createNode(null, null);
	 *
	 * n.setIsRoot(true); assertTrue(n.isRoot()); n.setIsRoot(false);
	 * assertTrue(!n.isRoot());
	 *
	 * } catch (Exception e) { e.printStackTrace(); fail(""); } }
	 *
	 * @Test public void testSetFlag() {
	 *
	 * Node n; try { n = trieNew.createNode(null, null);
	 *
	 * n.setFlag(true); assertTrue(n.getFlag()); n.setFlag(false);
	 * assertTrue(!n.getFlag());
	 *
	 * } catch (Exception e) { e.printStackTrace(); fail(""); } }
	 */
	@Test
	public void setAllFlagsTest() {
		this.insertSearchTest();
		// assertEquals(insertSequences.size(), trieNew.getEndNodeSet().size());

		long start = System.nanoTime();

		trieNew.setAllFlagsOld(true);

		long time = System.nanoTime() - start;

		System.out.println("Setting all flags to true took: " + time + " nanoseconds");

		for (Node<?> n : trieNew.getNodes(true)) {
			assertTrue(n.getFlag());
		}
	}

	@Test
	public void setAllFlagsLinkedTest() {
		this.insertSearchTest();
		// assertEquals(insertSequences.size(), trieNew.getEndNodeSet().size());

		long start = System.nanoTime();

		trieNew.setAllFlags(true);

		long time = System.nanoTime() - start;

		System.out.println("Setting all flags to true took: " + time + " nanoseconds");

		for (Node<?> n : trieNew.getNodes(true)) {
			assertTrue(n.getFlag());
		}

		/*
		 * start = System.nanoTime();
		 *
		 * trieNew.setAllFlagsLinked(false);
		 *
		 * time = System.nanoTime() - start;
		 *
		 * System.out.println("Setting all flags to false took: " + time +
		 * " nanoseconds");
		 */
	}

	@Test
	public void multipleFlagSetTest() {
		this.insertSearchTest();

		System.out.println("OLD");
		for (int i = 0; i < 20; i++) {
			this.setAllFlagsTest();
		}
		System.out.println("\nLINKED");
		for (int i = 0; i < 20; i++) {
			this.setAllFlagsLinkedTest();
			;
		}
	}

	@Test
	public void multipleNodeSetTest() {
		this.insertSearchTest();

		System.out.println("NEW");
		for (int i = 0; i < 20; i++) {
			this.nodeSetTest();
		}

	}

	@Test
	public void nodeSetTest() {
		// System.out.println("trie.size(): " + trieNew.getSize());
		this.insertSearchTest();
		long start = System.nanoTime();

		Node<?>[] nodeSet = trieNew.getNodes(true);
		long time = System.nanoTime() - start;

		System.out.print("Building nodeSet took: " + time + " nanoseconds");

		int nodes = 0;
		int nulls = 0;

		for (Node<?> n : nodeSet) {
			if (n instanceof Node) {
				nodes++;
			} else if (n == null) {
				nulls++;
			}
		}

		System.out.print("...Found " + nodes + " of " + trieNew.getSize() + " nodes");
		System.out.println("...Found " + nulls + " nulls");

	}

	@Test
	public void insertSearchTest() {

		long start = System.nanoTime();

		for (List<String> seq : insertSequences) {
			try {
				trieNew.insert(seq, false);
			} catch (ProcessTransitionException | ValueOutOfBoundsException e) {
				e.printStackTrace();
				fail("");
			}
		}

		long time = System.nanoTime() - start;

		System.out.println("=== INSERT TEST ===");
		System.out.println("sequences   : " + insertSequences.size());
		System.out.println("mean length : " + MathTools.meanLength(insertSequences));
		System.out.println("time elapsed: " + time + " nanoseconds\n");

		start = System.nanoTime();

		for (List<String> seq : searchSequences) {
			try {
				trieNew.search(seq);
			} catch (NodeNotFoundException | ValueOutOfBoundsException e) {
				e.printStackTrace();
				fail("");
			}
		}

		time = System.nanoTime() - start;

		System.out.println("=== SEARCH TEST ===");
		System.out.println("sequences   : " + searchSequences.size());
		System.out.println("mean length : " + MathTools.meanLength(searchSequences));
		System.out.println("time elapsed: " + time + " nanoseconds\n");

		System.out.println("INSERTED SEQUENCES");
		for (List<String> seq : insertSequences) {
			for (String s : seq) {
				System.out.print(s);
			}
			System.out.println("");
		}

		System.out.println("\n\n" + trieNew);
	}
}
