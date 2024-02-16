package javaLeetcode.Interview;

import java.util.Iterator;

public class LengthOfLastWord {

	public static void main(String[] args) {

		String str = "Hello World";
		String[] split = str.split(str);
		for (int i = 0; i < split.length; i++) {
			System.out.println();
		}
		
		lengthOfLastWord(str);
		
	}

	private static void lengthOfLastWord(String str) {
		
		String[] split = str.split(" ");
		
		int laststr = split[split.length - 1].length();
		
		System.out.println(laststr);
	}

}
