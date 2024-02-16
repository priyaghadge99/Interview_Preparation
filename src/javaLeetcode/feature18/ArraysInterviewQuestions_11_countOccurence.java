package javaLeetcode.feature18;

import java.util.HashMap;

public class ArraysInterviewQuestions_11_countOccurence {
	public static void main(String[] args) {
		
		countOccurance(new int[] { 4, 5, 8, 7, 4, 7, 6, 7 });
	}

	private static void countOccurance(int[] inputArray) {

		HashMap<Integer, Integer> map = new HashMap<>();
		
		for (int i = 0; i < inputArray.length; i++) {
			
			if (map.containsKey(inputArray[i])) {
				map.put(inputArray[i], map.get(inputArray[i])+1);
				
			} else {
				map.put(inputArray[i],1);
			}
		}
		System.out.println(map);
	}

}
