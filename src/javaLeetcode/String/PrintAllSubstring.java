package javaLeetcode.String;

public class PrintAllSubstring {
	public static void main(String[] args) {

		String str = "APPLE";

		char[] charArray = str.toCharArray();

		for (int i = 0; i < charArray.length; i++) {
			
			String substring = str.substring(i);
			System.out.println(substring);

		}

	}

}
