package javaLeetcode.String;

public class StringToInteger {

	public static void main(String[] args) {
	
		String str = "2013";
		
		System.out.println("=============String to Integer==========================================");
		Integer i = Integer.parseInt(str); // if its char--it will give numberformat exception
		System.out.println(i);
		
		System.out.println("Integer.valueOf(str)");
		
//		inttoStr(i,str);
		System.out.println("=================Integer to String ==================================");
		
		//to Convert Integer To String Using String.valueOf() method :
		String valueOf = String.valueOf(i);
		System.out.println(valueOf);

		
	// integer to string	
		
		String string = Integer.toString(23);
		System.out.println(string);
	
	}

	

}
