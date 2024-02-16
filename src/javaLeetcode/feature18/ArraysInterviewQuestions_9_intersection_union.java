package javaLeetcode.feature18;

import java.util.Arrays;
import java.util.HashSet;

public class ArraysInterviewQuestions_9_intersection_union {
	public static void main(String[] args) {

		String[] s1 = { "ONE", "TWO", "THREE", "FOUR", "FIVE", "FOUR" };

		String[] s2 = { "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "FOUR" };

		intersectionOfTwoArray(s1, s2);

		System.out.println("Union Of All Input Arrays :");

		System.out.println("===========================");

		unionOfTwoArray(s1, s2);

	}

	private static void unionOfTwoArray(String[]... inputArray) {
		HashSet<String> setunion = new HashSet<>();
		System.out.println("Input Arrays :");

		System.out.println("======================");

		for (String[] inputA : inputArray) {
	
			for (int i = 0; i < inputA.length; i++) {

				setunion.add(inputA[i]);
			}
		}

		System.out.println(setunion);
	}

	private static void intersectionOfTwoArray(String[] s1, String[] s2) {

		HashSet<String> hashset = new HashSet<>();
		for (int i = 0; i < s1.length; i++) {
			for (int j = 0; j < s2.length; j++) {
				if (s1[i].equals(s2[j])) {
//					System.out.println("["+s1[i]+ "]");
					hashset.add(s1[i]);
				}

			}
		}

		System.out.println(hashset);

		// one more way to print the array coomon elements using retailall

		HashSet<String> set1 = new HashSet<>(Arrays.asList(s1));
		HashSet<String> set2 = new HashSet<>(Arrays.asList(s2));

		set1.retainAll(set2);
		System.out.print("Using set1 : " + set1);
	}

}
