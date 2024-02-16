package javaLeetcode.String;

public class ReverseString {
	public static void main(String[] args) {

		String fruit = "APPLE";
		String substring = fruit.substring(1)+ fruit.charAt(0);
		System.out.println("Substring : "+substring);
		reverse(fruit);
		System.out.println();
		String reverseUsingRecursion = reverseUsingRecursion(fruit);
		System.out.println("reverseUsingRecursion");
		System.out.println(reverseUsingRecursion );
		
		System.out.print("==================Reverse Each word==============================");
		String Sample = "Java Concept Of The Day";
		String[] split = Sample.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String string : split) {
			String reverseUsingRecursion2 = reverseUsingRecursion(string);
			sb.append(reverseUsingRecursion2 +" ");
		}
		System.out.println(sb.toString());
		
	}

	private static String reverseUsingRecursion(String str) {

		if (null == str || str.length() <= 1) {
			return str;
		}
		return reverseUsingRecursion(str.substring(1)) + str.charAt(0);

	}
	

	private static void reverse(String fruit) {

		char[] charArray = fruit.toCharArray();

		for (int i = charArray.length - 1; i >= 0; i--) {
			System.out.print(charArray[i]);
		}
	}

}
