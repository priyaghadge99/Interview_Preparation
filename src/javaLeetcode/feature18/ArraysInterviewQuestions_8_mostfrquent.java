package javaLeetcode.feature18;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class ArraysInterviewQuestions_8_mostfrquent {

	public static void main(String[] args) {

		getMostFrequentElement(new int[] { 4, 5, 8, 7, 4, 7, 6, 7 });

		getMostFrequentElement(new int[] { 1, 2, 7, 5, 3, 6 });

	}

	private static void getMostFrequentElement(int[] array) {

		HashMap<Integer, Integer> elementMap = new LinkedHashMap<>();
		
		for (int i = 0; i < array.length; i++) {
			
			if (elementMap.containsKey(array[i])) {
				
				elementMap.put(array[i], elementMap.get(array[i])+1);
			}
			else {
				elementMap.put(array[i], 1);
			}
		}
		
		int element=0;
		int frequency =1;
		for (Entry<Integer, Integer> entry : elementMap.entrySet()) {
			Integer key = entry.getKey();
			Integer val = entry.getValue();
			if (val>frequency) {
				element=key;
				frequency= val;
			}
			
		}
		
		
		 if(frequency > 1)
	        {
	            System.out.println("Input Array : "+Arrays.toString(array));
	              
	            System.out.println("The most frequent element : "+element);
	              
	            System.out.println("Its frequency : "+frequency);
	              
	            System.out.println("========================");
	        }
		
	}

}
