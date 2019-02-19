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

package org.qmpm.logtrie.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qmpm.logtrie.exceptions.LabelTypeException;
import org.qmpm.logtrie.exceptions.NodeNotFoundException;
import org.qmpm.logtrie.exceptions.ProcessTransitionException;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.trie.TrieImpl;

class TrieTest {

	static Trie trie;
	static List<String> seq1 = Arrays.asList("a","b","c");
	static List<String> seq2 = Arrays.asList("a","b","d");
	static List<String> seq3 = Arrays.asList("a","c","b");
	static List<String> seq4 = Arrays.asList("b","c","a");
	static List<String> seq5 = Arrays.asList("b","c","d");
	
	@BeforeAll
	static void setupBeforeClass() {
		trie = new TrieImpl();
		try {
			trie.insert(seq1, false);
			trie.insert(seq2, false);
			trie.insert(seq3, false);
			trie.insert(seq4, false);
			trie.insert(seq5, false);
		} catch (LabelTypeException e) {
			fail("FAIL: Failed to insert sequence (LabelTypeException)");
		} catch (ProcessTransitionException e) {
			fail("FAIL: Failed to insert sequence (ProcessTransitionException)");
		}
	}
	
	@Test
	public void searchTest() throws LabelTypeException, ProcessTransitionException {

		List<String> seq6 = Arrays.asList("a","c","d");
		
		Assertions.assertThrows(NodeNotFoundException.class, (() -> trie.search(seq6)));
		try {
			trie.search(seq1);
		} catch (NodeNotFoundException e) {
			//e.printStackTrace();
			fail("FAIL: Could not find inserted sequence: " + seq1);
		}
	}
}
