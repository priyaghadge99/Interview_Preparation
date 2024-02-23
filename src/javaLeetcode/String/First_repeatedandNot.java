package javaLeetcode.String;

import java.util.HashMap;
import java.util.Map;

public class First_repeatedandNot {

	// j non repeated
	// a repeated

	public static void main(String[] args) {

		String inputString = "JavaConceptOfTheDay";
		firstRepeatedNonRepeatedChar(inputString);
	}

	private static void firstRepeatedNonRepeatedChar(String inputString) {
		char[] charArray = inputString.toCharArray();

		HashMap<Character, Integer> map = new HashMap<>();

		for (int i = 0; i < charArray.length; i++) {

			if (map.containsKey(charArray[i])) {
				map.put(charArray[i], map.get(charArray[i]) + 1);

			} else {
				map.put(charArray[i], 1);
			}

		}

		for (char c : charArray) {
			if (map.get(c) == 1) {
				System.out.println("First Non Repeated " + c);
				break;
			}
		}

		for (char c : charArray) {
			if (map.get(c) > 1) {
				System.out.println("First Repeated " + c);
				break;

			}
		}

	}

}
