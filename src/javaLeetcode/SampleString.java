package javaLeetcode;

public class SampleString {
	
	public static void main(String[] args) {
		
		
		String str1 = "abc";
		String str2 = "pqr";
		merge(str1,str2);
		char[] charArray2 = str2.toCharArray();
//		char[] charArray = str.toCharArray();
		StringBuffer bf = new StringBuffer();
		StringBuilder builder = new StringBuilder();
		
	
		 
		
		
		
		
		System.out.println(bf.toString());
	}

	private static String merge(String word1, String word2) {
		
		
		  String result = "";
		  
		  
		  char[] charArray1 = word1.toCharArray();
		  char[] charArray2 = word2.toCharArray();
		   StringBuilder bf = new StringBuilder();
		   int i=0;
		   while(i<charArray1.length || i< charArray2.length ) {
		   bf.append(charArray1[i]);
		   bf.append(charArray2[i]);
		   i++;
		   }
		    result = bf.toString().intern();
		 return result ;
	}

}
