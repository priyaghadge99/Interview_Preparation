package javaLeetcode.Interview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LongestCommonPrefix {
	public static void main(String[] args) {

		String[] strArray = { "Flowers", "Flow", "Flight" };
//		String[] strArray = { "aaao", "aaajo", "aaau" };

		Arrays.sort(strArray);
		System.out.println();
		List<String> list = new ArrayList<>();
		for (String string : strArray) {
			list.add(string);
		}
		list.forEach((n)-> System.out.println(n));

		StringBuilder sb = new StringBuilder();

		char[] first = strArray[0].toCharArray();

		char[] last = strArray[strArray.length - 1].toCharArray();

		for (int i = 0; i < first.length; i++) {

			if (first[i] != last[i]) {
				break;
			}

			sb.append(first[i]);
		}
		
		
		System.out.println("LongestCommonPrefix: "+sb.toString());

	}

}