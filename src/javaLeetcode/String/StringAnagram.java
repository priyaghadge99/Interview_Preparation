package javaLeetcode.String;

import java.util.Arrays;

public class StringAnagram {

	public static void main(String[] args) {
		String race = "race";
		String car = "care";

		isAnagram(race, car);

	}

	private static void isAnagram(String race, String car) {
		
		
		char[] charArray1 = race.toCharArray();
		char[] charArray2 = car.toCharArray();
		
		Arrays.sort(charArray2);
		Arrays.sort(charArray1);
		
	     boolean equals = Arrays.equals(charArray1, charArray2);
		if (equals) {
			System.out.println("Anagrma");
		}
		else {
		
		System.out.println("Not Angram");
	}

}
	
}
