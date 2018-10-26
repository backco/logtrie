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

package org.qmpm.logtrie.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
//import org.qmpm.batchtester.enums.Label;

import org.qmpm.logtrie.exceptions.ValueOutOfBoundsException;

public class MathTools {
	
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
		
		for (int i=0; i<n; i++) {
			result[i] = Float.MAX_VALUE;
		}

		for (float v : values) {
			
			for (int i=0; i<n; i++) {
				
				if (v < result[i]) {
					
					for (int j=n-1; j>i; j--) {
						result[j] = result[j-1];
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
	public static Set<? super Object> complement(Collection<? extends Object> colA, Collection<? extends Object> colB) {
		
		Set<? super Object> complement = new HashSet<>();
		
		complement.addAll(colA);
		complement.removeAll(colB);
		
		return complement;
	}
	
	public static Set<? super Object> intersection(Collection<? extends Object> colA, Collection<? extends Object> colB) {
		
		Set<? super Object> intersection = new HashSet<>();
		
		for (Object element : colB) {
			if (colA.contains(element)) {
				intersection.add(element);
			}
		}
		
		return intersection;
	}
	
	public static Set<? super Object> union(Collection<? extends Object> colA, Collection<? extends Object> colB) {
		
		Set<? super Object> union = new HashSet<>();
		
		union.addAll(colA);
		union.addAll(colB);
		
		return union;
	}

	// ROUNDING
	public static double round(double d, int p) {
		
		double m = Math.pow(10, p);
		return Math.round(d * m) / m;
	}
	
	public static double round(Number d, int p) {
		return round((double) d,p);
	}

	// ENTROPY & INFORMATION THEORY
	// TODO: Strictly speaking, this should be labeled "information" of "self-information", not entropy.
	public static double information(double p) throws ValueOutOfBoundsException {
		
		double result = 0.0;
		
		if (p < 0 || p > 1.0) {
			throw new ValueOutOfBoundsException("Probability value must be between 0 and 1. Received input: " + p);
		} else if (p == 0.0) {
			result = 0.0;
		} else {
			result = p * binaryLog(p);
		}
		
		return result;
	}
}