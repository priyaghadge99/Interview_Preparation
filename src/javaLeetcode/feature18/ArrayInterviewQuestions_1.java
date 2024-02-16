package javaLeetcode.feature18;

import java.util.Arrays;

public class ArrayInterviewQuestions_1 {
	public static void main(String[] args) {
		
		//1. Write a Java program to find duplicate elements in an array?
		 int[] inputArray = new int[] {111, 333, 555, 777, 333, 444, 555};
	      
	        System.out.println("======Duplicates Using Brute Force======");
	          
	        findDuplicatesUsingBruteForce(inputArray);
	        
	        System.out.println("======Duplicates Using Sorting======");
	          
	        findDuplicatesUsingSorting(inputArray);
	}

	private static void findDuplicatesUsingSorting(int[] inputArray) {
		Arrays.sort(inputArray);
		for (int i = 0; i < inputArray.length-1; i++) {
			if (inputArray[i]==inputArray[i+1]) {
				System.out.println("Duplictes : " +inputArray[i]);
			}
		}
		
		
	}

	private static void findDuplicatesUsingBruteForce(int[] inputArray) {

		
		for (int i = 0; i < inputArray.length; i++) {
			for (int j=i+1; j < inputArray.length; j++) {
				if (inputArray[i]==inputArray[j]) {
					System.out.println("Duplictae : "+inputArray[i]);
				}
				
			}
		}
	}

}
