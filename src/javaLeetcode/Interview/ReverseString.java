package javaLeetcode.Interview;

public class ReverseString {
	
	public static void main(String[] args) {
		
		String str = "Kitten";
		
		int i=0;
		int j=str.length()-1;
		
		for (int j2 = str.length()-1; j2>=0; j2-- ) {
			System.out.print(str.charAt(j2));
		}
		
		//Remove Specific Character from array using replace 
		System.out.println();
		String mystr = "I Am Priya Ghadge";
		
		mystr.replace("i",""); 
		
		
		System.out.println(mystr.replace("i",""));
	}

}
