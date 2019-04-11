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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qmpm.qtrie.tools.MathTools;

class MathToolsTest {

	/*
	 * @BeforeEach void setUp() throws Exception { }
	 */

	int[] array1 = { 1, 2, 3, 4, 5, 6, 7 };
	int[] array2 = { 4, 5, 4, 6, 7, 8, 9, 9, 9 };
	String[] array3 = { "a", "b", "c", "d", "e", "f", "f" };
	String[] array4 = { "d", "d", "e", "f", "g", "h", "i", "j" };
	List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
	List<Integer> list2 = Arrays.asList(4, 5, 4, 6, 7, 8, 9, 9, 9);
	List<String> list3 = Arrays.asList("a", "b", "c", "d", "e", "f", "f");
	List<String> list4 = Arrays.asList("d", "d", "e", "f", "g", "h", "i", "j");
	Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
	Set<Integer> set2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8, 9));
	Set<String> set3 = new HashSet<>(Arrays.asList("a", "b", "c", "d", "e", "f"));
	Set<String> set4 = new HashSet<>(Arrays.asList("d", "e", "f", "g", "h", "i", "j"));

	@Test
	void testMinMax() {
		Integer three = 3;
		Integer eleven = 11;
		assertEquals(three, MathTools.minimum(Arrays.asList(11, 9, 8, 7, 8, 11, 5, 3, 3)));
		assertEquals(eleven, MathTools.maximum(Arrays.asList(11, 9, 8, 7, 8, 11, 5, 3, 3)));
	}

	@Test
	void testComplement() {
		Set<Integer> inter1 = new HashSet<>(Arrays.asList(1, 2, 3));
		Set<?> inter2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8, 9));
		assertEquals(inter1, MathTools.complement(this.set1, this.list2));
		assertEquals(inter2, MathTools.complement(this.list2, this.set3));
	}

	@Test
	void testIntersection() {
		Set<Integer> inter1 = new HashSet<>(Arrays.asList(4, 5, 6, 7));
		Set<?> inter2 = new HashSet<>();
		assertEquals(inter1, MathTools.intersection(this.set1, this.list2));
		assertEquals(inter2, MathTools.intersection(this.list2, this.set3));
	}

	@Test
	void testUnion() {
		Set<Integer> inter1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
		Set<?> inter2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8, 9, "a", "b", "c", "d", "e", "f"));
		assertEquals(inter1, MathTools.union(this.set1, this.list2));
		assertEquals(inter2, MathTools.union(this.list2, this.set3));
	}

	@Test
	void testPartition() {
		List<Integer> l = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		for (int k = -3; k < 11; k++) {
			MathTools.partition(l, k);
		}

		List<Integer> m = Arrays.asList(1, 2, 3);
		for (int k = -3; k < 11; k++) {
			MathTools.partition(m, k);
		}

		List<Integer> n = Arrays.asList(1, 2);
		for (int k = -1; k < 4; k++) {
			MathTools.partition(n, k);
		}

		List<Integer> o = Arrays.asList(1);
		for (int k = -1; k < 4; k++) {
			MathTools.partition(o, k);
		}
	}

	@Test
	public void testGetIndicesPartitions() {

		List<Object> seq = Arrays.asList("a", "b", "c", "d");

		try {
			for (List<List<Object>> partition : MathTools.getPartitions(seq)) {
				System.out.println(partition);
			}
		}

		catch (Exception e1) {
			e1.printStackTrace();
			fail("");
		}

	}

	@Test
	public void testIndicesPartitioning() {

		List<Integer> indices = Arrays.asList(1, 2, 3);
		Set<List<Integer>> result;
		try {
			result = MathTools.getPartitionIndices(indices, 6);
			System.out.println("indicesPartitioning: " + result);
		} catch (Exception e) {
			e.printStackTrace();
			fail("");
		}

	}

	@Test
	void testProduct() {

		double[] arr = { 0.5, 0.8, 0.25 };
		assertEquals(0.1, MathTools.product(arr));
	}

}
