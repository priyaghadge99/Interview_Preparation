package javaLeetcode.Interview;

import java.util.Arrays;
//import java.util.Scanner;

public class Anagram {

	public static void main(String[] args) {

		String str1 = "race";
		String str2 = "care";
		
		char[] charArray1 = str1.toCharArray();
		char[] charArray2 = str2.toCharArray();
		
		Arrays.sort(charArray1);
		Arrays.sort(charArray2);
		
		
		boolean result = Arrays.equals(charArray1, charArray2);
		
		if (result) {
			System.out.println(str1+ " and " +str2 +" are anagram");
		} else {

			System.out.println(str1+ " and  " +str2 +" are anagram");
		}
		
		
		
	}

}
