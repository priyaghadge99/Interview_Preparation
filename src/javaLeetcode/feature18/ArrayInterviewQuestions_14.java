package javaLeetcode.feature18;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

//Write a Java program which prints contiguous sub arrays with given sum?
public class ArrayInterviewQuestions_14 { /// complex to understand

	public static void main(String[] args) {

		hashingMethod(new int[] { 2, 4, 2, 8, 3, 3, 2, -4, 12 }, 8);

		System.out.println("=====================================");

		hashingMethod(new int[] { 5, -9, 4, -2, 7, 1, -4, -3, -7 }, -7);

		System.out.println("=====================================");

		findSubArray(new int[] { 42, 15, 12, 8, 6, 32 }, 26);

		findSubArray(new int[] { 12, 5, 31, 13, 21, 8 }, 49);
	}

	private static void findSubArray(int[] inputArray, int sum) {
		
		HashMap<Integer, Integer> map = new HashMap<>();
		map.put(0, -1);
		
		int currentSum=0;
		for (int i = 0; i < inputArray.length; i++) {
			currentSum= currentSum+inputArray[i];
			map.put(currentSum, i);
			if (map.containsKey(currentSum-sum)) {
				
				printSubArray(inputArray, map.get(currentSum-sum)+1, i);
			}
			
		}
		
		

	}

	private static void hashingMethod(int[] inputArray, int givenSum) {
		// TODO Auto-generated method stub

		System.out.println("Given Array : " + Arrays.toString(inputArray));

		System.out.println("Given Sum : " + givenSum);

		System.out.println("Contiguous Sub Arrays With Sum " + givenSum + " Are : ");

		HashMap<Integer, Integer> map = new HashMap<>();

		map.put(0, -1);

		int currentSum = 0;
		for (int i = 0; i < inputArray.length; i++) {

			currentSum = currentSum + inputArray[i];

			if (map.containsKey(currentSum - givenSum)) {
				printSubArray(inputArray, map.get(currentSum - givenSum) + 1, i);
			}

			map.put(currentSum, i);

		}

	}

	private static void printSubArray(int[] inputArray, int start, int end) {

		System.out.print("{");
		for (int i = start; i <= end; i++) {
			System.out.print(" " + inputArray[i]);
		}
		System.out.print("}");
		System.out.println();

	}

}
