package javaLeetcode.String;

import java.util.HashMap;

public class StringCountOccurence {

	public static void main(String[] args) {

		String str = "Better Better";

		countOccurance(str);
	}

	private static void countOccurance(String str) {
		char[] charArray = str.toCharArray();
		
		HashMap<Character, Integer> map = new HashMap<>();
		for (int i = 0; i < charArray.length; i++) {
			
			
			
			if (map.containsKey(charArray[i])) {
				map.put(charArray[i], map.get(charArray[i])+1);
				
			}else {
				map.put(charArray[i], 1);
			}
		}
		
		System.out.println(map);
		
		System.out.println(map.get('B'));  // return the value of key
		
	}
}
