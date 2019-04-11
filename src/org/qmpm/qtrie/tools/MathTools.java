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

package org.qmpm.qtrie.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import org.qmpm.batchtester.enums.Label;

import org.qmpm.qtrie.exceptions.LabelTypeException;
import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;
import org.qmpm.qtrie.trie.QIntTrie;
import org.qmpm.qtrie.trie.Trie;
import org.qmpm.qtrie.trie.Trie.Node;

public class MathTools {

	// OTHER

	public static double product(double[] values) {

		double result = 1.0;

		for (int i = 0; i < values.length; i++) {
			result = result * values[i];
		}

		return result;
	}

	public static double sum(double[] values) {

		double result = 0.0;

		for (int i = 0; i < values.length; i++) {
			result = result + values[i];
		}

		return result;
	}

	public static double sum(int[] values) {

		double result = 0.0;

		for (int i = 0; i < values.length; i++) {
			result = result + values[i];
		}

		return result;
	}

	public static double sum(List<Double> values) {

		double result = 0.0;

		for (int i = 0; i < values.size(); i++) {
			result = result + values.get(i);
		}

		return result;
	}

	public static <T> double meanLength(List<List<T>> sequences) {

		long sum = 0;

		for (List<T> seq : sequences) {
			sum += seq.size();
		}

		return (double) sum / sequences.size();
	}

	// COMBINATORICS

	public static List<List<List<Object>>> getPartitions(List<Object> seq) throws LabelTypeException, Exception {

		List<List<List<Object>>> result = new ArrayList<>();

		for (int j = 1; j < seq.size(); j++) {
			// System.out.println("j: " + j);
			List<Integer> initIndices = new ArrayList<>();
			for (int i = 1; i <= j; i++) {
				// System.out.println("i: " + i);
				initIndices.add(i);
			}

			Set<List<Integer>> partitionIndicesSet = getPartitionIndices(initIndices, seq.size());
			// System.out.println(" getting actual partitions...");
			for (List<Integer> partitionIndices : partitionIndicesSet) {
				List<List<Object>> partition = new ArrayList<>();
				for (int i = 1; i < partitionIndices.size(); i++) {
					List<Object> part = seq.subList(partitionIndices.get(i - 1), partitionIndices.get(i));
					partition.add(part);
				}
				result.add(partition);
			}
		}

		return result;
	}

	private static Set<Node<Integer>> buildIndexTrie(Trie<Integer> t, Node<Integer> n, int depth, int numOfIndices, int length, int parentVal) throws LabelTypeException, Exception {

		Set<Node<Integer>> endNodes = new HashSet<>();

		if (depth < numOfIndices) {
			for (Integer i = parentVal; i <= length - numOfIndices + depth; i++) {
				Node<Integer> m = t.createNode(i, n);
				endNodes.addAll(buildIndexTrie(t, m, depth + 1, numOfIndices, length, i + 1));
			}
		} else {
			endNodes.add(n);
		}

		return endNodes;
	}

	public static Set<List<Integer>> getPartitionIndices(List<Integer> indices, int length) throws LabelTypeException, Exception {

		if (indices.size() > 0) {

			Set<List<Integer>> result = new HashSet<>();

			Trie<Integer> indexTrie = new QIntTrie();
			Node<Integer> root = indexTrie.getRoot();

			Set<Node<Integer>> endNodes = buildIndexTrie(indexTrie, root, 0, indices.size(), length, indices.get(0));

			for (Node<Integer> n : endNodes) {
				List<Integer> prefix = indexTrie.getVisitingPrefix(n);
				List<Integer> resIndices = new ArrayList<>();
				resIndices.add(0);
				for (Object e : prefix) {
					resIndices.add((Integer) e);
				}
				resIndices.add(length);
				result.add(resIndices);
			}

			return result;

		} else {
			return null;
		}
	}

	public static Set<List<Integer>> getPartitionIndicesRecursive(List<Integer> indices, int length, Map<Integer, Map<List<Integer>, Set<List<Integer>>>> indicesMap) throws LabelTypeException, Exception {

		System.out.println("         length: " + length + " - indices: " + indices);

		if (indicesMap.containsKey(length)) {
			if (indicesMap.get(length).containsKey(indices)) {

				System.out.println("         found in map!");
				return indicesMap.get(length).get(indices);
			}
		}

		System.out.println("         not found in map, computing...");

		Set<List<Integer>> result = getPartitionIndices(indices, length);

		Map<List<Integer>, Set<List<Integer>>> map = new HashMap<>();
		map.put(indices, result);
		indicesMap.put(length, map);

		return result;

	}

	public static Set<List<Integer>> getPartitionIndicesRecursive(List<Integer> indices, int length, int depth) throws LabelTypeException, Exception {

		for (int i = 0; i < depth; i++) {
			System.out.print(" ");
		}
		System.out.println(depth + ": " + length + " - indices: " + indices);

		Set<List<Integer>> result = new HashSet<>();
		List<Integer> resIndices = new ArrayList<>();
		resIndices.add(0);

		for (Integer i : indices) {
			if (i > 0) {
				resIndices.add(i);
			} else {
				return null;
			}
		}

		resIndices.add(length);
		result.add(resIndices);

		for (int i = 0; i < indices.size(); i++) {
			List<Integer> newIndices = new ArrayList<>();

			if (i < indices.size() - 1 && indices.get(i) + 1 != indices.get(i + 1) || i == indices.size() - 1 && indices.get(i) + 1 < length) {
				for (int j : indices) {
					newIndices.add(j);
				}
				newIndices.set(i, indices.get(i) + 1);
				result.addAll(getPartitionIndicesRecursive(newIndices, length, depth + 1));
			}
		}

		return result;
	}

	// LOGARITHMS
	public static double binaryLog(double n) {
		return Math.log(n) / Math.log(2);
	}

	// MIN & MAX
	public static <T extends Comparable<T>> T maximum(Collection<T> values) {

		T m = null;

		for (T v : values) {
			if (m == null || v.compareTo(m) > 0) {
				m = v;
			}
		}

		return m;
	}

	public static double maximum(double[] values) {

		double m = values[0];

		for (double v : values) {
			if (v > m) {
				m = v;
			}
		}

		return m;
	}

	public static float maximum(float[] values) {

		float m = values[0];

		for (float v : values) {
			if (v > m) {
				m = v;
			}
		}

		return m;
	}

	public static int maximum(int[] values) {

		int m = values[0];

		for (int v : values) {
			if (v > m) {
				m = v;
			}
		}

		return m;
	}

	public static <T extends Comparable<T>> T minimum(Collection<T> values) {

		T m = null;

		for (T v : values) {
			if (m == null || v.compareTo(m) < 0) {
				m = v;
			}
		}

		return m;
	}

	public static double minimum(double[] values) {

		double m = values[0];

		for (double v : values) {
			if (v < m) {
				m = v;
			}
		}

		return m;
	}

	public static float[] minimum(float[] values, int n) {

		n = n > values.length ? values.length : n;

		float[] result = new float[n];

		for (int i = 0; i < n; i++) {
			result[i] = Float.MAX_VALUE;
		}

		for (float v : values) {

			for (int i = 0; i < n; i++) {

				if (v < result[i]) {

					for (int j = n - 1; j > i; j--) {
						result[j] = result[j - 1];
					}

					result[i] = v;
					break;
				}
			}
		}

		return result;
	}

	public static float minimum(float[] values) {

		float m = values[0];

		for (float v : values) {
			if (v < m) {
				m = v;
			}
		}

		return m;
	}

	public static int minimum(int[] values) {

		int m = values[0];

		for (int v : values) {
			if (v < m) {
				m = v;
			}
		}

		return m;
	}

	// SET OPERATIONS
	public static Set<?> complement(Collection<?> colA, Collection<?> colB) {

		Set<? super Object> complement = new HashSet<>();

		complement.addAll(colA);
		complement.removeAll(colB);

		return complement;
	}

	public static Set<?> intersection(Collection<?> colA, Collection<?> colB) {

		Set<? super Object> intersection = new HashSet<>();

		for (Object element : colB) {
			if (colA.contains(element)) {
				intersection.add(element);
			}
		}

		return intersection;
	}

	public static Set<?> union(Collection<?> colA, Collection<?> colB) {

		Set<? super Object> union = new HashSet<>();

		union.addAll(colA);
		union.addAll(colB);

		return union;
	}

	public static <T> List<List<T>> partition(Collection<T> col, int numOfPartitions) {

		List<List<T>> result = new ArrayList<>();
		List<T> input = new ArrayList<>();

		for (T o : col) {
			input.add(o);
		}

		if (numOfPartitions <= 0) {
			result.add(input);
			return result;
		}

		int s = col.size();
		int q = s > numOfPartitions ? s / numOfPartitions : 1;
		int r = s > numOfPartitions ? s % numOfPartitions : 0;

		int step = q;
		// int step = r > 0 ? q + 1 : q;
		for (int i = 0, c = 1; i < s; i += step, c++) {
			step = c <= r ? q + 1 : q;
			// System.out.println("[ " + i + ", " + new Integer(i + step - 1) + " ] - step:
			// " + step);
			result.add(input.subList(i, i + step));
		}

		return result;
	}

	// ROUNDING
	public static double round(double d, int p) {

		double m = Math.pow(10, p);
		return Math.round(d * m) / m;
	}

	public static double round(Number d, int p) {
		return round((double) d, p);
	}

	// ENTROPY & INFORMATION THEORY
	public static double information(double p) throws ValueOutOfBoundsException {

		double result = 0.0;

		if (p < 0 || p > 1.0) {
			throw new ValueOutOfBoundsException("Probability value must be between 0 and 1. Received input: " + p);
		} else if (p == 0.0) {
			result = 0.0;
		} else {
			result = -binaryLog(p);
		}

		return result;
	}

	public static double entropy(List<Double> probDist) throws ValueOutOfBoundsException {

		double[] probDistArr = new double[probDist.size()];

		for (int i = 0; i < probDist.size(); i++) {
			probDistArr[i] = probDist.get(i);
		}

		return entropy(probDistArr);
	}

	public static double entropy(double[] probDist) throws ValueOutOfBoundsException {

		double result = 0.0;

		/*
		 * double sum = 0.0;
		 *
		 * for (double p : probDist) { sum += p; }
		 *
		 * if (MathTools.round(sum, 4) != 1.0) { throw new ValueOutOfBoundsException(
		 * "Invalid probability distribution: does not sum to 1.0. Sums to " + sum); }
		 * else {
		 */
		for (double p : probDist) {
			result += p * information(p);
		}
		return result;
		// }
	}

}