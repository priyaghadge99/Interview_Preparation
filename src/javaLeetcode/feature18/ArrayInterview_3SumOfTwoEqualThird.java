package javaLeetcode.feature18;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class ArrayInterview_3SumOfTwoEqualThird {

	public static void main(String[] args) {

		getArrayTriplets(new int[] { 21, 13, 47, 61, 34, 40, 55, 71, 87 });

		System.out.println("===========================================");

		getArrayTriplets(new int[] { -1, 3, -2, 1, -4, 0, 5, 2, -3 });
	}

	private static void getArrayTriplets(int[] array) {

		HashSet<Integer> hashset = new HashSet<>();
		for (Integer integer : array) {
			hashset.add(integer);
		}
		
		for (int i = 0; i < array.length; i++) {
			for (int j = i+1; j < array.length; j++) {
				
				if (hashset.contains(array[i]+array[j])) {
					System.out.println("[ "+ array[i]+ "," +array[j]+ "," +(array[i]+array[j]) +"]");
					
				}
			}
		}

	}

}
