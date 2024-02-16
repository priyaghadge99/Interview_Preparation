package javaLeetcode.feature18;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class ArrayInterviewQuestions_7 {
//	How to sort array elements by frequency in Java? //tricky to understand
	public static void main(String[] args) {
		sortArrayElementsByFrequency(new int[] { 7, 1, 3, 4, 7, 1, 7, 1, 4, 5, 1, 9, 3 });

	}

	private static void sortArrayElementsByFrequency(int[] inputArray) {
		
		HashMap<Integer, Integer> map = new LinkedHashMap<>();
		for (int i = 0; i < inputArray.length; i++) {
			
			if (map.containsKey(inputArray[i])) {
				map.put(inputArray[i], map.get(inputArray[i])+1);
			}else {
				map.put(inputArray[i], 1);
			}
			
		}
		
		ArrayList<Entry<Integer, Integer>> list = new ArrayList<>(map.entrySet());
		
		Collections.sort(list, new Comparator<Entry<Integer, Integer>>()
		{
			
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
//				return o1.getValue().compareTo(o2.getValue());  // for asending its 
				return o2.getValue().compareTo(o1.getValue());  // for decending 
			}

		
		});
		
		//Print sorted array elements in descending order of their frequency
        
        System.out.println("Input Array : "+Arrays.toString(inputArray));
          
        System.out.println("Sorted Array Elements In Descending Order Of Their Frequency :");
        System.out.print("[");
        for (Entry<Integer, Integer> entry : list) {
			
        	Integer frequency = entry.getValue();
        	
        	while (frequency>=1) {
        		System.out.print( entry.getKey() +" ");
				frequency--;
			}
        	
		}
		
        System.out.print("]");
		
	}

}
