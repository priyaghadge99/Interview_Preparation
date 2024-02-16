package javaLeetcode;

public class StringDivide {
	public static void main(String[] args) {
		String str1 = "ABABAB";
		String str2 = "ABAB";
		 String divide = divide(str1,str2);
		 System.out.println(divide);
		 String gcd = gcd(str1,str2);
		 System.out.println("gcd"+gcd);
//		 String repeat =str1.
//		 System.out.println(repeat);
		
	}

	private static String gcd(String str1,String str2) {
		// TODO Auto-generated method stub
		if (str1 + str2 != str2 + str1){
		      // not possible
		      // no common element
		      return "";
		   } else if (str1 == str2){
		      return str1;
		   } else if (str1.length() > str2.length()){
		      return gcd(str1.substring(0,str1.length()), str2);
		   } else {
		      return gcd(str2.substring(0,str2.length()), str1);
		   }
	}

	private static String divide(String str1, String str2) {
		if(str1.contains(str2) )
		{
			  return str2;
		}
	  else if(str2.contains(str1)) {
			return str1;
		}
	  else {
		  return "";
	  }
		  
	}

}
