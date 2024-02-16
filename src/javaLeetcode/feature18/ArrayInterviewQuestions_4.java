package javaLeetcode.feature18;

import java.util.Arrays;

public class ArrayInterviewQuestions_4 {

	public static void main(String[] args) {

		getSmallestAndSecondSmallestElement(new int[] { 17, 11, 23, 64, 41, 88, 35 });

		System.out.println("===============================");

		getSmallestAndSecondSmallestElement(new int[] { -9, 3, 36, -25, -9, 71, 0 });
	}

	private static void getSmallestAndSecondSmallestElement(int[] array) {
		int smallest = array[0];
		int secondsmallest= array[0];
		for (int i = 0; i < array.length; i++) {
			
			if (array[i]<smallest) { 
				secondsmallest = smallest;
				smallest=array[i];
			}
		}
		
		System.out.println("Input Array : "+Arrays.toString(array));
        
        System.out.println("Smallest Element : "+smallest);
          
        System.out.println("Second Smallest Element : "+secondsmallest);
		
	}
}
