package javaLeetcode.String;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class RetainAllUse_forCommon {

	public static void main(String[] args) {

		String firstString = "Thin Sticks";
		String secondString = "Thick Bricks";
		
		String str = firstString.replaceAll(" ", "");
		System.out.println(str);

		char[] charArray1 = firstString.replaceAll("\\s+", "" ).toCharArray();
		char[] charArray2 = secondString.replaceAll("\\s+", "" ).toCharArray();

		Set<Character> set1 = new TreeSet<>();
		Set<Character> set2 = new TreeSet<>();

		for (char c : charArray2) {
			set2.add(c);
		}

		for (Character character : charArray1) {
			set1.add(character);
		}

		set1.retainAll(set2);

		System.out.println("Common characters in alphabetical order : " + set1);

		System.out.println("Count : " + set1.size());

	}

}
