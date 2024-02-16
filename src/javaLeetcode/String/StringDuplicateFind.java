package javaLeetcode.String;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StringDuplicateFind {

	public static void main(String[] args) {

		String str = "Better Better";

		duplicateFindinString(str);
	}

	private static void duplicateFindinString(String str) {
		char[] charArray = str.toCharArray();

		HashMap<Character, Integer> map = new HashMap<>();
		int frequency = 0;
		for (int i = 0; i < charArray.length; i++) {

			if (map.containsKey(charArray[i])) {
				map.put(charArray[i], map.get(charArray[i]) + 1);
				frequency++;

			} else {
				map.put(charArray[i], 1);
			}

		}
		
		Set<Character> setofkeys = map.keySet();
		System.out.println("Duplicate Character of String : ");

		System.out.println(map.get('B')); // return the value of key
		
		for (Character character : setofkeys) {
			if (map.get(character)>1) {
				System.out.println(character +":"+map.get(character)); //return value
			}
			
		}

	}
}
