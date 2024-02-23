package javaLeetcode.String;

public class ReverseWords {
//How to reverse a sentence word by word in Java?
	public static void main(String[] args) {

		String str = "Java Concept Of The Day";
		reverseSentence(str);
	}

	private static void reverseSentence(String str) {

		String[] split = str.split(" ");
		String reverse=" ";
		for (int i = split.length-1; i >=0; i--) {
			reverse+=split[i] + " ";
//			System.out.println(" ");
		}
		System.out.println(reverse);
	}

}
