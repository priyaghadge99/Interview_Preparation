package javaLeetcode.feature18;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class ArraysInterviewQuestions_10_removeDuplicates {

	public static void main(String[] args) {

		int[] array = new int[] { 1, 2, 3, 4, 5, 6, 8, 7, 8 };
		System.out.println("Before Duplication : " + Arrays.toString(array));
		int[] removeDuplicates = removeDuplicates(array);
		System.out.println(Arrays.toString(removeDuplicates));

	}

	private static int[] removeDuplicates(int[] array) {

		HashSet<Integer> set = new HashSet<>();
		for (int i : array) {
			set.add(i);
		}

		System.out.println("Set : "+set);
		// conevrt set to array
		int[] array1 = new int[set.size()];
		Iterator<Integer> iterator = set.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			array1[i] = iterator.next();
			i++;
		}

		return array1;

	}

}
