package javaLeetcode.Interview;

import java.util.Iterator;
import java.util.Scanner;

public class PalindromString {
	public static void main(String args[]) {
		Scanner s = new Scanner(System.in);
		String word = s.nextLine();
		System.out.println("Is " + word + " palindrome? - " + isWordPalindrome(word));

		String str = reverseString(word);

		if (str.equals(word)) {

			System.out.println("Palindrome : " + word);
		}
		else {
			System.out.println("Not PAlindrom");
		}
	}

	private static String reverseString(String word) {
		String reverse = "";
		StringBuilder sb = new StringBuilder();

		for (int i = word.length() - 1; i > 0; i--) {

			reverse += word.charAt(i);
			sb.append(word.charAt(i));

		}
        System.out.println("Printing using String builder "+sb.toString());
		return reverse;
	}

	public static boolean isWordPalindrome(String word) {
		String reverseWord = getReverseWord(word);
		System.out.println("ReverseWord " + reverseWord);
		// if word equals its reverse, then it is a palindrome
		System.out.println("Word : " + word);
		if (word.equals(reverseWord)) {
			return true;
		}
		return false;
	}

	public static String getReverseWord(String word) {
		if (word == null || word.isEmpty()) {
			return word;
		}
		System.out.println(word);
		return word.charAt(word.length() - 1) + getReverseWord(word.substring(0, word.length() - 1));
	}

}
