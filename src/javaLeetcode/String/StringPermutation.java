package javaLeetcode.String;

public class StringPermutation {
	
	public static void main(String[] args) {
		
		stringPermutation("JSP");
	}

	private static void stringPermutation(String input) {
		
		stringPermutation("", input );
	}

	private static void stringPermutation(String permuation, String input) {
		if (input.length()==0) {
			System.out.println(permuation);
		}
		
		else {
			for (int i = 0; i < input.length(); i++) {
				stringPermutation(permuation+input.charAt(i), input.substring(0, i)+input.substring(i+1, input.length()));
				
			}
		}

		
	}

}
