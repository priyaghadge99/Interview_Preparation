package javaLeetcode.String;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MaximumOccuringCharacter {

	public static void main(String[] args) {
		String str = "Java Concept Of The Day";
		maximumOccuringCharacter(str);

	}

	private static void maximumOccuringCharacter(String str) {

		char[] charArray = str.replaceAll("\\s+", "").toCharArray();  //imp replaceall

		HashMap<Character, Integer> elementmap = new HashMap<>();
		for (char c : charArray) {

			if (elementmap.containsKey(c)) {
				elementmap.put(c, elementmap.get(c) + 1);

			} else {
				elementmap.put(c, 1);
			}
		}

		int maxCount = 0;
		char maxChar = 0;
		//to Remove duplicates element of values 
		Set<Entry<Character, Integer>> entryset = elementmap.entrySet();  //a set view of the mappings contained in this map
		for (Entry<Character, Integer> entry : entryset) {

			if (entry.getValue() > maxCount) {
				maxCount = entry.getValue();
				maxChar = entry.getKey();
			}

			
		}
		

		System.out.println("Input String : " + str);

		System.out.println("Maximum Occurring char and its count :");

		System.out.println(maxChar + " : " + maxCount);

	}

}
