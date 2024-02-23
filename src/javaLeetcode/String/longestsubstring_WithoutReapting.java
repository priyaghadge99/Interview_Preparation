package javaLeetcode.String;

import java.util.LinkedHashMap;

public class longestsubstring_WithoutReapting {

	public static void main(String[] args) {
		longestsubstring_WithoutReapting("javaconceptoftheday");
	}

	private static void longestsubstring_WithoutReapting(String inputString) {

		String longestSubstring = "";
		// linkedhashmap for insertion order
		LinkedHashMap<Character, Integer> map = new LinkedHashMap<>();

		char[] charArray = inputString.toCharArray();

		for (int i = 0; i < charArray.length; i++) {
			char ch = charArray[i];

			if (!map.containsKey(charArray[i])) {
				map.put(charArray[i], i);

			} else {
				i = map.get(ch);  //geting value of key
				map.clear();

			}
		}

		if (map.size() > 0) {
			longestSubstring = map.keySet().toString();
			System.out.println("Input String : " + inputString);

			System.out.println("The longest substring : " + longestSubstring);
		}
	}

}
