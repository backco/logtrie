package org.qmpm.logtrie.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qmpm.logtrie.tools.MathTools;

class MathToolsTest {

	/*
	@BeforeEach
	void setUp() throws Exception {
	}
	 */
		
	int[] array1 = {1,2,3,4,5,6,7};
	int[] array2 = {4,5,4,6,7,8,9,9,9};
	String[] array3 = {"a","b","c","d","e","f","f"};
	String[] array4 = {"d","d","e","f","g","h","i","j"};
	List<Integer> list1 = Arrays.asList(1,2,3,4,5,6,7);
	List<Integer> list2 = Arrays.asList(4,5,4,6,7,8,9,9,9);
	List<String> list3 = Arrays.asList("a","b","c","d","e","f","f");
	List<String> list4 = Arrays.asList("d","d","e","f","g","h","i","j");	
	Set<Integer> set1 = new HashSet<>(Arrays.asList(1,2,3,4,5,6,7));
	Set<Integer> set2 = new HashSet<>(Arrays.asList(4,5,6,7,8,9));
	Set<String> set3 = new HashSet<>(Arrays.asList("a","b","c","d","e","f"));
	Set<String> set4 = new HashSet<>(Arrays.asList("d","e","f","g","h","i","j"));
	
	@Test
	void testMinMax() {
		Integer three = 3;
		Integer eleven = 11;
		assertEquals(three, MathTools.minimum(Arrays.asList(11,9,8,7,8,11,5,3,3)));
		assertEquals(eleven, MathTools.maximum(Arrays.asList(11,9,8,7,8,11,5,3,3)));
	}
	
	@Test
	void testComplement() {
		Set<Integer> inter1 = new HashSet<>(Arrays.asList(1,2,3));
		Set<? extends Object> inter2 = new HashSet<>(Arrays.asList(4,5,6,7,8,9)); 
		assertEquals(inter1, MathTools.complement(set1, list2));
		assertEquals(inter2, MathTools.complement(list2, set3));
	}

	@Test
	void testIntersection() {
		Set<Integer> inter1 = new HashSet<>(Arrays.asList(4,5,6,7)); 
		Set<? extends Object> inter2 = new HashSet<>();
		assertEquals(inter1, MathTools.intersection(set1, list2));
		assertEquals(inter2, MathTools.intersection(list2, set3));
	}
	
	@Test
	void testUnion() {
		Set<Integer> inter1 = new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9)); 
		Set<? extends Object> inter2 = new HashSet<>(Arrays.asList(4,5,6,7,8,9,"a","b","c","d","e","f")); 
		assertEquals(inter1, MathTools.union(set1, list2));
		assertEquals(inter2, MathTools.union(list2, set3));
	}

}
